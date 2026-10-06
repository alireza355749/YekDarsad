package com.example.yekdarsad.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yekdarsad.data.DailyPlan
import com.example.yekdarsad.data.DailyPlanDao
import com.example.yekdarsad.data.DailyPlanWithTask
import com.example.yekdarsad.data.Task
import com.example.yekdarsad.data.TaskDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DailyPlanViewModel(
    private val dailyPlanDao: DailyPlanDao,
    private val taskDao: TaskDao
) : ViewModel() {

    private val _priorityItems =
        MutableStateFlow<Map<String, Set<Int>>>(emptyMap())

    val priorityItems: StateFlow<Map<String, Set<Int>>> =
        _priorityItems.asStateFlow()

    fun setPriority(
        date: String,
        id: Int,
        isPriority: Boolean
    ) {
        _priorityItems.update { current ->
            val currentDateItems =
                current[date] ?: emptySet()

            val updatedDateItems =
                if (isPriority) {
                    currentDateItems + id
                } else {
                    currentDateItems - id
                }

            if (updatedDateItems.isEmpty()) {
                current - date
            } else {
                current + (date to updatedDateItems)
            }
        }
    }

    fun removePriority(
        date: String,
        id: Int
    ) {
        _priorityItems.update { current ->
            val currentDateItems =
                current[date] ?: emptySet()

            val updatedDateItems =
                currentDateItems - id

            if (updatedDateItems.isEmpty()) {
                current - date
            } else {
                current + (date to updatedDateItems)
            }
        }
    }

    fun getPlans(
        date: String
    ): Flow<List<DailyPlanWithTask>> {
        return dailyPlanDao.getByDate(date)
    }

    fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks()
    }

    fun getTodayProgress(
        date: String
    ): Flow<Int> {
        return dailyPlanDao
            .getByDate(date)
            .map { plans ->

                if (plans.isEmpty()) {
                    0
                } else {

                    val totalScore =
                        plans.sumOf { item ->

                            if (item.task.type == "TIME") {
                                item.task.coefficient *
                                        (
                                                item.dailyPlan.plannedMinutes /
                                                        60.0
                                                )
                            } else {
                                item.task.coefficient
                            }
                        }

                    val completedScore =
                        plans.sumOf { item ->

                            if (item.task.type == "TIME") {
                                item.task.coefficient *
                                        (
                                                item.dailyPlan.actualMinutes /
                                                        60.0
                                                )
                            } else {
                                if (item.dailyPlan.completed) {
                                    item.task.coefficient
                                } else {
                                    0.0
                                }
                            }
                        }

                    if (totalScore == 0.0) {
                        0
                    } else {
                        (
                                completedScore /
                                        totalScore *
                                        100
                                ).toInt()
                    }
                }
            }
    }

    fun addPlan(
        plan: DailyPlan
    ) {
        viewModelScope.launch {
            dailyPlanDao.insert(plan)
        }
    }

    fun toggleCompleted(
        item: DailyPlanWithTask,
        completed: Boolean
    ) {
        viewModelScope.launch {

            val isHamoom = item.task.title == "حموم"

            if (isHamoom) {
                Log.d(
                    "HAMOOM_TOGGLE",
                    "TOGGLE CALLED: " +
                            "id=${item.dailyPlan.id}, " +
                            "date=${item.dailyPlan.date}, " +
                            "checked=$completed, " +
                            "oldCompleted=${item.dailyPlan.completed}"
                )
            }

            val actualMinutes =
                if (completed) {
                    item.dailyPlan.plannedMinutes
                } else {
                    item.dailyPlan.actualMinutes
                }

            val earnedScore =
                if (completed) {

                    if (item.task.type == "TIME") {
                        (
                                actualMinutes / 60.0
                                ) * item.task.coefficient
                    } else {
                        item.task.coefficient
                    }

                } else {
                    0.0
                }

            val exerciseCalories =
                calculateExerciseCalories(
                    item,
                    actualMinutes
                )

            try {
                dailyPlanDao.updateProgress(
                    id = item.dailyPlan.id,
                    actualMinutes = actualMinutes,
                    earnedScore = earnedScore,
                    completed = completed,
                    exerciseCalories = exerciseCalories
                )

                if (isHamoom) {
                    val savedPlan =
                        dailyPlanDao
                            .getAllPlansOnce()
                            .firstOrNull {
                                it.id == item.dailyPlan.id
                            }

                    Log.d(
                        "HAMOOM_TOGGLE",
                        "AFTER DATABASE UPDATE: " +
                                "id=${item.dailyPlan.id}, " +
                                "requestedCompleted=$completed, " +
                                "savedCompleted=${savedPlan?.completed}, " +
                                "foundInDatabase=${savedPlan != null}"
                    )
                }

            } catch (e: Exception) {
                Log.e(
                    "HAMOOM_TOGGLE",
                    "UPDATE FAILED: " +
                            "id=${item.dailyPlan.id}, " +
                            "checked=$completed",
                    e
                )
            }
        }
    }

    fun updateProgress(
        item: DailyPlanWithTask,
        actualMinutes: Int
    ) {
        viewModelScope.launch {

            val planned =
                item.dailyPlan.plannedMinutes

            val fullScore =
                if (item.task.type == "TIME") {
                    (
                            planned / 60.0
                            ) * item.task.coefficient
                } else {
                    item.task.coefficient
                }

            val earnedScore =
                if (item.task.type == "TIME") {

                    if (planned > 0) {
                        fullScore *
                                minOf(
                                    actualMinutes.toDouble() /
                                            planned,
                                    1.0
                                )
                    } else {
                        0.0
                    }

                } else {

                    if (actualMinutes > 0) {
                        fullScore
                    } else {
                        0.0
                    }
                }

            val exerciseCalories =
                calculateExerciseCalories(
                    item,
                    actualMinutes
                )

            dailyPlanDao.updateProgress(
                id = item.dailyPlan.id,
                actualMinutes = actualMinutes,
                earnedScore = earnedScore,
                completed = actualMinutes > 0,
                exerciseCalories = exerciseCalories
            )
        }
    }

    private fun calculateExerciseCalories(
        item: DailyPlanWithTask,
        actualMinutes: Int
    ): Double {

        if (actualMinutes <= 0) {
            return 0.0
        }

        if (item.task.caloriesPerHour <= 0.0) {
            return 0.0
        }

        return item.task.caloriesPerHour *
                actualMinutes /
                60.0
    }

    fun deletePlan(
        id: Int
    ) {
        viewModelScope.launch {
            dailyPlanDao.markDeleted(id)
        }
    }

    fun updateOrder(
        items: List<DailyPlanWithTask>
    ) {
        viewModelScope.launch {

            items.forEachIndexed { index, item ->

                dailyPlanDao.updateOrder(
                    id = item.dailyPlan.id,
                    orderIndex = index
                )
            }
        }
    }

    fun updateDayRegistered(
        date: String,
        registered: Boolean
    ) {
        viewModelScope.launch {
            dailyPlanDao.updateDayRegistered(
                date,
                registered
            )
        }
    }

    fun isDayRegistered(
        date: String
    ): Flow<Boolean> {
        return dailyPlanDao
            .isDayRegistered(date)
            .map { it }
    }
}