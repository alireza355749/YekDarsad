package com.example.yekdarsad.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import kotlin.math.roundToInt


@Composable
fun FlyingTimeAnimation(

    startX: Float,
    startY: Float,

    endX: Float,
    endY: Float,

    minutes: Int,

    onFinished: () -> Unit

) {


    var progress by remember {
        mutableFloatStateOf(0f)
    }


    LaunchedEffect(Unit) {


        animate(

            initialValue = 0f,

            targetValue = 1f,

            animationSpec = tween(
                durationMillis = 800,
                easing = FastOutSlowInEasing
            )

        ) { value, _ ->

            progress = value

        }


        onFinished()

    }



    val x = startX + (endX - startX) * progress

    val y = startY + (endY - startY) * progress - 70



    Text(

        text = "+$minutes",

        color = Color.Black,

        fontSize = 14.sp,


        modifier = Modifier
            .offset {

                IntOffset(

                    (x - 10).roundToInt(),

                    (y - 10).roundToInt()

                )

            }

    )

}