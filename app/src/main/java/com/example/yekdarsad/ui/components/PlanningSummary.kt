package com.example.yekdarsad.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PlanningSummary(

    totalMinutes: Int

) {

    val message = when {

        totalMinutes == 0 ->
            "امروز هنوز برنامه‌ای نچیدی."

        totalMinutes < 120 ->
            "شروع خوبیه، ادامه بده."

        totalMinutes < 240 ->
            "روزت کم‌کم داره شکل می‌گیره."

        totalMinutes < 360 ->
            "برنامه امروزت متعادل به نظر می‌رسه."

        totalMinutes < 480 ->
            "امروز پر از کارهای مفیده."

        else ->
            "یه روز شلوغ و پربار در انتظارتـه."

    }

    Column {

        Text(
            text = "برنامه امروز",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(
            modifier = androidx.compose.ui.Modifier.height(8.dp)
        )

        Text(
            text = "${totalMinutes / 60} ساعت و ${totalMinutes % 60} دقیقه",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(
            modifier = androidx.compose.ui.Modifier.height(6.dp)
        )

        Text(
            text = message,
            color = Color.Gray,
            style = MaterialTheme.typography.bodyMedium
        )

    }

}