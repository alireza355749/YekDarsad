package com.example.yekdarsad

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun HomeScreen() {


    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),

        horizontalAlignment = Alignment.CenterHorizontally

    ) {


        Text(

            text = "خانه",

            style = MaterialTheme.typography.headlineMedium

        )


        Spacer(
            modifier = Modifier.height(30.dp)
        )



        Text(

            text = "پیشرفت امروز",

            style = MaterialTheme.typography.titleLarge

        )



        Spacer(
            modifier = Modifier.height(20.dp)
        )



        CircularProgress(
            progress = 78
        )



        Spacer(
            modifier = Modifier.height(35.dp)
        )



        DashboardCard(
            "⏱ زمان فعالیت امروز",
            "2 ساعت و 15 دقیقه"
        )


        DashboardCard(
            "⭐ امتیاز امروز",
            "24 امتیاز"
        )


        DashboardCard(
            "✅ فعالیت انجام شده",
            "5 از 7"
        )


    }

}




@Composable
fun DashboardCard(
    title: String,
    value: String
){


    Card(

        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)

    ){


        Column(

            modifier = Modifier.padding(16.dp)

        ){


            Text(
                text = title
            )


            Spacer(
                modifier = Modifier.height(6.dp)
            )


            Text(

                text = value,

                style = MaterialTheme.typography.headlineSmall

            )


        }


    }


}