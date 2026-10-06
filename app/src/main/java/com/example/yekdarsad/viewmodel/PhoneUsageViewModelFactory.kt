package com.example.yekdarsad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.yekdarsad.data.phoneusage.PhoneUsageRepository

class PhoneUsageViewModelFactory(
    private val repository: PhoneUsageRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(PhoneUsageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PhoneUsageViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}