package com.example.yekdarsad.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_plans")
data class DailyPlan(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val taskId: Int,

    val date: String,

    val plannedMinutes: Int,

    val actualMinutes: Int = 0,

    val earnedScore: Double = 0.0,

    val completed: Boolean = false

)