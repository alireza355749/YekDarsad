package com.example.yekdarsad.data.nutrition

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nutrition_settings")
data class NutritionSettings(

    @PrimaryKey
    val id: Int = 1,

    val targetCalories: Int = 2500,

    val targetProtein: Int = 150,

    val targetCarbs: Int = 300,

    val targetFat: Int = 70,

    val breakfastReminder: String = "08:30",

    val lunchReminder: String = "13:30",

    val snackReminder: String = "17:30",

    val dinnerReminder: String = "21:00",

    val notificationsEnabled: Boolean = true
)