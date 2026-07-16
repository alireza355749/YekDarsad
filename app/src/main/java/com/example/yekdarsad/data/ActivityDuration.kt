package com.example.yekdarsad.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "activity_durations")
data class ActivityDuration(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val taskId: Int,

    val minutes: Int

)