package com.example.yekdarsad.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EmptyTodayView() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "برای این روز هنوز برنامه‌ای ثبت نشده است.",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            modifier = Modifier.padding(top = 12.dp),
            text = "از تب «فعالیت‌ها» یک فعالیت را به این روز اضافه کنید.",
            style = MaterialTheme.typography.bodyMedium
        )

    }

}