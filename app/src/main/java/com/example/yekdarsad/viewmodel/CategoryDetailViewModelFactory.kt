package com.example.yekdarsad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.yekdarsad.data.ActivityDurationDao
import com.example.yekdarsad.data.DailyPlanDao
import com.example.yekdarsad.data.TaskDao


class CategoryDetailViewModelFactory(
    private val taskDao: TaskDao,
    private val durationDao: ActivityDurationDao,
    private val dailyPlanDao: DailyPlanDao,
    private val categoryId: Int
) : ViewModelProvider.Factory {


    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {


        if (
            modelClass.isAssignableFrom(
                CategoryDetailViewModel::class.java
            )
        ) {


            return CategoryDetailViewModel(
                taskDao,
                durationDao,
                dailyPlanDao,
                categoryId
            ) as T


        }


        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )

    }

}