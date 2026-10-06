
package com.example.yekdarsad.data.sleep

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "sleep_entries"
)
data class SleepEntry(
    @PrimaryKey
    val date: String,
    val sleepStartHour: Int = 23,
    val sleepStartMinute: Int = 0,
    val wakeHour: Int = 7,
    val wakeMinute: Int = 30,
    val napsMinutes: Int = 0,
    val updatedAt: Long = 0L
)