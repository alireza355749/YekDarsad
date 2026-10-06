package com.example.yekdarsad.data.sync

import android.util.Log
import com.example.yekdarsad.data.OverviewNote
import com.example.yekdarsad.data.OverviewNoteDao
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

class OverviewNoteSyncRepository(
    private val overviewNoteDao: OverviewNoteDao
) {

    private val supabase =
        SupabaseClientProvider.client

    suspend fun syncNotes() {

        Log.d(
            "OverviewNoteSync",
            "SYNC: started"
        )

        val userId =
            supabase.auth.currentUserOrNull()?.id
                ?: run {
                    Log.d(
                        "OverviewNoteSync",
                        "SYNC: no logged-in user"
                    )
                    return
                }

        /*
         * =====================================================
         * LOCAL
         * =====================================================
         */

        val localNotes =
            overviewNoteDao.getAllNotes()

        Log.d(
            "OverviewNoteSync",
            "SYNC: local=${localNotes.size}"
        )

        /*
         * =====================================================
         * CLOUD
         * =====================================================
         */

        val cloudNotes =
            loadCloudNotes(userId)

        Log.d(
            "OverviewNoteSync",
            "SYNC: cloud=${cloudNotes.size}"
        )

        /*
         * =====================================================
         * LOCAL -> CLOUD
         * =====================================================
         */

        for (local in localNotes) {

            try {

                val cloud =
                    cloudNotes.firstOrNull {
                        it.periodKey ==
                                local.periodKey
                    }

                /*
                 * Note جدید روی Android
                 */

                if (cloud == null) {

                    if (local.text.isBlank()) {
                        continue
                    }

                    supabase
                        .from("overview_notes")
                        .insert(
                            SupabaseOverviewNoteInsert(
                                userId = userId,
                                periodKey =
                                    local.periodKey,
                                text =
                                    local.text,
                                version = 1
                            )
                        ) {
                            select()
                        }
                        .decodeSingle<SupabaseOverviewNote>()

                    Log.d(
                        "OverviewNoteSync",
                        "SYNC: LOCAL -> CLOUD " +
                                "'${local.periodKey}'"
                    )

                    continue
                }

                /*
                 * =================================================
                 * مقایسه زمان تغییر
                 * =================================================
                 */

                val cloudUpdatedAt =
                    cloud.updatedAtMillis

                val localUpdatedAt =
                    local.updatedAt

                /*
                 * Android جدیدتر است
                 */

                if (
                    localUpdatedAt >
                    cloudUpdatedAt
                ) {

                    supabase
                        .from("overview_notes")
                        .update(
                            SupabaseOverviewNoteUpdate(
                                text =
                                    local.text,
                                version =
                                    cloud.version + 1
                            )
                        ) {
                            filter {
                                eq(
                                    "id",
                                    cloud.id
                                )

                                eq(
                                    "user_id",
                                    userId
                                )
                            }
                        }

                    Log.d(
                        "OverviewNoteSync",
                        "SYNC: LOCAL newer -> CLOUD " +
                                "'${local.periodKey}'"
                    )

                    continue
                }

                /*
                 * =================================================
                 * Cloud جدیدتر است
                 * =================================================
                 */

                if (
                    cloudUpdatedAt >
                    localUpdatedAt
                ) {

                    overviewNoteDao.saveNote(
                        OverviewNote(
                            periodKey =
                                cloud.periodKey,
                            text =
                                cloud.text,
                            updatedAt =
                                cloudUpdatedAt
                        )
                    )

                    Log.d(
                        "OverviewNoteSync",
                        "SYNC: CLOUD newer -> LOCAL " +
                                "'${local.periodKey}'"
                    )
                }

            } catch (e: Exception) {

                Log.e(
                    "OverviewNoteSync",
                    "SYNC: local note failed " +
                            "'${local.periodKey}'",
                    e
                )
            }
        }

        /*
         * =====================================================
         * CLOUD -> LOCAL
         * =====================================================
         */

        for (cloud in cloudNotes) {

            try {

                if (cloud.deletedAt != null) {
                    continue
                }

                val local =
                    localNotes.firstOrNull {
                        it.periodKey ==
                                cloud.periodKey
                    }

                /*
                 * Note فقط در Cloud وجود دارد.
                 */

                if (local == null) {

                    if (cloud.text.isBlank()) {
                        continue
                    }

                    overviewNoteDao.saveNote(
                        OverviewNote(
                            periodKey =
                                cloud.periodKey,
                            text =
                                cloud.text,
                            updatedAt =
                                cloud.updatedAtMillis
                        )
                    )

                    Log.d(
                        "OverviewNoteSync",
                        "SYNC: NEW CLOUD -> LOCAL " +
                                "'${cloud.periodKey}'"
                    )

                    continue
                }

                /*
                 * اگر Cloud جدیدتر باشد.
                 */

                if (
                    cloud.updatedAtMillis >
                    local.updatedAt
                ) {

                    overviewNoteDao.saveNote(
                        OverviewNote(
                            periodKey =
                                cloud.periodKey,
                            text =
                                cloud.text,
                            updatedAt =
                                cloud.updatedAtMillis
                        )
                    )

                    Log.d(
                        "OverviewNoteSync",
                        "SYNC: CLOUD -> LOCAL " +
                                "'${cloud.periodKey}'"
                    )
                }

            } catch (e: Exception) {

                Log.e(
                    "OverviewNoteSync",
                    "SYNC: cloud note failed " +
                            "'${cloud.periodKey}'",
                    e
                )
            }
        }

        Log.d(
            "OverviewNoteSync",
            "SYNC: finished"
        )
    }

    /*
     * =========================================================
     * CLOUD LOAD
     * =========================================================
     */

    private suspend fun loadCloudNotes(
        userId: String
    ): List<SupabaseOverviewNote> {

        return supabase
            .from("overview_notes")
            .select {
                filter {
                    eq(
                        "user_id",
                        userId
                    )
                }
            }
            .decodeList<SupabaseOverviewNote>()
    }

    /*
     * =========================================================
     * SUPABASE MODELS
     * =========================================================
     */

    @Serializable
    private data class SupabaseOverviewNote(

        val id: String,

        @SerialName("user_id")
        val userId: String,

        @SerialName("period_key")
        val periodKey: String,

        val text: String,

        val version: Long = 1,

        @SerialName("updated_at")
        val updatedAt: String? = null,

        @SerialName("deleted_at")
        val deletedAt: String? = null
    ) {

        val updatedAtMillis: Long
            get() {

                return try {

                    Instant.parse(
                        updatedAt
                            ?: return 0L
                    ).toEpochMilli()

                } catch (
                    _: Exception
                ) {

                    0L
                }
            }
    }

    @Serializable
    private data class SupabaseOverviewNoteInsert(

        @SerialName("user_id")
        val userId: String,

        @SerialName("period_key")
        val periodKey: String,

        val text: String,

        val version: Long = 1
    )

    @Serializable
    private data class SupabaseOverviewNoteUpdate(

        val text: String,

        val version: Long
    )
}