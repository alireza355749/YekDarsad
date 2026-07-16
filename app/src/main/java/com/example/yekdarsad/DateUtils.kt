package com.example.yekdarsad

import java.time.LocalDate


fun getDisplayDate(date: LocalDate): String {


    val days = mapOf(

        1 to "دوشنبه",
        2 to "سه‌شنبه",
        3 to "چهارشنبه",
        4 to "پنجشنبه",
        5 to "جمعه",
        6 to "شنبه",
        7 to "یکشنبه"

    )


    val dayName = days[date.dayOfWeek.value]



    val persian = getPersianDate(date).split("/")


    val day = persian[0]
    val month = persian[1]
    val year = persian[2]



    val months = listOf(

        "",
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


    val monthName = months[month.toInt()]



    return "$dayName ${toPersianNumber(day)} $monthName ${toPersianNumber(year)}"

}



fun toPersianNumber(value: String): String {


    val numbers = mapOf(

        '0' to '۰',
        '1' to '۱',
        '2' to '۲',
        '3' to '۳',
        '4' to '۴',
        '5' to '۵',
        '6' to '۶',
        '7' to '۷',
        '8' to '۸',
        '9' to '۹'

    )


    return value.map {

        numbers[it] ?: it

    }.joinToString("")

}