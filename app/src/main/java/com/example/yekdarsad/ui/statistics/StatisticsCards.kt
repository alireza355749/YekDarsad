package com.example.yekdarsad.ui.statistics

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun StatisticsCards(){


    Column(

        verticalArrangement = Arrangement.spacedBy(12.dp)

    ){


        StatCard(
            title = "رشد کلی",
            value = "0%"
        )


        StatCard(
            title = "خواب",
            value = "0 ساعت"
        )


        StatCard(
            title = "زمان گوشی",
            value = "0 ساعت"
        )


        StatCard(
            title = "کالری",
            value = "0"
        )


        StatCard(
            title = "مخارج",
            value = "0"
        )


    }


}



@Composable
fun StatCard(

    title:String,

    value:String

){


    Card(

        modifier = Modifier
            .fillMaxWidth(),

        elevation = CardDefaults.cardElevation(
            4.dp
        )

    ){


        Row(

            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),

            horizontalArrangement =
                Arrangement.SpaceBetween

        ){


            Text(title)


            Text(value)


        }


    }


}