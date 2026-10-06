package com.example.yekdarsad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.yekdarsad.data.ActivityDurationDao
import com.example.yekdarsad.data.CategoryDao
import com.example.yekdarsad.data.DailyPlanDao
import com.example.yekdarsad.data.DailyTaskDao
import com.example.yekdarsad.data.TaskDao

class ActivitiesViewModelFactory(
    private val categoryDao: CategoryDao,
    private val taskDao: TaskDao,
    private val durationDao: ActivityDurationDao,
    private val dailyTaskDao: DailyTaskDao,
    private val dailyPlanDao: DailyPlanDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                ActivitiesViewModel::class.java
            )
        ) {

            return ActivitiesViewModel(
                categoryDao = categoryDao,
                taskDao = taskDao,
                durationDao = durationDao,
                dailyTaskDao = dailyTaskDao,
                dailyPlanDao = dailyPlanDao
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}