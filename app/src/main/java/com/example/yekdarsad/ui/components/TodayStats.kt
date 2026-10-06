package com.example.yekdarsad.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TodayStats(

    totalMinutes: Int,

    completedMinutes: Int,

    totalCoefficient: Double,

    completedCoefficient: Double

) {

    Row(

        modifier = Modifier.fillMaxWidth(),

        horizontalArrangement = Arrangement.spacedBy(8.dp)

    ) {

        StatCard(

            modifier = Modifier.weight(1f),

            icon = Icons.Default.Schedule,

            color = Color(0xFF7A9A6D),

            value = formatMinutes(totalMinutes),

            title = "برنامه"

        )

        StatCard(

            modifier = Modifier.weight(1f),

            icon = Icons.Default.CheckCircle,

            color = Color(0xFF7A9A6D),

            value = formatMinutes(completedMinutes),

            title = "انجام شده"

        )

        StatCard(

            modifier = Modifier.weight(1f),

            icon = Icons.Default.TrackChanges,

            color = Color(0xFF7A9A6D),

            value = "%.1f".format(totalCoefficient),

            title = "ضریب"

        )

        StatCard(

            modifier = Modifier.weight(1f),

            icon = Icons.Default.EmojiEvents,

            color = Color(0xFF7A9A6D),

            value = "%.1f".format(completedCoefficient),

            title = "امتیاز"

        )

    }

}

private fun formatMinutes(minutes: Int): String {

    val hours = minutes / 60
    val mins = minutes % 60

    return String.format(
        "%d:%02d",
        hours,
        mins
    )

}