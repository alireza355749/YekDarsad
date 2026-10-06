package com.example.yekdarsad.ui.overview

import com.example.yekdarsad.data.Category
import com.example.yekdarsad.data.DailyPlanWithTask
import java.time.DayOfWeek
import java.time.LocalDate

data class MonthlyCategoryTotal(
    val category: Category,
    val totalMinutes: Int
)

fun daysInJalaliMonth(
    year: Int,
    month: Int
): Int {

    return when {

        month <= 6 ->
            31

        month <= 11 ->
            30

        else ->
            if (isJalaliLeapYear(year)) {
                30
            } else {
                29
            }
    }
}

fun calculateCalendarCells(
    startOffset: Int,
    daysInMonth: Int
): Int {

    return (
            (startOffset + daysInMonth + 6) / 7
            ) * 7
}

fun buildMonthlyCalendarDates(
    year: Int,
    month: Int,
    startOffset: Int,
    daysInMonth: Int,
    totalCells: Int
): List<LocalDate?> {

    return (0 until totalCells).map { index ->

        if (
            index < startOffset ||
            index >= startOffset + daysInMonth
        ) {
            null
        } else {

            val jalaliDay =
                index - startOffset + 1

            jalaliToGregorian(
                year,
                month,
                jalaliDay
            )
        }
    }
}

fun buildMonthlyCategoryTotals(
    plansByDate: Map<String, List<DailyPlanWithTask>>,
    categories: List<Category>
): List<MonthlyCategoryTotal> {

    val totals =
        mutableMapOf<Int, Int>()

    plansByDate.values
        .flatten()
        .forEach { plan ->

            val task = plan.task

            /*
             * ضریب صفر وارد خلاصه نمی‌شود.
             */
            if (task.coefficient == 0.0) {
                return@forEach
            }

            /*
             * فقط فعالیت‌های زمانی.
             */
            if (task.type != "TIME") {
                return@forEach
            }

            val minutes =
                plan.dailyPlan
                    .plannedMinutes
                    .coerceAtLeast(0)

            if (minutes <= 0) {
                return@forEach
            }

            totals[task.categoryId] =
                (totals[task.categoryId] ?: 0) +
                        minutes
        }

    return categories
        .mapNotNull { category ->

            val minutes =
                totals[category.id] ?: 0

            if (minutes > 0) {

                MonthlyCategoryTotal(
                    category = category,
                    totalMinutes = minutes
                )

            } else {
                null
            }
        }
        .sortedByDescending {
            it.totalMinutes
        }
}

fun jalaliDayFromGregorian(
    date: LocalDate
): Int {

    return gregorianToJalali(
        date.year,
        date.monthValue,
        date.dayOfMonth
    )[2]
}

fun saturdayBasedDayIndex(
    date: LocalDate
): Int {

    return when (date.dayOfWeek) {

        DayOfWeek.SATURDAY -> 0
        DayOfWeek.SUNDAY -> 1
        DayOfWeek.MONDAY -> 2
        DayOfWeek.TUESDAY -> 3
        DayOfWeek.WEDNESDAY -> 4
        DayOfWeek.THURSDAY -> 5
        DayOfWeek.FRIDAY -> 6
    }
}

fun jalaliToGregorian(
    jy: Int,
    jm: Int,
    jd: Int
): LocalDate {

    val jyTemp = jy - 979

    var jDayNo =
        365 * jyTemp +
                (jyTemp / 33) * 8 +
                ((jyTemp % 33) + 3) / 4

    for (i in 0 until jm - 1) {

        jDayNo +=
            if (i < 6) 31 else 30
    }

    jDayNo += jd - 1

    var gDayNo = jDayNo + 79

    var gy =
        1600 +
                400 * (gDayNo / 146097)

    gDayNo %= 146097

    var leap = true

    if (gDayNo >= 36525) {

        gDayNo--

        gy +=
            100 * (gDayNo / 36524)

        gDayNo %= 36524

        if (gDayNo >= 365) {
            gDayNo++
        } else {
            leap = false
        }
    }

    gy +=
        4 * (gDayNo / 1461)

    gDayNo %= 1461

    if (gDayNo >= 366) {

        leap = false

        gDayNo--

        gy += gDayNo / 365

        gDayNo %= 365
    }

    var gm = 0

    val gDaysInMonth =
        intArrayOf(
            31,
            if (leap) 29 else 28,
            31,
            30,
            31,
            30,
            31,
            31,
            30,
            31,
            30,
            31
        )

    while (
        gm < 12 &&
        gDayNo >= gDaysInMonth[gm]
    ) {

        gDayNo -= gDaysInMonth[gm]
        gm++
    }

    return LocalDate.of(
        gy,
        gm + 1,
        gDayNo + 1
    )
}

fun gregorianToJalali(
    gy: Int,
    gm: Int,
    gd: Int
): IntArray {

    val gDaysInMonth =
        intArrayOf(
            31,
            28,
            31,
            30,
            31,
            30,
            31,
            31,
            30,
            31,
            30,
            31
        )

    val jDaysInMonth =
        intArrayOf(
            31,
            31,
            31,
            31,
            31,
            31,
            30,
            30,
            30,
            30,
            30,
            29
        )

    val gyTemp = gy - 1600
    val gmTemp = gm - 1
    val gdTemp = gd - 1

    var gDayNo =
        365 * gyTemp +
                (gyTemp + 3) / 4 -
                (gyTemp + 99) / 100 +
                (gyTemp + 399) / 400

    for (i in 0 until gmTemp) {
        gDayNo += gDaysInMonth[i]
    }

    if (
        gmTemp > 1 &&
        gy % 4 == 0 &&
        (
                gy % 100 != 0 ||
                        gy % 400 == 0
                )
    ) {
        gDayNo++
    }

    gDayNo += gdTemp

    var jDayNo = gDayNo - 79

    val jNp = jDayNo / 12053

    jDayNo %= 12053

    var jy =
        979 +
                33 * jNp +
                4 * (jDayNo / 1461)

    jDayNo %= 1461

    if (jDayNo >= 366) {

        jy +=
            (jDayNo - 1) / 365

        jDayNo =
            (jDayNo - 1) % 365
    }

    var jm = 0

    while (
        jm < 11 &&
        jDayNo >= jDaysInMonth[jm]
    ) {

        jDayNo -= jDaysInMonth[jm]

        jm++
    }

    return intArrayOf(
        jy,
        jm + 1,
        jDayNo + 1
    )
}

fun isJalaliLeapYear(
    year: Int
): Boolean {

    val currentYear =
        jalaliToGregorian(
            year,
            1,
            1
        )

    val nextYear =
        jalaliToGregorian(
            year + 1,
            1,
            1
        )

    val days =
        java.time.temporal.ChronoUnit.DAYS
            .between(
                currentYear,
                nextYear
            )

    return days == 366L
}

fun persianMonthName(
    month: Int
): String {

    return listOf(
        "فروردین",
        "اردیبهشت",
        "خرداد",
        "تیر",
        "مرداد",
        "شهریور",
        "مهر",
        "آبان",
        "آذر",
        "دی",
        "بهمن",
        "اسفند"
    )[month - 1]
}

fun formatMinutesMonthly(
    totalMinutes: Int
): String {

    val hours =
        totalMinutes / 60

    val minutes =
        totalMinutes % 60

    return when {

        hours > 0 && minutes > 0 ->

            "${toPersianDigits(
                hours.toString()
            )}:" +
                    toPersianDigits(
                        minutes
                            .toString()
                            .padStart(2, '0')
                    )

        hours > 0 ->

            "${toPersianDigits(
                hours.toString()
            )}:۰۰"

        else ->

            "${toPersianDigits(
                minutes.toString()
            )} دقیقه"
    }
}