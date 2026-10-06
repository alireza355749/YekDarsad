package com.example.yekdarsad

import androidx.compose.ui.graphics.Color

fun getCategoryColor(categoryName: String): Color {
    return when (categoryName) {
        "زبان" -> Color(0xFF4A90E2)
        "مهارت‌های شغلی" -> Color(0xFF7E57C2)
        "مطالعه" -> Color(0xFFFF9800)
        "ورزش" -> Color(0xFF43A047)
        "نماز" -> Color(0xFF009688)
        "شغل" -> Color(0xFF546E7A)
        "متفرقه" -> Color(0xFF78909C)
        else -> Color(0xFF78909C)
    }
}