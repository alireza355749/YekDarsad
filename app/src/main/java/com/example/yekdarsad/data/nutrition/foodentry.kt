package com.example.yekdarsad.data.nutrition

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_entries")
data class FoodEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val date: String,
    val mealType: MealType,
    val foodId: Long,

    // وزن واقعی مصرف‌شده بر حسب گرم
    val amount: Double,

    // مقدار اصلی که کاربر وارد کرده
    val originalAmount: Double = amount,

    // واحد اصلی که کاربر انتخاب کرده
    val originalUnit: String = "گرم",

    val createdAt: Long = System.currentTimeMillis(),

    // شناسه پایدار رکورد در Supabase
    val cloudId: String? = null,

    // زمان آخرین تغییر برای سینک
    val updatedAt: Long = System.currentTimeMillis(),

    // حذف نرم؛ برای انتقال حذف به دستگاه دیگر
    val deletedAt: Long? = null
)