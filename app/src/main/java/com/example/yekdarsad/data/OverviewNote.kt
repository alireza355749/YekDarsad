package com.example.yekdarsad.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "overview_notes"
)
data class OverviewNote(

    @PrimaryKey
    val periodKey: String,

    val text: String,

    val updatedAt: Long = System.currentTimeMillis()
)