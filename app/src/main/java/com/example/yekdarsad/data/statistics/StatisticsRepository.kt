package com.example.yekdarsad.data.statistics

import com.example.yekdarsad.data.DailyPlan
import com.example.yekdarsad.data.DailyPlanDao
import com.example.yekdarsad.data.phoneusage.PhoneUsageDao
import com.example.yekdarsad.data.sleep.SleepDao
import com.example.yekdarsad.data.nutrition.NutritionDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class StatisticsRepository(
    private val dailyPlanDao: DailyPlanDao,
    private val phoneUsageDao: PhoneUsageDao,
    private val sleepDao: SleepDao,
    private val nutritionDao: NutritionDao
) {

    private val zoneId =
        ZoneId.of("Asia/Tehran")

    // =====================================================
    // آمار فعلی
    // =====================================================

    fun getStatistics(
        period: StatisticsPeriod = StatisticsPeriod.WEEKLY
    ): Flow<StatisticsData> {

        val today =
            LocalDate.now(zoneId)

        val startDate =
            when (period) {

                StatisticsPeriod.WEEKLY ->
                    getSaturday(today)

                StatisticsPeriod.MONTHLY ->
                    today.withDayOfMonth(1)
            }

        val endDate =
            when (period) {

                StatisticsPeriod.WEEKLY ->
                    startDate.plusDays(6)

                StatisticsPeriod.MONTHLY ->
                    YearMonth.from(today)
                        .atEndOfMonth()
            }

        val dates =
            generateDateRange(
                startDate = startDate,
                endDate = endDate
            )

        return combine(
            dailyPlanDao.getAllCompletedPlans(),
            dailyPlanDao.getCategoryStatisticsRaw(),

            *dates.map { date ->
                createDayFlow(date)
            }.toTypedArray()

        ) { values ->

            val plans =
                values[0] as List<DailyPlan>

            val categoryRows =
                values[1] as List<String>

            val days =
                values
                    .drop(2)
                    .map {
                        it as DailyStatisticsDay
                    }

            buildStatistics(
                plans = plans,
                categoryRows = categoryRows,
                days = days,
                dates = dates,
                startDate = startDate,
                period = period
            )
        }
    }

    // =====================================================
    // شنبه هفته
    // =====================================================

    private fun getSaturday(
        date: LocalDate
    ): LocalDate {

        return date.minusDays(
            ((date.dayOfWeek.value + 1) % 7).toLong()
        )
    }

    // =====================================================
    // ساخت بازه تاریخ
    // =====================================================

    private fun generateDateRange(
        startDate: LocalDate,
        endDate: LocalDate
    ): List<LocalDate> {

        val days =
            ChronoUnit.DAYS.between(
                startDate,
                endDate
            ).toInt()

        return (0..days).map { index ->
            startDate.plusDays(
                index.toLong()
            )
        }
    }

    // =====================================================
    // اطلاعات یک روز
    // =====================================================

    private fun createDayFlow(
        date: LocalDate
    ): Flow<DailyStatisticsDay> {

        val dateString =
            date.toString()

        return combine(

            // ---------------------------------------------
            // برنامه‌های روز
            // ---------------------------------------------

            dailyPlanDao.getAllPlansOfDate(
                dateString
            ),

            // ---------------------------------------------
            // مصرف موبایل
            // ---------------------------------------------

            phoneUsageDao.getTotal(
                dateString
            ),

            // ---------------------------------------------
            // خواب
            // ---------------------------------------------

            sleepDao.getByDate(
                dateString
            ),

            // ---------------------------------------------
            // غذا
            // ---------------------------------------------

            nutritionDao.getFoodEntries(
                dateString
            ),

            // ---------------------------------------------
            // کالری ورزش
            // ---------------------------------------------

            dailyPlanDao.getExerciseCalories(
                dateString
            )

        ) { plans, phoneUsage, sleep, foods, exerciseCalories ->

            // =================================================
            // فعالیت
            // =================================================

            val activityMinutes =
                plans.sumOf {
                    it.actualMinutes
                }

            val activityHours =
                activityMinutes / 60f

            // =================================================
            // ورزش
            // =================================================

            val exercisePlans =
                plans.filter {
                    it.exerciseCalories > 0.0
                }

            val exerciseMinutes =
                exercisePlans.sumOf {
                    it.actualMinutes
                }

            val exerciseHours =
                exerciseMinutes / 60f

            // =================================================
            // خواب
            // =================================================

            val sleepHours =
                calculateSleepHours(
                    sleep
                )

            // =================================================
            // موبایل
            // =================================================

            val phoneHours =
                (phoneUsage?.totalMinutes ?: 0L) / 60f

            // =================================================
            // کالری دریافتی
            // =================================================

            var calories =
                0.0

            foods.forEach { entryWithFood ->

                val food =
                    entryWithFood.food

                if (food != null) {

                    val caloriesPerGram =
                        food.caloriesPer100g / 100.0

                    calories +=
                        entryWithFood.entry.amount *
                                caloriesPerGram
                }
            }

            DailyStatisticsDay(

                activityHours =
                    activityHours,

                phoneUsageHours =
                    phoneHours,

                sleepHours =
                    sleepHours,

                calorieIntake =
                    calories.toFloat(),

                exerciseHours =
                    exerciseHours,

                exerciseCalories =
                    exerciseCalories.toFloat()
            )
        }
    }

    // =====================================================
    // ساخت StatisticsData
    // =====================================================

    private fun buildStatistics(
        plans: List<DailyPlan>,
        categoryRows: List<String>,
        days: List<DailyStatisticsDay>,
        dates: List<LocalDate>,
        startDate: LocalDate,
        period: StatisticsPeriod
    ): StatisticsData {

        // =================================================
        // برچسب‌های بازه
        // =================================================

        val periodDays =
            when (period) {

                StatisticsPeriod.WEEKLY ->
                    listOf(
                        "ش",
                        "ی",
                        "د",
                        "س",
                        "چ",
                        "پ",
                        "ج"
                    )

                StatisticsPeriod.MONTHLY ->
                    dates.map {
                        it.dayOfMonth.toString()
                    }
            }

        // =================================================
        // سازگاری با UI هفتگی
        // =================================================

        val weekDays =
            if (period == StatisticsPeriod.WEEKLY) {
                periodDays
            } else {
                emptyList()
            }

        // =================================================
        // فعالیت
        // =================================================

        val activityHours =
            days.map {
                it.activityHours
            }

        val totalActivityHours =
            activityHours.sum()

        // =================================================
        // خواب
        // =================================================

        val sleepHours =
            days.map {
                it.sleepHours
            }

        val positiveSleepValues =
            sleepHours.filter {
                it > 0f
            }

        val averageSleepHours =
            if (positiveSleepValues.isNotEmpty()) {
                positiveSleepValues
                    .average()
                    .toFloat()
            } else {
                0f
            }

        // =================================================
        // موبایل
        // =================================================

        val phoneUsageHours =
            days.map {
                it.phoneUsageHours
            }

        // =================================================
        // کالری
        // =================================================

        val calorieIntake =
            days.map {
                it.calorieIntake
            }

        val totalCalories =
            calorieIntake.sum()

        val positiveCalorieValues =
            calorieIntake.filter {
                it > 0f
            }

        val averageCalories =
            if (positiveCalorieValues.isNotEmpty()) {
                positiveCalorieValues
                    .average()
                    .toFloat()
            } else {
                0f
            }

        // =================================================
        // ورزش
        // =================================================

        val exerciseHours =
            days.map {
                it.exerciseHours
            }

        val exerciseCalories =
            days.map {
                it.exerciseCalories
            }

        // =================================================
        // دسته‌بندی‌ها
        // =================================================

        val categoryTotals =
            mutableMapOf<String, Float>()

        val categoryPeriod =
            mutableMapOf<String, MutableList<Float>>()

        categoryRows.forEach { row ->

            val parts =
                row.split("|")

            if (parts.size != 3) {
                return@forEach
            }

            val dateString =
                parts[0].trim()

            val categoryName =
                parts[1].trim()

            val minutes =
                parts[2]
                    .trim()
                    .toIntOrNull()
                    ?: return@forEach

            val date =
                runCatching {
                    LocalDate.parse(
                        dateString
                    )
                }.getOrNull()
                    ?: return@forEach

            // فقط تاریخ‌های داخل بازه
            val dayIndex =
                ChronoUnit.DAYS.between(
                    startDate,
                    date
                ).toInt()

            if (
                dayIndex !in
                dates.indices
            ) {
                return@forEach
            }

            val hours =
                minutes / 60f

            // ---------------------------------------------
            // مجموع دسته
            // ---------------------------------------------

            categoryTotals[categoryName] =
                (categoryTotals[categoryName] ?: 0f) +
                        hours

            // ---------------------------------------------
            // اطلاعات روزانه دسته
            // ---------------------------------------------

            val periodList =
                categoryPeriod.getOrPut(
                    categoryName
                ) {
                    MutableList(
                        dates.size
                    ) {
                        0f
                    }
                }

            periodList[dayIndex] +=
                hours
        }

        // =================================================
        // تبدیل نهایی دسته‌ها
        // =================================================

        val finalCategoryPeriod =
            categoryPeriod.mapValues {
                it.value.toList()
            }

        val finalCategoryWeekly =
            if (
                period ==
                StatisticsPeriod.WEEKLY
            ) {
                finalCategoryPeriod
            } else {
                emptyMap()
            }

        // =================================================
        // دسته‌های خاص
        // =================================================

        val leisureHours =
            findCategoryPeriodHours(
                categories =
                    finalCategoryPeriod,

                possibleNames =
                    listOf(
                        "تفریح",
                        "سرگرمی"
                    )
            )

        val spiritualityHours =
            findCategoryPeriodHours(
                categories =
                    finalCategoryPeriod,

                possibleNames =
                    listOf(
                        "معنویت",
                        "نماز",
                        "عبادت"
                    )
            )

        // =================================================
        // امتیاز روزانه
        // =================================================

        val dailyScores =
            dates.map { date ->

                plans
                    .filter {
                        it.date.trim() ==
                                date.toString()
                    }
                    .sumOf {
                        it.earnedScore
                    }
                    .toFloat()
            }

        val periodScore =
            dailyScores.sum()

        // =================================================
        // خروجی
        // =================================================

        return StatisticsData(

            periodType =
                period,

            periodDays =
                periodDays,

            weekDays =
                weekDays,

            activityHours =
                activityHours,

            weeklyActivityHours =
                activityHours,

            totalActivityHours =
                totalActivityHours,

            categoryActivityHours =
                categoryTotals,

            categoryPeriodActivityHours =
                finalCategoryPeriod,

            categoryWeeklyActivityHours =
                finalCategoryWeekly,

            sleepHours =
                sleepHours,

            averageSleepHours =
                averageSleepHours,

            phoneUsageHours =
                phoneUsageHours,

            calorieIntake =
                calorieIntake,

            totalCalories =
                totalCalories,

            averageCalories =
                averageCalories,

            exerciseHours =
                exerciseHours,

            exerciseCalories =
                exerciseCalories,

            leisureHours =
                leisureHours,

            spiritualityHours =
                spiritualityHours,

            dailyScores =
                dailyScores,

            weeklyScore =
                if (
                    period ==
                    StatisticsPeriod.WEEKLY
                ) {
                    periodScore
                } else {
                    0f
                },

            periodScore =
                periodScore
        )
    }

    // =====================================================
    // پیدا کردن دسته خاص
    // =====================================================

    private fun findCategoryPeriodHours(
        categories:
        Map<String, List<Float>>,

        possibleNames:
        List<String>
    ): List<Float> {

        val size =
            categories.values
                .firstOrNull()
                ?.size
                ?: 0

        if (size == 0) {
            return emptyList()
        }

        val result =
            MutableList(size) {
                0f
            }

        categories.forEach { entry ->

            val categoryName =
                entry.key.trim()

            val matched =
                possibleNames.any { possibleName ->

                    categoryName.contains(
                        possibleName,
                        ignoreCase = true
                    )
                }

            if (matched) {

                entry.value
                    .forEachIndexed { index, value ->

                        if (
                            index in
                            result.indices
                        ) {
                            result[index] +=
                                value
                        }
                    }
            }
        }

        return result
    }

    // =====================================================
    // محاسبه خواب
    // =====================================================

    private fun calculateSleepHours(
        sleep:
        com.example.yekdarsad.data.sleep.SleepEntry?
    ): Float {

        if (sleep == null) {
            return 0f
        }

        val startMinutes =
            sleep.sleepStartHour * 60 +
                    sleep.sleepStartMinute

        val wakeMinutes =
            sleep.wakeHour * 60 +
                    sleep.wakeMinute

        var sleepMinutes =
            wakeMinutes -
                    startMinutes

        // خواب از شب قبل تا صبح
        if (sleepMinutes <= 0) {

            sleepMinutes +=
                24 * 60
        }

        sleepMinutes +=
            sleep.napsMinutes

        return sleepMinutes / 60f
    }
}

// =========================================================
// اطلاعات آماری یک روز
// =========================================================

private data class DailyStatisticsDay(

    val activityHours: Float,

    val phoneUsageHours: Float,

    val sleepHours: Float,

    val calorieIntake: Float,

    val exerciseHours: Float,

    val exerciseCalories: Float
)