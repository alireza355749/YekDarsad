
package com.example.yekdarsad.data.sync

import android.util.Log
import com.example.yekdarsad.data.sleep.SleepDao
import com.example.yekdarsad.data.sleep.SleepEntry
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

class SleepSyncRepository(
    private val sleepDao: SleepDao
) {

    private val supabase = SupabaseClientProvider.client
    private val tag = "SleepSync"

    suspend fun syncSleep() {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: throw IllegalStateException(
                "برای همگام‌سازی خواب ابتدا وارد حساب کاربری شوید."
            )

        Log.d(tag, "Sync started")

        // 1. دریافت خواب‌های ذخیره‌شده در فضای ابری
        val cloudEntries = supabase
            .from("sleep_entries")
            .select {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList<SupabaseSleepEntry>()

        // 2. دریافت خواب‌های محلی
        val localEntries = sleepDao.getAllEntriesForSync()

        val localByDate = localEntries.associateBy { it.date }
        val cloudByDate = cloudEntries.associateBy { it.date }

        var downloaded = 0
        var uploaded = 0

        // 3. بررسی تمام تاریخ‌های موجود در هر یک از دو طرف
        val allDates = localByDate.keys + cloudByDate.keys

        for (date in allDates) {
            val local = localByDate[date]
            val cloud = cloudByDate[date]

            when {
                // فقط در وب وجود دارد: دریافت در گوشی
                local == null && cloud != null -> {
                    sleepDao.insert(cloud.toLocalEntry())
                    downloaded++
                }

                // فقط در گوشی وجود دارد: ارسال به وب
                local != null && cloud == null -> {
                    upload(userId, local)
                    uploaded++
                }

                // در هر دو طرف وجود دارد: نسخه جدیدتر انتخاب شود
                local != null && cloud != null -> {
                    val cloudUpdatedAt = cloud.updatedAtMillis()

                    if (cloudUpdatedAt > local.updatedAt) {
                        sleepDao.insert(cloud.toLocalEntry())
                        downloaded++
                    } else if (local.updatedAt > cloudUpdatedAt) {
                        upload(userId, local)
                        uploaded++
                    }
                }
            }
        }

        Log.d(
            tag,
            "Sync finished: downloaded=$downloaded, uploaded=$uploaded"
        )
    }

    private suspend fun upload(
        userId: String,
        entry: SleepEntry
    ) {
        supabase
            .from("sleep_entries")
            .upsert(
                SupabaseSleepEntry(
                    userId = userId,
                    date = entry.date,
                    sleepStartHour = entry.sleepStartHour,
                    sleepStartMinute = entry.sleepStartMinute,
                    wakeHour = entry.wakeHour,
                    wakeMinute = entry.wakeMinute,
                    napsMinutes = entry.napsMinutes,
                    updatedAt = Instant.ofEpochMilli(
                        entry.updatedAt.takeIf { it > 0L }
                            ?: System.currentTimeMillis()
                    ).toString()
                )
            ) {
                onConflict = "user_id,date"
            }
    }

    private fun SupabaseSleepEntry.toLocalEntry(): SleepEntry {
        return SleepEntry(
            date = date,
            sleepStartHour = sleepStartHour,
            sleepStartMinute = sleepStartMinute,
            wakeHour = wakeHour,
            wakeMinute = wakeMinute,
            napsMinutes = napsMinutes,
            updatedAt = updatedAtMillis()
        )
    }

    private fun SupabaseSleepEntry.updatedAtMillis(): Long {
        return try {
            Instant.parse(updatedAt).toEpochMilli()
        } catch (_: Exception) {
            0L
        }
    }

    @Serializable
    private data class SupabaseSleepEntry(
        @SerialName("user_id")
        val userId: String,

        val date: String,

        @SerialName("sleep_start_hour")
        val sleepStartHour: Int,

        @SerialName("sleep_start_minute")
        val sleepStartMinute: Int,

        @SerialName("wake_hour")
        val wakeHour: Int,

        @SerialName("wake_minute")
        val wakeMinute: Int,

        @SerialName("naps_minutes")
        val napsMinutes: Int,

        @SerialName("updated_at")
        val updatedAt: String = ""
    )
}