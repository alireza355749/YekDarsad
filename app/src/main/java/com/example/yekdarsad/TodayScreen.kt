package com.example.yekdarsad

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.yekdarsad.ui.components.AnimatedProgressBar
import com.example.yekdarsad.ui.components.DailyPlanCard
import com.example.yekdarsad.ui.components.EmptyTodayView
import com.example.yekdarsad.ui.components.PersianCalendarDialog
import com.example.yekdarsad.ui.components.TodayHeader
import com.example.yekdarsad.ui.components.TodayStats
import com.example.yekdarsad.viewmodel.DailyPlanViewModel
import com.example.yekdarsad.viewmodel.DailyPlanViewModelFactory
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun TodayScreen() {

    val context = LocalContext.current

    val database = DatabaseProvider.getDatabase(context)

    val viewModel: DailyPlanViewModel = viewModel(
        factory = DailyPlanViewModelFactory(
            database.dailyPlanDao(),
            database.taskDao()
        )
    )

    var selectedDate by remember {
        mutableStateOf(
            LocalDate.now(
                ZoneId.of("Asia/Tehran")
            )
        )
    }

    var showCalendar by remember {
        mutableStateOf(false)
    }

    var selectedTimeItem by remember {
        mutableStateOf<DailyPlanWithTask?>(null)
    }

    var showTimeBottomSheet by remember {
        mutableStateOf(false)
    }

    val date = selectedDate.toString()

    val plans by viewModel
        .getPlans(date)
        .collectAsState(initial = emptyList())

    val progress by viewModel
        .getTodayProgress(date)
        .collectAsState(initial = 0)

    val totalCoefficient = plans.sumOf {

        if (it.task.type == "TIME") {

            (it.dailyPlan.plannedMinutes / 60.0) *
                    it.task.coefficient

        } else {

            it.task.coefficient

        }

    }

    val completedCoefficient =
        plans.sumOf { it.dailyPlan.earnedScore }

    val totalMinutes =
        plans.sumOf { it.dailyPlan.plannedMinutes }

    val completedMinutes =
        plans.sumOf { it.dailyPlan.actualMinutes }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFDD0))
            .padding(20.dp)
    ) {

        TodayHeader(
            selectedDate = selectedDate,
            onPreviousDay = {
                selectedDate = selectedDate.minusDays(1)
            },
            onNextDay = {
                selectedDate = selectedDate.plusDays(1)
            },
            onTodayClick = {
                selectedDate = LocalDate.now(
                    ZoneId.of("Asia/Tehran")
                )
            },
            onDateClick = {
                showCalendar = true
            }
        )

        AnimatedProgressBar(
            progress = progress.toFloat()
        )

        Spacer(
            modifier = Modifier.height(15.dp)
        )

        TodayStats(
            totalMinutes = totalMinutes,
            completedMinutes = completedMinutes,
            totalCoefficient = totalCoefficient,
            completedCoefficient = completedCoefficient
        )

        Spacer(
            modifier = Modifier.height(15.dp)
        )

        if (plans.isEmpty()) {

            EmptyTodayView()

        } else {

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {

                items(plans) { item ->
                    DailyPlanCard(

                        item = item,

                        onCheckedChange = { checked ->

                            if (item.task.type == "TIME") {

                                if (checked) {

                                    viewModel.updateProgress(
                                        item,
                                        item.dailyPlan.plannedMinutes
                                    )

                                } else {

                                    viewModel.updateProgress(
                                        item,
                                        0
                                    )

                                }

                            } else {

                                viewModel.toggleCompleted(
                                    item.dailyPlan.id,
                                    checked
                                )

                            }

                        },

                        onTimeClick = {

                            selectedTimeItem = item
                            showTimeBottomSheet = true

                        },

                        onDeleteClick = {

                            viewModel.deletePlan(
                                item.dailyPlan.id
                            )

                        },

                        onProgressSave = { minutes ->

                            viewModel.updateProgress(
                                item,
                                minutes
                            )

                        }

                    )

                }

            }

        }

        if (
            showTimeBottomSheet &&
            selectedTimeItem != null
        ) {

            TimeProgressBottomSheet(

                item = selectedTimeItem!!,

                onDismiss = {

                    showTimeBottomSheet = false

                },

                onSave = { minutes ->

                    viewModel.updateProgress(
                        selectedTimeItem!!,
                        minutes
                    )

                }

            )

        }

        if (showCalendar) {

            PersianCalendarDialog(

                selectedDate = selectedDate,

                onDateSelected = {

                    selectedDate = it
                    showCalendar = false

                },

                onDismiss = {

                    showCalendar = false

                }

            )

        }

    }

}