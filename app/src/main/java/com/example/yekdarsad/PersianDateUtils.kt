package com.example.yekdarsad

import java.time.LocalDate


fun getPersianDate(date: LocalDate): String {


    val gYear = date.year
    val gMonth = date.monthValue
    val gDay = date.dayOfMonth


    val gDaysInMonth = intArrayOf(
        0,
        31,28,31,30,31,30,
        31,31,30,31,30,31
    )


    val jDaysInMonth = intArrayOf(
        0,
        31,31,31,31,31,31,
        30,30,30,30,30,29
    )


    var gy = gYear - 1600
    var gm = gMonth - 1
    var gd = gDay - 1


    var gDayNo =
        365 * gy +
                (gy + 3) / 4 -
                (gy + 99) / 100 +
                (gy + 399) / 400


    for (i in 0 until gm) {

        gDayNo += gDaysInMonth[i + 1]

    }


    if (
        gm > 1 &&
        ((gYear % 4 == 0 && gYear % 100 != 0) ||
                gYear % 400 == 0)
    ) {

        gDayNo++

    }


    gDayNo += gd


    var jDayNo = gDayNo - 79


    val jNp = jDayNo / 12053

    jDayNo %= 12053


    var jy = 979 + 33 * jNp + 4 * (jDayNo / 1461)

    jDayNo %= 1461


    if (jDayNo >= 366) {

        jy += (jDayNo - 1) / 365

        jDayNo = (jDayNo - 1) % 365

    }


    var jm = 1


    while (
        jm <= 11 &&
        jDayNo >= jDaysInMonth[jm]
    ) {

        jDayNo -= jDaysInMonth[jm]

        jm++

    }


    val jd = jDayNo + 1


    return "$jd/$jm/$jy"

}



fun getPersianMonthName(month: Int): String {


    return when(month) {

        1 -> "فروردین"
        2 -> "اردیبهشت"
        3 -> "خرداد"
        4 -> "تیر"
        5 -> "مرداد"
        6 -> "شهریور"
        7 -> "مهر"
        8 -> "آبان"
        9 -> "آذر"
        10 -> "دی"
        11 -> "بهمن"
        12 -> "اسفند"

        else -> ""

    }

}

fun persianToGregorian(
    jy: Int,
    jm: Int,
    jd: Int
): LocalDate {


    val gy = jy - 979
    val gm = jm - 1
    val gd = jd - 1


    var jDayNo =
        365 * gy +
                (gy / 33) * 8 +
                ((gy % 33) + 3) / 4


    for (i in 0 until gm) {

        jDayNo += if (i < 6) 31 else 30

    }


    jDayNo += gd



    var gDayNo = jDayNo + 79



    var gy2 = 1600 + 400 * (gDayNo / 146097)

    gDayNo %= 146097



    var leap = true


    if (gDayNo >= 36525) {

        gDayNo--

        gy2 += 100 * (gDayNo / 36524)

        gDayNo %= 36524


        if (gDayNo >= 365) {

            gDayNo++

        } else {

            leap = false

        }

    }



    gy2 += 4 * (gDayNo / 1461)

    gDayNo %= 1461



    if (gDayNo >= 366) {

        leap = false

        gDayNo--

        gy2 += gDayNo / 365

        gDayNo %= 365

    }



    val monthDays = intArrayOf(
        0,
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



    var gm2 = 1


    while (

        gm2 <= 12 &&
        gDayNo >= monthDays[gm2]

    ) {

        gDayNo -= monthDays[gm2]

        gm2++

    }



    val gd2 = gDayNo + 1



    return LocalDate.of(
        gy2,
        gm2,
        gd2
    )

}