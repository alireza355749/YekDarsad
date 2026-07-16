package com.example.yekdarsad.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun DayEnergyRing(

    totalMinutes: Int

) {

    val progress = (totalMinutes / 600f)
        .coerceIn(0f, 1f)

    val animated = animateFloatAsState(
        targetValue = progress,
        label = ""
    )

    Box(
        modifier = Modifier.size(140.dp),
        contentAlignment = Alignment.Center
    ) {

        Canvas(
            modifier = Modifier.size(140.dp)
        ) {

            drawArc(
                color = Color(0xFFE8E8E8),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(
                    width = 18f,
                    cap = StrokeCap.Round
                )
            )

            drawArc(
                color = Color(0xFF6BAA75),
                startAngle = -90f,
                sweepAngle = animated.value * 360,
                useCenter = false,
                style = Stroke(
                    width = 18f,
                    cap = StrokeCap.Round
                )
            )

        }

        Text(
            text = "${totalMinutes / 60}h\n${totalMinutes % 60}m",
            style = MaterialTheme.typography.titleMedium
        )

    }

}