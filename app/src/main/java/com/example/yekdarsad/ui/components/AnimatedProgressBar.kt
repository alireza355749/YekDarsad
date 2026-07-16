package com.example.yekdarsad.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedProgressBar(
    progress: Float
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = "پیشرفت امروز",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.width(75.dp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(14.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFE5E5E5))
        ) {

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress / 100f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Color(
                            80,
                            (130 + progress.toInt()).coerceAtMost(220),
                            90
                        )
                    )
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "${progress.toInt()}%",
            style = MaterialTheme.typography.labelMedium
        )
    }
}