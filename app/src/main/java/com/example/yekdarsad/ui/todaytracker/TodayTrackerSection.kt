package com.example.yekdarsad.ui.todaytracker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yekdarsad.data.DatabaseProvider
import com.example.yekdarsad.data.expense.ExpenseRepository
import com.example.yekdarsad.data.nutrition.NutritionRepository
import com.example.yekdarsad.data.phoneusage.PhoneUsageRepository
import com.example.yekdarsad.viewmodel.ExpenseViewModel
import com.example.yekdarsad.viewmodel.ExpenseViewModelFactory
import com.example.yekdarsad.viewmodel.NutritionSummary
import com.example.yekdarsad.viewmodel.NutritionViewModel
import com.example.yekdarsad.viewmodel.NutritionViewModelFactory
import com.example.yekdarsad.viewmodel.PhoneUsageViewModel
import com.example.yekdarsad.viewmodel.PhoneUsageViewModelFactory
import com.example.yekdarsad.viewmodel.SleepViewModel
import com.example.yekdarsad.viewmodel.SleepViewModelFactory
import com.example.yekdarsad.viewmodel.TrackerViewModel
import com.example.yekdarsad.viewmodel.TrackerViewModelFactory

@Composable
fun TodayTrackerSection(
    date: String,
    onPhoneClick: () -> Unit,
    onNutritionClick: () -> Unit,
    onSleepClick: () -> Unit,
    onExpenseClick: () -> Unit
) {

    val context = LocalContext.current

    val database = remember {
        DatabaseProvider.getDatabase(context)
    }

    // =====================================================
    // Tracker
    // =====================================================

    val trackerViewModel: TrackerViewModel = viewModel(
        factory = TrackerViewModelFactory(
            database.trackerDao()
        )
    )

    val entries by trackerViewModel
        .getEntries(date)
        .collectAsState(initial = emptyList())

    // =====================================================
    // Nutrition
    // =====================================================

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

    val nutritionSummary by nutritionViewModel
        .getSummary(date)
        .collectAsState(
            initial = NutritionSummary()
        )

    // =====================================================
    // Phone Usage
    // =====================================================

    val phoneUsageViewModel: PhoneUsageViewModel = viewModel(
        factory = PhoneUsageViewModelFactory(
            remember {
                PhoneUsageRepository(
                    context,
                    database.phoneUsageDao()
                )
            }
        )
    )

    val phoneUsage by phoneUsageViewModel
        .phoneUsage
        .collectAsState()

    LaunchedEffect(date) {
        phoneUsageViewModel.refresh(
            context,
            date
        )
    }

    // =====================================================
    // Sleep
    // =====================================================

    val sleepViewModel: SleepViewModel = viewModel(
        factory = SleepViewModelFactory(
            database.sleepDao()
        )
    )

    val sleepEntry by sleepViewModel
        .getEntry(date)
        .collectAsState(initial = null)

    val sleepMinutes =
        sleepViewModel.getTotalSleepMinutes(
            sleepEntry
        )

    // =====================================================
    // Expense
    // =====================================================

    val expenseViewModel: ExpenseViewModel = viewModel(
        factory = ExpenseViewModelFactory(
            remember {
                ExpenseRepository(
                    database.expenseDao()
                )
            }
        )
    )

    val totalExpenses by expenseViewModel
        .getTotalExpensesForDate(date)
        .collectAsState(initial = 0L)

    // =====================================================
    // Water
    // =====================================================

    val water =
        entries.firstOrNull {
            it.type == "WATER"
        }?.value ?: 0.0

    // =====================================================
    // UI
    // =====================================================

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        // -------------------------------------------------
        // Sleep + Phone
        // -------------------------------------------------

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            SmallTrackerCard(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        onSleepClick()
                    },
                icon = Icons.Default.Bedtime,
                value = formatSleepTime(
                    sleepMinutes
                ),
                color = Color(0xFFEDEAF7)
            )

            SmallTrackerCard(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        onPhoneClick()
                    },
                icon = Icons.Default.PhoneAndroid,
                value = formatPhoneTime(
                    (phoneUsage?.screenTimeMinutes ?: 0).toLong()
                ),
                color = Color(0xFFE8F3FF)
            )
        }

        // -------------------------------------------------
        // Nutrition + Expense
        // -------------------------------------------------

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            SmallTrackerCard(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        onNutritionClick()
                    },
                icon = Icons.Default.LocalFireDepartment,
                value = "${nutritionSummary.calories.toInt()} kcal",
                color = Color(0xFFFFF0DD)
            )

            SmallTrackerCard(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        onExpenseClick()
                    },
                icon = Icons.Default.Payments,
                value = formatExpense(
                    totalExpenses
                ),
                color = Color(0xFFE8F0E5)
            )
        }

        // -------------------------------------------------
        // Water
        // -------------------------------------------------

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE4F1E1)
            )
        ) {

            Column(
                modifier = Modifier.padding(12.dp)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = null,
                        tint = Color(0xFF496A42),
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Text(
                        text = "${water.toInt()} / 6",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                WaterTracker(
                    glasses = water.toInt(),

                    onAdd = {
                        if (water < 6) {
                            trackerViewModel.save(
                                date,
                                "WATER",
                                water + 1
                            )
                        }
                    },

                    onRemove = {
                        if (water > 0) {
                            trackerViewModel.save(
                                date,
                                "WATER",
                                water - 1
                            )
                        }
                    }
                )
            }
        }
    }
}

// =========================================================
// Sleep formatting
// =========================================================

private fun formatSleepTime(
    minutes: Int
): String {

    if (minutes <= 0) {
        return "ثبت نشده"
    }

    val hours = minutes / 60
    val remaining = minutes % 60

    return when {

        hours > 0 && remaining > 0 ->
            "$hours ساعت\n$remaining دقیقه"

        hours > 0 ->
            "$hours ساعت"

        else ->
            "$remaining دقیقه"
    }
}

// =========================================================
// Phone formatting
// =========================================================

private fun formatPhoneTime(
    minutes: Long
): String {

    val hours = minutes / 60
    val remaining = minutes % 60

    return when {

        hours > 0 && remaining > 0 ->
            "${hours} ساعت\n${remaining} دقیقه"

        hours > 0 ->
            "${hours} ساعت"

        else ->
            "${remaining} دقیقه"
    }
}

// =========================================================
// Expense formatting
// =========================================================

private fun formatExpense(
    amount: Long
): String {

    if (amount <= 0) {
        return "۰ تومان"
    }

    return "%,d تومان".format(
        java.util.Locale.US,
        amount
    )
}

// =========================================================
// Small Tracker Card
// =========================================================

@Composable
private fun SmallTrackerCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    value: String,
    color: Color
) {

    Card(
        modifier = modifier
            .height(95.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF496A42),
                modifier = Modifier.size(24.dp)
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}