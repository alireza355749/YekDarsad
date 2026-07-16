package com.example.yekdarsad

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun CircularProgress(
    progress: Int
) {


    val animatedProgress by animateFloatAsState(

        targetValue = progress / 100f,

        animationSpec = tween(
            durationMillis = 1500,
            easing = FastOutSlowInEasing
        ),

        label = ""

    )



    val infiniteTransition = rememberInfiniteTransition(
        label = ""
    )



    val scale by infiniteTransition.animateFloat(

        initialValue = 1f,

        targetValue = 1.03f,

        animationSpec = infiniteRepeatable(

            animation = tween(
                1800,
                easing = FastOutSlowInEasing
            ),

            repeatMode = RepeatMode.Reverse

        ),

        label = ""

    )



    val shimmerAngle by infiniteTransition.animateFloat(

        initialValue = 0f,

        targetValue = 360f,

        animationSpec = infiniteRepeatable(

            animation = tween(
                durationMillis = 2500,
                easing = LinearEasing
            )

        ),

        label = ""

    )





    Box(

        modifier = Modifier
            .size(190.dp)
            .scale(scale),

        contentAlignment = Alignment.Center

    ) {



        Canvas(

            modifier = Modifier.fillMaxSize()

        ) {



            val strokeWidth = 12.dp.toPx()



            // پس زمینه حلقه

            drawArc(

                color = Color(0xFFE5E3C8),

                startAngle = -90f,

                sweepAngle = 360f,

                useCenter = false,

                style = androidx.compose.ui.graphics.drawscope.Stroke(

                    width = strokeWidth,

                    cap = StrokeCap.Round

                )

            )





            // مقدار پیشرفت

            drawArc(

                brush = Brush.sweepGradient(

                    colors = listOf(

                        Color(0xFF6F8F63),

                        Color(0xFFA8C29A),

                        Color(0xFF6F8F63)

                    )

                ),

                startAngle = -90f,

                sweepAngle = animatedProgress * 360,

                useCenter = false,

                style = androidx.compose.ui.graphics.drawscope.Stroke(

                    width = strokeWidth,

                    cap = StrokeCap.Round

                )

            )





            // نور متحرک

            rotate(

                degrees = shimmerAngle

            ) {


                drawArc(

                    color = Color.White.copy(
                        alpha = 0.20f
                    ),

                    startAngle = -25f,

                    sweepAngle = 35f,

                    useCenter = false,

                    style = androidx.compose.ui.graphics.drawscope.Stroke(

                        width = strokeWidth,

                        cap = StrokeCap.Round

                    )

                )


            }



        }





        Column(

            horizontalAlignment = Alignment.CenterHorizontally

        ) {



            Text(

                text = "$progress%",

                fontSize = 38.sp,

                fontWeight = FontWeight.Bold,

                color = Color(0xFF526B4A)

            )



            Text(

                text = "تکمیل شده",

                fontSize = 14.sp,

                color = Color.Gray

            )


        }


    }


}