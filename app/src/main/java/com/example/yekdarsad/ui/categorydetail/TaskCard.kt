package com.example.yekdarsad

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.yekdarsad.data.TaskWithDurations
import com.example.yekdarsad.ui.theme.categoryColors

@Composable
fun TaskCard(
    item: TaskWithDurations,

    categoryName: String,

    modifier: Modifier = Modifier,

    onClick: () -> Unit,

    onEdit: () -> Unit,

    onDelete: () -> Unit,

    onPositionChanged: (IntOffset) -> Unit
) {

    Log.d(
        "TaskCardUI",
        "RENDER id=${item.task.id} " +
                "title='${item.task.title}' " +
                "categoryId=${item.task.categoryId} " +
                "deleted=${item.task.deleted} " +
                "type=${item.task.type} " +
                "order=${item.task.orderIndex}"
    )

    var menuExpanded by remember {
        mutableStateOf(false)
    }

    // =====================================================
    // همان رنگی که CategoryVisualCard استفاده می‌کند
    // =====================================================

    val colors =
        categoryColors(categoryName)

    // =====================================================
    // رنگ اصلی کارت
    // =====================================================

    val cardColor =
        colors.background

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(145.dp)

            .background(
                color = cardColor,
                shape = RoundedCornerShape(10.dp)
            )

            .onGloballyPositioned { coordinates ->

                val position =
                    coordinates.localToWindow(
                        androidx.compose.ui.geometry.Offset.Zero
                    )

                Log.d(
                    "TaskCardSIZE",
                    "title='${item.task.title}' " +
                            "id=${item.task.id} " +
                            "x=${position.x} " +
                            "y=${position.y} " +
                            "width=${coordinates.size.width} " +
                            "height=${coordinates.size.height}"
                )

                onPositionChanged(
                    IntOffset(
                        position.x.toInt(),
                        position.y.toInt()
                    )
                )
            }

            .clickable {
                onClick()
            }
    ) {

        // =================================================
        // عنوان
        // =================================================

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 9.dp,
                    end = 9.dp,
                    top = 8.dp,
                    bottom = 25.dp
                ),

            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text = item.task.title,

                style =
                    MaterialTheme.typography.bodyMedium,

                color =
                    androidx.compose.ui.graphics.Color(0xFF252525),

                maxLines = 3,

                modifier =
                    Modifier.fillMaxWidth(),

                textAlign =
                    TextAlign.Center
            )
        }

        // =================================================
        // ضریب
        // =================================================

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(
                    start = 8.dp,
                    bottom = 7.dp
                )
        ) {

            Text(
                text =
                    "${formatCoefficient(item.task.coefficient)}X",

                style =
                    MaterialTheme.typography.labelSmall,

                color =
                    colors.accent
            )
        }

        // =================================================
        // منوی سه نقطه
        // =================================================

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(
                    top = 2.dp,
                    end = 2.dp
                )
        ) {

            IconButton(
                modifier =
                    Modifier.size(26.dp),

                onClick = {
                    menuExpanded = true
                }
            ) {

                Icon(
                    imageVector =
                        Icons.Default.MoreVert,

                    contentDescription =
                        "گزینه‌های فعالیت",

                    tint =
                        colors.accent,

                    modifier =
                        Modifier.size(16.dp)
                )
            }

            DropdownMenu(
                expanded =
                    menuExpanded,

                onDismissRequest = {
                    menuExpanded = false
                }
            ) {

                DropdownMenuItem(
                    text = {
                        Text("ویرایش")
                    },

                    onClick = {

                        menuExpanded = false

                        onEdit()
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("حذف")
                    },

                    onClick = {

                        menuExpanded = false

                        onDelete()
                    }
                )
            }
        }
    }
}

// =========================================================
// Coefficient
// =========================================================

private fun formatCoefficient(
    value: Double
): String {

    return if (value % 1.0 == 0.0) {

        value.toInt().toString()

    } else {

        value.toString()
            .removeSuffix("0")
            .removeSuffix(".")
    }
}