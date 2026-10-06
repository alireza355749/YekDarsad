package com.example.yekdarsad.ui.overview

import com.example.yekdarsad.data.Category
import com.example.yekdarsad.data.DailyPlanWithTask
import java.time.LocalDate

enum class OverviewMode {
    WEEKLY,
    MONTHLY
}

data class OverviewCategoryTime(
    val category: Category,
    val minutes: Int
)

fun calculateCategoryTimes(
    plansByDate: Map<String, List<DailyPlanWithTask>>,
    categories: List<Category>
): List<OverviewCategoryTime> {

    val totals = mutableMapOf<Int, Int>()

    plansByDate.values
        .flatten()
        .forEach { plan ->

            val task = plan.task

            if (task.coefficient == 0.0) {
                return@forEach
            }

            if (task.type != "TIME") {
                return@forEach
            }

            val minutes = plan.dailyPlan
                .plannedMinutes
                .coerceAtLeast(0)

            if (minutes <= 0) {
                return@forEach
            }

            totals[task.categoryId] =
                (totals[task.categoryId] ?: 0) + minutes
        }

    return categories
        .mapNotNull { category ->

            val minutes = totals[category.id] ?: 0

            if (minutes > 0) {
                OverviewCategoryTime(
                    category = category,
                    minutes = minutes
                )
            } else {
                null
            }
        }
        .sortedByDescending {
            it.minutes
        }
}


/*
 * نمایش کامل زمان به فارسی.
 *
 * مثال:
 * 30  -> ۳۰ دقیقه
 * 60  -> ۱ ساعت
 * 90  -> ۱ ساعت و ۳۰ دقیقه
 * 150 -> ۲ ساعت و ۳۰ دقیقه
 *
 * استفاده از RLM باعث می‌شود ترکیب عدد و متن فارسی
 * در Text به‌صورت درست راست‌به‌چپ نمایش داده شود.
 */
fun formatOverviewMinutes(
    minutes: Int
): String {

    if (minutes <= 0) {
        return "\u200F۰ دقیقه"
    }

    val hours = minutes / 60
    val remainingMinutes = minutes % 60

    val result = when {

        hours > 0 && remainingMinutes > 0 -> {

            "${toPersianDigits(hours.toString())} ساعت و ${
                toPersianDigits(
                    remainingMinutes.toString()
                )
            } دقیقه"
        }

        hours > 0 -> {

            "${toPersianDigits(
                hours.toString()
            )} ساعت"
        }

        else -> {

            "${toPersianDigits(
                remainingMinutes.toString()
            )} دقیقه"
        }
    }

    return "\u200F$result"
}


/*
 * تبدیل اعداد انگلیسی به اعداد فارسی
 */
fun toPersianDigits(
    value: String
): String {

    return value
        .replace("0", "۰")
        .replace("1", "۱")
        .replace("2", "۲")
        .replace("3", "۳")
        .replace("4", "۴")
        .replace("5", "۵")
        .replace("6", "۶")
        .replace("7", "۷")
        .replace("8", "۸")
        .replace("9", "۹")
}


/*
 * تبدیل تاریخ میلادی به شمسی
 */
fun gregorianToJalaliOverview(
    gy: Int,
    gm: Int,
    gd: Int
): IntArray {

    val gDaysInMonth = intArrayOf(
        31, 28, 31, 30, 31, 30,
        31, 31, 30, 31, 30, 31
    )

    val jDaysInMonth = intArrayOf(
        31, 31, 31, 31, 31, 31,
        30, 30, 30, 30, 30, 29
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

        jDayNo -=
            jDaysInMonth[jm]

        jm++
    }

    return intArrayOf(
        jy,
        jm + 1,
        jDayNo + 1
    )
}


/*
 * نام ماه‌های شمسی
 */
fun overviewJalaliMonthName(
    month: Int
): String {

    val monthNames = listOf(
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
    )

    return monthNames.getOrElse(month - 1) {
        ""
    }
}


/*
 * نمایش تاریخ شمسی:
 *
 * مثال:
 * ۲۳ مرداد
 */
fun overviewJalaliDate(
    date: LocalDate
): String {

    val result = gregorianToJalaliOverview(
        date.year,
        date.monthValue,
        date.dayOfMonth
    )

    return "\u200F${
        toPersianDigits(
            result[2].toString()
        )
    } ${
        overviewJalaliMonthName(
            result[1]
        )
    }"
}


/*
 * فقط روز شمسی
 */
fun overviewJalaliDay(
    date: LocalDate
): String {

    val result = gregorianToJalaliOverview(
        date.year,
        date.monthValue,
        date.dayOfMonth
    )

    return "\u200F${
        toPersianDigits(
            result[2].toString()
        )
    }"
}