package com.example.yekdarsad.ui.todaytracker

enum class TrackerType(

    val title: String,

    val unit: String

) {

    CALORIES(
        "کالری",
        "kcal"
    ),

    EXPENSE(
        "مخارج",
        "تومان"
    ),

    WATER(
        "آب",
        "لیوان"
    ),

    WEIGHT(
        "وزن",
        "kg"
    )

}