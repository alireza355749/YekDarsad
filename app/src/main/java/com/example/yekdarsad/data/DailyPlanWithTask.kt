package com.example.yekdarsad.data

import androidx.room.Embedded
import androidx.room.Relation


data class DailyPlanWithTask(

    @Embedded
    val dailyPlan: DailyPlan,


    @Relation(
        parentColumn = "taskId",
        entityColumn = "id"
    )
    val task: Task

)