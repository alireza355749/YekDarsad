package com.example.yekdarsad.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.dp
import kotlin.math.atan2
import kotlin.math.pow

@Composable
fun AnimatedProgressBar(
    progress: Float
) {

    val p by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 100f),
        animationSpec = tween(
            durationMillis = 900,
            easing = FastOutSlowInEasing
        ),
        label = ""
    )

    Column {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = "پیشرفت امروز",
                style = MaterialTheme.typography.labelLarge
            )

            Text(
                text = "${p.toInt()}%",
                style = MaterialTheme.typography.labelLarge,
                color = Color(0xFF496A42)
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(108.dp)
        ) {

            val startX = 20f
            val endX = size.width - 20f

            val bottomY = size.height - 10f
            val topY = 8f

            val curveWidth = endX - startX
            val curveHeight = bottomY - topY

            // شبیه x² ولی ملایم‌تر
            val curvePower = 1.7f

            fun curveValue(t: Float): Float {

                return t
                    .coerceIn(0f, 1f)
                    .pow(curvePower)
            }

            fun curveDerivative(t: Float): Float {

                val safeT =
                    t.coerceIn(0.001f, 1f)

                return curvePower *
                        safeT.pow(curvePower - 1f)
            }

            // مسیر کامل
            val fullPath = Path().apply {

                val steps = 180

                for (i in 0..steps) {

                    val t =
                        i.toFloat() / steps.toFloat()

                    val x =
                        startX + curveWidth * t

                    val y =
                        bottomY -
                                curveHeight *
                                curveValue(t)

                    if (i == 0) {
                        moveTo(x, y)
                    } else {
                        lineTo(x, y)
                    }
                }
            }

            // مسیر خاکستری
            drawPath(
                path = fullPath,
                color = Color(0xFFDCE8D7),
                style = Stroke(
                    width = 6.5f,
                    cap = StrokeCap.Round
                )
            )

            val t =
                (p / 100f)
                    .coerceIn(0f, 1f)

            val currentX =
                startX + curveWidth * t

            val currentY =
                bottomY -
                        curveHeight *
                        curveValue(t)

            // مسیر سبز
            val progressPath = Path().apply {

                val steps =
                    maxOf(
                        1,
                        (t * 180f).toInt()
                    )

                for (i in 0..steps) {

                    val localT =
                        t *
                                (i.toFloat() /
                                        steps.toFloat())

                    val x =
                        startX +
                                curveWidth * localT

                    val y =
                        bottomY -
                                curveHeight *
                                curveValue(localT)

                    if (i == 0) {
                        moveTo(x, y)
                    } else {
                        lineTo(x, y)
                    }
                }

                lineTo(
                    currentX,
                    currentY
                )
            }

            drawPath(
                path = progressPath,
                color = Color(0xFF496A42),
                style = Stroke(
                    width = 6.5f,
                    cap = StrokeCap.Round
                )
            )

            // شیب دقیق منحنی
            val slope =
                -curveHeight *
                        curveDerivative(t) /
                        curveWidth

            val angle =
                atan2(
                    slope,
                    1f
                ) *
                        180f /
                        Math.PI.toFloat()

            // فلش متحرک
            withTransform({

                translate(
                    currentX,
                    currentY
                )

                rotate(
                    degrees = angle,
                    pivot = Offset.Zero
                )

            }) {

                val arrow = Path().apply {

                    moveTo(
                        18f,
                        0f
                    )

                    lineTo(
                        -10f,
                        -13f
                    )

                    lineTo(
                        -10f,
                        13f
                    )

                    close()
                }

                drawPath(
                    path = arrow,
                    color = Color(0xFF496A42)
                )
            }
        }
    }
}