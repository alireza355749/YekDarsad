package com.example.yekdarsad.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "tracker_entries"
)
data class TrackerEntry(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val date: String,

    val type: String,

    val value: Double

)