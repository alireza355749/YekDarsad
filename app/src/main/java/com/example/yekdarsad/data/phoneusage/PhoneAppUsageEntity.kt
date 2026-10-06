package com.example.yekdarsad.data.phoneusage

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(
    tableName = "phone_app_usage"
)
data class PhoneAppUsageEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val date: String,

    val packageName: String,

    val appName: String,

    val minutes: Long

)