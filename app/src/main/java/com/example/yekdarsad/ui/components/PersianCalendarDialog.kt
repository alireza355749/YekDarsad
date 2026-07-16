package com.example.yekdarsad.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.yekdarsad.getPersianDate
import com.example.yekdarsad.getPersianMonthName
import java.time.LocalDate
import com.example.yekdarsad.persianToGregorian


@Composable
fun PersianCalendarDialog(

    selectedDate: LocalDate,

    onDateSelected: (LocalDate) -> Unit,

    onDismiss: () -> Unit

) {


    val persianDate =
        getPersianDate(selectedDate).split("/")



    var year by remember {

        mutableStateOf(
            persianDate[2].toInt()
        )

    }



    var month by remember {

        mutableStateOf(
            persianDate[1].toInt()
        )

    }




    val daysCount = when {

        month <= 6 -> 31

        month <= 11 -> 30

        else -> 29

    }





    AlertDialog(

        onDismissRequest = {

            onDismiss()

        },



        title = {


            Row(

                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.SpaceBetween,

                verticalAlignment = Alignment.CenterVertically

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

                        imageVector = Icons.Default.ArrowBack,

                        contentDescription = null

                    )

                }




                Text(

                    text = "${getPersianMonthName(month)} $year"

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

                        imageVector = Icons.Default.ArrowForward,

                        contentDescription = null

                    )

                }



            }



        },




        text = {


            Column {



                Row(

                    modifier = Modifier.fillMaxWidth(),

                    horizontalArrangement = Arrangement.SpaceAround

                ) {


                    listOf(

                        "ش",
                        "ی",
                        "د",
                        "س",
                        "چ",
                        "پ",
                        "ج"

                    ).forEach {


                        Text(it)


                    }


                }




                Spacer(

                    modifier = Modifier.height(12.dp)

                )





                var day = 1




                repeat(

                    (daysCount + 6) / 7

                ) {



                    Row(

                        modifier = Modifier.fillMaxWidth(),

                        horizontalArrangement = Arrangement.SpaceAround

                    ) {



                        repeat(7) {



                            if (day <= daysCount) {



                                val currentDay = day



                                Text(

                                    text = currentDay.toString(),


                                    modifier = Modifier

                                        .padding(8.dp)

                                        .clickable {


                                            val newDate = persianToGregorian(
                                                year,
                                                month,
                                                currentDay
                                            )


                                            onDateSelected(newDate)


                                        }


                                )



                                day++



                            } else {



                                Spacer(

                                    modifier = Modifier.size(25.dp)

                                )


                            }



                        }


                    }



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