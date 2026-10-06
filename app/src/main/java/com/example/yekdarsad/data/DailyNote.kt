package com.example.yekdarsad.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_notes"
)
data class DailyNote(
    @PrimaryKey
    val date: String,

    // یادداشت‌های روزانه
    val note: String = "",

    // مشارطه
    val mosharete: String = "",

    // محاسبه
    val mohasebe: String = "",

    // زمان آخرین ویرایش برحسب میلی‌ثانیه
    @ColumnInfo(defaultValue = "0")
    val updatedAt: Long = 0L
)