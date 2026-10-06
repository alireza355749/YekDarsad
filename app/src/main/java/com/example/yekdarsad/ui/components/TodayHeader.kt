package com.example.yekdarsad.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.yekdarsad.getDisplayDate
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun TodayHeader(
    selectedDate: LocalDate,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onTodayClick: () -> Unit,
    onDateClick: () -> Unit
) {

    val today = LocalDate.now()

    val dayText = when (
        ChronoUnit.DAYS.between(today, selectedDate)
    ) {

        0L -> "امروز"

        1L -> "فردا"

        2L -> "پس فردا"

        -1L -> "دیروز"

        -2L -> "پریروز"

        else -> ""
    }

    Column {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onPreviousDay
            ) {

                Text(
                    text = "‹",
                    style = MaterialTheme.typography.headlineMedium
                )
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

                // ارتفاع ثابت برای جلوگیری از جابه‌جایی UI
                Box(
                    modifier = Modifier.height(36.dp),
                    contentAlignment = Alignment.Center
                ) {

                    if (dayText.isNotEmpty()) {

                        TextButton(
                            onClick = {

                                if (selectedDate != today) {
                                    onTodayClick()
                                }

                            }
                        ) {

                            Text(
                                text = dayText,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                    } else {

                        // فقط فضا را حفظ می‌کند
                        Spacer(
                            modifier = Modifier.height(36.dp)
                        )
                    }
                }
            }

            IconButton(
                onClick = onNextDay
            ) {

                Text(
                    text = "›",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )
    }
}