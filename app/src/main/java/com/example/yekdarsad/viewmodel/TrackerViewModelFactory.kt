package com.example.yekdarsad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.yekdarsad.data.TrackerDao

class TrackerViewModelFactory(

    private val dao: TrackerDao

) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(

        modelClass: Class<T>

    ): T {

        return TrackerViewModel(
            dao
        ) as T

    }

}