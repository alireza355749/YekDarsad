package com.example.yekdarsad.ui.categorydetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.yekdarsad.TaskCard
import com.example.yekdarsad.data.TaskWithDurations

@Composable
fun TaskList(
    tasks: List<TaskWithDurations>,

    categoryName: String,

    onTaskClick: (TaskWithDurations) -> Unit,

    onTaskEdit: (TaskWithDurations) -> Unit,

    onTaskDelete: (TaskWithDurations) -> Unit,

    onTaskPositionChanged: (
        Int,
        IntOffset
    ) -> Unit
) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),

        modifier =
            Modifier.fillMaxWidth(),

        contentPadding =
            PaddingValues(
                top = 4.dp,
                bottom = 20.dp
            ),

        horizontalArrangement =
            Arrangement.spacedBy(7.dp),

        verticalArrangement =
            Arrangement.spacedBy(7.dp)
    ) {

        items(
            items = tasks,

            key = {
                it.task.id
            }
        ) { item ->

            TaskCard(

                item = item,

                categoryName = categoryName,

                onClick = {
                    onTaskClick(item)
                },

                onEdit = {
                    onTaskEdit(item)
                },

                onDelete = {
                    onTaskDelete(item)
                },

                onPositionChanged = { position ->

                    onTaskPositionChanged(
                        item.task.id,
                        position
                    )
                }
            )
        }
    }
}