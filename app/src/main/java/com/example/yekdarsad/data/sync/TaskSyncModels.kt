package com.example.yekdarsad.data.sync

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SupabaseTask(
    val id: String? = null,

    @SerialName("user_id")
    val userId: String,

    @SerialName("category_id")
    val categoryId: String,

    val title: String,

    val coefficient: Double,

    val type: String = "TIME",

    @SerialName("order_index")
    val orderIndex: Int = 0,

    @SerialName("calories_per_hour")
    val caloriesPerHour: Double = 0.0,

    @SerialName("local_id")
    val localId: Int? = null,

    @SerialName("local_category_id")
    val localCategoryId: Int? = null,

    @SerialName("deleted_at")
    val deletedAt: String? = null,

    val version: Long = 1,

    @SerialName("updated_at")
    val updatedAt: String? = null
)

@Serializable
data class SupabaseTaskUpdate(
    @SerialName("local_id")
    val localId: Int,

    @SerialName("local_category_id")
    val localCategoryId: Int? = null,

    @SerialName("category_id")
    val categoryId: String,

    val title: String,

    val coefficient: Double,

    val type: String,

    @SerialName("order_index")
    val orderIndex: Int,

    @SerialName("calories_per_hour")
    val caloriesPerHour: Double
)