package com.example.yekdarsad.data.sync

import android.util.Log
import com.example.yekdarsad.data.CategoryDao
import com.example.yekdarsad.data.DailyPlan
import com.example.yekdarsad.data.DailyPlanDao
import com.example.yekdarsad.data.TaskDao
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DailyPlanSyncRepository(
    private val dailyPlanDao: DailyPlanDao,
    private val taskDao: TaskDao,
    private val categoryDao: CategoryDao
) {

    private val supabase =
        SupabaseClientProvider.client

    private val json =
        Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        }

    private companion object {
        const val AUDIT_TAG = "SYNC_AUDIT"
        const val TAG = "DailyPlanSync"
    }

    // =========================================================
    // SNAPSHOT
    // =========================================================

    @Serializable
    private data class DailyPlanSnapshot(
        val taskId: Int,
        val date: String,
        val plannedMinutes: Int,
        val actualMinutes: Int,
        val earnedScore: Double,
        val completed: Boolean,
        val exerciseCalories: Double,
        val dayRegistered: Boolean,
        val orderIndex: Int,
        val note: String,
        val deleted: Boolean
    )

    // =========================================================
    // INSERT MODEL
    // =========================================================

    @Serializable
    private data class SupabaseDailyPlanInsert(
        @SerialName("user_id")
        val userId: String,

        @SerialName("task_id")
        val taskId: String,

        @SerialName("local_task_id")
        val localTaskId: Int? = null,

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

        val note: String,

        @SerialName("local_id")
        val localId: Int,

        @SerialName("deleted_at")
        val deletedAt: String? = null
    )

    // =========================================================
    // MAIN SYNC
    // =========================================================

    suspend fun syncDailyPlans() {

        val syncStart = System.currentTimeMillis()

        var localProcessed = 0
        var localFailed = 0
        var cloudProcessed = 0
        var cloudFailed = 0

        Log.d(
            AUDIT_TAG,
            "DailyPlans START"
        )

        Log.d(
            TAG,
            "SYNC: started"
        )

        val userId =
            supabase.auth.currentUserOrNull()?.id
                ?: run {
                    Log.e(
                        TAG,
                        "SYNC: no logged-in user"
                    )

                    Log.e(
                        AUDIT_TAG,
                        "DailyPlans ABORTED: no logged-in user"
                    )

                    return
                }

        val localPlans =
            dailyPlanDao.getAllPlansOnce()

        val localTasks =
            taskDao.getAllTasksOnce()

        Log.d(
            TAG,
            "SYNC: local plans=${localPlans.size}, local tasks=${localTasks.size}"
        )

        Log.d(
            AUDIT_TAG,
            "Initial counts: localPlans=${localPlans.size}, " +
                    "localTasks=${localTasks.size}"
        )

        // =====================================================
        // CLOUD TASKS
        // =====================================================

        val cloudTasks =
            try {
                supabase
                    .from("tasks")
                    .select {
                        filter {
                            eq(
                                "user_id",
                                userId
                            )
                        }
                    }
                    .decodeList<SupabaseTask>()
            } catch (e: Exception) {

                Log.e(
                    TAG,
                    "SYNC: loading cloud tasks failed: ${e.message}",
                    e
                )

                Log.e(
                    AUDIT_TAG,
                    "FAILED loading cloud tasks",
                    e
                )

                throw e
            }

        Log.d(
            TAG,
            "SYNC: cloud tasks=${cloudTasks.size}"
        )

        // =====================================================
        // CLOUD DAILY PLANS
        // =====================================================

        val cloudPlans =
            try {
                supabase
                    .from("daily_plans")
                    .select {
                        filter {
                            eq(
                                "user_id",
                                userId
                            )
                        }
                    }
                    .decodeList<SupabaseDailyPlan>()
            } catch (e: Exception) {

                Log.e(
                    TAG,
                    "SYNC: loading cloud daily plans failed: ${e.message}",
                    e
                )

                Log.e(
                    AUDIT_TAG,
                    "FAILED loading cloud daily plans",
                    e
                )

                throw e
            }

        Log.d(
            TAG,
            "SYNC: cloud plans=${cloudPlans.size}"
        )

        Log.d(
            AUDIT_TAG,
            "Initial cloud counts: cloudTasks=${cloudTasks.size}, " +
                    "cloudPlans=${cloudPlans.size}"
        )

        // =====================================================
        // MAPS
        // =====================================================

        val cloudTaskByLocalId =
            cloudTasks
                .filter {
                    it.localId != null &&
                            it.id != null
                }
                .associateBy(
                    { it.localId!! },
                    { it }
                )

        val cloudTaskById =
            cloudTasks
                .filter {
                    it.id != null
                }
                .associateBy {
                    it.id!!
                }

        val localTaskById =
            localTasks.associateBy {
                it.id
            }

        // =====================================================
        // LOCAL -> CLOUD / MERGE
        // =====================================================

        for (local in localPlans) {

            try {

                val localTask =
                    localTaskById[local.taskId]

                if (localTask == null) {

                    /*
                     * ممکن است taskId برنامهٔ روزانه قدیمی باشد، درحالی‌که
                     * Task متناظر با cloudId در Room شناسهٔ دیگری دارد.
                     * فقط در صورت تطبیق یکتای Task و DailyPlan، ارتباط را اصلاح می‌کنیم.
                     * پیدا نشدن Task به‌تنهایی دلیل حذف برنامهٔ روزانه نیست.
                     */

                    val candidateCloudTasks =
                        cloudTasks.filter {
                            it.localId == local.taskId &&
                                    it.id != null &&
                                    it.deletedAt == null
                        }

                    val candidateCloudTask =
                        candidateCloudTasks.singleOrNull()

                    val realLocalTask =
                        candidateCloudTask?.id?.let { candidateCloudId ->
                            localTasks.singleOrNull {
                                it.cloudId == candidateCloudId && !it.deleted
                            }
                        }

                    if (realLocalTask != null) {

                        // Task UUID is the reliable link. The cloud local_id may be stale.
                        // Repair the foreign key even when the DailyPlan cloud row cannot
                        // be uniquely identified in this pass; do not invent a cloudId.
                        dailyPlanDao.updateTaskId(
                            planId = local.id,
                            taskId = realLocalTask.id
                        )

                        Log.w(
                            AUDIT_TAG,
                            "REPAIRED stale taskId: plan=${local.id}, " +
                                    "oldTaskId=${local.taskId}, " +
                                    "newTaskId=${realLocalTask.id}, " +
                                    "taskCloudId=${candidateCloudTask?.id}, " +
                                    "date=${local.date}; plan sync deferred to next pass"
                        )

                    } else {

                        Log.w(
                            AUDIT_TAG,
                            "LOCAL -> CLOUD skipped without deleting: " +
                                    "plan=${local.id}, taskId=${local.taskId}, " +
                                    "date=${local.date}; no unique safe Task match"
                        )
                    }

                    localProcessed++
                    continue
                }

                if (localTask.deleted) {

                    Log.d(
                        TAG,
                        "SYNC: local task deleted for plan=${local.id} -> marking plan deleted"
                    )

                    Log.d(
                        AUDIT_TAG,
                        "LOCAL -> CLOUD skipped: plan=${local.id}, " +
                                "taskId=${local.taskId}, date=${local.date}; " +
                                "task is deleted"
                    )

                    if (!local.deleted) {
                        dailyPlanDao.markDeleted(local.id)
                    }

                    localProcessed++
                    continue
                }

                /*
                 * مرجع اصلی Task:
                 *
                 * Room Task.cloudId
                 *        ↓
                 * Cloud Task UUID
                 *
                 * localId فقط fallback برای داده‌های قدیمی است.
                 */

                val cloudTask =
                    localTask.cloudId?.let {
                        cloudTaskById[it]
                    }
                        ?: cloudTaskByLocalId[localTask.id]

                if (cloudTask?.id == null) {

                    Log.e(
                        TAG,
                        "SYNC: no cloud task for local task=${localTask.id} title='${localTask.title}'"
                    )

                    Log.e(
                        AUDIT_TAG,
                        "LOCAL -> CLOUD skipped: plan=${local.id}, " +
                                "taskId=${local.taskId}, date=${local.date}; " +
                                "no matching cloud task"
                    )

                    localProcessed++
                    continue
                }

                val cloudTaskId =
                    cloudTask.id
                        ?: continue

                if (cloudTask.deletedAt != null) {

                    Log.d(
                        TAG,
                        "SYNC: cloud task deleted for plan=${local.id} -> marking plan deleted"
                    )

                    Log.d(
                        AUDIT_TAG,
                        "LOCAL -> CLOUD skipped: plan=${local.id}, " +
                                "taskId=${local.taskId}, date=${local.date}; " +
                                "cloud task is deleted"
                    )

                    if (!local.deleted) {
                        dailyPlanDao.markDeleted(local.id)
                    }

                    localProcessed++
                    continue
                }

                val cloud =
                    findMatchingCloudPlan(
                        local = local,
                        cloudPlans = cloudPlans,
                        cloudTaskId = cloudTaskId
                    )

                // =================================================
                // NEW LOCAL PLAN
                // =================================================

                if (cloud == null) {

                    val cloudPlansForSameTaskAndDate = cloudPlans.filter {
                        it.taskId == cloudTaskId && it.date == local.date
                    }

                    if (cloudPlansForSameTaskAndDate.size > 1) {
                        Log.w(
                            AUDIT_TAG,
                            "LOCAL -> CLOUD skipped to prevent duplicate: " +
                                    "plan=${local.id}, taskId=${local.taskId}, " +
                                    "date=${local.date}; " +
                                    "multiple cloud plans match task/date=" +
                                    cloudPlansForSameTaskAndDate.size
                        )
                        localProcessed++
                        continue
                    }

                    if (local.deleted) {
                        Log.d(
                            AUDIT_TAG,
                            "LOCAL -> CLOUD skipped deleted local plan=${local.id}, " +
                                    "taskId=${local.taskId}, date=${local.date}; " +
                                    "no matching cloud plan"
                        )

                        localProcessed++
                        continue
                    }

                    insertLocalPlanToCloud(
                        userId = userId,
                        local = local,
                        cloudTask = cloudTask
                    )

                    localProcessed++
                    continue
                }

                val cloudId =
                    cloud.id
                        ?: run {
                            Log.e(
                                AUDIT_TAG,
                                "LOCAL -> CLOUD skipped: matching cloud plan has null ID; " +
                                        "local=${local.id}, date=${local.date}"
                            )

                            localProcessed++
                            continue
                        }

                if (cloud.deletedAt != null) {

                    Log.d(
                        TAG,
                        "SYNC: cloud plan deleted cloud=$cloudId -> local=${local.id}"
                    )

                    if (!local.deleted) {
                        dailyPlanDao.markDeleted(local.id)
                    }

                    localProcessed++
                    continue
                }

                // =================================================
                // BOOTSTRAP
                // =================================================

                if (local.cloudId == null) {

                    dailyPlanDao.updateSyncState(
                        planId = local.id,
                        cloudId = cloudId,
                        syncBase = snapshotJson(
                            snapshotFromCloud(
                                cloud = cloud,
                                localTaskId = local.taskId
                            )
                        ),
                        syncVersion = cloud.version
                    )

                    Log.d(
                        TAG,
                        "SYNC: bootstrap local=${local.id} -> cloud=$cloudId taskId=${local.taskId}"
                    )

                    localProcessed++
                    continue
                }

                // =================================================
                // BASE SNAPSHOT
                // =================================================

                val base =
                    decodeSnapshot(
                        local.syncBase
                    )

                if (base == null) {

                    val cloudSnapshot =
                        snapshotFromCloud(
                            cloud = cloud,
                            localTaskId = local.taskId
                        )

                    dailyPlanDao.updateSyncSnapshot(
                        planId = local.id,
                        syncBase = snapshotJson(
                            cloudSnapshot
                        ),
                        syncVersion = cloud.version
                    )

                    Log.d(
                        TAG,
                        "SYNC: created missing base snapshot local=${local.id}"
                    )

                    localProcessed++
                    continue
                }

                // =================================================
                // SNAPSHOTS
                // =================================================

                val localSnapshot =
                    snapshotFromLocal(
                        local
                    )

                val remoteSnapshot =
                    snapshotFromCloud(
                        cloud = cloud,
                        localTaskId = local.taskId
                    )

                if (local.date == "2026-10-03") {
                    Log.d(
                        "HAMOOM_DEBUG",
                        "LOCAL id=${local.id}, title=${localTask.title}, " +
                                "cloudId=${local.cloudId}, matchedCloudId=${cloud.id}, " +
                                "localCompleted=${localSnapshot.completed}, " +
                                "remoteCompleted=${remoteSnapshot.completed}, " +
                                "baseCompleted=${base.completed}, " +
                                "localActual=${localSnapshot.actualMinutes}, " +
                                "remoteActual=${remoteSnapshot.actualMinutes}, " +
                                "localScore=${localSnapshot.earnedScore}, " +
                                "remoteScore=${remoteSnapshot.earnedScore}"
                    )
                }

                val localChangedFields =
                    changedFields(
                        base = base,
                        current = localSnapshot
                    )

                val remoteChangedFields =
                    changedFields(
                        base = base,
                        current = remoteSnapshot
                    )

                // =================================================
                // NOTHING CHANGED
                // =================================================

                if (
                    localChangedFields.isEmpty() &&
                    remoteChangedFields.isEmpty()
                ) {

                    dailyPlanDao.updateSyncSnapshot(
                        planId = local.id,
                        syncBase = snapshotJson(
                            remoteSnapshot
                        ),
                        syncVersion = cloud.version
                    )

                    localProcessed++
                    continue
                }

                // =================================================
                // ANDROID ONLY
                // =================================================

                if (
                    localChangedFields.isNotEmpty() &&
                    remoteChangedFields.isEmpty()
                ) {

                    if (localSnapshot.deleted) {

                        softDeleteCloud(cloudId)

                    } else {

                        // UPDATE پاسخ Cloud را همان‌جا می‌گیریم؛ دیگر برای هر Plan
                        // یک SELECT جداگانه و پرهزینه اجرا نمی‌کنیم.
                        val updatedCloud =
                            updateCloudFromLocal(
                                local = local,
                                cloud = cloud,
                                cloudTaskId = cloudTaskId
                            )

                        dailyPlanDao.updateSyncSnapshot(
                            planId = local.id,
                            syncBase = snapshotJson(
                                snapshotFromCloud(
                                    cloud = updatedCloud,
                                    localTaskId = local.taskId
                                )
                            ),
                            syncVersion = updatedCloud.version
                        )
                    }

                    Log.d(
                        TAG,
                        "SYNC: Android-only change pushed local=${local.id}"
                    )

                    localProcessed++
                    continue
                }

                // =================================================
                // WEB ONLY
                // =================================================

                if (
                    localChangedFields.isEmpty() &&
                    remoteChangedFields.isNotEmpty()
                ) {

                    applyRemoteToLocal(
                        local = local,
                        cloud = cloud
                    )

                    val finalSnapshot =
                        snapshotFromCloud(
                            cloud = cloud,
                            localTaskId = local.taskId
                        )

                    dailyPlanDao.updateSyncSnapshot(
                        planId = local.id,
                        syncBase = snapshotJson(
                            finalSnapshot
                        ),
                        syncVersion = cloud.version
                    )

                    Log.d(
                        TAG,
                        "SYNC: Web-only change pulled cloud=$cloudId"
                    )

                    localProcessed++
                    continue
                }

                // =================================================
                // BOTH SIDES CHANGED
                // =================================================

                val conflicts =
                    localChangedFields.intersect(
                        remoteChangedFields
                    )

                if (conflicts.isNotEmpty()) {

                    val actualConflictFields =
                        conflicts.filter {
                            fieldValue(
                                localSnapshot,
                                it
                            ) != fieldValue(
                                remoteSnapshot,
                                it
                            )
                        }

                    if (actualConflictFields.isNotEmpty()) {

                        saveConflict(
                            entityId = cloudId,
                            base = base,
                            local = localSnapshot,
                            remote = remoteSnapshot,
                            fields = actualConflictFields
                        )

                        Log.w(
                            TAG,
                            "SYNC: CONFLICT cloud=$cloudId fields=$actualConflictFields"
                        )

                        Log.w(
                            AUDIT_TAG,
                            "CONFLICT: local=${local.id}, cloud=$cloudId, " +
                                    "taskId=${local.taskId}, date=${local.date}, " +
                                    "fields=$actualConflictFields"
                        )

                        localProcessed++
                        continue
                    }
                }

                // =================================================
                // INDEPENDENT CHANGES -> MERGE
                // =================================================

                val merged =
                    mergeSnapshots(
                        base = base,
                        local = localSnapshot,
                        remote = remoteSnapshot
                    )

                if (merged.deleted) {

                    softDeleteCloud(cloudId)

                    applySnapshotToLocal(
                        local = local,
                        snapshot = merged
                    )

                    localProcessed++
                    continue
                }

                // UPDATE همراه SELECT؛ یک رفت‌وبرگشت شبکه به‌جای UPDATE + SELECT.
                val updatedCloud =
                    updateCloudFromSnapshot(
                        cloud = cloud,
                        snapshot = merged,
                        taskCloudId = cloudTaskId
                    )

                applySnapshotToLocal(
                    local = local,
                    snapshot = merged
                )

                dailyPlanDao.updateSyncSnapshot(
                    planId = local.id,
                    syncBase = snapshotJson(
                        snapshotFromCloud(
                            cloud = updatedCloud,
                            localTaskId = merged.taskId
                        )
                    ),
                    syncVersion = updatedCloud.version
                )

                Log.d(
                    TAG,
                    "SYNC: independent changes merged cloud=$cloudId"
                )

                localProcessed++

            } catch (e: Exception) {

                localFailed++

                Log.e(
                    TAG,
                    "SYNC: processing local plan=${local.id} failed: ${e.message}",
                    e
                )

                Log.e(
                    AUDIT_TAG,
                    "LOCAL -> CLOUD FAILED: localId=${local.id}, " +
                            "taskId=${local.taskId}, date=${local.date}, " +
                            "deleted=${local.deleted}",
                    e
                )
            }
        }

        Log.d(
            AUDIT_TAG,
            "LOCAL -> CLOUD summary: total=${localPlans.size}, " +
                    "processed=$localProcessed, failed=$localFailed"
        )

        // =========================================================
        // REFRESH CLOUD
        // =========================================================

        val refreshedCloudPlans =
            try {
                supabase
                    .from("daily_plans")
                    .select {
                        filter {
                            eq(
                                "user_id",
                                userId
                            )
                        }
                    }
                    .decodeList<SupabaseDailyPlan>()
            } catch (e: Exception) {

                Log.e(
                    TAG,
                    "SYNC: refreshing cloud daily plans failed: ${e.message}",
                    e
                )

                Log.e(
                    AUDIT_TAG,
                    "FAILED refreshing cloud daily plans",
                    e
                )

                throw e
            }

        val refreshedLocalPlans =
            dailyPlanDao.getAllPlansOnce()

        Log.d(
            TAG,
            "SYNC: CLOUD -> LOCAL cloud=${refreshedCloudPlans.size}, local=${refreshedLocalPlans.size}"
        )

        Log.d(
            AUDIT_TAG,
            "Refreshed counts: initialLocal=${localPlans.size}, " +
                    "initialCloud=${cloudPlans.size}, " +
                    "refreshedLocal=${refreshedLocalPlans.size}, " +
                    "refreshedCloud=${refreshedCloudPlans.size}"
        )

        // =========================================================
        // CLOUD -> LOCAL
        // =========================================================

        for (cloud in refreshedCloudPlans) {

            try {

                val cloudId =
                    cloud.id
                        ?: run {
                            Log.e(
                                AUDIT_TAG,
                                "CLOUD -> LOCAL skipped: cloud plan has null ID, " +
                                        "date=${cloud.date}"
                            )

                            cloudProcessed++
                            continue
                        }

                // -------------------------------------------------
                // IMPORTANT:
                //
                // برای اتصال Cloud DailyPlan به Room DailyPlan
                // ابتدا فقط cloudId معتبر است.
                //
                // cloud.localId به تنهایی باعث اتصال
                // رکورد به یک DailyPlan موجود نمی‌شود.
                // -------------------------------------------------

                val existingLocalByCloudId =
                    refreshedLocalPlans.firstOrNull {
                        it.cloudId == cloudId
                    }

                if (cloud.deletedAt != null) {

                    if (
                        existingLocalByCloudId != null &&
                        !existingLocalByCloudId.deleted
                    ) {

                        dailyPlanDao.markDeleted(
                            existingLocalByCloudId.id
                        )
                    }

                    Log.d(
                        TAG,
                        "SYNC: CLOUD -> LOCAL skip deleted plan=$cloudId"
                    )

                    cloudProcessed++
                    continue
                }

                // =================================================
                // RESOLVE CLOUD TASK -> REAL ROOM TASK
                // =================================================

                val cloudTask =
                    cloudTaskById[cloud.taskId]

                if (cloudTask == null) {

                    // نبودن Task در پاسخ Cloud، به‌تنهایی مجوز حذف DailyPlan نیست.
                    Log.w(
                        AUDIT_TAG,
                        "CLOUD -> LOCAL skipped without deleting: " +
                                "cloudPlan=$cloudId, date=${cloud.date}, " +
                                "cloudTaskId=${cloud.taskId}; task not found in cloud task list"
                    )

                    cloudProcessed++
                    continue
                }

                if (cloudTask.deletedAt != null) {

                    if (
                        existingLocalByCloudId != null &&
                        !existingLocalByCloudId.deleted
                    ) {
                        dailyPlanDao.markDeleted(existingLocalByCloudId.id)
                    }

                    Log.d(
                        TAG,
                        "SYNC: CLOUD -> LOCAL skip plan=$cloudId because task is deleted"
                    )

                    cloudProcessed++
                    continue
                }

                val cloudTaskId =
                    cloudTask.id
                        ?: run {
                            Log.e(
                                TAG,
                                "SYNC: cloud task has null id for plan=$cloudId"
                            )

                            Log.e(
                                AUDIT_TAG,
                                "CLOUD -> LOCAL skipped: cloudTask has null ID, " +
                                        "cloudPlan=$cloudId, date=${cloud.date}"
                            )

                            cloudProcessed++
                            continue
                        }

                /*
                 * مهم:
                 *
                 * cloudTask.localId قابل اعتماد نیست.
                 *
                 * اتصال اصلی:
                 *
                 * cloud.task_id
                 *       ↓
                 * SupabaseTask.id
                 *       ↓
                 * Room Task.cloudId
                 *       ↓
                 * Room Task.id
                 */

                val localTask =
                    localTasks.firstOrNull {
                        it.cloudId == cloudTaskId
                    }
                        ?: taskDao.getByCloudId(
                            cloudTaskId
                        )

                val taskLocalId =
                    localTask?.id

                if (taskLocalId == null) {

                    Log.e(
                        TAG,
                        "SYNC: cannot resolve REAL Room task id for cloud task=$cloudTaskId title='${cloudTask.title}' cloudLocalId=${cloudTask.localId}"
                    )

                    Log.e(
                        AUDIT_TAG,
                        "CLOUD -> LOCAL skipped: cloudPlan=$cloudId, " +
                                "cloudTaskId=$cloudTaskId, date=${cloud.date}; " +
                                "no matching Room task"
                    )

                    cloudProcessed++
                    continue
                }

                if (
                    cloudTask.localId != null &&
                    localTask.id != cloudTask.localId
                ) {

                    Log.w(
                        TAG,
                        "SYNC: TASK ID MISMATCH cloudTask='${cloudTask.title}' cloudLocalId=${cloudTask.localId} realRoomId=${localTask.id} cloudId=$cloudTaskId"
                    )

                    Log.w(
                        AUDIT_TAG,
                        "TASK ID MISMATCH: cloudTaskId=$cloudTaskId, " +
                                "cloudLocalId=${cloudTask.localId}, " +
                                "roomTaskId=${localTask.id}"
                    )
                }

                Log.d(
                    TAG,
                    "SYNC: TASK RESOLVED title='${cloudTask.title}' cloudId=$cloudTaskId roomTaskId=$taskLocalId"
                )

                // =================================================
                // FIND EXISTING LOCAL PLAN
                // =================================================

                val local =
                    existingLocalByCloudId
                        ?: refreshedLocalPlans.firstOrNull {
                            it.taskId == taskLocalId &&
                                    it.date == cloud.date &&
                                    it.cloudId == null
                        }

                if (local != null) {

                    if (local.cloudId == null) {

                        dailyPlanDao.updateSyncState(
                            planId = local.id,
                            cloudId = cloudId,
                            syncBase = snapshotJson(
                                snapshotFromCloud(
                                    cloud = cloud,
                                    localTaskId = taskLocalId
                                )
                            ),
                            syncVersion = cloud.version
                        )

                        Log.d(
                            TAG,
                            "SYNC: linked existing local=${local.id} to cloud=$cloudId taskId=$taskLocalId"
                        )
                    } else if (
                        local.syncBase.isNullOrBlank()
                    ) {

                        dailyPlanDao.updateSyncSnapshot(
                            planId = local.id,
                            syncBase = snapshotJson(
                                snapshotFromCloud(
                                    cloud = cloud,
                                    localTaskId = taskLocalId
                                )
                            ),
                            syncVersion = cloud.version
                        )
                    }

                    cloudProcessed++
                    continue
                }

                // =================================================
                // NEW CLOUD-ONLY PLAN
                // =================================================

                /*
                 * cloud.localId ممکن است:
                 *
                 * 1. متعلق به Web باشد.
                 * 2. قدیمی باشد.
                 * 3. با یک Room DailyPlan دیگر collision داشته باشد.
                 *
                 * بنابراین قبل از INSERT باید ID را بررسی کنیم.
                 */

                val requestedLocalId =
                    cloud.localId
                        ?: localIdFromUuid(
                            cloudId
                        )

                val newLocalId =
                    chooseSafeLocalPlanId(
                        requestedId = requestedLocalId,
                        cloudId = cloudId,
                        existingPlans = refreshedLocalPlans
                    )

                if (newLocalId != requestedLocalId) {

                    Log.w(
                        TAG,
                        "SYNC: DAILY PLAN ID COLLISION cloud=$cloudId requestedLocalId=$requestedLocalId -> safeLocalId=$newLocalId"
                    )

                    Log.w(
                        AUDIT_TAG,
                        "LOCAL ID COLLISION: cloud=$cloudId, " +
                                "requestedLocalId=$requestedLocalId, " +
                                "safeLocalId=$newLocalId, date=${cloud.date}"
                    )
                }

                val newLocal =
                    DailyPlan(
                        id = newLocalId,
                        taskId = taskLocalId,
                        date = cloud.date,
                        plannedMinutes = cloud.plannedMinutes,
                        actualMinutes = cloud.actualMinutes,
                        earnedScore = cloud.earnedScore,
                        completed = cloud.completed,
                        exerciseCalories = cloud.exerciseCalories,
                        dayRegistered = cloud.dayRegistered,
                        orderIndex = cloud.orderIndex,
                        note = cloud.note,
                        cloudId = cloudId,
                        deleted = false,
                        syncBase = snapshotJson(
                            snapshotFromCloud(
                                cloud = cloud,
                                localTaskId = taskLocalId
                            )
                        ),
                        syncVersion = cloud.version
                    )

                try {

                    dailyPlanDao.insert(
                        newLocal
                    )

                } catch (e: Exception) {

                    Log.e(
                        TAG,
                        "SYNC: inserting cloud-only plan FAILED cloud=$cloudId localId=$newLocalId taskId=$taskLocalId date=${cloud.date}: ${e.message}",
                        e
                    )

                    Log.e(
                        AUDIT_TAG,
                        "ROOM INSERT FAILED: cloud=$cloudId, " +
                                "localId=$newLocalId, taskId=$taskLocalId, " +
                                "date=${cloud.date}",
                        e
                    )

                    cloudFailed++
                    continue
                }

                /*
                 * Cloud local_id را با ID واقعی Room هماهنگ می‌کنیم.
                 *
                 * این کار فقط برای رکورد DailyPlan انجام می‌شود
                 * و به Task.localId کاری ندارد.
                 */

                if (cloud.localId != newLocalId) {

                    try {

                        supabase
                            .from("daily_plans")
                            .update(
                                mapOf(
                                    "local_id" to newLocalId
                                )
                            ) {
                                filter {
                                    eq(
                                        "id",
                                        cloudId
                                    )
                                }
                            }

                    } catch (e: Exception) {

                        Log.e(
                            TAG,
                            "SYNC: updating cloud local_id failed: ${e.message}",
                            e
                        )

                        Log.e(
                            AUDIT_TAG,
                            "CLOUD local_id UPDATE FAILED: cloud=$cloudId, " +
                                    "localId=$newLocalId, date=${cloud.date}",
                            e
                        )
                    }
                }

                Log.d(
                    TAG,
                    "SYNC: cloud-only plan inserted local=$newLocalId cloud=$cloudId taskId=$taskLocalId title='${cloudTask.title}' date=${cloud.date} planned=${cloud.plannedMinutes}"
                )

                cloudProcessed++

            } catch (e: Exception) {

                cloudFailed++

                Log.e(
                    TAG,
                    "SYNC: CLOUD -> LOCAL failed cloud=${cloud.id}: ${e.message}",
                    e
                )

                Log.e(
                    AUDIT_TAG,
                    "CLOUD -> LOCAL FAILED: cloudId=${cloud.id}, " +
                            "taskId=${cloud.taskId}, date=${cloud.date}, " +
                            "localId=${cloud.localId}",
                    e
                )
            }
        }

        // =========================================================
        // FINAL AUDIT
        // =========================================================

        val finalLocalPlans =
            dailyPlanDao.getAllPlansOnce()

        Log.d(
            AUDIT_TAG,
            "CLOUD -> LOCAL summary: total=${refreshedCloudPlans.size}, " +
                    "processed=$cloudProcessed, failed=$cloudFailed"
        )

        Log.d(
            AUDIT_TAG,
            "DailyPlans FINISHED: initialLocal=${localPlans.size}, " +
                    "initialCloud=${cloudPlans.size}, " +
                    "finalLocal=${finalLocalPlans.size}, " +
                    "finalCloud=${refreshedCloudPlans.size}, " +
                    "localFailed=$localFailed, cloudFailed=$cloudFailed, " +
                    "elapsedMs=${System.currentTimeMillis() - syncStart}"
        )

        Log.d(
            TAG,
            "SYNC: finished successfully"
        )
    }

    // =========================================================
    // MATCH CLOUD PLAN
    // =========================================================

    private fun findMatchingCloudPlan(
        local: DailyPlan,
        cloudPlans: List<SupabaseDailyPlan>,
        cloudTaskId: String
    ): SupabaseDailyPlan? {

        /*
         * قوی‌ترین match:
         * Cloud DailyPlan UUID
         */

        if (local.cloudId != null) {

            cloudPlans.firstOrNull {
                it.id == local.cloudId
            }?.let {
                return it
            }
        }

        /*
         * برای Local Plan قدیمی که cloudId ندارد،
         * local_id هنوز می‌تواند برای bootstrap استفاده شود.
         */

        val localIdMatches = cloudPlans.filter {
            it.localId == local.id &&
                    it.taskId == cloudTaskId &&
                    it.date == local.date
        }

        localIdMatches.singleOrNull()?.let {
            return it
        }

        /*
         * آخرین fallback:
         * Task UUID + Date
         */

        val sameTaskAndDate = cloudPlans.filter {
            it.taskId == cloudTaskId &&
                    it.date == local.date
        }

        // Only auto-link when the match is unique. Never create a duplicate
        // merely because cloud.local_id came from another installation/device.
        return sameTaskAndDate.singleOrNull()
    }

    // =========================================================
    // INSERT LOCAL -> CLOUD
    // =========================================================

    private suspend fun insertLocalPlanToCloud(
        userId: String,
        local: DailyPlan,
        cloudTask: SupabaseTask
    ) {

        val taskCloudId =
            cloudTask.id
                ?: run {
                    Log.e(
                        AUDIT_TAG,
                        "CLOUD INSERT skipped: task cloud ID is null; " +
                                "local=${local.id}, taskId=${local.taskId}, date=${local.date}"
                    )
                    return
                }

        try {

            val inserted =
                supabase
                    .from("daily_plans")
                    .insert(
                        SupabaseDailyPlanInsert(
                            userId = userId,
                            taskId = taskCloudId,
                            localTaskId = local.taskId,
                            date = local.date,
                            plannedMinutes = local.plannedMinutes,
                            actualMinutes = local.actualMinutes,
                            earnedScore = local.earnedScore,
                            completed = local.completed,
                            exerciseCalories = local.exerciseCalories,
                            dayRegistered = local.dayRegistered,
                            orderIndex = local.orderIndex,
                            note = local.note,
                            localId = local.id
                        )
                    ) {
                        select()
                    }
                    .decodeSingle<SupabaseDailyPlan>()

            val cloudId =
                inserted.id
                    ?: run {
                        Log.e(
                            AUDIT_TAG,
                            "CLOUD INSERT returned null ID: local=${local.id}, " +
                                    "taskId=${local.taskId}, date=${local.date}"
                        )
                        return
                    }

            dailyPlanDao.updateSyncState(
                planId = local.id,
                cloudId = cloudId,
                syncBase = snapshotJson(
                    snapshotFromCloud(
                        cloud = inserted,
                        localTaskId = local.taskId
                    )
                ),
                syncVersion = inserted.version
            )

            Log.d(
                TAG,
                "SYNC: inserted local=${local.id} -> cloud=$cloudId taskId=${local.taskId}"
            )

            Log.d(
                AUDIT_TAG,
                "CLOUD INSERT successful: local=${local.id}, cloud=$cloudId, " +
                        "taskId=${local.taskId}, date=${local.date}"
            )

        } catch (e: Exception) {

            Log.e(
                AUDIT_TAG,
                "CLOUD INSERT FAILED: local=${local.id}, " +
                        "taskId=${local.taskId}, date=${local.date}",
                e
            )

            throw e
        }
    }

    // =========================================================
    // UPDATE CLOUD
    // =========================================================

    private suspend fun updateCloudFromLocal(
        local: DailyPlan,
        cloud: SupabaseDailyPlan,
        cloudTaskId: String
    ): SupabaseDailyPlan {

        return updateCloudFromSnapshot(
            cloud = cloud,
            snapshot = snapshotFromLocal(local),
            taskCloudId = cloudTaskId
        )
    }

    private suspend fun updateCloudFromSnapshot(
        cloud: SupabaseDailyPlan,
        snapshot: DailyPlanSnapshot,
        taskCloudId: String
    ): SupabaseDailyPlan {

        val cloudId =
            cloud.id ?: throw IllegalStateException(
                "CLOUD UPDATE cannot run with null cloud ID; date=${snapshot.date}"
            )

        try {

            val updated =
                supabase
                    .from("daily_plans")
                    .update(
                        SupabaseDailyPlanUpdate(
                            localId = cloud.localId ?: 0,
                            localTaskId = snapshot.taskId,
                            taskId = taskCloudId,
                            date = snapshot.date,
                            plannedMinutes = snapshot.plannedMinutes,
                            actualMinutes = snapshot.actualMinutes,
                            earnedScore = snapshot.earnedScore,
                            completed = snapshot.completed,
                            exerciseCalories = snapshot.exerciseCalories,
                            dayRegistered = snapshot.dayRegistered,
                            orderIndex = snapshot.orderIndex,
                            note = snapshot.note
                        )
                    ) {
                        filter {
                            eq("id", cloudId)
                        }
                        select()
                    }
                    .decodeSingle<SupabaseDailyPlan>()

            Log.d(
                AUDIT_TAG,
                "CLOUD UPDATE successful: cloud=$cloudId, " +
                        "taskId=$taskCloudId, date=${snapshot.date}"
            )

            return updated

        } catch (e: Exception) {

            Log.e(
                AUDIT_TAG,
                "CLOUD UPDATE FAILED: cloud=$cloudId, " +
                        "taskId=$taskCloudId, date=${snapshot.date}",
                e
            )

            throw e
        }
    }

    // =========================================================
    // SOFT DELETE CLOUD
    // =========================================================

    private suspend fun softDeleteCloud(
        cloudId: String
    ) {

        try {

            supabase
                .from("daily_plans")
                .delete {
                    filter {
                        eq(
                            "id",
                            cloudId
                        )
                    }
                }

            Log.d(
                TAG,
                "SYNC: cloud soft-delete requested id=$cloudId"
            )

            Log.d(
                AUDIT_TAG,
                "CLOUD DELETE request completed: cloud=$cloudId"
            )

        } catch (e: Exception) {

            Log.e(
                AUDIT_TAG,
                "CLOUD DELETE FAILED: cloud=$cloudId",
                e
            )

            throw e
        }
    }

    // =========================================================
    // FETCH CLOUD PLAN
    // =========================================================

    private suspend fun fetchCloudPlan(
        cloudId: String
    ): SupabaseDailyPlan? {

        return try {

            supabase
                .from("daily_plans")
                .select {
                    filter {
                        eq(
                            "id",
                            cloudId
                        )
                    }
                }
                .decodeList<SupabaseDailyPlan>()
                .firstOrNull()

        } catch (e: Exception) {

            Log.e(
                AUDIT_TAG,
                "FETCH CLOUD PLAN FAILED: cloud=$cloudId",
                e
            )

            throw e
        }
    }

    // =========================================================
    // APPLY REMOTE -> LOCAL
    // =========================================================

    private suspend fun applyRemoteToLocal(
        local: DailyPlan,
        cloud: SupabaseDailyPlan
    ) {

        /*
         * Cloud Task UUID
         *       ↓
         * Room Task.cloudId
         *       ↓
         * Room Task.id
         *
         * local_task_id فقط fallback نیست؛
         * برای اتصال اصلی از آن استفاده نمی‌کنیم.
         */

        val cloudTaskId =
            cloud.taskId

        val resolvedTask =
            taskDao.getByCloudId(
                cloudTaskId
            )

        val taskLocalId =
            resolvedTask?.id
                ?: local.taskId

        if (resolvedTask == null) {

            Log.w(
                TAG,
                "SYNC: remote plan=${cloud.id} task UUID=$cloudTaskId could not resolve to Room task; keeping existing taskId=$taskLocalId"
            )

            Log.w(
                AUDIT_TAG,
                "REMOTE APPLY WARNING: cloud=${cloud.id}, local=${local.id}, " +
                        "date=${cloud.date}; task UUID not resolved, " +
                        "keeping taskId=$taskLocalId"
            )
        }

        dailyPlanDao.applySyncedDailyPlan(
            planId = local.id,
            cloudId = cloud.id
                ?: local.cloudId
                ?: "",
            taskId = taskLocalId,
            date = cloud.date,
            plannedMinutes = cloud.plannedMinutes,
            actualMinutes = cloud.actualMinutes,
            earnedScore = cloud.earnedScore,
            completed = cloud.completed,
            exerciseCalories = cloud.exerciseCalories,
            dayRegistered = cloud.dayRegistered,
            orderIndex = cloud.orderIndex,
            note = cloud.note,
            deleted = cloud.deletedAt != null,
            syncBase = snapshotJson(
                snapshotFromCloud(
                    cloud = cloud,
                    localTaskId = taskLocalId
                )
            ),
            syncVersion = cloud.version
        )

        Log.d(
            TAG,
            "SYNC: remote -> local plan=${local.id} cloudTask=$cloudTaskId resolvedTaskId=$taskLocalId"
        )

        Log.d(
            AUDIT_TAG,
            "REMOTE APPLY successful: local=${local.id}, " +
                    "cloud=${cloud.id}, taskId=$taskLocalId, date=${cloud.date}"
        )
    }

    private suspend fun applySnapshotToLocal(
        local: DailyPlan,
        snapshot: DailyPlanSnapshot
    ) {

        dailyPlanDao.applySyncedDailyPlan(
            planId = local.id,
            cloudId = local.cloudId ?: "",
            taskId = snapshot.taskId,
            date = snapshot.date,
            plannedMinutes = snapshot.plannedMinutes,
            actualMinutes = snapshot.actualMinutes,
            earnedScore = snapshot.earnedScore,
            completed = snapshot.completed,
            exerciseCalories = snapshot.exerciseCalories,
            dayRegistered = snapshot.dayRegistered,
            orderIndex = snapshot.orderIndex,
            note = snapshot.note,
            deleted = snapshot.deleted,
            syncBase = snapshotJson(
                snapshot
            ),
            syncVersion = local.syncVersion
        )
    }

    // =========================================================
    // MERGE
    // =========================================================

    private fun mergeSnapshots(
        base: DailyPlanSnapshot,
        local: DailyPlanSnapshot,
        remote: DailyPlanSnapshot
    ): DailyPlanSnapshot {

        fun <T> mergeField(
            baseValue: T,
            localValue: T,
            remoteValue: T
        ): T {

            return when {

                localValue == baseValue ->
                    remoteValue

                remoteValue == baseValue ->
                    localValue

                else ->
                    localValue
            }
        }

        return DailyPlanSnapshot(
            taskId =
                mergeField(
                    base.taskId,
                    local.taskId,
                    remote.taskId
                ),

            date =
                mergeField(
                    base.date,
                    local.date,
                    remote.date
                ),

            plannedMinutes =
                mergeField(
                    base.plannedMinutes,
                    local.plannedMinutes,
                    remote.plannedMinutes
                ),

            actualMinutes =
                mergeField(
                    base.actualMinutes,
                    local.actualMinutes,
                    remote.actualMinutes
                ),

            earnedScore =
                mergeField(
                    base.earnedScore,
                    local.earnedScore,
                    remote.earnedScore
                ),

            completed =
                mergeField(
                    base.completed,
                    local.completed,
                    remote.completed
                ),

            exerciseCalories =
                mergeField(
                    base.exerciseCalories,
                    local.exerciseCalories,
                    remote.exerciseCalories
                ),

            dayRegistered =
                mergeField(
                    base.dayRegistered,
                    local.dayRegistered,
                    remote.dayRegistered
                ),

            orderIndex =
                mergeField(
                    base.orderIndex,
                    local.orderIndex,
                    remote.orderIndex
                ),

            note =
                mergeField(
                    base.note,
                    local.note,
                    remote.note
                ),

            deleted =
                mergeField(
                    base.deleted,
                    local.deleted,
                    remote.deleted
                )
        )
    }

    // =========================================================
    // CHANGE DETECTION
    // =========================================================

    private fun changedFields(
        base: DailyPlanSnapshot,
        current: DailyPlanSnapshot
    ): Set<String> {

        val result =
            mutableSetOf<String>()

        if (base.taskId != current.taskId) {
            result += "taskId"
        }

        if (base.date != current.date) {
            result += "date"
        }

        if (base.plannedMinutes != current.plannedMinutes) {
            result += "plannedMinutes"
        }

        if (base.actualMinutes != current.actualMinutes) {
            result += "actualMinutes"
        }

        if (base.earnedScore != current.earnedScore) {
            result += "earnedScore"
        }

        if (base.completed != current.completed) {
            result += "completed"
        }

        if (base.exerciseCalories != current.exerciseCalories) {
            result += "exerciseCalories"
        }

        if (base.dayRegistered != current.dayRegistered) {
            result += "dayRegistered"
        }

        if (base.orderIndex != current.orderIndex) {
            result += "orderIndex"
        }

        if (base.note != current.note) {
            result += "note"
        }

        if (base.deleted != current.deleted) {
            result += "deleted"
        }

        return result
    }

    // =========================================================
    // FIELD VALUE
    // =========================================================

    private fun fieldValue(
        snapshot: DailyPlanSnapshot,
        field: String
    ): String {

        return when (field) {

            "taskId" ->
                snapshot.taskId.toString()

            "date" ->
                snapshot.date

            "plannedMinutes" ->
                snapshot.plannedMinutes.toString()

            "actualMinutes" ->
                snapshot.actualMinutes.toString()

            "earnedScore" ->
                snapshot.earnedScore.toString()

            "completed" ->
                snapshot.completed.toString()

            "exerciseCalories" ->
                snapshot.exerciseCalories.toString()

            "dayRegistered" ->
                snapshot.dayRegistered.toString()

            "orderIndex" ->
                snapshot.orderIndex.toString()

            "note" ->
                snapshot.note

            "deleted" ->
                snapshot.deleted.toString()

            else ->
                ""
        }
    }

    // =========================================================
    // SAVE CONFLICT
    // =========================================================

    private suspend fun saveConflict(
        entityId: String,
        base: DailyPlanSnapshot,
        local: DailyPlanSnapshot,
        remote: DailyPlanSnapshot,
        fields: List<String>
    ) {

        val existing =
            categoryDao.getUnresolvedSyncConflict(
                entityType = "daily_plan",
                entityId = entityId
            )

        if (existing != null) {
            return
        }

        categoryDao.insertSyncConflict(
            SyncConflict(
                entityType = "daily_plan",
                entityId = entityId,
                baseSnapshot = snapshotJson(
                    base
                ),
                localSnapshot = snapshotJson(
                    local
                ),
                remoteSnapshot = snapshotJson(
                    remote
                ),
                conflictingFields =
                    json.encodeToString(
                        ListSerializer(
                            String.serializer()
                        ),
                        fields
                    )
            )
        )
    }

    // =========================================================
    // LOCAL SNAPSHOT
    // =========================================================

    private fun snapshotFromLocal(
        local: DailyPlan
    ): DailyPlanSnapshot {

        return DailyPlanSnapshot(
            taskId = local.taskId,
            date = local.date,
            plannedMinutes = local.plannedMinutes,
            actualMinutes = local.actualMinutes,
            earnedScore = local.earnedScore,
            completed = local.completed,
            exerciseCalories = local.exerciseCalories,
            dayRegistered = local.dayRegistered,
            orderIndex = local.orderIndex,
            note = local.note,
            deleted = local.deleted
        )
    }

    // =========================================================
    // CLOUD SNAPSHOT
    // =========================================================

    private fun snapshotFromCloud(
        cloud: SupabaseDailyPlan,
        localTaskId: Int
    ): DailyPlanSnapshot {

        /*
         * مهم:
         *
         * cloud.localTaskId را اینجا عمداً استفاده نمی‌کنیم.
         *
         * چون ممکن است مربوط به یک ID قدیمی یا Web باشد.
         *
         * Snapshot باید همیشه ID واقعی Task در Room را نگه دارد.
         */

        return DailyPlanSnapshot(
            taskId = localTaskId,

            date =
                cloud.date,

            plannedMinutes =
                cloud.plannedMinutes,

            actualMinutes =
                cloud.actualMinutes,

            earnedScore =
                cloud.earnedScore,

            completed =
                cloud.completed,

            exerciseCalories =
                cloud.exerciseCalories,

            dayRegistered =
                cloud.dayRegistered,

            orderIndex =
                cloud.orderIndex,

            note =
                cloud.note,

            deleted =
                cloud.deletedAt != null
        )
    }

    // =========================================================
    // SNAPSHOT JSON
    // =========================================================

    private fun snapshotJson(
        snapshot: DailyPlanSnapshot
    ): String {

        return json.encodeToString(
            snapshot
        )
    }

    private fun decodeSnapshot(
        value: String?
    ): DailyPlanSnapshot? {

        if (value.isNullOrBlank()) {
            return null
        }

        return try {

            json.decodeFromString<DailyPlanSnapshot>(
                value
            )

        } catch (e: Exception) {

            Log.e(
                TAG,
                "SYNC: invalid syncBase: ${e.message}"
            )

            Log.e(
                AUDIT_TAG,
                "INVALID syncBase; snapshot will be recreated",
                e
            )

            null
        }
    }

    // =========================================================
    // SAFE DAILY PLAN LOCAL ID
    // =========================================================

    private fun chooseSafeLocalPlanId(
        requestedId: Int,
        cloudId: String,
        existingPlans: List<DailyPlan>
    ): Int {

        val existing =
            existingPlans.firstOrNull {
                it.id == requestedId
            }

        /*
         * اگر ID آزاد است، همان ID را استفاده می‌کنیم.
         */

        if (existing == null) {
            return requestedId
        }

        /*
         * اگر همین Cloud Plan قبلاً با همین ID وجود داشته،
         * همان ID از نظر منطقی قابل استفاده است.
         *
         * در حالت عادی نباید اینجا برسیم چون قبل از Insert
         * cloudId را بررسی کرده‌ایم.
         */

        if (existing.cloudId == cloudId) {
            return requestedId
        }

        /*
         * Collision:
         *
         * requestedId متعلق به DailyPlan دیگری است.
         * بنابراین از UUID یک ID جدید و آزاد می‌سازیم.
         */

        var candidate =
            localIdFromUuid(
                cloudId
            )

        if (candidate <= 0) {
            candidate = 1
        }

        while (
            existingPlans.any {
                it.id == candidate
            }
        ) {

            candidate =
                if (candidate == Int.MAX_VALUE) {
                    1
                } else {
                    candidate + 1
                }
        }

        return candidate
    }

    // =========================================================
    // UUID -> LOCAL ID
    // =========================================================

    private fun localIdFromUuid(
        uuid: String
    ): Int {

        var hash = 0

        for (char in uuid) {

            hash =
                hash * 31 + char.code

            if (hash == Int.MIN_VALUE) {
                hash = 0
            }
        }

        return kotlin.math.abs(hash)
            .coerceAtLeast(1)
    }
}
