package com.example.yekdarsad.data.nutrition

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_foods")
data class CustomFood(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,

    val caloriesPer100g: Double,

    val protein: Double,

    val carbs: Double,

    val fat: Double,

    val fiber: Double = 0.0
)