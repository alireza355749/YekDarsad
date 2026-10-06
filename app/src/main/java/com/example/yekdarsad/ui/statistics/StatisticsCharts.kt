package com.example.yekdarsad.ui.statistics


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun StatisticsCharts(

    activityData: List<Float>,
    sleepData: List<Float>,
    phoneData: List<Float>,
    expenseData: List<Float>

) {


    Column(

        modifier = Modifier
            .fillMaxWidth()

    ) {


        ChartCard(

            title = "فعالیت هفتگی",

            values = activityData

        )


        ChartCard(

            title = "روند خواب",

            values = sleepData

        )


        ChartCard(

            title = "استفاده از گوشی",

            values = phoneData

        )


        ChartCard(

            title = "مخارج",

            values = expenseData

        )


    }

}



@Composable
private fun ChartCard(

    title: String,

    values: List<Float>

) {


    Card(

        modifier = Modifier

            .fillMaxWidth()

            .padding(vertical = 8.dp)

    ) {


        Column(

            modifier = Modifier
                .padding(16.dp)

        ) {


            Text(

                text = title,

                style = MaterialTheme.typography.titleMedium

            )


            Spacer(
                modifier = Modifier.height(12.dp)
            )


            if(values.isEmpty()) {


                Text(
                    text = "داده‌ای وجود ندارد"
                )


            } else {


                Text(

                    text = values.joinToString(
                        " | "
                    )

                )


            }


        }


    }


}