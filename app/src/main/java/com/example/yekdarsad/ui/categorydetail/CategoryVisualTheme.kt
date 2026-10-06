package com.example.yekdarsad.ui.categorydetail

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class CategoryVisualTheme(
    val color: Color,
    val softColor: Color,
    val icon: ImageVector,
    val title: String,
    val description: String
)

fun getCategoryVisualTheme(
    categoryName: String
): CategoryVisualTheme {

    return when (categoryName.trim()) {

        // =====================================================
        // ورزش
        // =====================================================

        "ورزش" -> CategoryVisualTheme(
            color = Color(0xFF20BFA9),
            softColor = Color(0xFFE3F8F4),
            icon = Icons.Default.DirectionsRun,
            title = "فعال و پرانرژی",
            description = "برای حرکت، سلامتی و ساختن بدن قوی‌تر"
        )

        // =====================================================
        // معنویت
        // =====================================================

        "معنویت" -> CategoryVisualTheme(
            color = Color(0xFF4CAF50),
            softColor = Color(0xFFE8F5E9),
            icon = Icons.Default.Park,
            title = "آرامش و رشد درونی",
            description = "زمانی برای آرامش، تمرکز و رشد شخصی"
        )

        // =====================================================
        // زبان انگلیسی
        // =====================================================

        "زبان انگلیسی" -> CategoryVisualTheme(
            color = Color(0xFFE53935),
            softColor = Color(0xFFFFEBEE),
            icon = Icons.Default.Language,
            title = "English",
            description = "هر روز یک قدم برای بهتر شدن زبان"
        )

        // =====================================================
        // کتاب و پادکست
        // =====================================================

        "کتاب و پادکست" -> CategoryVisualTheme(
            color = Color(0xFFFF9800),
            softColor = Color(0xFFFFF3E0),
            icon = Icons.Default.AutoStories,
            title = "یادگیری و کشف",
            description = "ایده‌های جدید، داستان‌های تازه و یادگیری"
        )

        // =====================================================
        // مهارت های شغلی
        // =====================================================

        "مهارت های شغلی",
        "مهارت‌های شغلی" -> CategoryVisualTheme(
            color = Color(0xFFF48FB1),
            softColor = Color(0xFFFCE4EC),
            icon = Icons.Default.BusinessCenter,
            title = "ساختن آینده",
            description = "مهارت‌هایی که تو را برای آینده آماده می‌کنند"
        )

        // =====================================================
        // شغل
        // =====================================================

        "شغل" -> CategoryVisualTheme(
            color = Color(0xFF757575),
            softColor = Color(0xFFF1F1F1),
            icon = Icons.Default.Work,
            title = "تمرکز و عملکرد",
            description = "زمانی برای کار و پیش بردن اهداف"
        )

        // =====================================================
        // تفریح
        // =====================================================

        "تفریح" -> CategoryVisualTheme(
            color = Color(0xFFFFC107),
            softColor = Color(0xFFFFF8E1),
            icon = Icons.Default.Casino,
            title = "وقت خوش",
            description = "استراحت، سرگرمی و لذت بردن از زندگی"
        )

        // =====================================================
        // متفرقه
        // =====================================================

        else -> CategoryVisualTheme(
            color = Color(0xFF42A5F5),
            softColor = Color(0xFFE3F2FD),
            icon = Icons.Default.Headphones,
            title = "سایر فعالیت‌ها",
            description = "فعالیت‌هایی که جای خودشان را در برنامه دارند"
        )
    }
}