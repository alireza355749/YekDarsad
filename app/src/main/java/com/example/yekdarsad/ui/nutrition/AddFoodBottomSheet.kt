package com.example.yekdarsad.ui.nutrition

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yekdarsad.data.DatabaseProvider
import com.example.yekdarsad.data.nutrition.Food
import com.example.yekdarsad.data.nutrition.FoodEntry
import com.example.yekdarsad.data.nutrition.MealType
import com.example.yekdarsad.data.nutrition.NutritionRepository
import com.example.yekdarsad.viewmodel.NutritionViewModel
import com.example.yekdarsad.viewmodel.NutritionViewModelFactory
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodBottomSheet(
    date: String,
    initialMealType: MealType = MealType.LUNCH,
    onDismiss: () -> Unit
) {

    val context = LocalContext.current

    val database = remember {
        DatabaseProvider.getDatabase(context)
    }

    val viewModel: NutritionViewModel = viewModel(
        factory = NutritionViewModelFactory(
            NutritionRepository(
                database.foodDao(),
                database.nutritionDao(),
                database.manualCalorieDao(),
                database.dailyPlanDao()
            )
        )
    )

    var foods by remember {
        mutableStateOf<List<Food>>(emptyList())
    }

    var selectedFood by remember {
        mutableStateOf<Food?>(null)
    }

    // مقدار واردشده توسط کاربر
    // مثال:
    // 2 عدد
    // 3 کف دست
    // 28 قاشق
    // 150 گرم
    var amount by remember {
        mutableStateOf("1")
    }

    var selectedMeal by remember {
        mutableStateOf(initialMealType)
    }

    var searchQuery by remember {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {

        viewModel.getAllFoods { result ->
            foods = result
        }
    }

    val filteredFoods = remember(
        foods,
        searchQuery
    ) {

        foods
            .filter {
                it.name.contains(
                    searchQuery,
                    ignoreCase = true
                )
            }
            .distinctBy {
                it.name
            }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        Text(
            text = "🍽 انتخاب غذا",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedTextField(
            value = searchQuery,

            onValueChange = {
                searchQuery = it
            },

            label = {
                Text("جستجوی غذا")
            },

            modifier = Modifier.fillMaxWidth(),

            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 260.dp)
        ) {

            items(
                items = filteredFoods,
                key = { it.id }
            ) { food ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clickable {

                            selectedFood = food

                            amount = "1"
                        },

                    colors = CardDefaults.cardColors(
                        containerColor =
                            if (selectedFood?.id == food.id) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {

                        Text(
                            text = food.name,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text =
                                "${food.caloriesPer100g.toInt()} kcal / 100g"
                        )

                        if (
                            food.servingUnit.isNotBlank() &&
                            food.servingUnit != "گرم" &&
                            food.servingWeightGrams > 0.0 &&
                            food.servingAmount > 0.0
                        ) {

                            val servingAmountText =
                                formatFoodAmount(
                                    food.servingAmount
                                )

                            Text(
                                text =
                                    "$servingAmountText ${food.servingUnit} ≈ " +
                                            "${food.servingWeightGrams.toInt()} گرم",

                                style =
                                    MaterialTheme.typography.bodySmall,

                                color =
                                    MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        if (selectedFood != null) {

            val food = selectedFood!!

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "وعده غذایی",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                MealType.values().forEach { meal ->

                    FilterChip(
                        selected = selectedMeal == meal,

                        onClick = {
                            selectedMeal = meal
                        },

                        label = {

                            Text(
                                when (meal) {

                                    MealType.BREAKFAST ->
                                        "صبحانه"

                                    MealType.LUNCH ->
                                        "ناهار"

                                    MealType.SNACK ->
                                        "میان وعده"

                                    MealType.DINNER ->
                                        "شام"
                                }
                            )
                        }
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            val unit =
                food.servingUnit
                    .ifBlank {
                        "گرم"
                    }

            val servingAmountText =
                formatFoodAmount(
                    food.servingAmount
                )

            val hasValidServing =
                food.servingAmount > 0.0 &&
                        food.servingWeightGrams > 0.0

            OutlinedTextField(
                value = amount,

                onValueChange = {
                    amount = it
                },

                label = {

                    Text(
                        "مقدار ($unit)"
                    )
                },

                supportingText = {

                    if (
                        unit != "گرم" &&
                        hasValidServing
                    ) {

                        Text(
                            "هر $servingAmountText $unit ≈ " +
                                    "${food.servingWeightGrams.toInt()} گرم"
                        )

                    } else {

                        Text(
                            "مقدار را بر حسب گرم وارد کنید"
                        )
                    }
                },

                modifier = Modifier.fillMaxWidth(),

                singleLine = true
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Button(

                onClick = {

                    val enteredAmount =
                        amount
                            .replace(",", ".")
                            .trim()
                            .toDoubleOrNull()

                    if (
                        enteredAmount == null ||
                        enteredAmount <= 0.0
                    ) {
                        return@Button
                    }

                    /*
                     * مهم:
                     *
                     * enteredAmount همان چیزی است که کاربر وارد کرده.
                     *
                     * مثال:
                     *
                     * 2 عدد تخم مرغ
                     * enteredAmount = 2
                     *
                     * 3 کف دست بربری
                     * enteredAmount = 3
                     *
                     * 28 قاشق برنج
                     * enteredAmount = 28
                     *
                     * سپس فقط همین‌جا آن را به گرم تبدیل می‌کنیم.
                     */

                    val amountInGrams: Double

                    if (
                        unit.equals(
                            "گرم",
                            ignoreCase = true
                        )
                    ) {

                        // وقتی واحد گرم است،
                        // مقدار واردشده خودش وزن واقعی است.
                        amountInGrams = enteredAmount

                    } else if (
                        food.servingAmount > 0.0 &&
                        food.servingWeightGrams > 0.0
                    ) {

                        // مثال:
                        //
                        // تخم مرغ:
                        // 1 عدد = 50 گرم
                        //
                        // 2 عدد:
                        // 2 / 1 × 50 = 100 گرم
                        //
                        // بربری:
                        // 1 کف دست = 30 گرم
                        //
                        // 3 کف دست:
                        // 3 / 1 × 30 = 90 گرم

                        val gramsPerUnit =
                            food.servingWeightGrams /
                                    food.servingAmount

                        amountInGrams =
                            enteredAmount *
                                    gramsPerUnit

                    } else {

                        // اگر اطلاعات واحد ناقص باشد،
                        // به عنوان آخرین حالت مقدار واردشده
                        // را همان گرم در نظر می‌گیریم.
                        amountInGrams = enteredAmount
                    }

                    viewModel.addEntry(

                        FoodEntry(

                            date = date,

                            mealType = selectedMeal,

                            foodId = food.id,

                            // فقط وزن واقعی برای محاسبه
                            amount = amountInGrams,

                            // چیزی که کاربر واقعاً وارد کرده
                            originalAmount = enteredAmount,

                            // واحدی که کاربر انتخاب کرده
                            originalUnit = unit
                        )
                    )

                    onDismiss()
                },

                modifier = Modifier.fillMaxWidth(),

                enabled = amount
                    .replace(",", ".")
                    .trim()
                    .toDoubleOrNull()
                    ?.let { it > 0.0 } == true

            ) {

                Text(
                    "ثبت غذا"
                )
            }
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        TextButton(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("بستن")
        }
    }
}

/**
 * نمایش تمیز مقدارهای اعشاری.
 *
 * 1.0  -> 1
 * 2.0  -> 2
 * 0.5  -> 0.5
 * 1.25 -> 1.25
 */
private fun formatFoodAmount(
    value: Double
): String {

    return if (value % 1.0 == 0.0) {
        value.toInt().toString()
    } else {
        value.toString()
            .trimEnd('0')
            .trimEnd('.')
    }
}