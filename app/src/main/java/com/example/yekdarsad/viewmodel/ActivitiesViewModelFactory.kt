package com.example.yekdarsad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.yekdarsad.data.CategoryDao


class ActivitiesViewModelFactory(
    private val dao: CategoryDao
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
                dao
            ) as T

        }


        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )

    }

}