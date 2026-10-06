package com.example.yekdarsad.data.sync

import android.util.Log
import com.example.yekdarsad.data.expense.ExpenseDao
import com.example.yekdarsad.data.expense.ExpenseEntry
import com.example.yekdarsad.data.expense.ExpenseType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

class ExpenseSyncRepository(
    private val expenseDao: ExpenseDao
) {

    private val supabase = SupabaseClientProvider.client
    private val tag = "ExpenseSync"

    suspend fun syncExpenses() {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: throw IllegalStateException(
                "برای همگام‌سازی مخارج ابتدا وارد حساب کاربری شوید."
            )

        Log.d(tag, "Sync started")

        val localEntries = expenseDao.getAllEntriesForSync()

        val cloudEntries = supabase
            .from("expense_entries")
            .select {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList<SupabaseExpenseEntry>()

        val cloudById = cloudEntries
            .mapNotNull { entry ->
                entry.cloudId?.let { it to entry }
            }
            .toMap()

        val localByCloudId = localEntries
            .mapNotNull { entry ->
                entry.cloudId?.let { it to entry }
            }
            .toMap()

        // دریافت تغییرات Cloud و مقایسه زمان آخرین ویرایش
        for (cloud in cloudEntries) {
            val cloudId = cloud.cloudId ?: continue
            val local = localByCloudId[cloudId]

            if (local == null) {
                downloadEntry(cloud)
                continue
            }

            val cloudUpdatedAt = parseTimestamp(cloud.updatedAt)
            val localUpdatedAt = local.updatedAt

            when {
                localUpdatedAt > cloudUpdatedAt -> {
                    uploadEntry(
                        local = local,
                        userId = userId,
                        existingCloud = cloud
                    )
                }

                cloudUpdatedAt > localUpdatedAt -> {
                    applyCloudEntry(local, cloud)
                }

                cloud.deletedAt != null && local.deletedAt == null -> {
                    // در زمان برابر، حذف Cloud برنده است.
                    applyCloudEntry(local, cloud)
                }
            }
        }

        // ارسال رکوردهای جدید محلی یا رکوردهایی که در Cloud نیستند
        for (local in localEntries) {
            val cloudId = local.cloudId

            if (cloudId == null || cloudById[cloudId] == null) {
                uploadEntry(
                    local = local,
                    userId = userId,
                    existingCloud = null
                )
            }
        }

        Log.d(tag, "Sync finished")
    }

    private suspend fun downloadEntry(
        cloud: SupabaseExpenseEntry
    ) {
        val cloudId = cloud.cloudId ?: return

        expenseDao.insert(
            ExpenseEntry(
                cloudId = cloudId,
                date = cloud.date,
                type = parseExpenseType(cloud.type),
                amount = cloud.amount,
                category = cloud.category,
                title = cloud.title,
                description = cloud.description,
                createdAt = parseTimestamp(cloud.createdAt),
                updatedAt = parseTimestamp(cloud.updatedAt),
                deletedAt = cloud.deletedAt?.let(::parseTimestamp)
            )
        )

        Log.d(tag, "Downloaded expense: $cloudId")
    }

    private suspend fun applyCloudEntry(
        local: ExpenseEntry,
        cloud: SupabaseExpenseEntry
    ) {
        val cloudId = cloud.cloudId ?: return

        expenseDao.insert(
            local.copy(
                cloudId = cloudId,
                date = cloud.date,
                type = parseExpenseType(cloud.type),
                amount = cloud.amount,
                category = cloud.category,
                title = cloud.title,
                description = cloud.description,
                createdAt = parseTimestamp(cloud.createdAt),
                updatedAt = parseTimestamp(cloud.updatedAt),
                deletedAt = cloud.deletedAt?.let(::parseTimestamp)
            )
        )

        Log.d(tag, "Applied cloud changes: $cloudId")
    }

    private suspend fun uploadEntry(
        local: ExpenseEntry,
        userId: String,
        existingCloud: SupabaseExpenseEntry?
    ) {
        val cloudId = local.cloudId ?: UUID.randomUUID().toString()

        // برای رکوردهای قدیمی که updatedAt آن‌ها صفر است،
        // زمان فعلی به‌عنوان زمان ویرایش ثبت می‌شود.
        val updatedAt = maxOf(
            local.updatedAt,
            System.currentTimeMillis()
        )

        val payload = SupabaseExpensePayload(
            cloudId = cloudId,
            userId = userId,
            date = local.date,
            type = local.type.name,
            amount = local.amount,
            category = local.category,
            title = local.title,
            description = local.description,
            createdAt = timestampString(local.createdAt),
            updatedAt = timestampString(updatedAt),
            deletedAt = local.deletedAt?.let(::timestampString)
        )

        if (existingCloud == null) {
            supabase
                .from("expense_entries")
                .insert(payload)
        } else {
            supabase
                .from("expense_entries")
                .update(payload) {
                    filter {
                        eq("cloud_id", cloudId)
                        eq("user_id", userId)
                    }
                }
        }

        // شناسه Cloud را در Room ذخیره می‌کنیم تا در سینک بعدی
        // همان رکورد شناسایی شود و رکورد تکراری نسازیم.
        expenseDao.insert(
            local.copy(
                cloudId = cloudId,
                updatedAt = updatedAt
            )
        )

        Log.d(tag, "Uploaded expense: $cloudId")
    }

    private fun parseExpenseType(value: String): ExpenseType {
        return runCatching {
            ExpenseType.valueOf(value)
        }.getOrDefault(ExpenseType.EXPENSE)
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
    private data class SupabaseExpenseEntry(
        val id: Long? = null,

        @SerialName("user_id")
        val userId: String? = null,

        @SerialName("cloud_id")
        val cloudId: String? = null,

        val date: String,

        val type: String,

        val amount: Long,

        val category: String,

        val title: String,

        val description: String = "",

        @SerialName("created_at")
        val createdAt: String? = null,

        @SerialName("updated_at")
        val updatedAt: String? = null,

        @SerialName("deleted_at")
        val deletedAt: String? = null
    )

    @Serializable
    private data class SupabaseExpensePayload(
        @SerialName("cloud_id")
        val cloudId: String,

        @SerialName("user_id")
        val userId: String,

        val date: String,

        val type: String,

        val amount: Long,

        val category: String,

        val title: String,

        val description: String,

        @SerialName("created_at")
        val createdAt: String,

        @SerialName("updated_at")
        val updatedAt: String,

        @SerialName("deleted_at")
        val deletedAt: String?
    )
}