package com.example.yekdarsad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yekdarsad.data.statistics.StatisticsData
import com.example.yekdarsad.data.statistics.StatisticsPeriod
import com.example.yekdarsad.data.statistics.StatisticsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class StatisticsViewModel(
    private val repository: StatisticsRepository
) : ViewModel() {

    // =====================================================
    // حالت فعلی آمار
    // =====================================================

    private val _period =
        MutableStateFlow(
            StatisticsPeriod.WEEKLY
        )

    val period:
            StateFlow<StatisticsPeriod> =
        _period.asStateFlow()

    // =====================================================
    // آمار
    // =====================================================

    val statistics:
            StateFlow<StatisticsData> =

        _period
            .flatMapLatest { selectedPeriod ->

                repository.getStatistics(
                    period = selectedPeriod
                )
            }
            .stateIn(
                scope =
                    viewModelScope,

                started =
                    SharingStarted
                        .WhileSubscribed(
                            5000
                        ),

                initialValue =
                    StatisticsData()
            )

    // =====================================================
    // تغییر حالت آمار
    // =====================================================

    fun setPeriod(
        period: StatisticsPeriod
    ) {

        _period.value =
            period
    }

    // =====================================================
    // حالت هفتگی
    // =====================================================

    fun showWeekly() {

        _period.value =
            StatisticsPeriod.WEEKLY
    }

    // =====================================================
    // حالت ماهانه
    // =====================================================

    fun showMonthly() {

        _period.value =
            StatisticsPeriod.MONTHLY
    }
}