package com.example.yekdarsad.data.statistics

data class StatisticsData(

    // =====================================================
    // حالت فعلی آمار
    // =====================================================

    val periodType: StatisticsPeriod =
        StatisticsPeriod.WEEKLY,

    // =====================================================
    // برچسب روزها
    // =====================================================

    val periodDays: List<String> =
        emptyList(),

    // برای سازگاری با UI فعلی
    val weekDays: List<String> =
        emptyList(),

    // =====================================================
    // فعالیت
    // =====================================================

    val activityHours: List<Float> =
        emptyList(),

    // سازگاری با کد فعلی Repository و UI
    val weeklyActivityHours: List<Float> =
        emptyList(),

    val totalActivityHours: Float =
        0f,

    // =====================================================
    // فعالیت بر اساس دسته‌بندی
    // =====================================================

    val categoryActivityHours:
    Map<String, Float> =
        emptyMap(),

    val categoryPeriodActivityHours:
    Map<String, List<Float>> =
        emptyMap(),

    // سازگاری با کد فعلی
    val categoryWeeklyActivityHours:
    Map<String, List<Float>> =
        emptyMap(),

    // =====================================================
    // خواب
    // =====================================================

    val sleepHours: List<Float> =
        emptyList(),

    val averageSleepHours: Float =
        0f,

    // =====================================================
    // مصرف موبایل
    // =====================================================

    val phoneUsageHours: List<Float> =
        emptyList(),

    // =====================================================
    // تغذیه / کالری
    // =====================================================

    val calorieIntake: List<Float> =
        emptyList(),

    val totalCalories: Float =
        0f,

    val averageCalories: Float =
        0f,

    // =====================================================
    // ورزش
    // =====================================================

    val exerciseHours: List<Float> =
        emptyList(),

    val exerciseCalories: List<Float> =
        emptyList(),

    // =====================================================
    // دسته‌های خاص
    // =====================================================

    val leisureHours: List<Float> =
        emptyList(),

    val spiritualityHours: List<Float> =
        emptyList(),

    // =====================================================
    // امتیاز
    // =====================================================

    val dailyScores: List<Float> =
        emptyList(),

    val weeklyScore: Float =
        0f,

    val periodScore: Float =
        0f,

    // =====================================================
    // مخارج
    // =====================================================

    val expenses: List<Float> =
        emptyList()
)

enum class StatisticsPeriod {

    WEEKLY,

    MONTHLY
}