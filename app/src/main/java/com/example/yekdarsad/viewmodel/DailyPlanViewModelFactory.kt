package com.example.yekdarsad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.yekdarsad.data.DailyPlanDao
import com.example.yekdarsad.data.TaskDao

class DailyPlanViewModelFactory(
    private val dailyPlanDao: DailyPlanDao,
    private val taskDao: TaskDao
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(DailyPlanViewModel::class.java)) {

            return DailyPlanViewModel(
                dailyPlanDao,
                taskDao
            ) as T

        }

        throw IllegalArgumentException("Unknown ViewModel class")

    }

}