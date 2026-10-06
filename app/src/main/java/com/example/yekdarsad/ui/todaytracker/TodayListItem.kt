package com.example.yekdarsad.ui.todaytracker

import com.example.yekdarsad.data.DailyPlanWithTask


sealed class TodayListItem {

    data class Task(
        val item: DailyPlanWithTask
    ) : TodayListItem()


    object PriorityDivider : TodayListItem()

}