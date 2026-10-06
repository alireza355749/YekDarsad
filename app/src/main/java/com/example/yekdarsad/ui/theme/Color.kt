package com.example.yekdarsad.ui.theme

import androidx.compose.ui.graphics.Color

// =====================================================
// App Colors
// =====================================================

// =========================
// Light Theme
// =========================

val AppBackground = Color(0xFFFFFDD0)

val CardBackground = Color(0xFFFFFBEA)

val PrimaryGreen = Color(0xFF496A42)

val PrimaryGreenLight = Color(0xFFDDE8C8)

val PrimaryGreenDark = Color(0xFF526B4A)

val TextPrimary = Color(0xFF333333)

val TextSecondary = Color(0xFF7F8878)

val SelectedIcon = PrimaryGreen

val UnselectedIcon = TextSecondary

val ProgressColor = Color(0xFF7A9A6D)


// =====================================================
// Category Colors
// =====================================================

data class CategoryColorSet(
    val background: Color,
    val iconBackground: Color,
    val accent: Color
)

fun categoryColors(
    categoryName: String
): CategoryColorSet {

    return when {

        categoryName == "ورزش" ->
            CategoryColorSet(
                background = Color(0xFFD7F4F0),
                iconBackground = Color(0xFF9DDED7),
                accent = Color(0xFF008F83)
            )

        categoryName == "معنویت" ->
            CategoryColorSet(
                background = Color(0xFFDFF1DF),
                iconBackground = Color(0xFFAED5B0),
                accent = Color(0xFF2E823B)
            )

        categoryName == "زبان انگلیسی" ->
            CategoryColorSet(
                background = Color(0xFFF9D7D7),
                iconBackground = Color(0xFFEEA4A4),
                accent = Color(0xFFC92F32)
            )

        categoryName == "کتاب و پادکست" ->
            CategoryColorSet(
                background = Color(0xFFFFE2C2),
                iconBackground = Color(0xFFFFB968),
                accent = Color(0xFFE06B00)
            )

        categoryName == "مهارت‌های شغلی" ->
            CategoryColorSet(
                background = Color(0xFFF6D8E5),
                iconBackground = Color(0xFFE9A8C3),
                accent = Color(0xFFC34F7D)
            )

        categoryName == "شغل" ->
            CategoryColorSet(
                background = Color(0xFFD9DDE2),
                iconBackground = Color(0xFFADB4BC),
                accent = Color(0xFF414A54)
            )

        categoryName == "تفریح" ->
            CategoryColorSet(
                background = Color(0xFFFFF0B5),
                iconBackground = Color(0xFFFFD85C),
                accent = Color(0xFFC38A00)
            )

        categoryName == "متفرقه" ->
            CategoryColorSet(
                background = Color(0xFFD7E8FA),
                iconBackground = Color(0xFFA3C9F0),
                accent = Color(0xFF2E70B7)
            )

        categoryName.contains("دانشگاه") ->
            CategoryColorSet(
                background = Color(0xFFE6D9F7),
                iconBackground = Color(0xFFC7AAEA),
                accent = Color(0xFF7040AD)
            )

        categoryName.contains("هنر") ->
            CategoryColorSet(
                background = Color(0xFFF6D6E7),
                iconBackground = Color(0xFFE8A6C6),
                accent = Color(0xFFB83270)
            )

        categoryName.contains("سفر") ->
            CategoryColorSet(
                background = Color(0xFFD5EDF1),
                iconBackground = Color(0xFFA5D5DE),
                accent = Color(0xFF277C8A)
            )

        categoryName.contains("مالی") ->
            CategoryColorSet(
                background = Color(0xFFF1E4BD),
                iconBackground = Color(0xFFD9C47D),
                accent = Color(0xFF80651C)
            )

        else ->
            CategoryColorSet(
                background = Color(0xFFE4E8ED),
                iconBackground = Color(0xFFC6CDD5),
                accent = Color(0xFF596572)
            )
    }
}


// =====================================================
// Dark Theme
// =====================================================

val DarkAppBackground = Color(0xFF10130F)

val DarkCardBackground = Color(0xFF191D18)

val DarkCardSecondary = Color(0xFF222820)

val DarkPrimaryGreen = Color(0xFF9BB88F)

val DarkPrimaryGreenLight = Color(0xFF30412D)

val DarkTextPrimary = Color(0xFFF0F2EC)

val DarkTextSecondary = Color(0xFFA9B1A5)

val DarkProgressColor = Color(0xFF8EAA82)