package com.example.yekdarsad.data.phoneusage

data class PhoneUsageData(
    val screenTimeMinutes: Long,
    val topApps: List<AppUsage>
)