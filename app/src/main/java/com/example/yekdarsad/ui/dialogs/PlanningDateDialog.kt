package com.example.yekdarsad.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PlanningDateDialog(
    selectedDate: Int?,
    onDateSelected: (Int) -> Unit,
    onContinue: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f)),
        contentAlignment = Alignment.Center
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {

            Column(
                modifier = Modifier.padding(24.dp)
            ) {

                Text(
                    text = "برای چه روزی به طور خاص می‌خواهی برنامه‌ریزی کنی؟",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onDateSelected(0)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            if (selectedDate == 0)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text("امروز")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onDateSelected(1)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            if (selectedDate == 1)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text("فردا")
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onDateSelected(2)
                    }
                ) {
                    Text("انتخاب از تقویم")
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedDate != null,
                    onClick = onContinue
                ) {
                    Text("ادامه")
                }

            }

        }

    }

}