package com.example.yekdarsad.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.yekdarsad.getPersianDate
import com.example.yekdarsad.getPersianMonthName
import com.example.yekdarsad.persianToGregorian
import java.time.LocalDate
import java.time.ZoneId


@Composable
fun PersianCalendarDialog(

    selectedDate: LocalDate,

    onDateSelected: (LocalDate) -> Unit,

    onDismiss: () -> Unit

) {


    val today = LocalDate.now(
        ZoneId.of("Asia/Tehran")
    )


    val selectedPersian =
        getPersianDate(selectedDate)
            .split("/")



    var year by remember {

        mutableStateOf(
            selectedPersian[2].toInt()
        )

    }



    var month by remember {

        mutableStateOf(
            selectedPersian[1].toInt()
        )

    }



    val daysCount =
        when {

            month <= 6 -> 31

            month <= 11 -> 30

            else -> 30

        }




    AlertDialog(

        onDismissRequest = {
            onDismiss()
        },


        title = {


            Row(

                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically

            ) {


                IconButton(

                    onClick = {

                        month--

                        if (month == 0) {

                            month = 12
                            year--

                        }

                    }

                ) {


                    Icon(
                        Icons.Default.ArrowBack,
                        null
                    )

                }



                Text(
                    "${getPersianMonthName(month)} ${toPersianDigits(year.toString())}"
                )




                IconButton(

                    onClick = {

                        month++

                        if (month == 13) {

                            month = 1
                            year++

                        }

                    }

                ) {


                    Icon(
                        Icons.Default.ArrowForward,
                        null
                    )

                }


            }


        },



        text = {


            Column {


                Row(

                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.SpaceAround

                ) {


                    listOf(
                        "ش",
                        "ی",
                        "د",
                        "س",
                        "چ",
                        "پ",
                        "ج"
                    )
                        .forEach {

                            Text(it)

                        }


                }



                Spacer(
                    Modifier.height(12.dp)
                )



                val firstDay =
                    persianToGregorian(
                        year,
                        month,
                        1
                    )



                val offset =
                    when(firstDay.dayOfWeek.value) {

                        1 -> 2 // دوشنبه
                        2 -> 3 // سه شنبه
                        3 -> 4 // چهارشنبه
                        4 -> 5 // پنجشنبه
                        5 -> 6 // جمعه
                        6 -> 0 // شنبه
                        7 -> 1 // یکشنبه

                        else -> 0

                    }



                var day = 1



                val totalCells =
                    daysCount + offset



                repeat(
                    (totalCells + 6) / 7
                ) {


                    Row(

                        modifier =
                            Modifier.fillMaxWidth(),

                        horizontalArrangement =
                            Arrangement.SpaceAround

                    ) {



                        repeat(7) { index ->



                            val cellIndex =
                                it * 7 + index



                            if (
                                cellIndex >= offset &&
                                day <= daysCount
                            ) {


                                val currentDay =
                                    day



                                val currentDate =
                                    persianToGregorian(
                                        year,
                                        month,
                                        currentDay
                                    )



                                val isSelected =
                                    currentDate == selectedDate




                                Box(

                                    modifier =
                                        Modifier
                                            .size(36.dp)
                                            .background(

                                                color =
                                                    if(isSelected)
                                                        Color(0xFF496A42)
                                                    else
                                                        Color.Transparent,

                                                shape =
                                                    CircleShape

                                            )
                                            .clickable {

                                                onDateSelected(
                                                    currentDate
                                                )

                                            },

                                    contentAlignment =
                                        Alignment.Center

                                ) {



                                    Text(

                                        text =
                                            toPersianDigits(
                                                currentDay.toString()
                                            ),

                                        color =
                                            if(isSelected)
                                                Color.White
                                            else
                                                MaterialTheme.colorScheme.onSurface

                                    )


                                }



                                day++



                            } else {


                                Spacer(
                                    Modifier.size(36.dp)
                                )


                            }


                        }


                    }


                }




                Spacer(
                    Modifier.height(12.dp)
                )




                TextButton(

                    onClick = {

                        onDateSelected(today)
                        onDismiss()

                    },

                    modifier =
                        Modifier.align(
                            Alignment.CenterHorizontally
                        )

                ) {


                    Text(
                        "برو به امروز",
                        color = Color(0xFF496A42)
                    )


                }


            }


        },



        confirmButton = {


            TextButton(

                onClick = {
                    onDismiss()
                }

            ) {


                Text("بستن")


            }


        }


    )


}


// =========================================================
// فقط برای نمایش اعداد فارسی
// منطق تاریخ از این تابع استفاده نمی‌کند.
// =========================================================

private fun toPersianDigits(
    value: String
): String {

    return value
        .replace("0", "۰")
        .replace("1", "۱")
        .replace("2", "۲")
        .replace("3", "۳")
        .replace("4", "۴")
        .replace("5", "۵")
        .replace("6", "۶")
        .replace("7", "۷")
        .replace("8", "۸")
        .replace("9", "۹")
}