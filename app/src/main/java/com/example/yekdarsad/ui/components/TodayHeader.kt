package com.example.yekdarsad.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.yekdarsad.getDisplayDate
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun TodayHeader(
    selectedDate: LocalDate,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onTodayClick: () -> Unit,
    onDateClick: () -> Unit
) {

    Text(
        text = "برنامه امروز",
        style = MaterialTheme.typography.headlineMedium
    )

    Spacer(
        modifier = Modifier.height(15.dp)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(
            onClick = onPreviousDay
        ) {
            Text("◀")
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = getDisplayDate(selectedDate),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.clickable {
                    onDateClick()
                }
            )

            TextButton(
                onClick = onTodayClick
            ) {
                Text("امروز")
            }

        }

        IconButton(
            onClick = onNextDay
        ) {
            Text("▶")
        }

    }

    Spacer(
        modifier = Modifier.height(10.dp)
    )

}