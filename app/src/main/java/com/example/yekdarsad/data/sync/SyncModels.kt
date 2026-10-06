package com.example.yekdarsad.data.sync

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SupabaseCategory(
    val id: String? = null,

    @SerialName("user_id")
    val userId: String,

    val name: String,

    val icon: String,

    @SerialName("local_id")
    val localId: Int? = null,

    @SerialName("deleted_at")
    val deletedAt: String? = null,

    val version: Long = 1,

    @SerialName("updated_at")
    val updatedAt: String? = null
)

@Serializable
data class SupabaseCategoryUpdate(
    @SerialName("local_id")
    val localId: Int,

    val name: String,

    val icon: String
)

@Serializable
data class SupabaseSyncChange(
    val id: Long,

    @SerialName("user_id")
    val userId: String,

    @SerialName("entity_type")
    val entityType: String,

    @SerialName("entity_id")
    val entityId: String,

    val operation: String,

    val version: Long,

    @SerialName("changed_at")
    val changedAt: String
)

/**
 * Conflict واقعی بین وضعیت محلی Android و وضعیت Cloud.
 *
 * این جدول فقط زمانی استفاده می‌شود که تغییرات Android
 * و Web روی یک رکورد با هم تداخل داشته باشند.
 *
 * baseSnapshot:
 * آخرین وضعیتی که Android و Cloud روی آن با هم Sync بوده‌اند.
 *
 * localSnapshot:
 * وضعیت فعلی Android.
 *
 * remoteSnapshot:
 * وضعیت فعلی Supabase.
 *
 * conflictingFields:
 * نام فیلدهایی که واقعاً با هم Conflict دارند.
 *
 * Snapshotها به صورت JSON ذخیره می‌شوند تا بتوانیم
 * بعداً بدون تغییر Schema اطلاعات کامل هر سه وضعیت
 * را برای UI نمایش دهیم.
 */
@Entity(tableName = "sync_conflicts")
data class SyncConflict(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // category / task / daily_plan
    val entityType: String,

    // cloud UUID رکورد
    val entityId: String,

    // آخرین وضعیت مشترک Android و Cloud
    val baseSnapshot: String,

    // وضعیت فعلی Android
    val localSnapshot: String,

    // وضعیت فعلی Cloud
    val remoteSnapshot: String,

    // JSON array مثل:
    // ["title","coefficient"]
    val conflictingFields: String,

    val createdAt: Long = System.currentTimeMillis(),

    // false = هنوز حل نشده
    // true = کاربر Conflict را حل کرده
    val resolved: Boolean = false,

    val resolvedAt: Long? = null
)