package com.example.yekdarsad.data.nutrition

import com.example.yekdarsad.data.DailyPlanDao
import kotlinx.coroutines.flow.Flow

class NutritionRepository(
    private val foodDao: FoodDao,
    private val nutritionDao: NutritionDao,
    private val manualCalorieDao: ManualCalorieDao,
    private val dailyPlanDao: DailyPlanDao
) {

    // =====================================================
    // Food Entries
    // =====================================================

    fun getEntries(
        date: String
    ): Flow<List<FoodEntryWithFood>> {
        return nutritionDao.getFoodEntries(date)
    }

    suspend fun addEntry(
        entry: FoodEntry
    ) {
        nutritionDao.insertFoodEntry(
            entry.copy(
                updatedAt = System.currentTimeMillis(),
                deletedAt = null
            )
        )
    }

    suspend fun deleteEntry(
        id: Long
    ) {
        val now = System.currentTimeMillis()

        nutritionDao.softDeleteFoodEntry(
            id = id,
            deletedAt = now,
            updatedAt = now
        )
    }

    // =====================================================
    // Foods
    // =====================================================

    suspend fun getFood(
        id: Long
    ): Food? {
        return foodDao.getFoodById(id)
    }

    suspend fun getFoodByName(
        name: String
    ): Food? {
        return foodDao.getFoodByName(name)
    }

    suspend fun searchFoods(
        query: String
    ): List<Food> {
        return foodDao.searchFoods(query)
    }

    suspend fun getAllFoods(): List<Food> {
        return foodDao.getAllFoods()
    }

    // =====================================================
    // Nutrition Settings
    // =====================================================

    suspend fun saveSettings(
        settings: NutritionSettings
    ) {
        nutritionDao.saveSettings(settings)
    }

    suspend fun getSettings(): NutritionSettings? {
        return nutritionDao.getSettings()
    }

    // =====================================================
    // Manual Calories
    // =====================================================

    suspend fun addManualCalories(
        entry: ManualCalorieEntry
    ) {
        manualCalorieDao.insert(
            entry.copy(
                updatedAt = System.currentTimeMillis(),
                deletedAt = null
            )
        )
    }

    fun getManualCalories(
        date: String
    ): Flow<Double> {
        return manualCalorieDao.getTotalCalories(date)
    }

    fun getManualCalorieEntries(
        date: String
    ): Flow<List<ManualCalorieEntry>> {
        return manualCalorieDao.getEntries(date)
    }

    suspend fun deleteManualCalories(
        id: Long
    ) {
        val now = System.currentTimeMillis()

        manualCalorieDao.softDelete(
            id = id,
            deletedAt = now,
            updatedAt = now
        )
    }

    suspend fun deleteAllManualCalories(
        date: String
    ) {
        val now = System.currentTimeMillis()

        manualCalorieDao.softDeleteAllForDate(
            date = date,
            deletedAt = now,
            updatedAt = now
        )
    }

    // =====================================================
    // Exercise Calories
    // =====================================================

    fun getExerciseCalories(
        date: String
    ): Flow<Double> {
        return dailyPlanDao.getExerciseCalories(date)
    }
}