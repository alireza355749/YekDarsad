package com.example.yekdarsad.data.sync

import android.content.Context
import com.example.yekdarsad.data.DatabaseProvider

object NutritionSyncRunner {

    suspend fun run(context: Context) {
        val database = DatabaseProvider.getDatabase(context)

        val repository = NutritionSyncRepository(
            foodDao = database.foodDao(),
            nutritionDao = database.nutritionDao(),
            manualCalorieDao = database.manualCalorieDao()
        )

        repository.syncNutrition()
    }
}