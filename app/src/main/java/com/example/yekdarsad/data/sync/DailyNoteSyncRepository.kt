
package com.example.yekdarsad.data.sync

import android.util.Log
import com.example.yekdarsad.data.DailyNote
import com.example.yekdarsad.data.DailyNoteDao
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

class DailyNoteSyncRepository(
    private val dailyNoteDao: DailyNoteDao
) {

    private val supabase =
        SupabaseClientProvider.client

    suspend fun syncNotes() {
        Log.d(TAG, "SYNC: started")

        val userId =
            supabase.auth.currentUserOrNull()?.id
                ?: run {
                    Log.d(TAG, "SYNC: no logged-in user")
                    return
                }

        val localNotes = dailyNoteDao.getAllNotes()

        Log.d(TAG, "SYNC: local=${localNotes.size}")

        val cloudNotes = loadCloudNotes(userId)

        Log.d(TAG, "SYNC: cloud=${cloudNotes.size}")

        val localByDate = localNotes.associateBy { it.date }
        val cloudByDate = cloudNotes.associateBy { it.date }

        /*
         * LOCAL -> CLOUD
         */

        for (local in localNotes) {
            try {
                val cloud = cloudByDate[local.date]

                if (cloud == null) {
                    if (isEmpty(local)) {
                        continue
                    }

                    // نوت قدیمی که هنوز timestamp ندارد
                    // هنگام اولین ارسال، timestamp می‌گیرد.
                    val timestampMillis =
                        local.updatedAt.takeIf { it > 0L }
                            ?: System.currentTimeMillis()

                    val timestamp =
                        Instant.ofEpochMilli(timestampMillis).toString()

                    supabase
                        .from(TABLE)
                        .upsert(
                            SupabaseDailyNoteWrite(
                                userId = userId,
                                date = local.date,
                                note = local.note,
                                mosharete = local.mosharete,
                                mohasebe = local.mohasebe,
                                version = 1,
                                updatedAt = timestamp
                            )
                        ) {
                            onConflict = "user_id,date"
                        }

                    if (local.updatedAt == 0L) {
                        dailyNoteDao.saveNote(
                            local.copy(updatedAt = timestampMillis)
                        )
                    }

                    Log.d(
                        TAG,
                        "SYNC: LOCAL -> CLOUD date=${local.date}"
                    )

                    continue
                }

                /*
                 * اگر نوت محلی timestamp داشته باشد،
                 * فقط وقتی جدیدتر از نسخه ابری باشد
                 * آن را به Supabase ارسال می‌کنیم.
                 *
                 * اگر timestamp محلی صفر باشد،
                 * فعلاً آن را روی نسخه ابری نمی‌نویسیم؛
                 * چون زمان واقعی ویرایش قدیمی مشخص نیست.
                 */
                if (
                    local.updatedAt > 0L &&
                    local.updatedAt > cloud.updatedAtMillis
                ) {
                    val timestamp =
                        Instant.ofEpochMilli(local.updatedAt).toString()

                    supabase
                        .from(TABLE)
                        .update(
                            SupabaseDailyNoteUpdate(
                                note = local.note,
                                mosharete = local.mosharete,
                                mohasebe = local.mohasebe,
                                version = cloud.version + 1,
                                updatedAt = timestamp
                            )
                        ) {
                            filter {
                                eq("id", cloud.id)
                                eq("user_id", userId)
                            }
                        }

                    Log.d(
                        TAG,
                        "SYNC: LOCAL newer -> CLOUD date=${local.date}"
                    )
                }
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "SYNC: local note failed date=${local.date}",
                    e
                )
            }
        }

        /*
         * CLOUD -> LOCAL
         */

        for (cloud in cloudNotes) {
            try {
                val local = localByDate[cloud.date]

                if (local == null) {
                    if (isEmpty(cloud)) {
                        continue
                    }

                    dailyNoteDao.saveNote(
                        DailyNote(
                            date = cloud.date,
                            note = cloud.note,
                            mosharete = cloud.mosharete,
                            mohasebe = cloud.mohasebe,
                            updatedAt = cloud.updatedAtMillis
                        )
                    )

                    Log.d(
                        TAG,
                        "SYNC: CLOUD -> LOCAL date=${cloud.date}"
                    )

                    continue
                }

                /*
                 * اگر نوت محلی قدیمی و بدون timestamp
                 * محتوای غیرخالی داشته باشد، فعلاً آن را
                 * با نسخه ابری جایگزین نمی‌کنیم.
                 *
                 * پس از ویرایش و ذخیره نوت در گوشی،
                 * timestamp محلی ثبت می‌شود و مقایسه
                 * نسخه‌ها قابل انجام خواهد بود.
                 */
                if (
                    local.updatedAt == 0L &&
                    !isEmpty(local)
                ) {
                    Log.w(
                        TAG,
                        "SYNC: legacy local note needs timestamp " +
                                "date=${cloud.date}"
                    )
                    continue
                }

                if (
                    cloud.updatedAtMillis > 0L &&
                    cloud.updatedAtMillis > local.updatedAt
                ) {
                    dailyNoteDao.saveNote(
                        DailyNote(
                            date = cloud.date,
                            note = cloud.note,
                            mosharete = cloud.mosharete,
                            mohasebe = cloud.mohasebe,
                            updatedAt = cloud.updatedAtMillis
                        )
                    )

                    Log.d(
                        TAG,
                        "SYNC: CLOUD newer -> LOCAL date=${cloud.date}"
                    )
                }
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "SYNC: cloud note failed date=${cloud.date}",
                    e
                )
            }
        }

        Log.d(TAG, "SYNC: finished")
    }

    private suspend fun loadCloudNotes(
        userId: String
    ): List<SupabaseDailyNote> {
        return supabase
            .from(TABLE)
            .select {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList<SupabaseDailyNote>()
    }

    private fun isEmpty(note: DailyNote): Boolean {
        return note.note.isBlank() &&
                note.mosharete.isBlank() &&
                note.mohasebe.isBlank()
    }

    private fun isEmpty(
        note: SupabaseDailyNote
    ): Boolean {
        return note.note.isBlank() &&
                note.mosharete.isBlank() &&
                note.mohasebe.isBlank()
    }

    @Serializable
    private data class SupabaseDailyNote(
        val id: String,

        @SerialName("user_id")
        val userId: String,

        val date: String,

        val note: String = "",

        val mosharete: String = "",

        val mohasebe: String = "",

        val version: Long = 1,

        @SerialName("updated_at")
        val updatedAt: String? = null
    ) {
        val updatedAtMillis: Long
            get() {
                val timestamp = updatedAt ?: return 0L

                return try {
                    Instant.parse(timestamp).toEpochMilli()
                } catch (_: Exception) {
                    0L
                }
            }
    }

    @Serializable
    private data class SupabaseDailyNoteWrite(
        @SerialName("user_id")
        val userId: String,

        val date: String,

        val note: String,

        val mosharete: String,

        val mohasebe: String,

        val version: Long = 1,

        @SerialName("updated_at")
        val updatedAt: String
    )

    @Serializable
    private data class SupabaseDailyNoteUpdate(
        val note: String,

        val mosharete: String,

        val mohasebe: String,

        val version: Long,

        @SerialName("updated_at")
        val updatedAt: String
    )

    private companion object {
        const val TAG = "DailyNoteSync"
        const val TABLE = "daily_notes"
    }
}