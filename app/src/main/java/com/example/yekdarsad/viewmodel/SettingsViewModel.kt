package com.example.yekdarsad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yekdarsad.ui.settings.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: SettingsRepository
) : ViewModel() {

    val allowPastPlanning: StateFlow<Boolean> =
        repository.allowPastPlanning
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = false
            )

    fun setAllowPastPlanning(
        enabled: Boolean
    ) {
        viewModelScope.launch {
            repository.setAllowPastPlanning(
                enabled
            )
        }
    }
}