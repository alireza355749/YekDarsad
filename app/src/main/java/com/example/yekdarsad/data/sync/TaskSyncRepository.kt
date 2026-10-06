package com.example.yekdarsad.data.sync

import android.util.Log
import com.example.yekdarsad.data.Category
import com.example.yekdarsad.data.CategoryDao
import com.example.yekdarsad.data.Task
import com.example.yekdarsad.data.TaskDao
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class TaskSyncRepository(
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

    suspend fun syncTasks() {

        Log.d("TaskSync", "SYNC: started")

        val userId =
            supabase.auth.currentUserOrNull()?.id
                ?: return

        val localTasks =
            taskDao.getAllTasksIncludingDeleted()

        val localCategories =
            categoryDao.getAllIncludingDeleted()

        val cloudCategories =
            supabase
                .from("categories")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<SupabaseCategory>()

        val cloudTasks =
            supabase
                .from("tasks")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<SupabaseTask>()

        Log.d(
            "TaskSync",
            "SYNC: local=${localTasks.size}, cloud=${cloudTasks.size}"
        )

        /*
         * =====================================================
         * LOCAL -> CLOUD / MERGE
         * =====================================================
         */

        for (local in localTasks) {

            try {

                Log.d(
                    "TaskSync",
                    "SYNC: LOCAL CHECK " +
                            "id=${local.id} " +
                            "cloudId=${local.cloudId} " +
                            "categoryId=${local.categoryId} " +
                            "title='${local.title}' " +
                            "deleted=${local.deleted} " +
                            "hasBase=${local.syncBase != null}"
                )

                val existing =
                    findCloudTask(
                        local = local,
                        cloudTasks = cloudTasks
                    )

                if (existing != null) {

                    Log.d(
                        "TaskSync",
                        "SYNC: LOCAL MATCH " +
                                "localId=${local.id} " +
                                "title='${local.title}' " +
                                "-> cloudId=${existing.id} " +
                                "cloudTitle='${existing.title}' " +
                                "cloudLocalId=${existing.localId} " +
                                "cloudDeleted=${existing.deletedAt != null}"
                    )
                } else {

                    Log.d(
                        "TaskSync",
                        "SYNC: LOCAL NEW " +
                                "localId=${local.id} " +
                                "title='${local.title}'"
                    )
                }

                /*
                 * =================================================
                 * Task جدید Android
                 * =================================================
                 */

                if (existing == null) {

                    if (local.deleted) {

                        taskDao.deleteById(local.id)

                        Log.d(
                            "TaskSync",
                            "SYNC: local new task already deleted " +
                                    "'${local.title}'"
                        )

                        continue
                    }

                    val categoryCloudId =
                        findCategoryCloudId(
                            localCategoryId = local.categoryId,
                            localCategories = localCategories,
                            cloudCategories = cloudCategories
                        )

                    if (categoryCloudId == null) {

                        Log.e(
                            "TaskSync",
                            "SYNC: cannot create cloud task because " +
                                    "category has no cloudId " +
                                    "localCategoryId=${local.categoryId} " +
                                    "title='${local.title}'"
                        )

                        continue
                    }

                    Log.d(
                        "TaskSync",
                        "SYNC: LOCAL -> CLOUD INSERT " +
                                "title='${local.title}' " +
                                "localId=${local.id} " +
                                "localCategoryId=${local.categoryId} " +
                                "categoryCloudId=$categoryCloudId"
                    )

                    val inserted =
                        supabase
                            .from("tasks")
                            .insert(
                                SupabaseTaskInsert(
                                    userId = userId,
                                    categoryId = categoryCloudId,
                                    title = local.title,
                                    coefficient = local.coefficient,
                                    type = local.type,
                                    orderIndex = local.orderIndex,
                                    caloriesPerHour = local.caloriesPerHour,
                                    localId = local.id,
                                    localCategoryId = local.categoryId
                                )
                            ) {
                                select()
                            }
                            .decodeSingle<SupabaseTask>()

                    val cloudId =
                        inserted.id

                    if (cloudId != null) {

                        val snapshot =
                            snapshot(
                                local = local,
                                categoryLocalId = local.categoryId,
                                deleted = false
                            )

                        taskDao.updateSyncState(
                            taskId = local.id,
                            cloudId = cloudId,
                            syncBase = snapshot,
                            syncVersion = inserted.version
                        )

                        Log.d(
                            "TaskSync",
                            "SYNC: INSERT SUCCESS " +
                                    "'${local.title}' " +
                                    "localId=${local.id} " +
                                    "cloudId=$cloudId"
                        )
                    } else {

                        Log.e(
                            "TaskSync",
                            "SYNC: INSERT returned null cloudId " +
                                    "title='${local.title}'"
                        )
                    }

                    continue
                }

                val cloudId =
                    existing.id
                        ?: continue

                /*
                 * =================================================
                 * BOOTSTRAP
                 * =================================================
                 */

                if (
                    local.cloudId == null ||
                    local.syncBase == null
                ) {

                    if (existing.deletedAt != null) {

                        taskDao.deleteById(local.id)

                        Log.d(
                            "TaskSync",
                            "SYNC: bootstrap cloud-deleted " +
                                    "'${local.title}'"
                        )

                        continue
                    }

                    val categoryLocalId =
                        findCategoryLocalId(
                            cloudCategoryId = existing.categoryId,
                            localCategories = localCategories,
                            cloudCategories = cloudCategories
                        )
                            ?: existing.localCategoryId
                            ?: local.categoryId

                    val remoteSnapshot =
                        TaskSnapshot(
                            categoryLocalId = categoryLocalId,
                            title = existing.title,
                            coefficient = existing.coefficient,
                            type = existing.type,
                            orderIndex = existing.orderIndex,
                            caloriesPerHour = existing.caloriesPerHour,
                            deleted = false
                        )

                    taskDao.applySyncedTask(
                        taskId = local.id,
                        cloudId = cloudId,
                        categoryId = categoryLocalId,
                        title = existing.title,
                        coefficient = existing.coefficient,
                        type = existing.type,
                        orderIndex = existing.orderIndex,
                        caloriesPerHour = existing.caloriesPerHour,
                        deleted = false,
                        syncBase = json.encodeToString(
                            TaskSnapshot.serializer(),
                            remoteSnapshot
                        ),
                        syncVersion = existing.version
                    )

                    val refreshedForBootstrap =
                        supabase
                            .from("tasks")
                            .select {
                                filter {
                                    eq("user_id", userId)
                                }
                            }
                            .decodeList<SupabaseTask>()

                    val safeLocalId =
                        chooseSafeCloudLocalId(
                            preferredId = local.id,
                            cloudTasks = refreshedForBootstrap,
                            currentCloudId = cloudId
                        )

                    if (
                        existing.localId != safeLocalId ||
                        existing.localCategoryId != categoryLocalId
                    ) {

                        try {

                            supabase
                                .from("tasks")
                                .update(
                                    SupabaseTaskUpdate(
                                        localId = safeLocalId,
                                        localCategoryId = categoryLocalId,
                                        categoryId = existing.categoryId,
                                        title = existing.title,
                                        coefficient = existing.coefficient,
                                        type = existing.type,
                                        orderIndex = existing.orderIndex,
                                        caloriesPerHour = existing.caloriesPerHour
                                    )
                                ) {
                                    filter {
                                        eq("id", cloudId)
                                        eq("user_id", userId)
                                    }
                                }

                            Log.d(
                                "TaskSync",
                                "SYNC: bootstrap metadata fixed " +
                                        "'${existing.title}' " +
                                        "local_id=$safeLocalId"
                            )

                        } catch (e: Exception) {

                            Log.e(
                                "TaskSync",
                                "SYNC: bootstrap metadata update failed " +
                                        "task=$cloudId",
                                e
                            )
                        }
                    }

                    Log.d(
                        "TaskSync",
                        "SYNC: bootstrap '${existing.title}'"
                    )

                    continue
                }

                /*
                 * =================================================
                 * SNAPSHOTS
                 * =================================================
                 */

                val base =
                    try {
                        json.decodeFromString(
                            TaskSnapshot.serializer(),
                            local.syncBase
                        )
                    } catch (e: Exception) {

                        Log.e(
                            "TaskSync",
                            "SYNC: invalid syncBase for '${local.title}'",
                            e
                        )

                        continue
                    }

                val localCategoryId =
                    local.categoryId

                val remoteCategoryId =
                    findCategoryLocalId(
                        cloudCategoryId = existing.categoryId,
                        localCategories = localCategories,
                        cloudCategories = cloudCategories
                    )
                        ?: existing.localCategoryId
                        ?: localCategoryId

                val localSnapshot =
                    TaskSnapshot(
                        categoryLocalId = localCategoryId,
                        title = local.title,
                        coefficient = local.coefficient,
                        type = local.type,
                        orderIndex = local.orderIndex,
                        caloriesPerHour = local.caloriesPerHour,
                        deleted = local.deleted
                    )

                val remoteSnapshot =
                    TaskSnapshot(
                        categoryLocalId = remoteCategoryId,
                        title = existing.title,
                        coefficient = existing.coefficient,
                        type = existing.type,
                        orderIndex = existing.orderIndex,
                        caloriesPerHour = existing.caloriesPerHour,
                        deleted = existing.deletedAt != null
                    )

                val merge =
                    mergeTask(
                        base = base,
                        local = localSnapshot,
                        remote = remoteSnapshot
                    )

                /*
                 * =================================================
                 * CONFLICT
                 * =================================================
                 */

                if (merge.conflictingFields.isNotEmpty()) {

                    val conflict =
                        categoryDao.getUnresolvedSyncConflict(
                            entityType = "task",
                            entityId = cloudId
                        )

                    if (conflict == null) {

                        categoryDao.insertSyncConflict(
                            SyncConflict(
                                entityType = "task",
                                entityId = cloudId,
                                baseSnapshot = json.encodeToString(
                                    TaskSnapshot.serializer(),
                                    base
                                ),
                                localSnapshot = json.encodeToString(
                                    TaskSnapshot.serializer(),
                                    localSnapshot
                                ),
                                remoteSnapshot = json.encodeToString(
                                    TaskSnapshot.serializer(),
                                    remoteSnapshot
                                ),
                                conflictingFields = json.encodeToString(
                                    ListSerializer(String.serializer()),
                                    merge.conflictingFields
                                )
                            )
                        )

                        Log.w(
                            "TaskSync",
                            "SYNC: CONFLICT '${local.title}' " +
                                    "fields=${merge.conflictingFields}"
                        )
                    }

                    continue
                }

                val merged =
                    merge.merged

                /*
                 * =================================================
                 * DELETE
                 * =================================================
                 */

                if (merged.deleted) {

                    if (!remoteSnapshot.deleted) {

                        supabase
                            .from("tasks")
                            .delete {
                                filter {
                                    eq("id", cloudId)
                                    eq("user_id", userId)
                                }
                            }

                        Log.d(
                            "TaskSync",
                            "SYNC: SOFT DELETE '${local.title}'"
                        )
                    }

                    taskDao.deleteById(local.id)

                    continue
                }

                val localChanged =
                    localSnapshot != base

                val remoteChanged =
                    remoteSnapshot != base

                /*
                 * =================================================
                 * ONLY ANDROID CHANGED
                 * =================================================
                 */

                if (localChanged && !remoteChanged) {

                    val categoryCloudId =
                        findCategoryCloudId(
                            localCategoryId = merged.categoryLocalId,
                            localCategories = localCategories,
                            cloudCategories = cloudCategories
                        )
                            ?: existing.categoryId

                    val updated =
                        supabase
                            .from("tasks")
                            .update(
                                SupabaseTaskUpdate(
                                    localId = local.id,
                                    localCategoryId = merged.categoryLocalId,
                                    categoryId = categoryCloudId,
                                    title = merged.title,
                                    coefficient = merged.coefficient,
                                    type = merged.type,
                                    orderIndex = merged.orderIndex,
                                    caloriesPerHour = merged.caloriesPerHour
                                )
                            ) {
                                filter {
                                    eq("id", cloudId)
                                    eq("user_id", userId)
                                }

                                select()
                            }
                            .decodeSingle<SupabaseTask>()

                    taskDao.applySyncedTask(
                        taskId = local.id,
                        cloudId = cloudId,
                        categoryId = merged.categoryLocalId,
                        title = merged.title,
                        coefficient = merged.coefficient,
                        type = merged.type,
                        orderIndex = merged.orderIndex,
                        caloriesPerHour = merged.caloriesPerHour,
                        deleted = false,
                        syncBase = json.encodeToString(
                            TaskSnapshot.serializer(),
                            merged
                        ),
                        syncVersion = updated.version
                    )

                    Log.d(
                        "TaskSync",
                        "SYNC: LOCAL -> CLOUD '${merged.title}'"
                    )

                    continue
                }

                /*
                 * =================================================
                 * ONLY WEB CHANGED
                 * =================================================
                 */

                if (!localChanged && remoteChanged) {

                    taskDao.applySyncedTask(
                        taskId = local.id,
                        cloudId = cloudId,
                        categoryId = merged.categoryLocalId,
                        title = merged.title,
                        coefficient = merged.coefficient,
                        type = merged.type,
                        orderIndex = merged.orderIndex,
                        caloriesPerHour = merged.caloriesPerHour,
                        deleted = false,
                        syncBase = json.encodeToString(
                            TaskSnapshot.serializer(),
                            merged
                        ),
                        syncVersion = existing.version
                    )

                    syncCloudTaskMetadataIfNeeded(
                        userId = userId,
                        cloudTask = existing,
                        localTaskId = local.id,
                        localCategoryId = merged.categoryLocalId,
                        allCloudTasks = cloudTasks
                    )

                    Log.d(
                        "TaskSync",
                        "SYNC: CLOUD -> LOCAL '${merged.title}'"
                    )

                    continue
                }

                /*
                 * =================================================
                 * BOTH CHANGED INDEPENDENTLY
                 * =================================================
                 */

                if (localChanged && remoteChanged) {

                    val categoryCloudId =
                        findCategoryCloudId(
                            localCategoryId = merged.categoryLocalId,
                            localCategories = localCategories,
                            cloudCategories = cloudCategories
                        )
                            ?: existing.categoryId

                    val cloudNeedsUpdate =
                        merged.categoryLocalId !=
                                remoteSnapshot.categoryLocalId ||
                                merged.title != remoteSnapshot.title ||
                                merged.coefficient != remoteSnapshot.coefficient ||
                                merged.type != remoteSnapshot.type ||
                                merged.orderIndex != remoteSnapshot.orderIndex ||
                                merged.caloriesPerHour !=
                                remoteSnapshot.caloriesPerHour

                    val finalVersion =
                        if (cloudNeedsUpdate) {

                            val updated =
                                supabase
                                    .from("tasks")
                                    .update(
                                        SupabaseTaskUpdate(
                                            localId = local.id,
                                            localCategoryId =
                                                merged.categoryLocalId,
                                            categoryId =
                                                categoryCloudId,
                                            title = merged.title,
                                            coefficient =
                                                merged.coefficient,
                                            type =
                                                merged.type,
                                            orderIndex =
                                                merged.orderIndex,
                                            caloriesPerHour =
                                                merged.caloriesPerHour
                                        )
                                    ) {
                                        filter {
                                            eq("id", cloudId)
                                            eq("user_id", userId)
                                        }

                                        select()
                                    }
                                    .decodeSingle<SupabaseTask>()

                            updated.version

                        } else {

                            syncCloudTaskMetadataIfNeeded(
                                userId = userId,
                                cloudTask = existing,
                                localTaskId = local.id,
                                localCategoryId =
                                    merged.categoryLocalId,
                                allCloudTasks = cloudTasks
                            )

                            existing.version
                        }

                    taskDao.applySyncedTask(
                        taskId = local.id,
                        cloudId = cloudId,
                        categoryId = merged.categoryLocalId,
                        title = merged.title,
                        coefficient = merged.coefficient,
                        type = merged.type,
                        orderIndex = merged.orderIndex,
                        caloriesPerHour = merged.caloriesPerHour,
                        deleted = false,
                        syncBase = json.encodeToString(
                            TaskSnapshot.serializer(),
                            merged
                        ),
                        syncVersion = finalVersion
                    )

                    Log.d(
                        "TaskSync",
                        "SYNC: MERGED '${merged.title}'"
                    )

                    continue
                }

                /*
                 * =================================================
                 * NOTHING CHANGED
                 * =================================================
                 */

                taskDao.updateSyncSnapshot(
                    taskId = local.id,
                    syncBase = json.encodeToString(
                        TaskSnapshot.serializer(),
                        merged
                    ),
                    syncVersion = existing.version
                )

                syncCloudTaskMetadataIfNeeded(
                    userId = userId,
                    cloudTask = existing,
                    localTaskId = local.id,
                    localCategoryId = merged.categoryLocalId,
                    allCloudTasks = cloudTasks
                )

            } catch (e: Exception) {

                Log.e(
                    "TaskSync",
                    "SYNC: failed localId=${local.id} title='${local.title}'",
                    e
                )
            }
        }

        /*
         * =====================================================
         * CLOUD -> ANDROID
         * =====================================================
         */

        val refreshedCloudTasks =
            supabase
                .from("tasks")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<SupabaseTask>()

        val refreshedLocalTasks =
            taskDao.getAllTasksIncludingDeleted()

        Log.d(
            "TaskSync",
            "SYNC: CLOUD -> LOCAL checking " +
                    "cloud=${refreshedCloudTasks.size}, " +
                    "local=${refreshedLocalTasks.size}"
        )

        for (cloud in refreshedCloudTasks) {

            try {

                val cloudId =
                    cloud.id
                        ?: continue

                Log.d(
                    "TaskSync",
                    "SYNC: CLOUD CHECK " +
                            "title='${cloud.title}' " +
                            "id=$cloudId " +
                            "localId=${cloud.localId} " +
                            "localCategoryId=${cloud.localCategoryId} " +
                            "categoryId=${cloud.categoryId} " +
                            "deletedAt=${cloud.deletedAt}"
                )

                if (cloud.deletedAt != null) {

                    Log.d(
                        "TaskSync",
                        "SYNC: CLOUD SKIP deleted " +
                                "'${cloud.title}'"
                    )

                    continue
                }

                val alreadyLocal =
                    refreshedLocalTasks.firstOrNull {
                        it.cloudId == cloudId
                    }

                if (alreadyLocal != null) {

                    val categoryLocalId =
                        findCategoryLocalId(
                            cloudCategoryId = cloud.categoryId,
                            localCategories = localCategories,
                            cloudCategories = cloudCategories
                        )
                            ?: cloud.localCategoryId
                            ?: alreadyLocal.categoryId

                    syncCloudTaskMetadataIfNeeded(
                        userId = userId,
                        cloudTask = cloud,
                        localTaskId = alreadyLocal.id,
                        localCategoryId = categoryLocalId,
                        allCloudTasks = refreshedCloudTasks
                    )

                    Log.d(
                        "TaskSync",
                        "SYNC: CLOUD SKIP already local " +
                                "'${cloud.title}' " +
                                "localId=${alreadyLocal.id}"
                    )

                    continue
                }

                val categoryLocalId =
                    findCategoryLocalId(
                        cloudCategoryId = cloud.categoryId,
                        localCategories = localCategories,
                        cloudCategories = cloudCategories
                    )
                        ?: cloud.localCategoryId

                if (categoryLocalId == null) {

                    Log.e(
                        "TaskSync",
                        "SYNC: cannot map cloud task category " +
                                "task=$cloudId " +
                                "title='${cloud.title}' " +
                                "category=${cloud.categoryId}"
                    )

                    continue
                }

                Log.d(
                    "TaskSync",
                    "SYNC: resolved category " +
                            "'${cloud.title}' -> localCategoryId=$categoryLocalId"
                )

                if (cloud.localId != null) {

                    val sameId =
                        refreshedLocalTasks.firstOrNull {
                            it.id == cloud.localId
                        }

                    if (sameId != null) {

                        if (sameId.cloudId == null) {

                            val snapshot =
                                TaskSnapshot(
                                    categoryLocalId = categoryLocalId,
                                    title = cloud.title,
                                    coefficient = cloud.coefficient,
                                    type = cloud.type,
                                    orderIndex = cloud.orderIndex,
                                    caloriesPerHour = cloud.caloriesPerHour,
                                    deleted = false
                                )

                            taskDao.applySyncedTask(
                                taskId = sameId.id,
                                cloudId = cloudId,
                                categoryId = categoryLocalId,
                                title = cloud.title,
                                coefficient = cloud.coefficient,
                                type = cloud.type,
                                orderIndex = cloud.orderIndex,
                                caloriesPerHour = cloud.caloriesPerHour,
                                deleted = false,
                                syncBase = json.encodeToString(
                                    TaskSnapshot.serializer(),
                                    snapshot
                                ),
                                syncVersion = cloud.version
                            )

                            Log.d(
                                "TaskSync",
                                "SYNC: BOOTSTRAP CLOUD -> LOCAL " +
                                        "'${cloud.title}' " +
                                        "id=${sameId.id} " +
                                        "cloudId=$cloudId"
                            )

                            continue
                        }

                        if (sameId.cloudId != cloudId) {

                            Log.w(
                                "TaskSync",
                                "SYNC: local_id collision " +
                                        "cloudTask='${cloud.title}' " +
                                        "cloudLocalId=${cloud.localId} " +
                                        "alreadyUsedBy='${sameId.title}' " +
                                        "existingCloudId=${sameId.cloudId}"
                            )
                        }
                    }
                }

                val snapshot =
                    TaskSnapshot(
                        categoryLocalId = categoryLocalId,
                        title = cloud.title,
                        coefficient = cloud.coefficient,
                        type = cloud.type,
                        orderIndex = cloud.orderIndex,
                        caloriesPerHour = cloud.caloriesPerHour,
                        deleted = false
                    )

                val generatedLocalId =
                    taskDao.insert(
                        Task(
                            id = 0,
                            cloudId = cloudId,
                            categoryId = categoryLocalId,
                            title = cloud.title,
                            coefficient = cloud.coefficient,
                            type = cloud.type,
                            orderIndex = cloud.orderIndex,
                            caloriesPerHour = cloud.caloriesPerHour,
                            deleted = false,
                            syncBase = json.encodeToString(
                                TaskSnapshot.serializer(),
                                snapshot
                            ),
                            syncVersion = cloud.version
                        )
                    )

                val safeCloudLocalId =
                    chooseSafeCloudLocalId(
                        preferredId = generatedLocalId.toInt(),
                        cloudTasks = refreshedCloudTasks,
                        currentCloudId = cloudId
                    )

                if (cloud.localId != safeCloudLocalId) {

                    supabase
                        .from("tasks")
                        .update(
                            SupabaseTaskUpdate(
                                localId = safeCloudLocalId,
                                localCategoryId = categoryLocalId,
                                categoryId = cloud.categoryId,
                                title = cloud.title,
                                coefficient = cloud.coefficient,
                                type = cloud.type,
                                orderIndex = cloud.orderIndex,
                                caloriesPerHour = cloud.caloriesPerHour
                            )
                        ) {
                            filter {
                                eq("id", cloudId)
                                eq("user_id", userId)
                            }
                        }

                    Log.d(
                        "TaskSync",
                        "SYNC: assigned safe local_id=$safeCloudLocalId " +
                                "to cloud task '${cloud.title}' " +
                                "cloudId=$cloudId"
                    )
                }

                Log.d(
                    "TaskSync",
                    "SYNC: NEW CLOUD -> LOCAL " +
                            "'${cloud.title}' " +
                            "localId=$generatedLocalId " +
                            "cloudLocalId=$safeCloudLocalId " +
                            "cloudId=$cloudId"
                )

            } catch (e: Exception) {

                Log.e(
                    "TaskSync",
                    "SYNC: CLOUD -> LOCAL failed " +
                            "task=${cloud.id} " +
                            "title='${cloud.title}'",
                    e
                )
            }
        }

        Log.d(
            "TaskSync",
            "SYNC: finished successfully"
        )
    }

    /*
     * =========================================================
     * CLOUD TASK METADATA
     * =========================================================
     */

    private suspend fun syncCloudTaskMetadataIfNeeded(
        userId: String,
        cloudTask: SupabaseTask,
        localTaskId: Int,
        localCategoryId: Int,
        allCloudTasks: List<SupabaseTask>
    ) {

        val cloudId =
            cloudTask.id
                ?: return

        val safeLocalId =
            chooseSafeCloudLocalId(
                preferredId = localTaskId,
                cloudTasks = allCloudTasks,
                currentCloudId = cloudId
            )

        val needsUpdate =
            cloudTask.localId != safeLocalId ||
                    cloudTask.localCategoryId != localCategoryId

        if (!needsUpdate) {
            return
        }

        try {

            supabase
                .from("tasks")
                .update(
                    SupabaseTaskUpdate(
                        localId = safeLocalId,
                        localCategoryId = localCategoryId,
                        categoryId = cloudTask.categoryId,
                        title = cloudTask.title,
                        coefficient = cloudTask.coefficient,
                        type = cloudTask.type,
                        orderIndex = cloudTask.orderIndex,
                        caloriesPerHour = cloudTask.caloriesPerHour
                    )
                ) {
                    filter {
                        eq("id", cloudId)
                        eq("user_id", userId)
                    }
                }

            Log.d(
                "TaskSync",
                "SYNC: metadata fixed " +
                        "'${cloudTask.title}' " +
                        "cloudId=$cloudId " +
                        "local_id=$safeLocalId " +
                        "local_category_id=$localCategoryId"
            )

        } catch (e: Exception) {

            Log.e(
                "TaskSync",
                "SYNC: metadata update failed " +
                        "task=$cloudId " +
                        "title='${cloudTask.title}'",
                e
            )
        }
    }

    /*
     * =========================================================
     * SAFE CLOUD LOCAL ID
     * =========================================================
     */

    private fun chooseSafeCloudLocalId(
        preferredId: Int,
        cloudTasks: List<SupabaseTask>,
        currentCloudId: String? = null
    ): Int {

        val usedIds =
            cloudTasks
                .filter {
                    it.id != currentCloudId &&
                            it.localId != null
                }
                .map {
                    it.localId!!
                }
                .toHashSet()

        if (
            preferredId > 0 &&
            preferredId !in usedIds
        ) {
            return preferredId
        }

        var candidate =
            if (preferredId > 0) {
                preferredId.toLong() + 1L
            } else {
                1L
            }

        if (candidate > Int.MAX_VALUE) {
            candidate = 1L
        }

        while (
            candidate <= Int.MAX_VALUE &&
            candidate.toInt() in usedIds
        ) {
            candidate++
        }

        if (candidate > Int.MAX_VALUE) {

            candidate = 1L

            while (
                candidate.toInt() in usedIds &&
                candidate <= Int.MAX_VALUE
            ) {
                candidate++
            }
        }

        return candidate.toInt()
    }

    /*
     * =========================================================
     * FIND CLOUD TASK
     * =========================================================
     *
     * ترتیب مهم:
     *
     * 1. cloudId = هویت اصلی
     * 2. برای رکوردهای bootstrap:
     *    local_id فقط وقتی معتبر است که عنوان و دسته
     *    هم با رکورد Cloud یکی باشند.
     * 3. برای Task جدید بدون cloudId/syncBase:
     *    category + title
     *
     * این مانع از این می‌شود که یک Task جدید Android
     * به خاطر برخورد local_id با یک Task قدیمی Cloud،
     * اشتباهی به آن متصل شود.
     */

    private fun findCloudTask(
        local: Task,
        cloudTasks: List<SupabaseTask>
    ): SupabaseTask? {

        /*
         * =================================================
         * 1. CLOUD ID
         * =================================================
         */

        if (local.cloudId != null) {

            val byCloudId =
                cloudTasks.firstOrNull {
                    it.id == local.cloudId
                }

            if (byCloudId != null) {
                return byCloudId
            }
        }

        /*
         * =================================================
         * 2. BOOTSTRAP BY LOCAL ID
         * =================================================
         *
         * فقط برای رکوردی که هنوز cloudId/syncBase ندارد
         * و فقط اگر category + title هم یکی باشند.
         */

        if (
            local.cloudId == null &&
            local.syncBase == null
        ) {

            val byLocalIdAndContent =
                cloudTasks.firstOrNull {

                    it.localId == local.id &&
                            it.deletedAt == null &&
                            it.title.trim() == local.title.trim() &&
                            (
                                    it.localCategoryId == local.categoryId ||
                                            it.localCategoryId == null
                                    )
                }

            if (byLocalIdAndContent != null) {

                Log.d(
                    "TaskSync",
                    "SYNC: bootstrap match by localId+content " +
                            "localId=${local.id} " +
                            "title='${local.title}' " +
                            "cloudId=${byLocalIdAndContent.id}"
                )

                return byLocalIdAndContent
            }
        }

        /*
         * =================================================
         * 3. NEW LOCAL TASK MATCH BY CONTENT
         * =================================================
         *
         * فقط برای Taskهای بدون cloudId.
         *
         * اگر Task جدیدی با همان عنوان و همان دسته از قبل
         * در Cloud باشد، از ساخت duplicate جلوگیری می‌کنیم.
         */

        if (local.cloudId == null) {

            val byCategoryAndTitle =
                cloudTasks.firstOrNull {

                    it.deletedAt == null &&
                            it.localCategoryId == local.categoryId &&
                            it.title.trim() == local.title.trim()
                }

            if (byCategoryAndTitle != null) {

                Log.d(
                    "TaskSync",
                    "SYNC: matched local by category+title " +
                            "localId=${local.id} " +
                            "title='${local.title}' " +
                            "cloudId=${byCategoryAndTitle.id}"
                )

                return byCategoryAndTitle
            }
        }

        return null
    }

    /*
     * =========================================================
     * CATEGORY CLOUD ID
     * =========================================================
     */

    private fun findCategoryCloudId(
        localCategoryId: Int,
        localCategories: List<Category>,
        cloudCategories: List<SupabaseCategory>
    ): String? {

        val category =
            localCategories.firstOrNull {
                it.id == localCategoryId
            }
                ?: return null

        if (category.cloudId != null) {
            return category.cloudId
        }

        return cloudCategories
            .firstOrNull {
                it.localId == localCategoryId
            }
            ?.id
    }

    /*
     * =========================================================
     * CATEGORY LOCAL ID
     * =========================================================
     */

    private fun findCategoryLocalId(
        cloudCategoryId: String,
        localCategories: List<Category>,
        cloudCategories: List<SupabaseCategory>
    ): Int? {

        val localByCloudId =
            localCategories.firstOrNull {
                it.cloudId == cloudCategoryId
            }

        if (localByCloudId != null) {
            return localByCloudId.id
        }

        return cloudCategories
            .firstOrNull {
                it.id == cloudCategoryId
            }
            ?.localId
    }

    /*
     * =========================================================
     * MERGE
     * =========================================================
     */

    private fun mergeTask(
        base: TaskSnapshot,
        local: TaskSnapshot,
        remote: TaskSnapshot
    ): TaskMergeResult {

        val conflicts =
            mutableListOf<String>()

        val category =
            mergeField(
                field = "categoryId",
                base = base.categoryLocalId,
                local = local.categoryLocalId,
                remote = remote.categoryLocalId,
                conflicts = conflicts
            )

        val title =
            mergeField(
                field = "title",
                base = base.title,
                local = local.title,
                remote = remote.title,
                conflicts = conflicts
            )

        val coefficient =
            mergeField(
                field = "coefficient",
                base = base.coefficient,
                local = local.coefficient,
                remote = remote.coefficient,
                conflicts = conflicts
            )

        val type =
            mergeField(
                field = "type",
                base = base.type,
                local = local.type,
                remote = remote.type,
                conflicts = conflicts
            )

        val orderIndex =
            mergeField(
                field = "orderIndex",
                base = base.orderIndex,
                local = local.orderIndex,
                remote = remote.orderIndex,
                conflicts = conflicts
            )

        val caloriesPerHour =
            mergeField(
                field = "caloriesPerHour",
                base = base.caloriesPerHour,
                local = local.caloriesPerHour,
                remote = remote.caloriesPerHour,
                conflicts = conflicts
            )

        val deleted =
            when {
                local.deleted == base.deleted &&
                        remote.deleted == base.deleted ->
                    base.deleted

                local.deleted != base.deleted &&
                        remote.deleted == base.deleted ->
                    local.deleted

                local.deleted == base.deleted &&
                        remote.deleted != base.deleted ->
                    remote.deleted

                local.deleted == remote.deleted ->
                    local.deleted

                else -> {
                    conflicts += "deleted"
                    local.deleted
                }
            }

        return TaskMergeResult(
            merged = TaskSnapshot(
                categoryLocalId = category,
                title = title,
                coefficient = coefficient,
                type = type,
                orderIndex = orderIndex,
                caloriesPerHour = caloriesPerHour,
                deleted = deleted
            ),
            conflictingFields = conflicts
        )
    }

    private fun <T> mergeField(
        field: String,
        base: T,
        local: T,
        remote: T,
        conflicts: MutableList<String>
    ): T {

        return when {

            local == base && remote == base ->
                base

            local != base && remote == base ->
                local

            local == base && remote != base ->
                remote

            local == remote ->
                local

            else -> {
                conflicts += field
                local
            }
        }
    }

    /*
     * =========================================================
     * SNAPSHOT
     * =========================================================
     */

    private fun snapshot(
        local: Task,
        categoryLocalId: Int,
        deleted: Boolean
    ): String {

        return json.encodeToString(
            TaskSnapshot.serializer(),
            TaskSnapshot(
                categoryLocalId = categoryLocalId,
                title = local.title,
                coefficient = local.coefficient,
                type = local.type,
                orderIndex = local.orderIndex,
                caloriesPerHour = local.caloriesPerHour,
                deleted = deleted
            )
        )
    }

    /*
     * =========================================================
     * MODELS
     * =========================================================
     */

    @Serializable
    private data class TaskSnapshot(
        val categoryLocalId: Int,
        val title: String,
        val coefficient: Double,
        val type: String,
        val orderIndex: Int,
        val caloriesPerHour: Double,
        val deleted: Boolean
    )

    private data class TaskMergeResult(
        val merged: TaskSnapshot,
        val conflictingFields: List<String>
    )

    @Serializable
    private data class SupabaseTaskInsert(

        @SerialName("user_id")
        val userId: String,

        @SerialName("category_id")
        val categoryId: String,

        val title: String,

        val coefficient: Double,

        val type: String,

        @SerialName("order_index")
        val orderIndex: Int,

        @SerialName("calories_per_hour")
        val caloriesPerHour: Double,

        @SerialName("local_id")
        val localId: Int,

        @SerialName("local_category_id")
        val localCategoryId: Int
    )
}