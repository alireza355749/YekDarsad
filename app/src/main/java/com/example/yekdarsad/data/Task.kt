package com.example.yekdarsad.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "tasks")
data class Task(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val categoryId: Int,

    val title: String,

    val coefficient: Double,

    // نوع فعالیت
    // TIME = فعالیت زمانی
    // CHECK = فعالیت تیکی
    val type: String = "TIME"

)