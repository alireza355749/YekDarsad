package com.example.yekdarsad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yekdarsad.data.nutrition.Food
import com.example.yekdarsad.data.nutrition.FoodEntry
import com.example.yekdarsad.data.nutrition.FoodEntryWithFood
import com.example.yekdarsad.data.nutrition.ManualCalorieEntry
import com.example.yekdarsad.data.nutrition.NutritionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class NutritionSummary(

    val calories: Double = 0.0,

    val protein: Double = 0.0,
    val carbs: Double = 0.0,
    val fat: Double = 0.0,

    val fiber: Double = 0.0,

    val calcium: Double = 0.0,
    val iron: Double = 0.0,

    val vitaminA: Double = 0.0,
    val vitaminB: Double = 0.0,
    val vitaminC: Double = 0.0,
    val vitaminD: Double = 0.0,

    val exerciseCalories: Double = 0.0
)

class NutritionViewModel(
    private val repository: NutritionRepository
) : ViewModel() {

    // =====================================================
    // Food Entries
    // =====================================================

    fun getEntries(
        date: String
    ): Flow<List<FoodEntryWithFood>> {

        return repository.getEntries(date)
    }

    // =====================================================
    // Nutrition Summary
    // =====================================================

    fun getSummary(
        date: String
    ): Flow<NutritionSummary> {

        /*
         * نکته بسیار مهم:
         *
         * FoodEntry.amount همیشه گرم واقعی است.
         *
         * بنابراین اینجا دیگر نباید:
         *
         * servingUnit
         * servingAmount
         * servingWeightGrams
         *
         * را برای تبدیل استفاده کنیم.
         *
         * تبدیل فقط یک بار در AddFoodBottomSheet انجام شده.
         */

        val foodSummaryFlow =
            repository
                .getEntries(date)
                .map { entries ->

                    var calories = 0.0

                    var protein = 0.0
                    var carbs = 0.0
                    var fat = 0.0

                    var fiber = 0.0

                    var calcium = 0.0
                    var iron = 0.0

                    var vitaminA = 0.0
                    var vitaminB = 0.0
                    var vitaminC = 0.0
                    var vitaminD = 0.0

                    entries.forEach { item ->

                        val food =
                            item.food
                                ?: return@forEach

                        /*
                         * amount همیشه گرم است.
                         *
                         * مثال:
                         *
                         * 2 عدد تخم مرغ
                         * amount = 100
                         *
                         * 3 کف دست بربری
                         * amount = 90
                         *
                         * 28 قاشق برنج
                         * amount = وزن واقعی 28 قاشق
                         */

                        val actualWeightGrams =
                            item.entry.amount
                                .coerceAtLeast(0.0)

                        /*
                         * اطلاعات Food بر اساس 100 گرم هستند.
                         *
                         * 100 گرم -> 1.0
                         * 50 گرم  -> 0.5
                         * 200 گرم -> 2.0
                         */

                        val multiplier =
                            actualWeightGrams / 100.0

                        // =================================================
                        // Calories
                        // =================================================

                        calories +=
                            food.caloriesPer100g *
                                    multiplier

                        // =================================================
                        // Macronutrients
                        // =================================================

                        protein +=
                            food.protein *
                                    multiplier

                        carbs +=
                            food.carbs *
                                    multiplier

                        fat +=
                            food.fat *
                                    multiplier

                        fiber +=
                            food.fiber *
                                    multiplier

                        // =================================================
                        // Minerals
                        // =================================================

                        calcium +=
                            food.calcium *
                                    multiplier

                        iron +=
                            food.iron *
                                    multiplier

                        // =================================================
                        // Vitamins
                        // =================================================

                        vitaminA +=
                            food.vitaminA *
                                    multiplier

                        vitaminB +=
                            food.vitaminB *
                                    multiplier

                        vitaminC +=
                            food.vitaminC *
                                    multiplier

                        vitaminD +=
                            food.vitaminD *
                                    multiplier
                    }

                    NutritionSummary(

                        calories = calories,

                        protein = protein,
                        carbs = carbs,
                        fat = fat,

                        fiber = fiber,

                        calcium = calcium,
                        iron = iron,

                        vitaminA = vitaminA,
                        vitaminB = vitaminB,
                        vitaminC = vitaminC,
                        vitaminD = vitaminD
                    )
                }

        // =====================================================
        // کالری دستی
        // =====================================================

        val manualCaloriesFlow =
            repository.getManualCalories(date)

        // =====================================================
        // کالری ورزش
        // =====================================================

        val exerciseCaloriesFlow =
            repository.getExerciseCalories(date)

        return combine(

            foodSummaryFlow,

            manualCaloriesFlow,

            exerciseCaloriesFlow

        ) { foodSummary, manualCalories, exerciseCalories ->

            NutritionSummary(

                calories =
                    foodSummary.calories +
                            manualCalories,

                protein =
                    foodSummary.protein,

                carbs =
                    foodSummary.carbs,

                fat =
                    foodSummary.fat,

                fiber =
                    foodSummary.fiber,

                calcium =
                    foodSummary.calcium,

                iron =
                    foodSummary.iron,

                vitaminA =
                    foodSummary.vitaminA,

                vitaminB =
                    foodSummary.vitaminB,

                vitaminC =
                    foodSummary.vitaminC,

                vitaminD =
                    foodSummary.vitaminD,

                exerciseCalories =
                    exerciseCalories
            )
        }
    }

    // =====================================================
    // Foods
    // =====================================================

    fun getAllFoods(
        onResult: (List<Food>) -> Unit
    ) {

        viewModelScope.launch {

            onResult(
                repository.getAllFoods()
            )
        }
    }

    // =====================================================
    // Add Food Entry
    // =====================================================

    fun addEntry(
        entry: FoodEntry
    ) {

        viewModelScope.launch {

            repository.addEntry(entry)
        }
    }

    // =====================================================
    // Delete Food Entry
    // =====================================================

    fun deleteEntry(
        id: Long
    ) {

        viewModelScope.launch {

            repository.deleteEntry(id)
        }
    }

    // =====================================================
    // Manual Calories
    // =====================================================

    fun addManualCalories(
        date: String,
        calories: Double,
        note: String = ""
    ) {

        if (calories <= 0.0) {
            return
        }

        viewModelScope.launch {

            repository.addManualCalories(

                ManualCalorieEntry(

                    date = date,

                    calories = calories,

                    note = note
                )
            )
        }
    }

    fun getManualCalories(
        date: String
    ): Flow<Double> {

        return repository.getManualCalories(date)
    }

    fun getManualCalorieEntries(
        date: String
    ): Flow<List<ManualCalorieEntry>> {

        return repository.getManualCalorieEntries(date)
    }

    fun deleteManualCalories(
        id: Long
    ) {

        viewModelScope.launch {

            repository.deleteManualCalories(id)
        }
    }
}