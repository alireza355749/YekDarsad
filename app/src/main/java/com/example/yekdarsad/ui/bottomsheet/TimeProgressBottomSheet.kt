package com.example.yekdarsad.ui.bottomsheet

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.yekdarsad.data.DailyPlanWithTask

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeProgressBottomSheet(
    item: DailyPlanWithTask,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit
) {

    var minutes by remember {
        mutableIntStateOf(item.dailyPlan.actualMinutes)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Text(
                text = item.task.title,
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text("${minutes} دقیقه")

            Spacer(modifier = Modifier.height(10.dp))

            Slider(
                value = minutes.toFloat(),
                onValueChange = {
                    minutes = it.toInt()
                },
                valueRange = 0f..item.dailyPlan.plannedMinutes.toFloat()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onSave(minutes)
                    onDismiss()
                }
            ) {
                Text("ذخیره")
            }

            Spacer(modifier = Modifier.height(15.dp))

        }

    }

}