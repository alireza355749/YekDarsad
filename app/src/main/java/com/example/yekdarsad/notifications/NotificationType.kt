package com.example.yekdarsad.notifications

enum class NotificationType(
    val id: Int,
    val title: String,
    val message: String
) {

    BREAKFAST(
        id = 1001,
        title = "🍳 صبحانه",
        message = "صبحانه چی خوردی؟"
    ),

    LUNCH(
        id = 1002,
        title = "🍛 ناهار",
        message = "ناهار چی خوردی؟"
    ),

    SLEEP(
        id = 1003,
        title = "😴 خواب",
        message = "امشب ساعت خوابت رو ثبت کردی؟"
    )
}