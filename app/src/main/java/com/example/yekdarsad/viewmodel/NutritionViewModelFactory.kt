package com.example.yekdarsad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.yekdarsad.data.nutrition.NutritionRepository


class NutritionViewModelFactory(
    private val repository: NutritionRepository
) : ViewModelProvider.Factory {


    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {


        if (modelClass.isAssignableFrom(NutritionViewModel::class.java)) {

            @Suppress("UNCHECKED_CAST")
            return NutritionViewModel(
                repository
            ) as T

        }


        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )

    }

}