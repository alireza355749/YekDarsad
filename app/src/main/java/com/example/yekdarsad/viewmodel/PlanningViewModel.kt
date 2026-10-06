package com.example.yekdarsad.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yekdarsad.data.DailyPlanDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate

class PlanningViewModel(
    private val dailyPlanDao: DailyPlanDao
) : ViewModel() {

    var planningStarted by mutableStateOf(false)
        private set

    var wizardStep by mutableStateOf(1)
        private set

    var selectedDate by mutableStateOf(LocalDate.now())
        private set

    var planningMode by mutableStateOf(0)
        private set

    var basketAnimationTrigger by mutableStateOf(0)
        private set


    fun selectToday() {
        selectedDate = LocalDate.now()
        wizardStep = 2
    }


    fun selectTomorrow() {
        selectedDate = LocalDate.now().plusDays(1)
        wizardStep = 2
    }


    fun selectCustomDate(date: LocalDate) {
        selectedDate = date
        wizardStep = 2
    }


    /*
     * =====================================================
     * روز قبل
     * =====================================================
     */

    fun selectPreviousDate() {

        selectedDate =
            selectedDate.minusDays(1)

        wizardStep = 2
    }


    /*
     * =====================================================
     * روز بعد
     * =====================================================
     */

    fun selectNextDate() {

        selectedDate =
            selectedDate.plusDays(1)

        wizardStep = 2
    }


    fun selectMode(mode: Int) {
        planningMode = mode
    }


    fun startPlanning() {
        planningStarted = true
    }


    fun reopenWizard() {
        planningStarted = false
        wizardStep = 1
    }


    fun getPlansForDate(
        date: String
    ) = dailyPlanDao.getByDate(date)


    fun getTotalMinutes(
        date: String
    ): Flow<Int> {

        return dailyPlanDao
            .getByDate(date)
            .map { plans ->

                var total = 0

                plans.forEach { plan ->

                    total += plan.dailyPlan.plannedMinutes
                }

                total
            }
    }


    // =====================================================
    // مجموع زمان برنامه‌ریزی‌شده یک Category در یک روز
    // =====================================================

    fun getTotalMinutesForCategory(
        date: String,
        categoryId: Int
    ): Flow<Int> {

        return dailyPlanDao
            .getByDate(date)
            .map { plans ->

                plans
                    .filter { plan ->
                        plan.task.categoryId == categoryId
                    }
                    .sumOf { plan ->
                        plan.dailyPlan.plannedMinutes
                    }
            }
    }


    fun addTask(
        taskId: Int,
        title: String,
        minutes: Int,
        coefficient: Double
    ) {
        basketAnimationTrigger++
    }


    /*
     * =====================================================
     * حذف یک آیتم از سبد
     * =====================================================
     */

    fun deletePlan(
        planId: Int
    ) {

        viewModelScope.launch {

            dailyPlanDao.deleteById(planId)
        }
    }


    fun reset() {

        planningStarted = false

        wizardStep = 1

        planningMode = 0

        selectedDate = LocalDate.now()
    }
}