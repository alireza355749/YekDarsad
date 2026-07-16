package com.example.yekdarsad.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PlanningPreviewCard(

    totalMinutes: Int

) {

    Card(

        modifier = Modifier
            .fillMaxWidth(),

        shape = RoundedCornerShape(28.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )

    ) {

        Box(

            modifier = Modifier
                .background(

                    Brush.linearGradient(

                        listOf(

                            Color(0xFFFFFCF5),

                            Color(0xFFF7F3E8)

                        )

                    )

                )

                .padding(20.dp)

        ) {

            Row(

                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.SpaceBetween,

                verticalAlignment = Alignment.CenterVertically

            ) {

                PlanningSummary(

                    totalMinutes = totalMinutes

                )

                DayEnergyRing(

                    totalMinutes = totalMinutes

                )

            }

        }

    }

}