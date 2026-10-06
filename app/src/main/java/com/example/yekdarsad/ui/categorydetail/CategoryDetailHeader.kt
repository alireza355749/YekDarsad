package com.example.yekdarsad.ui.categorydetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CategoryDetailHeader(
    categoryName: String,
    categoryColor: Color
) {

    val name = categoryName.trim()

    val icon = when {
        name == "ورزش" ->
            Icons.Default.DirectionsRun

        name == "تفریح" ->
            Icons.Default.Gamepad

        name.contains("کتاب") ||
                name.contains("پادکست") ->
            Icons.Default.Book

        name == "شغل" ->
            Icons.Default.BusinessCenter

        name.contains("زبان") ->
            Icons.Default.Language

        name.contains("مهارت") ->
            Icons.Default.Psychology

        name == "معنویت" ->
            Icons.Default.SelfImprovement

        else ->
            Icons.Default.Category
    }

    val subtitle = when {
        name == "ورزش" ->
            "حرکت کن و انرژی بگیر"

        name == "تفریح" ->
            "برای استراحت و حال خوب"

        name.contains("کتاب") ||
                name.contains("پادکست") ->
            "یاد بگیر و کشف کن"

        name == "شغل" ->
            "کار و فعالیت‌های روزانه"

        name.contains("زبان") ->
            "هر روز کمی بهتر شو"

        name.contains("مهارت") ->
            "مهارت‌هایت را بساز"

        name == "معنویت" ->
            "آرامش و تمرکز"

        else ->
            "فعالیت‌های شخصی تو"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = categoryColor.copy(alpha = 0.08f),
                shape = RoundedCornerShape(22.dp)
            )
            .padding(
                horizontal = 18.dp,
                vertical = 16.dp
            )
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        color = categoryColor.copy(alpha = 0.16f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {

                androidx.compose.material3.Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = categoryColor,
                    modifier = Modifier.size(29.dp)
                )
            }
        }
    }
}