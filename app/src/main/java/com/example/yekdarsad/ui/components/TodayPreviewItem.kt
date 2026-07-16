package com.example.yekdarsad.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.yekdarsad.data.DailyPlanWithTask

@Composable
fun TodayPreviewItem(

    item: DailyPlanWithTask

) {

    Column(

        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)

    ) {

        Text(

            text = item.task.title,

            style = MaterialTheme.typography.titleMedium

        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(

            text =
                if (item.task.type == "TIME")
                    "${item.dailyPlan.plannedMinutes} دقیقه"
                else
                    "فعالیت",

            style = MaterialTheme.typography.bodySmall,

            color = Color.Gray

        )

    }

}