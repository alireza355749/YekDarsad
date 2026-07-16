package com.example.yekdarsad.data

import androidx.room.Embedded
import androidx.room.Relation


data class TaskWithDurations(

    @Embedded
    val task: Task,


    @Relation(
        parentColumn = "id",
        entityColumn = "taskId"
    )
    val durations: List<ActivityDuration>

)