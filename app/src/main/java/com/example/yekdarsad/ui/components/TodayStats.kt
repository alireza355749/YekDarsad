package com.example.yekdarsad.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TodayStats(
    totalMinutes: Int,
    completedMinutes: Int,
    totalCoefficient: Double,
    completedCoefficient: Double
) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            StatCard(
                modifier = Modifier.weight(1f),
                value = "$totalMinutes",
                title = "دقیقه برنامه"
            )

            StatCard(
                modifier = Modifier.weight(1f),
                value = "$completedMinutes",
                title = "دقیقه انجام شده"
            )

        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            StatCard(
                modifier = Modifier.weight(1f),
                value = "%.2f".format(totalCoefficient),
                title = "امتیاز کل"
            )

            StatCard(
                modifier = Modifier.weight(1f),
                value = "%.2f".format(completedCoefficient),
                title = "امتیاز کسب شده"
            )

        }

    }

}