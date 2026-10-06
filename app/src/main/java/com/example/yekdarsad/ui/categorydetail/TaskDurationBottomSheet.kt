package com.example.yekdarsad

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.yekdarsad.data.TaskWithDurations

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
fun TaskDurationBottomSheet(
    task: TaskWithDurations,
    selectedMinutes: Int?,
    onMinuteSelected: (Int) -> Unit,
    onAddClick: (Int) -> Unit,
    onDismiss: () -> Unit
) {

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Text(
                text = task.task.title,
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text("مدت زمان")

            Spacer(modifier = Modifier.height(12.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                task.durations.forEach { duration ->

                    FilterChip(
                        selected = selectedMinutes == duration.minutes,
                        onClick = {
                            onMinuteSelected(duration.minutes)
                        },
                        label = {
                            Text("${duration.minutes} دقیقه")
                        }
                    )

                }

            }

            Spacer(modifier = Modifier.height(25.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedMinutes != null,
                onClick = {
                    selectedMinutes?.let(onAddClick)
                }
            ) {
                Text("➕ افزودن به برنامه")
            }

        }

    }

}