package com.example.yekdarsad.ui.todaytracker

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun WaterTracker(
    glasses: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {

    var pouring by remember { mutableStateOf(false) }

    LaunchedEffect(glasses) {
        pouring = true
        delay(700)
        pouring = false
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(
            onClick = onRemove,
            enabled = glasses > 0,
            modifier = Modifier.size(28.dp)
        ) {
            Text(
                "−",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF1976D2)
            )
        }

        Spacer(Modifier.width(4.dp))

        WaterGlass(
            glasses = glasses,
            pouring = pouring
        )

        Spacer(Modifier.width(4.dp))

        IconButton(
            onClick = onAdd,
            enabled = glasses < 6,
            modifier = Modifier.size(28.dp)
        ) {
            Text(
                "+",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF1976D2)
            )
        }
    }
}

@Composable
private fun WaterGlass(
    glasses: Int,
    pouring: Boolean
) {

    val targetWaterProgress = when (glasses) {
        0 -> 0f
        1 -> 0.35f
        2 -> 0.50f
        3 -> 0.63f
        4 -> 0.75f
        5 -> 0.88f
        6 -> 1f
        else -> 0f
    }

    val waterProgress by animateFloatAsState(
        targetValue = targetWaterProgress,
        animationSpec = tween(
            durationMillis = 900,
            easing = FastOutSlowInEasing
        ),
        label = ""
    )

    // انیمیشن شدیدتر برای موج آب
    val splash by animateFloatAsState(
        targetValue = if (pouring) 1f else 0f,
        animationSpec = tween(
            durationMillis = 700,
            easing = FastOutSlowInEasing
        ),
        label = ""
    )

    Canvas(
        modifier = Modifier
            .width(32.dp)
            .height(40.dp)
    ) {

        val glassColor = Color(0xFF496A42)
        val waterColor = Color(0xFF64B5F6)

        val glassFill = Path().apply {
            moveTo(size.width * 0.12f, 0f)
            lineTo(size.width * 0.88f, 0f)
            lineTo(size.width * 0.72f, size.height)
            lineTo(size.width * 0.28f, size.height)
            close()
        }

        clipPath(glassFill) {

            val waterTop = size.height * (1f - waterProgress)

            // دامنه موج بیشتر شده
            val waveHeight = 8f * splash

            val waterPath = Path().apply {

                moveTo(0f, size.height)

                lineTo(size.width, size.height)

                lineTo(size.width, waterTop)

                // موج اول
                cubicTo(
                    size.width * 0.82f,
                    waterTop - waveHeight,
                    size.width * 0.60f,
                    waterTop + waveHeight,
                    size.width * 0.40f,
                    waterTop
                )

                // موج دوم
                cubicTo(
                    size.width * 0.22f,
                    waterTop - waveHeight,
                    size.width * 0.10f,
                    waterTop + waveHeight * 0.7f,
                    0f,
                    waterTop
                )

                close()
            }

            drawPath(
                path = waterPath,
                color = waterColor
            )

            // جریان آب هنگام ریختن
            if (pouring) {

                drawRoundRect(
                    color = waterColor,
                    topLeft = Offset(
                        size.width / 2 - 2.5f,
                        -12f
                    ),
                    size = Size(
                        5f,
                        18f
                    ),
                    cornerRadius = CornerRadius(
                        4f,
                        4f
                    )
                )
            }
        }

        // ضلع چپ لیوان
        drawLine(
            color = glassColor,
            start = Offset(
                size.width * 0.12f,
                0f
            ),
            end = Offset(
                size.width * 0.28f,
                size.height
            ),
            strokeWidth = 5f
        )

        // ضلع راست لیوان
        drawLine(
            color = glassColor,
            start = Offset(
                size.width * 0.88f,
                0f
            ),
            end = Offset(
                size.width * 0.72f,
                size.height
            ),
            strokeWidth = 5f
        )

        // کف لیوان
        drawLine(
            color = glassColor,
            start = Offset(
                size.width * 0.28f,
                size.height
            ),
            end = Offset(
                size.width * 0.72f,
                size.height
            ),
            strokeWidth = 5f
        )
    }
}