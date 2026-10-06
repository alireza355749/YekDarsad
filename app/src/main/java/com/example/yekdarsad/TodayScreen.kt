package com.example.yekdarsad

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yekdarsad.data.DatabaseProvider
import com.example.yekdarsad.data.DailyPlanWithTask
import com.example.yekdarsad.ui.bottomsheet.TimeProgressBottomSheet
import com.example.yekdarsad.ui.components.*
import com.example.yekdarsad.ui.todaytracker.TodayTasksSection
import com.example.yekdarsad.ui.todaytracker.TodayTrackerSection
import com.example.yekdarsad.viewmodel.DailyPlanViewModel
import com.example.yekdarsad.viewmodel.DailyPlanViewModelFactory
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun TodayScreen(
    onPhoneClick: (String) -> Unit,
    onNutritionClick: (String) -> Unit,
    onSleepClick: (String) -> Unit,
    onExpenseClick: (String) -> Unit,
    initialDate: LocalDate? = null
) {

    val context = LocalContext.current

    val database =
        DatabaseProvider.getDatabase(context)

    val viewModel: DailyPlanViewModel =
        viewModel(
            factory =
                DailyPlanViewModelFactory(
                    database.dailyPlanDao(),
                    database.taskDao()
                )
        )

    // =====================================================
    // تاریخ انتخاب‌شده
    // =====================================================

    var selectedDate by remember(initialDate) {

        mutableStateOf(

            initialDate
                ?: LocalDate.now(
                    ZoneId.of("Asia/Tehran")
                )

        )

    }

    // =====================================================
    // تقویم
    // =====================================================

    var showCalendar by remember {

        mutableStateOf(false)

    }

    // =====================================================
    // فعالیت انتخاب‌شده برای BottomSheet
    // =====================================================

    var selectedTimeItem by remember {

        mutableStateOf<DailyPlanWithTask?>(null)

    }

    var showTimeBottomSheet by remember {

        mutableStateOf(false)

    }

    // =====================================================
    // اولویت‌های نمایشی
    //
    // داخل ViewModel نگهداری می‌شوند.
    //
    // بنابراین با عوض کردن تب یا رفتن به صفحه دیگر
    // از بین نمی‌روند.
    //
    // اولویت هر روز هم جدا از روزهای دیگر است.
    // =====================================================

    val allPriorityItems by viewModel
        .priorityItems
        .collectAsState()

    val date =
        selectedDate.toString()

    val priorityItems =
        allPriorityItems[date] ?: emptySet()

    // =====================================================
    // Plans
    // =====================================================

    val plans by viewModel
        .getPlans(date)
        .collectAsState(
            initial = emptyList()
        )

    // =====================================================
    // وضعیت ثبت روز
    // =====================================================

    val isRegistered =
        plans.isNotEmpty() &&
                plans.all {

                    it.dailyPlan.dayRegistered

                }

    // =====================================================
    // Progress
    // =====================================================

    val progress by viewModel
        .getTodayProgress(date)
        .collectAsState(
            initial = 0
        )

    // =====================================================
    // Statistics
    // =====================================================

    val totalMinutes =
        plans.sumOf {

            it.dailyPlan.plannedMinutes

        }

    val completedMinutes =
        plans.sumOf {

            it.dailyPlan.actualMinutes

        }

    val totalCoefficient =
        plans.sumOf {

            if (it.task.type == "TIME") {

                (
                        it.dailyPlan.plannedMinutes / 60.0
                        ) *
                        it.task.coefficient

            } else {

                it.task.coefficient

            }

        }

    val completedCoefficient =
        plans.sumOf {

            it.dailyPlan.earnedScore

        }

    // =====================================================
    // UI
    // =====================================================

    Box(

        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.White)

    ) {

        Column(

            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(20.dp)

        ) {

            // =================================================
            // Header
            // =================================================

            TodayHeader(

                selectedDate =
                    selectedDate,

                onPreviousDay = {

                    selectedDate =
                        selectedDate.minusDays(1)

                },

                onNextDay = {

                    selectedDate =
                        selectedDate.plusDays(1)

                },

                onTodayClick = {

                    selectedDate =
                        LocalDate.now(
                            ZoneId.of("Asia/Tehran")
                        )

                },

                onDateClick = {

                    showCalendar =
                        true

                }

            )

            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )

            // =================================================
            // Progress
            // =================================================

            Box(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(150.dp)

            ) {

                AnimatedProgressBar(

                    progress =
                        progress.toFloat()

                )

            }

            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )

            // =================================================
            // Stats
            // =================================================

            TodayStats(

                totalMinutes =
                    totalMinutes,

                completedMinutes =
                    completedMinutes,

                totalCoefficient =
                    totalCoefficient,

                completedCoefficient =
                    completedCoefficient

            )

            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )

            // =================================================
            // Main Content
            // =================================================

            Row(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),

                horizontalArrangement =
                    Arrangement.spacedBy(12.dp)

            ) {

                // =================================================
                // Tasks
                // =================================================

                Box(

                    modifier =
                        Modifier
                            .weight(0.60f)
                            .fillMaxHeight()

                ) {

                    TodayTasksSection(

                        plans =
                            plans,

                        locked =
                            isRegistered,

                        // =================================================
                        // اولویت‌های روز انتخاب‌شده
                        // =================================================

                        priorityItems =
                            priorityItems,

                        // =================================================
                        // Checkbox
                        // =================================================

                        onCheckedChange = { item, checked ->

                            if (!isRegistered) {

                                if (
                                    item.task.type ==
                                    "TIME"
                                ) {

                                    viewModel.updateProgress(

                                        item,

                                        if (checked)
                                            item.dailyPlan.plannedMinutes
                                        else
                                            0

                                    )

                                } else {

                                    viewModel.toggleCompleted(

                                        item,

                                        checked

                                    )

                                }

                            }

                        },

                        // =================================================
                        // باز کردن BottomSheet
                        // =================================================

                        onTimeClick = { item ->

                            if (!isRegistered) {

                                selectedTimeItem =
                                    item

                                showTimeBottomSheet =
                                    true

                            }

                        },

                        // =================================================
                        // Delete
                        // =================================================

                        onDeleteClick = { item ->

                            if (!isRegistered) {

                                viewModel.deletePlan(

                                    item.dailyPlan.id

                                )

                            }

                        },

                        // =================================================
                        // Progress
                        // =================================================

                        onProgressSave = { item, minutes ->

                            if (!isRegistered) {

                                viewModel.updateProgress(

                                    item,

                                    minutes

                                )

                            }

                        },

                        // =================================================
                        // Order
                        // =================================================

                        onOrderChanged = { newList ->

                            if (!isRegistered) {

                                viewModel.updateOrder(
                                    newList
                                )

                            }

                        }

                    )

                }

                // =================================================
                // Tracker
                // =================================================

                Column(

                    modifier =
                        Modifier
                            .weight(0.40f)
                            .fillMaxHeight()
                            .verticalScroll(
                                rememberScrollState()
                            )

                ) {

                    TodayTrackerSection(

                        date =
                            date,

                        onPhoneClick = {

                            onPhoneClick(date)

                        },

                        onNutritionClick = {

                            onNutritionClick(date)

                        },

                        onSleepClick = {

                            onSleepClick(date)

                        },

                        onExpenseClick = {

                            onExpenseClick(date)

                        }

                    )

                    Spacer(
                        modifier =
                            Modifier.height(20.dp)
                    )

                    // =================================================
                    // ثبت / ویرایش روز
                    // =================================================

                    Button(

                        onClick = {

                            if (isRegistered) {

                                viewModel.updateDayRegistered(

                                    date,
                                    false

                                )

                            } else {

                                viewModel.updateDayRegistered(

                                    date,
                                    true

                                )

                            }

                        },

                        colors =
                            ButtonDefaults.buttonColors(

                                containerColor =

                                    if (isRegistered)
                                        Color.Gray
                                    else
                                        Color(0xFF7A9A6D)

                            ),

                        modifier =
                            Modifier.fillMaxWidth()

                    ) {

                        Text(

                            text =

                                if (isRegistered)
                                    "ویرایش روز ✏️"
                                else
                                    "ثبت روز ✅"

                        )

                    }

                }

            }

        }

        // =====================================================
        // یادداشت روز
        // =====================================================

        DailyNoteButton(

            date =
                date,

            dao =
                database.dailyNoteDao()

        )

        // =====================================================
        // Time Progress BottomSheet
        // =====================================================

        if (
            showTimeBottomSheet &&
            selectedTimeItem != null
        ) {

            val selectedId =
                selectedTimeItem!!
                    .dailyPlan
                    .id

            val selectedIsPriority =
                priorityItems.contains(
                    selectedId
                )

            TimeProgressBottomSheet(

                item =
                    selectedTimeItem!!,

                // =================================================
                // وضعیت فعلی اولویت
                // =================================================

                isPriority =
                    selectedIsPriority,

                // =================================================
                // تغییر اولویت
                // =================================================

                onPriorityChange = { isPriority ->

                    if (!isRegistered) {

                        viewModel.setPriority(

                            date = date,

                            id = selectedId,

                            isPriority = isPriority

                        )

                    }

                },

                // =================================================
                // Dismiss
                // =================================================

                onDismiss = {

                    showTimeBottomSheet =
                        false

                },

                // =================================================
                // Save Progress
                // =================================================

                onSave = { minutes ->

                    if (!isRegistered) {

                        viewModel.updateProgress(

                            selectedTimeItem!!,

                            minutes

                        )

                    }

                },

                // =================================================
                // Delete
                // =================================================

                onDelete = {

                    if (!isRegistered) {

                        viewModel.deletePlan(

                            selectedTimeItem!!
                                .dailyPlan
                                .id

                        )

                    }

                    // اولویت نمایشی کارت حذف‌شده هم پاک شود.

                    viewModel.removePriority(

                        date = date,

                        id = selectedId

                    )

                    showTimeBottomSheet =
                        false

                },

                // =================================================
                // Note
                // =================================================

                onNoteSave = { }

            )

        }

        // =====================================================
        // Calendar
        // =====================================================

        if (showCalendar) {

            PersianCalendarDialog(

                selectedDate =
                    selectedDate,

                onDateSelected = {

                    selectedDate =
                        it

                    showCalendar =
                        false

                },

                onDismiss = {

                    showCalendar =
                        false

                }

            )

        }

    }

}