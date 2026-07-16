package com.example.yekdarsad

sealed class Screen(
    val route: String,
    val title: String
) {

    object Home : Screen("home", "خانه")

    object Today : Screen("today", "امروز")

    object Statistics : Screen("statistics", "آمار")

    object Activities : Screen("activities", "فعالیت‌ها")

    object CategoryDetail : Screen(
        "category_detail",
        "جزئیات فعالیت"
    )

}