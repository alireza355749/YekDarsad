package com.example.yekdarsad.data.nutrition

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "manual_calorie_entries")
data class ManualCalorieEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val date: String,
    val calories: Double,
    val note: String = "",

    val createdAt: Long = System.currentTimeMillis(),

    // شناسه پایدار رکورد در Supabase
    val cloudId: String? = null,

    // زمان آخرین تغییر برای سینک
    val updatedAt: Long = System.currentTimeMillis(),

    // حذف نرم برای سینک حذف‌ها
    val deletedAt: Long? = null
)