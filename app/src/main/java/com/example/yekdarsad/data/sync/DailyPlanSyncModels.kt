package com.example.yekdarsad.data.sync

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SupabaseDailyPlan(
    val id: String? = null,

    @SerialName("user_id")
    val userId: String,

    @SerialName("task_id")
    val taskId: String,

    @SerialName("local_task_id")
    val localTaskId: Int? = null,

    val date: String,

    @SerialName("planned_minutes")
    val plannedMinutes: Int = 0,

    @SerialName("actual_minutes")
    val actualMinutes: Int = 0,

    @SerialName("earned_score")
    val earnedScore: Double = 0.0,

    val completed: Boolean = false,

    @SerialName("exercise_calories")
    val exerciseCalories: Double = 0.0,

    @SerialName("day_registered")
    val dayRegistered: Boolean = false,

    @SerialName("order_index")
    val orderIndex: Int = 0,

    val note: String = "",

    @SerialName("local_id")
    val localId: Int? = null,

    @SerialName("deleted_at")
    val deletedAt: String? = null,

    val version: Long = 1,

    @SerialName("updated_at")
    val updatedAt: String? = null
)

@Serializable
data class SupabaseDailyPlanUpdate(
    @SerialName("local_id")
    val localId: Int,

    @SerialName("local_task_id")
    val localTaskId: Int? = null,

    @SerialName("task_id")
    val taskId: String,

    val date: String,

    @SerialName("planned_minutes")
    val plannedMinutes: Int,

    @SerialName("actual_minutes")
    val actualMinutes: Int,

    @SerialName("earned_score")
    val earnedScore: Double,

    val completed: Boolean,

    @SerialName("exercise_calories")
    val exerciseCalories: Double,

    @SerialName("day_registered")
    val dayRegistered: Boolean,

    @SerialName("order_index")
    val orderIndex: Int,

    val note: String
)