package com.example.yekdarsad.viewmodel

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yekdarsad.data.phoneusage.PhoneUsageData
import com.example.yekdarsad.data.phoneusage.PhoneUsageRepository
import com.example.yekdarsad.data.phoneusage.UsagePermission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class PhoneUsageViewModel(
    private val repository: PhoneUsageRepository
) : ViewModel() {


    private val _phoneUsage =
        MutableStateFlow<PhoneUsageData?>(null)


    val phoneUsage: StateFlow<PhoneUsageData?> =
        _phoneUsage



    private val _hasPermission =
        MutableStateFlow(false)


    val hasPermission: StateFlow<Boolean> =
        _hasPermission



    fun refresh(
        context: Context,
        date: String
    ) {

        _hasPermission.value =
            UsagePermission.hasUsageAccess(context)


        if (!_hasPermission.value) {

            _phoneUsage.value = null
            return

        }


        viewModelScope.launch {


            // پاک کردن عدد روز قبل
            _phoneUsage.value =
                PhoneUsageData(
                    screenTimeMinutes = 0,
                    topApps = emptyList()
                )


            val result =
                repository.getScreenTime(date)


            println(
                "VM DATE=$date RESULT=${result.screenTimeMinutes}"
            )


            _phoneUsage.value = result


        }

    }





    fun openUsageAccessSettings(
        context: Context
    ) {


        context.startActivity(

            Intent(
                Settings.ACTION_USAGE_ACCESS_SETTINGS
            )

        )


    }


}