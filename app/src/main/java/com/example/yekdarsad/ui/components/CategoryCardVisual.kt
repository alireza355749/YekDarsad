package com.example.yekdarsad.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.yekdarsad.ui.theme.TextPrimary

private data class CategoryStyle(
    val background: Color,
    val iconBackground: Color,
    val accent: Color
)

private fun categoryStyle(title: String): CategoryStyle {

    return when {

        title == "ورزش" ->
            CategoryStyle(
                background = Color(0xFFD7F4F0),
                iconBackground = Color(0xFF9DDED7),
                accent = Color(0xFF008F83)
            )

        title == "معنویت" ->
            CategoryStyle(
                background = Color(0xFFDFF1DF),
                iconBackground = Color(0xFFAED5B0),
                accent = Color(0xFF2E823B)
            )

        title == "زبان انگلیسی" ->
            CategoryStyle(
                background = Color(0xFFF9D7D7),
                iconBackground = Color(0xFFEEA4A4),
                accent = Color(0xFFC92F32)
            )

        title == "کتاب و پادکست" ->
            CategoryStyle(
                background = Color(0xFFFFE2C2),
                iconBackground = Color(0xFFFFB968),
                accent = Color(0xFFE06B00)
            )

        title == "مهارت‌های شغلی" ->
            CategoryStyle(
                background = Color(0xFFF6D8E5),
                iconBackground = Color(0xFFE9A8C3),
                accent = Color(0xFFC34F7D)
            )

        title == "شغل" ->
            CategoryStyle(
                background = Color(0xFFD9DDE2),
                iconBackground = Color(0xFFADB4BC),
                accent = Color(0xFF414A54)
            )

        title == "تفریح" ->
            CategoryStyle(
                background = Color(0xFFFFF0B5),
                iconBackground = Color(0xFFFFD85C),
                accent = Color(0xFFC38A00)
            )

        title == "متفرقه" ->
            CategoryStyle(
                background = Color(0xFFD7E8FA),
                iconBackground = Color(0xFFA3C9F0),
                accent = Color(0xFF2E70B7)
            )

        title.contains("دانشگاه") ->
            CategoryStyle(
                background = Color(0xFFE6D9F7),
                iconBackground = Color(0xFFC7AAEA),
                accent = Color(0xFF7040AD)
            )

        title.contains("هنر") ->
            CategoryStyle(
                background = Color(0xFFF6D6E7),
                iconBackground = Color(0xFFE8A6C6),
                accent = Color(0xFFB83270)
            )

        title.contains("سفر") ->
            CategoryStyle(
                background = Color(0xFFD5EDF1),
                iconBackground = Color(0xFFA5D5DE),
                accent = Color(0xFF277C8A)
            )

        title.contains("مالی") ->
            CategoryStyle(
                background = Color(0xFFF1E4BD),
                iconBackground = Color(0xFFD9C47D),
                accent = Color(0xFF80651C)
            )

        else ->
            CategoryStyle(
                background = Color(0xFFE4E8ED),
                iconBackground = Color(0xFFC6CDD5),
                accent = Color(0xFF596572)
            )
    }
}

@Composable
private fun categoryIcon(title: String) = when {

    title == "ورزش" ->
        Icons.Default.FitnessCenter

    title == "معنویت" ->
        Icons.Default.SelfImprovement

    title == "زبان انگلیسی" ->
        Icons.Default.Language

    title == "کتاب و پادکست" ->
        Icons.Default.AutoStories

    title == "مهارت‌های شغلی" ->
        Icons.Default.WorkspacePremium

    title == "شغل" ->
        Icons.Default.BusinessCenter

    title == "تفریح" ->
        Icons.Default.Explore

    else ->
        Icons.Default.MoreHoriz
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoryVisualCard(
    title: String,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {

    val style = categoryStyle(title)

    Card(
        modifier = modifier
            .height(118.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = style.background
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 7.dp,
                    vertical = 10.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(
                        RoundedCornerShape(13.dp)
                    )
                    .background(
                        style.iconBackground
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = categoryIcon(title),
                    contentDescription = title,
                    tint = style.accent,
                    modifier = Modifier.size(23.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = title,
                color = TextPrimary,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}