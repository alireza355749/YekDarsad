package com.example.yekdarsad.ui.todaytracker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun TrackerCard(

    type: TrackerType,

    value: Double?,

    onClick: () -> Unit

) {

    val mainColor = Color(0xFF7A9A6D)

    val icon = when (type) {

        TrackerType.CALORIES ->
            Icons.Default.LocalFireDepartment

        TrackerType.EXPENSE ->
            Icons.Default.Payments

        TrackerType.WATER ->
            Icons.Default.WaterDrop

        TrackerType.WEIGHT ->
            Icons.Default.MonitorWeight

    }


    Card(

        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable {
                onClick()
            },

        shape = RoundedCornerShape(18.dp),

        colors = CardDefaults.cardColors(

            containerColor = Color(0xFFF3F7EF)

        ),

        elevation = CardDefaults.cardElevation(

            defaultElevation = 0.dp

        )

    ) {


        Row(

            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),

            verticalAlignment = Alignment.CenterVertically

        ) {


            Icon(

                imageVector = icon,

                contentDescription = null,

                tint = mainColor,

                modifier = Modifier.size(26.dp)

            )


            Spacer(

                modifier = Modifier.width(12.dp)

            )


            Column(

                modifier = Modifier.weight(1f)

            ) {


                Text(

                    text = type.title,

                    style = MaterialTheme.typography.labelLarge

                )


                Text(

                    text =
                        if (value == null)
                            "ثبت نشده"
                        else
                            "${value.toInt()} ${type.unit}",

                    color = Color.Gray,

                    style = MaterialTheme.typography.bodySmall

                )


            }


        }


    }

}