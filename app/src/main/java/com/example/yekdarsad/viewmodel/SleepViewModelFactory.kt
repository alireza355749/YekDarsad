package com.example.yekdarsad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.yekdarsad.data.sleep.SleepDao

class SleepViewModelFactory(
    private val dao: SleepDao
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(SleepViewModel::class.java)) {

            return SleepViewModel(dao) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}