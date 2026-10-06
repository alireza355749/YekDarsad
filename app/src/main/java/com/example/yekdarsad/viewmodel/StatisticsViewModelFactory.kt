package com.example.yekdarsad.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.yekdarsad.data.statistics.StatisticsRepository


class StatisticsViewModelFactory(

    private val repository: StatisticsRepository

) : ViewModelProvider.Factory {


    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {


        if (modelClass.isAssignableFrom(
                StatisticsViewModel::class.java
            )
        ) {


            return StatisticsViewModel(
                repository
            ) as T


        }


        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )

    }

}