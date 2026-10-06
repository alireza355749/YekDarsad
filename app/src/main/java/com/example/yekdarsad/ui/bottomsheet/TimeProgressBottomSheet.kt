package com.example.yekdarsad.ui.bottomsheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yekdarsad.data.DailyPlanWithTask
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeProgressBottomSheet(
    item: DailyPlanWithTask,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit,
    onDelete: () -> Unit,
    onNoteSave: (String) -> Unit,
    isPriority: Boolean,
    onPriorityChange: (Boolean) -> Unit
) {
    var minutes by remember {
        mutableIntStateOf(
            item.dailyPlan.actualMinutes
        )
    }

    var note by remember {
        mutableStateOf(
            item.dailyPlan.note
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 15.dp
                )
        ) {

            // =========================================
            // عنوان + حذف
            // =========================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.task.title,
                    style = MaterialTheme.typography.titleLarge
                )

                IconButton(
                    onClick = {
                        onDelete()
                        onDismiss()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "حذف فعالیت"
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // =========================================
            // اولویت
            // =========================================

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onPriorityChange(!isPriority)
                    },
                shape = RoundedCornerShape(12.dp),
                color = if (isPriority) {
                    Color(0xFFFFF1E6)
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 14.dp,
                            vertical = 11.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = "اولویت",
                        tint = if (isPriority) {
                            Color(0xFFD67B3C)
                        } else {
                            Color.Gray
                        },
                        modifier = Modifier.size(21.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(10.dp)
                    )

                    Text(
                        text = "اولویت بالا",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isPriority) {
                            Color(0xFF9A5727)
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )

                    Switch(
                        checked = isPriority,
                        onCheckedChange = {
                            onPriorityChange(it)
                        }
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            // =========================================
            // زمان
            // =========================================

            Text(
                text = "$minutes دقیقه",
                fontSize = 18.sp
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Slider(
                value = minutes.toFloat(),

                onValueChange = { value ->
                    val rawMinutes = value.roundToInt()

                    val maxMinutes =
                        item.dailyPlan.plannedMinutes

                    val step = 15

                    val nearestStep =
                        (
                                rawMinutes.toFloat() / step
                                ).roundToInt() * step

                    val finalMinutes =
                        if (
                            abs(
                                rawMinutes - nearestStep
                            ) <= 2
                        ) {
                            nearestStep
                        } else {
                            rawMinutes
                        }

                    minutes = finalMinutes.coerceIn(
                        0,
                        maxMinutes
                    )
                },

                valueRange =
                    0f..item.dailyPlan.plannedMinutes.toFloat(),

                steps = 0,

                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "۰",
                    style = MaterialTheme.typography.labelSmall
                )

                Text(
                    text = "${item.dailyPlan.plannedMinutes} دقیقه",
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            // =========================================
            // توضیحات
            // =========================================

            OutlinedTextField(
                value = note,
                onValueChange = {
                    note = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                label = {
                    Text("توضیحات")
                },
                placeholder = {
                    Text(
                        "مثلاً امروز تمرکز خوبی داشتم..."
                    )
                },
                maxLines = 3
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            // =========================================
            // ذخیره
            // =========================================

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onSave(minutes)
                    onNoteSave(note)
                    onDismiss()
                }
            ) {
                Text("ذخیره")
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )
        }
    }
}