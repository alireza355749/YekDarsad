package com.example.yekdarsad

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddTaskDialog(
    show: Boolean,
    title: String,
    coefficient: String,
    caloriesPerHour: String,
    isExercise: Boolean,
    onTitleChange: (String) -> Unit,
    onCoefficientChange: (String) -> Unit,
    onCaloriesPerHourChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {

    if (!show) return

    AlertDialog(

        onDismissRequest = onDismiss,

        title = {
            Text(
                if (isExercise) {
                    "فعالیت ورزشی جدید"
                } else {
                    "فعالیت جدید"
                }
            )
        },

        text = {

            Column {

                TextField(
                    value = title,

                    onValueChange = onTitleChange,

                    modifier = Modifier.fillMaxWidth(),

                    label = {
                        Text("نام فعالیت")
                    },

                    singleLine = true
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                TextField(
                    value = coefficient,

                    onValueChange = onCoefficientChange,

                    modifier = Modifier.fillMaxWidth(),

                    label = {
                        Text("ضریب")
                    },

                    singleLine = true
                )

                if (isExercise) {

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    TextField(
                        value = caloriesPerHour,

                        onValueChange = onCaloriesPerHourChange,

                        modifier = Modifier.fillMaxWidth(),

                        label = {
                            Text("کالری مصرفی در یک ساعت")
                        },

                        placeholder = {
                            Text("مثلاً 400")
                        },

                        singleLine = true
                    )
                }
            }
        },

        confirmButton = {

            Button(
                onClick = onSave
            ) {

                Text("ذخیره")
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text("لغو")
            }
        }
    )
}