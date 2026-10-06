package com.example.yekdarsad.ui.todaytracker

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackerBottomSheet(

    tracker: TrackerCardModel,

    currentValue: Double?,

    onDismiss: () -> Unit,

    onSave: (Double) -> Unit

) {

    var value by remember {

        mutableStateOf(

            currentValue?.let {

                if (it % 1 == 0.0)

                    it.toInt().toString()

                else

                    it.toString()

            } ?: ""

        )

    }

    ModalBottomSheet(

        onDismissRequest = onDismiss

    ) {

        Column(

            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)

        ) {

            Text(

                text = tracker.title,

                style = MaterialTheme.typography.headlineSmall

            )

            Spacer(

                modifier = Modifier.height(20.dp)

            )

            OutlinedTextField(

                modifier = Modifier.fillMaxWidth(),

                value = value,

                onValueChange = {

                    value = it

                },

                singleLine = true,

                keyboardOptions = KeyboardOptions(

                    keyboardType = KeyboardType.Decimal

                ),

                label = {

                    Text(tracker.unit)

                }

            )

            Spacer(

                modifier = Modifier.height(24.dp)

            )

            Button(

                modifier = Modifier.fillMaxWidth(),

                onClick = {

                    value.toDoubleOrNull()?.let {

                        onSave(it)

                    }

                    onDismiss()

                }

            ) {

                Text("ذخیره")

            }

            Spacer(

                modifier = Modifier.height(20.dp)

            )

        }

    }

}