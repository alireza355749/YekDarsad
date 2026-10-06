package com.example.yekdarsad.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yekdarsad.data.ActivityDuration
import com.example.yekdarsad.data.ActivityDurationDao
import com.example.yekdarsad.data.DailyPlan
import com.example.yekdarsad.data.DailyPlanDao
import com.example.yekdarsad.data.Task
import com.example.yekdarsad.data.TaskDao
import com.example.yekdarsad.data.TaskWithDurations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoryDetailViewModel(
    private val taskDao: TaskDao,
    private val durationDao: ActivityDurationDao,
    private val dailyPlanDao: DailyPlanDao,
    private val categoryId: Int
) : ViewModel() {

    private val _tasks =
        MutableStateFlow<List<TaskWithDurations>>(emptyList())

    val tasks: StateFlow<List<TaskWithDurations>> =
        _tasks

    // =====================================================
    // Load Tasks
    // =====================================================

    init {
        loadTasks()
    }

    private fun loadTasks() {

        viewModelScope.launch {

            taskDao
                .getTasksByCategory(categoryId)
                .collect { taskList ->

                    Log.d(
                        "TaskUI",
                        "CATEGORY=$categoryId " +
                                "TASKS=${taskList.map { item ->
                                    "id=${item.task.id}, " +
                                            "title='${item.task.title}', " +
                                            "categoryId=${item.task.categoryId}, " +
                                            "deleted=${item.task.deleted}, " +
                                            "type=${item.task.type}, " +
                                            "order=${item.task.orderIndex}"
                                }}"
                    )

                    _tasks.value = taskList
                }
        }
    }

    // =====================================================
    // Add Task
    // =====================================================

    fun addTask(
        title: String,
        coefficient: Double,
        caloriesPerHour: Double = 0.0
    ) {

        viewModelScope.launch {

            val currentOrder =
                _tasks.value.size

            val taskId =
                taskDao.insert(
                    Task(
                        categoryId = categoryId,
                        title = title,
                        coefficient = coefficient,
                        type = "TIME",
                        orderIndex = currentOrder,
                        caloriesPerHour =
                            caloriesPerHour.coerceAtLeast(0.0)
                    )
                )

            listOf(
                30,
                60,
                90,
                120
            ).forEach { minutes ->

                durationDao.insert(
                    ActivityDuration(
                        taskId = taskId.toInt(),
                        minutes = minutes
                    )
                )
            }
        }
    }

    // =====================================================
    // Update Task
    // =====================================================

    fun updateTask(
        taskId: Int,
        title: String,
        coefficient: Double,
        caloriesPerHour: Double? = null
    ) {

        viewModelScope.launch {

            if (caloriesPerHour != null) {

                taskDao.updateTaskWithCalories(
                    taskId = taskId,
                    title = title,
                    coefficient = coefficient,
                    caloriesPerHour =
                        caloriesPerHour.coerceAtLeast(0.0)
                )

            } else {

                taskDao.updateTask(
                    taskId = taskId,
                    title = title,
                    coefficient = coefficient
                )
            }
        }
    }

    // =====================================================
    // Add Plan
    // =====================================================

    fun addPlan(
        taskId: Int,
        date: String,
        minutes: Int
    ) {

        viewModelScope.launch {

            dailyPlanDao.insert(
                DailyPlan(
                    taskId = taskId,
                    date = date,
                    plannedMinutes = minutes
                )
            )
        }
    }

    // =====================================================
    // Delete Task
    // =====================================================

    fun deleteTask(
        taskId: Int
    ) {

        viewModelScope.launch {

            Log.d(
                "CategoryDelete",
                "DELETE START taskId=$taskId"
            )

            val taskBefore =
                taskDao.getById(taskId)

            Log.d(
                "CategoryDelete",
                "BEFORE taskId=$taskId " +
                        "deleted=${taskBefore?.deleted} " +
                        "cloudId=${taskBefore?.cloudId} " +
                        "title=${taskBefore?.title}"
            )

            dailyPlanDao.markDeleted(
                taskId
            )

            durationDao.deleteByTask(
                taskId
            )

            val affectedRows =
                taskDao.markDeleted(
                    taskId
                )

            Log.d(
                "CategoryDelete",
                "TASK markDeleted affectedRows=$affectedRows"
            )

            val taskAfter =
                taskDao.getById(taskId)

            Log.d(
                "CategoryDelete",
                "AFTER taskId=$taskId " +
                        "deleted=${taskAfter?.deleted} " +
                        "cloudId=${taskAfter?.cloudId} " +
                        "title=${taskAfter?.title}"
            )

            Log.d(
                "CategoryDelete",
                "DELETE END taskId=$taskId"
            )
        }
    }

    // =====================================================
    // Update Task Order
    // =====================================================

    fun updateTaskOrder(
        newList: List<TaskWithDurations>
    ) {

        viewModelScope.launch {

            newList.forEachIndexed { index, item ->

                taskDao.updateOrder(
                    taskId = item.task.id,
                    orderIndex = index
                )
            }
        }
    }
}