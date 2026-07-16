package com.example.yekdarsad.viewmodel

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


    val tasks: StateFlow<List<TaskWithDurations>> = _tasks



    init {
        loadTasks()
    }



    private fun loadTasks() {

        viewModelScope.launch {

            taskDao.getTasksByCategory(categoryId)
                .collect {

                    _tasks.value = it

                }

        }

    }



    fun addTask(
        title: String,
        coefficient: Double,
        type: String
    ) {

        viewModelScope.launch {


            val taskId = taskDao.insert(

                Task(
                    categoryId = categoryId,
                    title = title,
                    coefficient = coefficient,
                    type = type
                )

            )


            if (type == "TIME") {


                listOf(
                    30,
                    60,
                    90,
                    120
                ).forEach {


                    durationDao.insert(

                        ActivityDuration(
                            taskId = taskId.toInt(),
                            minutes = it
                        )

                    )


                }


            }


        }

    }



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



    fun deleteTask(taskId: Int) {

        viewModelScope.launch {


            durationDao.deleteByTask(taskId)


            taskDao.deleteById(taskId)


        }

    }


}