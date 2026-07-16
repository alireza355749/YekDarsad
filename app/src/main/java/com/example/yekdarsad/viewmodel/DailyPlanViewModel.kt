package com.example.yekdarsad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yekdarsad.data.DailyPlan
import com.example.yekdarsad.data.DailyPlanDao
import com.example.yekdarsad.data.DailyPlanWithTask
import com.example.yekdarsad.data.Task
import com.example.yekdarsad.data.TaskDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class DailyPlanViewModel(
    private val dailyPlanDao: DailyPlanDao,
    private val taskDao: TaskDao
) : ViewModel() {

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

                    val totalScore = plans.sumOf { item ->

                        if (item.task.type == "TIME") {

                            item.task.coefficient *
                                    (item.dailyPlan.plannedMinutes / 60.0)

                        } else {

                            item.task.coefficient

                        }

                    }


                    val completedScore = plans.sumOf { item ->

                        if (item.task.type == "TIME") {

                            item.task.coefficient *
                                    (
                                            item.dailyPlan.actualMinutes / 60.0
                                            )

                        } else {

                            if (item.dailyPlan.completed)
                                item.task.coefficient
                            else
                                0.0

                        }

                    }


                    if (totalScore == 0.0) {

                        0

                    } else {

                        ((completedScore / totalScore) * 100)
                            .toInt()

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
        id: Int,
        completed: Boolean
    ) {

        viewModelScope.launch {

            dailyPlanDao.updateCompleted(
                id,
                completed
            )

        }

    }

    fun updateProgress(
        item: DailyPlanWithTask,
        actualMinutes: Int
    ) {

        viewModelScope.launch {

            val planned = item.dailyPlan.plannedMinutes

            val fullScore =
                if (item.task.type == "TIME") {

                    (planned / 60.0) *
                            item.task.coefficient

                } else {

                    item.task.coefficient

                }

            val earnedScore =
                if (item.task.type == "TIME") {

                    fullScore *
                            minOf(
                                actualMinutes.toDouble() / planned,
                                1.0
                            )

                } else {

                    if (actualMinutes > 0)
                        fullScore
                    else
                        0.0

                }

            dailyPlanDao.updateProgress(
                id = item.dailyPlan.id,
                actualMinutes = actualMinutes,
                earnedScore = earnedScore,
                completed = actualMinutes >= planned
            )

        }

    }

    fun deletePlan(
        id: Int
    ) {

        viewModelScope.launch {

            dailyPlanDao.deleteById(id)

        }

    }

}