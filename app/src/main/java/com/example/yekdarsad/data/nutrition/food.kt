package com.example.yekdarsad.data.nutrition

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "foods")
data class Food(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,

    val category: String,

    val caloriesPer100g: Double,

    val protein: Double,

    val carbs: Double,

    val fat: Double,

    val fiber: Double,

    val vitaminA: Double,

    val vitaminB: Double,

    val vitaminC: Double,

    val vitaminD: Double,

    val calcium: Double,

    val iron: Double,

    // واحد پیش‌فرض نمایش غذا
    // مثال: گرم، عدد، حبه، قاشق، لیوان، برش
    val servingUnit: String = "گرم",

    // مقدار واحد
    // مثال: 100 گرم، 1 عدد، 1 قاشق
    val servingAmount: Double = 100.0,

    // وزن واقعی این واحد بر حسب گرم
    // مثال: 1 عدد شلیل = حدود 100 گرم
    val servingWeightGrams: Double = 100.0
)