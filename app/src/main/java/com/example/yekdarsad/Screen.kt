package com.example.yekdarsad

sealed class Screen(
    val route: String,
    val title: String
) {

    object Home : Screen(
        "home",
        "خانه"
    )

    object Today : Screen(
        "today",
        "امروز"
    )

    object Overview : Screen(
        "overview",
        "در یک نگاه"
    )

    object Statistics : Screen(
        "statistics",
        "آمار"
    )

    object Activities : Screen(
        "activities",
        "فعالیت‌ها"
    )

    object CategoryDetail : Screen(
        "category_detail",
        "جزئیات فعالیت"
    )

    object RoutinePlanning : Screen(
        "routine_planning",
        "برنامه‌ریزی روتین"
    )

    object PhoneUsage : Screen(
        "phone_usage",
        "مصرف گوشی"
    )

    object Nutrition : Screen(
        "nutrition",
        "تغذیه"
    )

    object Sleep : Screen(
        "sleep",
        "خواب"
    )

    object Expense : Screen(
        "expense/{date}",
        "مخارج"
    )

    object Settings : Screen(
        "settings",
        "تنظیمات"
    )
}