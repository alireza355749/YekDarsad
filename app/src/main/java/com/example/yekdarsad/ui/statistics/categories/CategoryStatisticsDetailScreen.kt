package com.example.yekdarsad.ui.statistics.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CategoryStatisticsDetailScreen(
    categoryId: Int,
    categoryName: String,
    onBack: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),

        verticalArrangement =
            Arrangement.spacedBy(16.dp)
    ) {

        TextButton(
            onClick = onBack
        ) {

            Text(
                text = "← بازگشت"
            )
        }

        Text(
            text = categoryName,
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "آمار این دسته در این بخش نمایش داده می‌شود.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}