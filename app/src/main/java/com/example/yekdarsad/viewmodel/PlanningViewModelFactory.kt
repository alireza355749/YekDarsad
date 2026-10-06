package com.example.yekdarsad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.yekdarsad.data.DailyPlanDao


class PlanningViewModelFactory(
    private val dailyPlanDao: DailyPlanDao
) : ViewModelProvider.Factory {


    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {


        if (
            modelClass.isAssignableFrom(
                PlanningViewModel::class.java
            )
        ) {


            return PlanningViewModel(
                dailyPlanDao
            ) as T


        }


        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )

    }

}