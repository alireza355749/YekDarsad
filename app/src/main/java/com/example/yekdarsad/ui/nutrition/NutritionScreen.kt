package com.example.yekdarsad.ui.nutrition

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yekdarsad.data.DatabaseProvider
import com.example.yekdarsad.data.nutrition.FoodEntryWithFood
import com.example.yekdarsad.data.nutrition.ManualCalorieEntry
import com.example.yekdarsad.data.nutrition.MealType
import com.example.yekdarsad.data.nutrition.NutritionRepository
import com.example.yekdarsad.viewmodel.NutritionSummary
import com.example.yekdarsad.viewmodel.NutritionViewModel
import com.example.yekdarsad.viewmodel.NutritionViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionScreen(
    date: String,
    openAddFoodSheet: Boolean = false,
    initialMealType: MealType = MealType.LUNCH,
    onAddFoodSheetHandled: () -> Unit = {}
) {

    val context = LocalContext.current

    val database = remember {
        DatabaseProvider.getDatabase(context)
    }

    val nutritionViewModel: NutritionViewModel = viewModel(
        factory = NutritionViewModelFactory(
            NutritionRepository(
                database.foodDao(),
                database.nutritionDao(),
                database.manualCalorieDao(),
                database.dailyPlanDao()
            )
        )
    )

    val summary by nutritionViewModel
        .getSummary(date)
        .collectAsState(
            initial = NutritionSummary()
        )

    val entries by nutritionViewModel
        .getEntries(date)
        .collectAsState(
            initial = emptyList()
        )

    val manualCalories by nutritionViewModel
        .getManualCalorieEntries(date)
        .collectAsState(
            initial = emptyList()
        )

    var showAddFoodSheet by remember {
        mutableStateOf(false)
    }

    var showManualCalorieDialog by remember {
        mutableStateOf(false)
    }

    var manualEntryToDelete by remember {
        mutableStateOf<ManualCalorieEntry?>(null)
    }

    val manualCaloriesTotal =
        manualCalories.sumOf {
            it.calories
        }

    // =================================================
    // کالری‌ها
    // =================================================

    val consumedCalories =
        summary.calories

    val exerciseCalories =
        summary.exerciseCalories

    // مهم:
    // اینجا قدر مطلق نداریم.
    // اگر مصرف 300 و ورزش 800 باشد:
    // 300 - 800 = -500
    val netCalories =
        consumedCalories - exerciseCalories

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F6FF))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        // =================================================
        // Header
        // =================================================

        Text(
            text = "🍽 تغذیه",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = date,
            color = Color.Gray
        )

        // =================================================
        // کارت اصلی کالری
        // =================================================

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFEDE7FF)
            )
        ) {

            Column(
                modifier = Modifier.padding(18.dp)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = Color(0xFF6C63FF),
                        modifier = Modifier.size(22.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Text(
                        text = "وضعیت کالری امروز",
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                // -------------------------------------------------
                // سه مقدار کنار هم
                // -------------------------------------------------

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    CalorieStat(
                        title = "دریافت‌شده",
                        value = consumedCalories,
                        unit = "kcal",
                        icon = Icons.Default.Restaurant,
                        color = Color(0xFF6C63FF)
                    )

                    CalorieStat(
                        title = "ورزش",
                        value = exerciseCalories,
                        unit = "kcal",
                        icon = Icons.Default.FitnessCenter,
                        color = Color(0xFF43A047)
                    )

                    CalorieStat(
                        title = "خالص",
                        value = netCalories,
                        unit = "kcal",
                        icon = Icons.Default.Calculate,
                        color = if (netCalories < 0) {
                            Color(0xFF43A047)
                        } else {
                            Color(0xFF6C63FF)
                        }
                    )
                }

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                HorizontalDivider(
                    color = Color(0xFFD8D1EA)
                )

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                Text(
                    text = "هدف روزانه: 2500 kcal",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                LinearProgressIndicator(
                    progress = {
                        (consumedCalories / 2500.0)
                            .toFloat()
                            .coerceIn(0f, 1f)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(9.dp)
                )
            }
        }

        // =================================================
        // مواد مغذی
        // =================================================

        val nutritionItems = listOf(

            NutritionItem(
                "پروتئین",
                summary.protein,
                120.0,
                "g",
                Icons.Default.FitnessCenter
            ),

            NutritionItem(
                "چربی",
                summary.fat,
                70.0,
                "g",
                Icons.Default.LocalFireDepartment
            ),

            NutritionItem(
                "کربوهیدرات",
                summary.carbs,
                300.0,
                "g",
                Icons.Default.BakeryDining
            ),

            NutritionItem(
                "فیبر",
                summary.fiber,
                30.0,
                "g",
                Icons.Default.Grass
            ),

            NutritionItem(
                "کلسیم",
                summary.calcium,
                1000.0,
                "mg",
                Icons.Default.MonitorHeart
            ),

            NutritionItem(
                "آهن",
                summary.iron,
                18.0,
                "mg",
                Icons.Default.Bloodtype
            ),

            NutritionItem(
                "ویتامین A",
                summary.vitaminA,
                900.0,
                "",
                Icons.Default.Visibility
            ),

            NutritionItem(
                "ویتامین B",
                summary.vitaminB,
                1.3,
                "",
                Icons.Default.Science
            ),

            NutritionItem(
                "ویتامین C",
                summary.vitaminC,
                90.0,
                "mg",
                Icons.Default.LocalFlorist
            ),

            NutritionItem(
                "ویتامین D",
                summary.vitaminD,
                15.0,
                "",
                Icons.Default.WbSunny
            )
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            items(nutritionItems) { item ->

                NutritionSmallCard(
                    modifier = Modifier.width(125.dp),
                    title = item.title,
                    current = item.current,
                    target = item.target,
                    unit = item.unit,
                    icon = item.icon
                )
            }
        }

        // =================================================
        // کالری‌های دستی
        // =================================================

        if (manualCalories.isNotEmpty()) {

            ManualCaloriesCard(
                entries = manualCalories,
                totalCalories = manualCaloriesTotal,
                onDelete = {
                    manualEntryToDelete = it
                }
            )
        }

        // =================================================
        // وعده‌ها
        // =================================================

        Text(
            text = "وعده‌ها",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        MealCard(
            title = "🍳 صبحانه",
            foods = entries.filter {
                it.entry.mealType == MealType.BREAKFAST
            },
            onDelete = {
                nutritionViewModel.deleteEntry(it.entry.id)
            }
        )

        MealCard(
            title = "🍛 ناهار",
            foods = entries.filter {
                it.entry.mealType == MealType.LUNCH
            },
            onDelete = {
                nutritionViewModel.deleteEntry(it.entry.id)
            }
        )

        MealCard(
            title = "🥪 میان وعده",
            foods = entries.filter {
                it.entry.mealType == MealType.SNACK
            },
            onDelete = {
                nutritionViewModel.deleteEntry(it.entry.id)
            }
        )

        MealCard(
            title = "🍲 شام",
            foods = entries.filter {
                it.entry.mealType == MealType.DINNER
            },
            onDelete = {
                nutritionViewModel.deleteEntry(it.entry.id)
            }
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        // =================================================
        // افزودن کالری دستی
        // =================================================

        OutlinedButton(
            onClick = {
                showManualCalorieDialog = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(18.dp)
        ) {

            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Text("افزودن کالری دستی")
        }

        // =================================================
        // ثبت غذا
        // =================================================

        Button(
            onClick = {
                showAddFoodSheet = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(18.dp)
        ) {

            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Text("ثبت غذا")
        }

        // =================================================
        // Bottom Sheet غذا
        // =================================================

        if (showAddFoodSheet) {

            ModalBottomSheet(
                onDismissRequest = {

                    showAddFoodSheet = false

                    onAddFoodSheetHandled()
                }
            ) {

                AddFoodBottomSheet(
                    date = date,
                    initialMealType = initialMealType,
                    onDismiss = {

                        showAddFoodSheet = false

                        onAddFoodSheetHandled()
                    }
                )
            }
        }

        // =================================================
        // Dialog کالری دستی
        // =================================================

        if (showManualCalorieDialog) {

            ManualCalorieDialog(
                onDismiss = {
                    showManualCalorieDialog = false
                },
                onSave = { calories, note ->

                    nutritionViewModel.addManualCalories(
                        date = date,
                        calories = calories,
                        note = note
                    )

                    showManualCalorieDialog = false
                }
            )
        }
    }

    // =================================================
    // حذف کالری دستی
    // =================================================

    manualEntryToDelete?.let { entry ->

        AlertDialog(
            onDismissRequest = {
                manualEntryToDelete = null
            },

            title = {
                Text("حذف کالری دستی")
            },

            text = {

                val title =
                    entry.note.ifBlank {
                        "کالری دستی"
                    }

                Text(
                    "«$title» با مقدار ${entry.calories.toInt()} کالری حذف شود؟"
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        nutritionViewModel.deleteManualCalories(
                            entry.id
                        )

                        manualEntryToDelete = null
                    }
                ) {

                    Text("حذف")
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        manualEntryToDelete = null
                    }
                ) {

                    Text("انصراف")
                }
            }
        )
    }
}

// =====================================================
// آیتم آمار کالری
// =====================================================

@Composable
private fun CalorieStat(
    title: String,
    value: Double,
    unit: String,
    icon: ImageVector,
    color: Color
) {

    Column(
        modifier = Modifier.width(92.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(21.dp)
        )

        Spacer(
            modifier = Modifier.height(5.dp)
        )

        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray
        )

        Spacer(
            modifier = Modifier.height(2.dp)
        )

        Text(
            text = "${value.toInt()} $unit",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

// =====================================================
// Manual Calories Card
// =====================================================

@Composable
private fun ManualCaloriesCard(
    entries: List<ManualCalorieEntry>,
    totalCalories: Double,
    onDelete: (ManualCalorieEntry) -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = Color(0xFF6C63FF)
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "کالری دستی",
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "مجموع: ${totalCalories.toInt()} kcal",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            entries.forEach { entry ->

                val title =
                    entry.note.ifBlank {
                        "کالری دستی"
                    }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = {},
                            onLongClick = {
                                onDelete(entry)
                            }
                        )
                        .padding(vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        modifier = Modifier.size(19.dp),
                        tint = Color(0xFFFF8A65)
                    )

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Text(
                        text = title,
                        modifier = Modifier.weight(1f),
                        color = Color.DarkGray
                    )

                    Text(
                        text = "${entry.calories.toInt()} kcal",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "برای حذف، روی مورد نگه دارید",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}

// =====================================================
// Manual Calorie Dialog
// =====================================================

@Composable
private fun ManualCalorieDialog(
    onDismiss: () -> Unit,
    onSave: (
        Double,
        String
    ) -> Unit
) {

    var caloriesText by remember {
        mutableStateOf("")
    }

    var note by remember {
        mutableStateOf("")
    }

    val calories =
        caloriesText
            .replace(",", ".")
            .toDoubleOrNull()

    AlertDialog(

        onDismissRequest = onDismiss,

        title = {
            Text("افزودن کالری دستی")
        },

        text = {

            Column {

                OutlinedTextField(
                    value = caloriesText,

                    onValueChange = {
                        caloriesText = it
                    },

                    modifier = Modifier.fillMaxWidth(),

                    label = {
                        Text("کالری")
                    },

                    placeholder = {
                        Text("مثلاً 300")
                    },

                    singleLine = true
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                OutlinedTextField(
                    value = note,

                    onValueChange = {
                        note = it
                    },

                    modifier = Modifier.fillMaxWidth(),

                    label = {
                        Text("مربوط به چی؟")
                    },

                    placeholder = {
                        Text("مثلاً یک تکه کیک")
                    },

                    singleLine = true
                )
            }
        },

        confirmButton = {

            TextButton(

                enabled =
                    calories != null &&
                            calories > 0.0,

                onClick = {

                    onSave(
                        calories!!,
                        note.trim()
                    )
                }
            ) {

                Text("ثبت")
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text("انصراف")
            }
        }
    )
}

// =====================================================
// Nutrition Item
// =====================================================

private data class NutritionItem(
    val title: String,
    val current: Double,
    val target: Double,
    val unit: String,
    val icon: ImageVector
)

// =====================================================
// Nutrition Small Card
// =====================================================

@Composable
private fun NutritionSmallCard(
    modifier: Modifier = Modifier,
    title: String,
    current: Double,
    target: Double,
    unit: String,
    icon: ImageVector
) {

    val progress =
        if (target > 0.0) {

            (current / target)
                .toFloat()
                .coerceIn(0f, 1f)

        } else {

            0f
        }

    val progressColor = when {

        progress < 0.30f ->
            Color(0xFFE53935)

        progress < 0.70f ->
            Color(0xFFFFB300)

        else ->
            Color(0xFF43A047)
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 8.dp
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF6C63FF),
                modifier = Modifier.size(20.dp)
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = "${current.toInt()} / ${target.toInt()} $unit",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            LinearProgressIndicator(
                progress = {
                    progress
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp),
                color = progressColor,
                trackColor = Color(0xFFE8E8E8)
            )
        }
    }
}

// =====================================================
// Meal Card
// =====================================================

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MealCard(
    title: String,
    foods: List<FoodEntryWithFood>,
    onDelete: (FoodEntryWithFood) -> Unit
) {

    var itemToDelete by remember {
        mutableStateOf<FoodEntryWithFood?>(null)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = title,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            if (foods.isEmpty()) {

                Text(
                    text = "ثبت نشده",
                    color = Color.Gray
                )

            } else {

                foods.forEach { item ->

                    val food =
                        item.food

                    val foodName =
                        food?.name ?: "غذا"

                    // =========================================
                    // مقدار اصلی واردشده توسط کاربر
                    // =========================================

                    val displayAmount =
                        if (
                            item.entry.originalAmount % 1.0 == 0.0
                        ) {

                            item.entry.originalAmount
                                .toInt()
                                .toString()

                        } else {

                            item.entry.originalAmount
                                .toString()
                                .trimEnd('0')
                                .trimEnd('.')
                        }

                    // =========================================
                    // واحد اصلی
                    // =========================================

                    val displayUnit =
                        item.entry.originalUnit
                            .ifBlank {
                                "گرم"
                            }

                    // =========================================
                    // محاسبه کالری همین غذا
                    //
                    // amount همیشه وزن واقعی بر حسب گرم است.
                    //
                    // مثال:
                    // 20 قاشق برنج = 400 گرم
                    //
                    // اگر برنج 130 kcal / 100g باشد:
                    //
                    // 130 × (400 / 100) = 520 kcal
                    // =========================================

                    val itemCalories =
                        if (food != null) {

                            food.caloriesPer100g *
                                    (
                                            item.entry.amount /
                                                    100.0
                                            )

                        } else {

                            0.0
                        }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(

                                onClick = {},

                                onLongClick = {
                                    itemToDelete = item
                                }
                            )
                            .padding(
                                vertical = 8.dp
                            ),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        // =====================================
                        // نام و مقدار
                        // =====================================

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = foodName,
                                fontWeight =
                                    FontWeight.Medium,
                                color =
                                    Color.DarkGray
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(2.dp)
                            )

                            Text(
                                text =
                                    "$displayAmount $displayUnit",

                                style =
                                    MaterialTheme
                                        .typography
                                        .bodySmall,

                                color =
                                    Color.Gray
                            )
                        }

                        Spacer(
                            modifier =
                                Modifier.width(8.dp)
                        )

                        // =====================================
                        // کالری
                        // =====================================

                        Text(
                            text =
                                "${itemCalories.toInt()} kcal",

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                Color(0xFF6C63FF)
                        )
                    }
                }
            }
        }
    }

    // =====================================================
    // حذف غذا
    // =====================================================

    itemToDelete?.let { item ->

        AlertDialog(

            onDismissRequest = {
                itemToDelete = null
            },

            title = {
                Text("حذف غذا")
            },

            text = {
                Text(
                    "«${item.food?.name ?: "غذا"}» حذف شود؟"
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        onDelete(item)

                        itemToDelete = null
                    }
                ) {

                    Text("حذف")
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        itemToDelete = null
                    }
                ) {

                    Text("انصراف")
                }
            }
        )
    }
}