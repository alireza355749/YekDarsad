package com.example.yekdarsad.ui.overview

import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import java.time.LocalDate

fun Modifier.dayLongPressHandler(
    date: LocalDate,
    onDateTapped: () -> Unit,
    onDateLongPressed: (LocalDate) -> Unit
): Modifier {

    return pointerInput(date) {

        Log.d(
            "DAY_LONG_PRESS",
            "Handler attached: $date"
        )

        detectTapGestures(

            onTap = {

                Log.d(
                    "DAY_LONG_PRESS",
                    "TAP: $date"
                )

                onDateTapped()
            },

            onLongPress = {

                Log.d(
                    "DAY_LONG_PRESS",
                    "LONG PRESS: $date"
                )

                onDateLongPressed(date)
            }
        )
    }
}