package com.example.yekdarsad.ui.statistics.phoneusage

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.yekdarsad.data.phoneusage.PhoneUsageData

@Composable
fun PhoneUsageChart(
    phoneUsageData: PhoneUsageData?,
    onEnablePermission: () -> Unit
) {

    Text(
        text = "PHONE USAGE TEST"
    )

}