package com.example.yekdarsad.data.phoneusage

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(
    tableName = "phone_usage"
)
data class PhoneUsageEntity(

    @PrimaryKey
    val date: String,

    val totalMinutes: Long

)