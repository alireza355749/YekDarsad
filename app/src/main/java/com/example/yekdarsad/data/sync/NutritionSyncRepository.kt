package com.example.yekdarsad.data.sync

import android.util.Log
import com.example.yekdarsad.data.nutrition.FoodEntry
import com.example.yekdarsad.data.nutrition.FoodDao
import com.example.yekdarsad.data.nutrition.ManualCalorieEntry
import com.example.yekdarsad.data.nutrition.ManualCalorieDao
import com.example.yekdarsad.data.nutrition.NutritionDao
import com.example.yekdarsad.data.nutrition.MealType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

class NutritionSyncRepository(
    private val foodDao: FoodDao,
    private val nutritionDao: NutritionDao,
    private val manualCalorieDao: ManualCalorieDao
) {

    private val supabase = SupabaseClientProvider.client

    private val tag = "NutritionSync"

    suspend fun syncNutrition() {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: throw IllegalStateException(
                "برای همگام‌سازی تغذیه ابتدا وارد حساب کاربری شوید."
            )

        Log.d(tag, "Sync started")

        syncFoodEntries(userId)
        syncManualCalories(userId)

        Log.d(tag, "Sync finished")
    }

    // =========================================================
    // FOOD ENTRIES
    // =========================================================

    private suspend fun syncFoodEntries(userId: String) {
        val localEntries = nutritionDao.getAllEntriesForSync()

        val cloudEntries = supabase
            .from("food_entries")
            .select {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList<SupabaseFoodEntry>()

        val cloudById = cloudEntries
            .mapNotNull { entry ->
                entry.id?.let { it to entry }
            }
            .toMap()

        val localByCloudId = localEntries
            .mapNotNull { entry ->
                entry.cloudId?.let { it to entry }
            }
            .toMap()

        // ابتدا تغییرات Cloud را دریافت یا با نسخه محلی ادغام می‌کنیم.
        for (cloud in cloudEntries) {
            val cloudId = cloud.id ?: continue
            val local = localByCloudId[cloudId]

            if (local == null) {
                downloadFoodEntry(cloud)
                continue
            }

            val cloudUpdatedAt = parseTimestamp(cloud.updatedAt)
            val localUpdatedAt = local.updatedAt

            if (localUpdatedAt > cloudUpdatedAt) {
                uploadFoodEntry(
                    local = local,
                    userId = userId,
                    existingCloud = cloud
                )
            } else if (cloudUpdatedAt > localUpdatedAt) {
                applyCloudFoodEntry(local, cloud)
            } else {
                // در زمان برابر، وضعیت حذف‌شده را نیز باید حفظ کنیم.
                if (cloud.deletedAt != null && local.deletedAt == null) {
                    applyCloudFoodEntry(local, cloud)
                }
            }
        }

        // رکوردهای محلی جدید یا تغییرکرده‌ای که هنوز در Cloud نیستند.
        for (local in localEntries) {
            val cloudId = local.cloudId

            if (cloudId == null) {
                uploadFoodEntry(
                    local = local,
                    userId = userId,
                    existingCloud = null
                )
                continue
            }

            if (cloudById[cloudId] == null) {
                uploadFoodEntry(
                    local = local,
                    userId = userId,
                    existingCloud = null
                )
            }
        }
    }

    private suspend fun downloadFoodEntry(
        cloud: SupabaseFoodEntry
    ) {
        val cloudId = cloud.id ?: return

        val food = foodDao.getFoodByName(cloud.foodName)
        if (food == null) {
            Log.w(
                tag,
                "Food not found locally; skipping cloud entry: ${cloud.foodName}"
            )
            return
        }

        nutritionDao.insertFoodEntry(
            FoodEntry(
                date = cloud.date,
                mealType = parseMealType(cloud.mealType),
                foodId = food.id,
                amount = cloud.amount,
                originalAmount = cloud.originalAmount,
                originalUnit = cloud.originalUnit,
                createdAt = cloud.createdAt,
                cloudId = cloudId,
                updatedAt = parseTimestamp(cloud.updatedAt),
                deletedAt = cloud.deletedAt?.let(::parseTimestamp)
            )
        )
    }

    private suspend fun applyCloudFoodEntry(
        local: FoodEntry,
        cloud: SupabaseFoodEntry
    ) {
        val food = foodDao.getFoodByName(cloud.foodName)

        if (food == null) {
            Log.w(
                tag,
                "Food not found locally; cannot update entry: ${cloud.foodName}"
            )
            return
        }

        nutritionDao.insertFoodEntry(
            local.copy(
                date = cloud.date,
                mealType = parseMealType(cloud.mealType),
                foodId = food.id,
                amount = cloud.amount,
                originalAmount = cloud.originalAmount,
                originalUnit = cloud.originalUnit,
                createdAt = cloud.createdAt,
                cloudId = cloud.id,
                updatedAt = parseTimestamp(cloud.updatedAt),
                deletedAt = cloud.deletedAt?.let(::parseTimestamp)
            )
        )
    }

    private suspend fun uploadFoodEntry(
        local: FoodEntry,
        userId: String,
        existingCloud: SupabaseFoodEntry?
    ) {
        val food = foodDao.getFoodById(local.foodId)
            ?: throw IllegalStateException(
                "غذای مربوط به رکورد محلی ${local.id} پیدا نشد."
            )

        val cloudId = local.cloudId ?: UUID.randomUUID().toString()
        val now = System.currentTimeMillis()

        val payload = SupabaseFoodEntry(
            id = cloudId,
            userId = userId,
            foodName = food.name,
            date = local.date,
            mealType = local.mealType.name,
            amount = local.amount,
            originalAmount = local.originalAmount,
            originalUnit = local.originalUnit,
            createdAt = local.createdAt,
            updatedAt = timestampString(
                maxOf(local.updatedAt, now)
            ),
            deletedAt = local.deletedAt?.let(::timestampString)
        )

        if (existingCloud == null) {
            supabase
                .from("food_entries")
                .insert(payload)
        } else {
            supabase
                .from("food_entries")
                .update(payload) {
                    filter {
                        eq("id", cloudId)
                        eq("user_id", userId)
                    }
                }
        }

        nutritionDao.insertFoodEntry(
            local.copy(
                cloudId = cloudId,
                updatedAt = maxOf(local.updatedAt, now)
            )
        )
    }

    // =========================================================
    // MANUAL CALORIES
    // =========================================================

    private suspend fun syncManualCalories(userId: String) {
        val localEntries = manualCalorieDao.getAllEntriesForSync()

        val cloudEntries = supabase
            .from("manual_calorie_entries")
            .select {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList<SupabaseManualCalories>()

        val cloudById = cloudEntries
            .mapNotNull { entry ->
                entry.id?.let { it to entry }
            }
            .toMap()

        val localByCloudId = localEntries
            .mapNotNull { entry ->
                entry.cloudId?.let { it to entry }
            }
            .toMap()

        for (cloud in cloudEntries) {
            val cloudId = cloud.id ?: continue
            val local = localByCloudId[cloudId]

            if (local == null) {
                downloadManualCalories(cloud)
                continue
            }

            val cloudUpdatedAt = parseTimestamp(cloud.updatedAt)

            if (local.updatedAt > cloudUpdatedAt) {
                uploadManualCalories(
                    local = local,
                    userId = userId,
                    existingCloud = cloud
                )
            } else if (cloudUpdatedAt > local.updatedAt) {
                applyCloudManualCalories(local, cloud)
            } else if (cloud.deletedAt != null && local.deletedAt == null) {
                applyCloudManualCalories(local, cloud)
            }
        }

        for (local in localEntries) {
            val cloudId = local.cloudId

            if (cloudId == null || cloudById[cloudId] == null) {
                uploadManualCalories(
                    local = local,
                    userId = userId,
                    existingCloud = null
                )
            }
        }
    }

    private suspend fun downloadManualCalories(
        cloud: SupabaseManualCalories
    ) {
        manualCalorieDao.insert(
            ManualCalorieEntry(
                date = cloud.date,
                calories = cloud.calories,
                note = cloud.note,
                createdAt = cloud.createdAt,
                cloudId = cloud.id,
                updatedAt = parseTimestamp(cloud.updatedAt),
                deletedAt = cloud.deletedAt?.let(::parseTimestamp)
            )
        )
    }

    private suspend fun applyCloudManualCalories(
        local: ManualCalorieEntry,
        cloud: SupabaseManualCalories
    ) {
        manualCalorieDao.insert(
            local.copy(
                date = cloud.date,
                calories = cloud.calories,
                note = cloud.note,
                createdAt = cloud.createdAt,
                cloudId = cloud.id,
                updatedAt = parseTimestamp(cloud.updatedAt),
                deletedAt = cloud.deletedAt?.let(::parseTimestamp)
            )
        )
    }

    private suspend fun uploadManualCalories(
        local: ManualCalorieEntry,
        userId: String,
        existingCloud: SupabaseManualCalories?
    ) {
        val cloudId = local.cloudId ?: UUID.randomUUID().toString()
        val now = System.currentTimeMillis()

        val payload = SupabaseManualCalories(
            id = cloudId,
            userId = userId,
            date = local.date,
            calories = local.calories,
            note = local.note,
            createdAt = local.createdAt,
            updatedAt = timestampString(
                maxOf(local.updatedAt, now)
            ),
            deletedAt = local.deletedAt?.let(::timestampString)
        )

        if (existingCloud == null) {
            supabase
                .from("manual_calorie_entries")
                .insert(payload)
        } else {
            supabase
                .from("manual_calorie_entries")
                .update(payload) {
                    filter {
                        eq("id", cloudId)
                        eq("user_id", userId)
                    }
                }
        }

        manualCalorieDao.insert(
            local.copy(
                cloudId = cloudId,
                updatedAt = maxOf(local.updatedAt, now)
            )
        )
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private fun parseMealType(value: String): MealType {
        return runCatching {
            MealType.valueOf(value)
        }.getOrDefault(MealType.SNACK)
    }

    private fun timestampString(epochMillis: Long): String {
        return Instant.ofEpochMilli(epochMillis).toString()
    }

    private fun parseTimestamp(value: String?): Long {
        if (value.isNullOrBlank()) return 0L

        return runCatching {
            Instant.parse(value).toEpochMilli()
        }.getOrDefault(0L)
    }

    @Serializable
    private data class SupabaseFoodEntry(
        val id: String? = null,

        @SerialName("user_id")
        val userId: String? = null,

        @SerialName("food_name")
        val foodName: String,

        val date: String,

        @SerialName("meal_type")
        val mealType: String,

        val amount: Double,

        @SerialName("original_amount")
        val originalAmount: Double,

        @SerialName("original_unit")
        val originalUnit: String,

        @SerialName("created_at")
        val createdAt: Long,

        @SerialName("updated_at")
        val updatedAt: String? = null,

        @SerialName("deleted_at")
        val deletedAt: String? = null
    )

    @Serializable
    private data class SupabaseManualCalories(
        val id: String? = null,

        @SerialName("user_id")
        val userId: String? = null,

        val date: String,

        val calories: Double,

        val note: String = "",

        @SerialName("created_at")
        val createdAt: Long,

        @SerialName("updated_at")
        val updatedAt: String? = null,

        @SerialName("deleted_at")
        val deletedAt: String? = null
    )
}