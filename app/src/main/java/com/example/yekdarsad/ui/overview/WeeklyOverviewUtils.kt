package com.example.yekdarsad.ui.overview

// ============================================================
// فرمت زمان
// ============================================================

fun formatMinutesWeekly(
    minutes: Int
): String {

    if (minutes <= 0) {
        return "۰ دقیقه"
    }

    val hours =
        minutes / 60

    val remaining =
        minutes % 60

    return when {

        hours > 0 && remaining > 0 ->
            "${
                toPersianDigitsWeekly(
                    hours.toString()
                )
            }س ${
                toPersianDigitsWeekly(
                    remaining.toString()
                )
            }د"

        hours > 0 ->
            "${
                toPersianDigitsWeekly(
                    hours.toString()
                )
            } ساعت"

        else ->
            "${
                toPersianDigitsWeekly(
                    remaining.toString()
                )
            } دقیقه"
    }
}

// ============================================================
// عنوان هفته
// ============================================================

fun weeklyTitle(
    offset: Int
): String {

    return when {

        offset == 0 ->
            "برنامه این هفته"

        offset == -1 ->
            "برنامه هفته قبل"

        offset == 1 ->
            "برنامه هفته بعد"

        offset < 0 ->
            "برنامه ${
                toPersianDigitsWeekly(
                    (-offset).toString()
                )
            } هفته قبل"

        else ->
            "برنامه ${
                toPersianDigitsWeekly(
                    offset.toString()
                )
            } هفته بعد"
    }
}

// ============================================================
// تاریخ شمسی کامل
// ============================================================

fun jalaliDateWeekly(
    date: java.time.LocalDate
): String {

    val result =
        gregorianToJalaliWeekly(
            date.year,
            date.monthValue,
            date.dayOfMonth
        )

    val monthNames =
        listOf(
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

    return "${
        toPersianDigitsWeekly(
            result[2].toString()
        )
    } ${monthNames[result[1] - 1]}"
}

// ============================================================
// فقط روز شمسی
// ============================================================

fun jalaliDayWeekly(
    date: java.time.LocalDate
): String {

    val result =
        gregorianToJalaliWeekly(
            date.year,
            date.monthValue,
            date.dayOfMonth
        )

    return toPersianDigitsWeekly(
        result[2].toString()
    )
}

// ============================================================
// تبدیل میلادی به شمسی
// ============================================================

fun gregorianToJalaliWeekly(
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

    val gyTemp =
        gy - 1600

    val gmTemp =
        gm - 1

    val gdTemp =
        gd - 1

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

    var jDayNo =
        gDayNo - 79

    val jNp =
        jDayNo / 12053

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

// ============================================================
// اعداد فارسی
// ============================================================

fun toPersianDigitsWeekly(
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