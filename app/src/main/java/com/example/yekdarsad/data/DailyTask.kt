package com.example.yekdarsad.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_tasks")
data class DailyTask(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val taskId: Int,

    val date: String,

    val completed: Boolean,

    val plannedMinutes: Int,

    val actualMinutes: Int,

    val earnedScore: Double = 0.0

)