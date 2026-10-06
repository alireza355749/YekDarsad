package com.example.yekdarsad.ui.statistics

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.yekdarsad.data.statistics.StatisticsData
import com.example.yekdarsad.ui.theme.*


@Composable
fun WeeklyActivityChart(
    statistics: StatisticsData
) {


    Card(

        modifier = Modifier
            .wrapContentWidth(),

        shape = RoundedCornerShape(16.dp),

        colors = CardDefaults.cardColors(
            containerColor = PrimaryGreenLight.copy(alpha = 0.3f)
        )

    ) {


        Column(

            modifier = Modifier
                .padding(8.dp)

        ) {


            Text(
                text = "فعالیت هفته",
                style = MaterialTheme.typography.labelMedium
            )



            Spacer(
                modifier = Modifier.height(6.dp)
            )



            Row(

                verticalAlignment = Alignment.Bottom,

                modifier = Modifier
                    .height(125.dp)

            ) {



                // محور ساعت

                Column(

                    modifier = Modifier
                        .height(105.dp)
                        .width(16.dp),

                    verticalArrangement = Arrangement.SpaceBetween

                ) {


                    listOf("5","4","3","2","1","0")
                        .forEach {

                            Text(
                                text = it,
                                style = MaterialTheme.typography.labelSmall
                            )

                        }

                }



                Spacer(
                    modifier = Modifier.width(5.dp)
                )





                Row(

                    modifier = Modifier
                        .height(115.dp),

                    horizontalArrangement =
                        Arrangement.spacedBy(5.dp),

                    verticalAlignment =
                        Alignment.Bottom

                ) {



                    statistics.weekDays
                        .forEachIndexed { index, day ->



                            val hour =
                                statistics.weeklyActivityHours
                                    .getOrNull(index)
                                    ?: 0f



                            var show by remember {
                                mutableStateOf(false)
                            }



                            LaunchedEffect(Unit) {

                                show = true

                            }




                            val animatedHeight by animateFloatAsState(

                                targetValue =
                                    if(show)
                                        hour
                                    else
                                        0f,


                                animationSpec =
                                    tween(

                                        durationMillis = 700,

                                        delayMillis = index * 70,

                                        easing = FastOutSlowInEasing

                                    )

                            )





                            Column(

                                modifier = Modifier
                                    .width(10.dp),

                                horizontalAlignment =
                                    Alignment.CenterHorizontally,

                                verticalArrangement =
                                    Arrangement.Bottom

                            ) {




                                Box(

                                    modifier = Modifier

                                        .width(7.dp)

                                        .height(
                                            (animatedHeight * 18)
                                                .dp
                                        )

                                        .clip(
                                            RoundedCornerShape(50)
                                        )

                                        .background(
                                            PrimaryGreen
                                        )

                                )





                                Text(

                                    text = day.take(1),

                                    style =
                                        MaterialTheme.typography.labelSmall

                                )


                            }



                        }



                }



            }



        }



    }


}