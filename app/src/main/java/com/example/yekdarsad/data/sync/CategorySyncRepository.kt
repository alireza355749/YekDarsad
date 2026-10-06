package com.example.yekdarsad.data.sync

import android.util.Log
import com.example.yekdarsad.data.Category
import com.example.yekdarsad.data.CategoryDao
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.time.Instant
import kotlinx.serialization.builtins.serializer

class CategorySyncRepository(
    private val categoryDao: CategoryDao
) {

    private val supabase =
        SupabaseClientProvider.client

    private val json =
        Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        }

    /*
     * دسته‌های اصلی برنامه نباید با یک حذف اشتباه
     * در جریان همگام‌سازی از بین بروند.
     */
    private val protectedCategoryNames = setOf(
        "شغل",
        "ورزش",
        "معنویت",
        "زبان انگلیسی",
        "مهارت‌های شغلی",
        "کتاب و پادکست",
        "تفریح",
        "متفرقه"
    )

    private fun isProtectedCategory(name: String): Boolean {
        return name.trim() in protectedCategoryNames
    }

    suspend fun syncCategories() {

        Log.d("CategorySync", "SYNC: started")

        val userId =
            supabase.auth.currentUserOrNull()?.id
                ?: run {
                    Log.d(
                        "CategorySync",
                        "SYNC: no logged-in user"
                    )
                    return
                }

        /*
         * =====================================================
         * LOCAL
         * =====================================================
         */

        val localCategories =
            categoryDao.getAllIncludingDeleted()

        Log.d(
            "CategorySync",
            "SYNC: local=${localCategories.size}"
        )

        /*
         * =====================================================
         * CLOUD
         * =====================================================
         */

        var cloudCategories =
            loadCloudCategories(userId)

        Log.d(
            "CategorySync",
            "SYNC: cloud=${cloudCategories.size}"
        )

        /*
         * =====================================================
         * ANDROID -> CLOUD MERGE
         * =====================================================
         */

        for (local in localCategories) {

            try {

                var cloud =
                    findCloudMatch(
                        local = local,
                        cloudCategories = cloudCategories
                    )

                /*
                 * =================================================
                 * LOCAL RECORD HAS NO CLOUD RECORD
                 * =================================================
                 */

                if (cloud == null) {

                    /*
                     * دسته حذف‌شده‌ای که در Cloud وجود ندارد
                     * نباید دائماً از Room حذف شود.
                     */
                    if (local.deleted) {

                        Log.d(
                            "CategorySync",
                            "SYNC: local-only deleted category retained " +
                                    "id=${local.id}, name='${local.name}'"
                        )

                        continue
                    }

                    /*
                     * دسته جدید Android -> Cloud
                     */

                    val inserted =
                        supabase
                            .from("categories")
                            .insert(
                                SupabaseCategoryInsert(
                                    userId = userId,
                                    name = local.name,
                                    icon = local.icon,
                                    localId = local.id
                                )
                            ) {
                                select()
                            }
                            .decodeSingle<SupabaseCategory>()

                    val cloudId =
                        inserted.id

                    if (cloudId != null) {

                        val snapshot =
                            categorySnapshot(
                                name = local.name,
                                icon = local.icon,
                                deleted = false
                            )

                        categoryDao.updateSyncState(
                            categoryId = local.id,
                            cloudId = cloudId,
                            syncBase = snapshot,
                            syncVersion = inserted.version
                        )

                        Log.d(
                            "CategorySync",
                            "SYNC: INSERT '${local.name}' -> cloudId=$cloudId"
                        )

                        cloudCategories =
                            cloudCategories + inserted
                    }

                    continue
                }

                /*
                 * =================================================
                 * CLOUD RECORD EXISTS
                 * =================================================
                 */

                val cloudId =
                    cloud.id

                if (cloudId == null) {

                    Log.w(
                        "CategorySync",
                        "SYNC: cloud category has no id, skipped"
                    )

                    continue
                }

                /*
                 * =================================================
                 * FIRST BOOTSTRAP
                 * =================================================
                 */

                if (
                    local.cloudId == null ||
                    local.syncBase == null
                ) {

                    bootstrapExistingCategory(
                        local = local,
                        cloud = cloud,
                        cloudId = cloudId,
                        userId = userId
                    )

                    continue
                }

                /*
                 * =================================================
                 * NORMAL THREE-WAY MERGE
                 * =================================================
                 */

                val base =
                    decodeSnapshot(local.syncBase)

                val localSnapshot =
                    CategorySnapshot(
                        name = local.name,
                        icon = local.icon,
                        deleted = local.deleted
                    )

                val remoteSnapshot =
                    CategorySnapshot(
                        name = cloud.name,
                        icon = cloud.icon,
                        deleted = cloud.deletedAt != null
                    )

                val mergeResult =
                    mergeCategory(
                        base = base,
                        local = localSnapshot,
                        remote = remoteSnapshot
                    )

                /*
                 * دسته‌های اصلی در برابر حذف اشتباه محافظت می‌شوند.
                 * تعارض‌های نام و آیکون همچنان قابل ثبت هستند.
                 */

                val isProtected =
                    isProtectedCategory(local.name) ||
                            isProtectedCategory(cloud.name)

                val conflictingFields =
                    if (isProtected) {
                        mergeResult.conflictingFields
                            .filterNot { it == "deleted" }
                    } else {
                        mergeResult.conflictingFields
                    }

                if (conflictingFields.isNotEmpty()) {

                    val existingConflict =
                        categoryDao.getUnresolvedSyncConflict(
                            entityType = "category",
                            entityId = cloudId
                        )

                    if (existingConflict == null) {

                        categoryDao.insertSyncConflict(
                            SyncConflict(
                                entityType = "category",
                                entityId = cloudId,
                                baseSnapshot =
                                    json.encodeToString(
                                        CategorySnapshot.serializer(),
                                        base
                                    ),
                                localSnapshot =
                                    json.encodeToString(
                                        CategorySnapshot.serializer(),
                                        localSnapshot
                                    ),
                                remoteSnapshot =
                                    json.encodeToString(
                                        CategorySnapshot.serializer(),
                                        remoteSnapshot
                                    ),
                                conflictingFields =
                                    json.encodeToString(
                                        ListSerializer(String.serializer()),
                                        conflictingFields
                                    )
                            )
                        )

                        Log.w(
                            "CategorySync",
                            "SYNC: CONFLICT '${local.name}' " +
                                    "fields=$conflictingFields"
                        )
                    }

                    continue
                }

                /*
                 * برای دسته اصلی، نتیجه حذف را نادیده می‌گیریم.
                 */
                val merged =
                    if (isProtected) {
                        mergeResult.merged.copy(
                            deleted = false
                        )
                    } else {
                        mergeResult.merged
                    }

                val mergedSnapshot =
                    json.encodeToString(
                        CategorySnapshot.serializer(),
                        merged
                    )

                /*
                 * =================================================
                 * SOFT DELETE
                 * =================================================
                 */

                if (merged.deleted) {

                    if (!remoteSnapshot.deleted) {

                        supabase
                            .from("categories")
                            .update(
                                SupabaseCategoryStatusUpdate(
                                    deletedAt = Instant.now().toString()
                                )
                            ) {
                                filter {
                                    eq("id", cloudId)
                                    eq("user_id", userId)
                                }
                            }

                        Log.d(
                            "CategorySync",
                            "SYNC: SOFT DELETE '${local.name}' " +
                                    "cloudId=$cloudId"
                        )
                    }

                    /*
                     * رکورد Room را دائماً حذف نمی‌کنیم.
                     * سابقه و cloudId برای همگام‌سازی‌های بعدی لازم‌اند.
                     */

                    categoryDao.updateSyncSnapshot(
                        categoryId = local.id,
                        syncBase = mergedSnapshot,
                        syncVersion = cloud.version
                    )

                    continue
                }

                /*
                 * =================================================
                 * RESTORE CLOUD CATEGORY
                 * =================================================
                 */

                if (
                    remoteSnapshot.deleted &&
                    !merged.deleted
                ) {

                    supabase
                        .from("categories")
                        .update(
                            SupabaseCategoryStatusUpdate(
                                deletedAt = null
                            )
                        ) {
                            filter {
                                eq("id", cloudId)
                                eq("user_id", userId)
                            }
                        }

                    Log.d(
                        "CategorySync",
                        "SYNC: RESTORED CLOUD '${local.name}'"
                    )
                }

                /*
                 * =================================================
                 * CHANGE DETECTION
                 * =================================================
                 */

                val localChanged =
                    localSnapshot != base

                val remoteChanged =
                    remoteSnapshot != base

                /*
                 * =================================================
                 * LOCAL-ONLY CHANGES
                 * =================================================
                 */

                if (
                    localChanged &&
                    !remoteChanged
                ) {

                    supabase
                        .from("categories")
                        .update(
                            SupabaseCategoryUpdate(
                                localId = cloud.localId ?: local.id,
                                name = merged.name,
                                icon = merged.icon
                            )
                        ) {
                            filter {
                                eq("id", cloudId)
                                eq("user_id", userId)
                            }
                        }

                    val snapshot =
                        categorySnapshot(
                            name = merged.name,
                            icon = merged.icon,
                            deleted = false
                        )

                    categoryDao.applySyncedCategory(
                        categoryId = local.id,
                        cloudId = cloudId,
                        name = merged.name,
                        icon = merged.icon,
                        deleted = false,
                        syncBase = snapshot,
                        syncVersion = cloud.version
                    )

                    Log.d(
                        "CategorySync",
                        "SYNC: LOCAL -> CLOUD '${merged.name}'"
                    )

                    continue
                }

                /*
                 * =================================================
                 * REMOTE-ONLY CHANGES
                 * =================================================
                 */

                if (
                    !localChanged &&
                    remoteChanged
                ) {

                    val snapshot =
                        categorySnapshot(
                            name = merged.name,
                            icon = merged.icon,
                            deleted = false
                        )

                    categoryDao.applySyncedCategory(
                        categoryId = local.id,
                        cloudId = cloudId,
                        name = merged.name,
                        icon = merged.icon,
                        deleted = false,
                        syncBase = snapshot,
                        syncVersion = cloud.version
                    )

                    Log.d(
                        "CategorySync",
                        "SYNC: CLOUD -> LOCAL '${merged.name}'"
                    )

                    continue
                }

                /*
                 * =================================================
                 * BOTH CHANGED
                 * =================================================
                 */

                if (
                    localChanged &&
                    remoteChanged
                ) {

                    if (
                        merged.name != remoteSnapshot.name ||
                        merged.icon != remoteSnapshot.icon
                    ) {

                        supabase
                            .from("categories")
                            .update(
                                SupabaseCategoryUpdate(
                                    localId = cloud.localId ?: local.id,
                                    name = merged.name,
                                    icon = merged.icon
                                )
                            ) {
                                filter {
                                    eq("id", cloudId)
                                    eq("user_id", userId)
                                }
                            }
                    }

                    /*
                     * اگر رکورد Cloud حذف نرم شده باشد، آن را
                     * دوباره فعال می‌کنیم.
                     */
                    if (remoteSnapshot.deleted) {

                        supabase
                            .from("categories")
                            .update(
                                SupabaseCategoryStatusUpdate(
                                    deletedAt = null
                                )
                            ) {
                                filter {
                                    eq("id", cloudId)
                                    eq("user_id", userId)
                                }
                            }
                    }

                    val snapshot =
                        categorySnapshot(
                            name = merged.name,
                            icon = merged.icon,
                            deleted = false
                        )

                    categoryDao.applySyncedCategory(
                        categoryId = local.id,
                        cloudId = cloudId,
                        name = merged.name,
                        icon = merged.icon,
                        deleted = false,
                        syncBase = snapshot,
                        syncVersion = cloud.version
                    )

                    Log.d(
                        "CategorySync",
                        "SYNC: MERGED '${merged.name}'"
                    )

                    continue
                }

                /*
                 * =================================================
                 * NOTHING CHANGED
                 * =================================================
                 */

                categoryDao.updateSyncSnapshot(
                    categoryId = local.id,
                    syncBase = mergedSnapshot,
                    syncVersion = cloud.version
                )

            } catch (e: Exception) {

                Log.e(
                    "CategorySync",
                    "SYNC: category failed id=${local.id}",
                    e
                )
            }
        }

        /*
         * =====================================================
         * CLOUD -> ANDROID
         * =====================================================
         */

        var currentLocalCategories =
            categoryDao.getAllIncludingDeleted()

        cloudCategories =
            loadCloudCategories(userId)

        for (cloud in cloudCategories) {

            val cloudId =
                cloud.id ?: continue

            /*
             * دسته‌های اصلی حذف‌شده در Cloud باید بازیابی شوند.
             * دسته‌های سفارشی حذف‌شده را خودکار برنمی‌گردانیم.
             */

            if (
                cloud.deletedAt != null &&
                !isProtectedCategory(cloud.name)
            ) {
                continue
            }

            /*
             * =================================================
             * MATCH BY CLOUD ID
             * =================================================
             */

            val alreadyLocalByCloudId =
                currentLocalCategories.firstOrNull {
                    it.cloudId == cloudId
                }

            if (alreadyLocalByCloudId != null) {

                if (
                    alreadyLocalByCloudId.deleted ||
                    cloud.deletedAt != null
                ) {

                    val shouldRestore =
                        isProtectedCategory(cloud.name) &&
                                cloud.deletedAt != null

                    if (shouldRestore) {

                        supabase
                            .from("categories")
                            .update(
                                SupabaseCategoryStatusUpdate(
                                    deletedAt = null
                                )
                            ) {
                                filter {
                                    eq("id", cloudId)
                                    eq("user_id", userId)
                                }
                            }

                        val snapshot =
                            categorySnapshot(
                                name = cloud.name,
                                icon = cloud.icon,
                                deleted = false
                            )

                        categoryDao.applySyncedCategory(
                            categoryId = alreadyLocalByCloudId.id,
                            cloudId = cloudId,
                            name = cloud.name,
                            icon = cloud.icon,
                            deleted = false,
                            syncBase = snapshot,
                            syncVersion = cloud.version
                        )

                        Log.d(
                            "CategorySync",
                            "SYNC: RESTORED PROTECTED CATEGORY " +
                                    "'${cloud.name}'"
                        )

                        currentLocalCategories =
                            categoryDao.getAllIncludingDeleted()
                    }
                }

                continue
            }

            /*
             * =================================================
             * MATCH BY NAME
             * =================================================
             */

            val alreadyLocalByName =
                currentLocalCategories.firstOrNull {
                    it.name.trim() == cloud.name.trim()
                }

            if (alreadyLocalByName != null) {

                if (
                    alreadyLocalByName.cloudId == null &&
                    !alreadyLocalByName.deleted
                ) {

                    bootstrapExistingCategory(
                        local = alreadyLocalByName,
                        cloud = cloud,
                        cloudId = cloudId,
                        userId = userId
                    )

                    currentLocalCategories =
                        categoryDao.getAllIncludingDeleted()
                }

                continue
            }

            /*
             * =================================================
             * SAFE LOCAL ID
             * =================================================
             */

            val localId =
                chooseSafeLocalId(
                    preferredId = cloud.localId,
                    localCategories = currentLocalCategories,
                    cloudCategories = cloudCategories
                )

            val sameLocalId =
                currentLocalCategories.firstOrNull {
                    it.id == localId
                }

            if (sameLocalId != null) {

                Log.w(
                    "CategorySync",
                    "SYNC: local id collision for '${cloud.name}' " +
                            "localId=$localId existing='${sameLocalId.name}'"
                )

                continue
            }

            try {

                val snapshot =
                    categorySnapshot(
                        name = cloud.name,
                        icon = cloud.icon,
                        deleted = false
                    )

                categoryDao.insert(
                    Category(
                        id = localId,
                        cloudId = cloudId,
                        name = cloud.name,
                        icon = cloud.icon,
                        deleted = false,
                        syncBase = snapshot,
                        syncVersion = cloud.version
                    )
                )

                /*
                 * local_id را فقط وقتی اصلاح می‌کنیم که لازم باشد.
                 */

                if (cloud.localId != localId) {

                    supabase
                        .from("categories")
                        .update(
                            SupabaseCategoryUpdate(
                                localId = localId,
                                name = cloud.name,
                                icon = cloud.icon
                            )
                        ) {
                            filter {
                                eq("id", cloudId)
                                eq("user_id", userId)
                            }
                        }

                    Log.d(
                        "CategorySync",
                        "SYNC: fixed cloud local_id " +
                                "'${cloud.name}' -> $localId"
                    )
                }

                currentLocalCategories =
                    categoryDao.getAllIncludingDeleted()

                Log.d(
                    "CategorySync",
                    "SYNC: NEW CLOUD -> LOCAL '${cloud.name}' " +
                            "id=$localId cloudId=$cloudId"
                )

            } catch (e: Exception) {

                Log.e(
                    "CategorySync",
                    "SYNC: CLOUD -> LOCAL failed '${cloud.name}'",
                    e
                )
            }
        }

        Log.d(
            "CategorySync",
            "SYNC: finished"
        )
    }

    /*
     * =========================================================
     * SAFE LOCAL CATEGORY ID
     * =========================================================
     */

    private fun chooseSafeLocalId(
        preferredId: Int?,
        localCategories: List<Category>,
        cloudCategories: List<SupabaseCategory>
    ): Int {

        val usedLocalIds =
            buildSet {

                localCategories.forEach {
                    add(it.id)
                }

                cloudCategories.forEach {
                    it.localId?.let { id ->
                        add(id)
                    }
                }
            }

        if (
            preferredId != null &&
            preferredId > 0 &&
            preferredId !in usedLocalIds
        ) {
            return preferredId
        }

        var candidate =
            ((usedLocalIds.maxOrNull() ?: 0).toLong()) + 1L

        if (candidate <= 0L) {
            candidate = 1L
        }

        while (
            candidate <= Int.MAX_VALUE &&
            candidate.toInt() in usedLocalIds
        ) {
            candidate++
        }

        if (candidate > Int.MAX_VALUE) {

            candidate = 1L

            while (
                candidate <= Int.MAX_VALUE &&
                candidate.toInt() in usedLocalIds
            ) {
                candidate++
            }
        }

        return candidate.toInt()
    }

    /*
     * =========================================================
     * CLOUD LOAD
     * =========================================================
     */

    private suspend fun loadCloudCategories(
        userId: String
    ): List<SupabaseCategory> {

        return supabase
            .from("categories")
            .select {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList<SupabaseCategory>()
    }

    /*
     * =========================================================
     * FIND CLOUD MATCH
     * =========================================================
     */

    private fun findCloudMatch(
        local: Category,
        cloudCategories: List<SupabaseCategory>
    ): SupabaseCategory? {

        /*
         * اولویت اول: UUID ابری
         */

        if (local.cloudId != null) {

            val byCloudId =
                cloudCategories.firstOrNull {
                    it.id == local.cloudId
                }

            if (byCloudId != null) {
                return byCloudId
            }
        }

        /*
         * تطبیق local_id فقط همراه نام انجام می‌شود.
         */

        val byLocalId =
            cloudCategories.firstOrNull {
                it.localId == local.id &&
                        it.name.trim() == local.name.trim()
            }

        if (byLocalId != null) {
            return byLocalId
        }

        /*
         * Bootstrap قدیمی بر اساس نام.
         */

        if (local.cloudId == null) {

            return cloudCategories.firstOrNull {
                it.name.trim() == local.name.trim()
            }
        }

        return null
    }

    /*
     * =========================================================
     * BOOTSTRAP EXISTING RECORD
     * =========================================================
     */

    private suspend fun bootstrapExistingCategory(
        local: Category,
        cloud: SupabaseCategory,
        cloudId: String,
        userId: String
    ) {

        val cloudSnapshot =
            CategorySnapshot(
                name = cloud.name,
                icon = cloud.icon,
                deleted = cloud.deletedAt != null
            )

        /*
         * اگر دسته اصلی در Cloud حذف نرم شده باشد،
         * آن را بازیابی می‌کنیم.
         *
         * دسته سفارشی حذف‌شده را بدون تصمیم روشن
         * به‌صورت خودکار بازیابی نمی‌کنیم.
         */

        if (cloudSnapshot.deleted) {

            if (
                isProtectedCategory(local.name) ||
                isProtectedCategory(cloud.name)
            ) {

                supabase
                    .from("categories")
                    .update(
                        SupabaseCategoryStatusUpdate(
                            deletedAt = null
                        )
                    ) {
                        filter {
                            eq("id", cloudId)
                            eq("user_id", userId)
                        }
                    }

                val snapshot =
                    categorySnapshot(
                        name = local.name,
                        icon = local.icon,
                        deleted = false
                    )

                categoryDao.applySyncedCategory(
                    categoryId = local.id,
                    cloudId = cloudId,
                    name = local.name,
                    icon = local.icon,
                    deleted = false,
                    syncBase = snapshot,
                    syncVersion = cloud.version
                )

                Log.d(
                    "CategorySync",
                    "SYNC: restored protected category '${local.name}'"
                )

            } else {

                /*
                 * حذف نرم Cloud را نگه می‌داریم؛
                 * رکورد Room را دائماً پاک نمی‌کنیم.
                 */

                categoryDao.applySyncedCategory(
                    categoryId = local.id,
                    cloudId = cloudId,
                    name = local.name,
                    icon = local.icon,
                    deleted = true,
                    syncBase = json.encodeToString(
                        CategorySnapshot.serializer(),
                        cloudSnapshot
                    ),
                    syncVersion = cloud.version
                )

                Log.d(
                    "CategorySync",
                    "SYNC: bootstrap retained cloud deletion '${local.name}'"
                )
            }

            return
        }

        /*
         * Cloud فعال است.
         * آن را به عنوان وضعیت اولیه مشترک ثبت می‌کنیم.
         */

        val snapshot =
            categorySnapshot(
                name = cloud.name,
                icon = cloud.icon,
                deleted = false
            )

        categoryDao.applySyncedCategory(
            categoryId = local.id,
            cloudId = cloudId,
            name = cloud.name,
            icon = cloud.icon,
            deleted = false,
            syncBase = snapshot,
            syncVersion = cloud.version
        )

        /*
         * local_id را در Cloud تغییر نمی‌دهیم مگر اینکه
         * با شناسه محلی فعلی برابر نباشد و مقدار امن باشد.
         */

        if (cloud.localId == null) {

            val allCloudCategories =
                loadCloudCategories(userId)

            val localCategories =
                categoryDao.getAllIncludingDeleted()

            val safeLocalId =
                chooseSafeLocalId(
                    preferredId = local.id,
                    localCategories = localCategories.filter {
                        it.id != local.id
                    },
                    cloudCategories = allCloudCategories.filter {
                        it.id != cloudId
                    }
                )

            try {

                supabase
                    .from("categories")
                    .update(
                        SupabaseCategoryUpdate(
                            localId = safeLocalId,
                            name = cloud.name,
                            icon = cloud.icon
                        )
                    ) {
                        filter {
                            eq("id", cloudId)
                            eq("user_id", userId)
                        }
                    }

            } catch (e: Exception) {

                Log.e(
                    "CategorySync",
                    "SYNC: bootstrap local_id update failed '${cloud.name}'",
                    e
                )
            }
        }

        Log.d(
            "CategorySync",
            "SYNC: bootstrap '${cloud.name}' cloudId=$cloudId"
        )
    }

    /*
     * =========================================================
     * CATEGORY MERGE
     * =========================================================
     */

    private fun mergeCategory(
        base: CategorySnapshot,
        local: CategorySnapshot,
        remote: CategorySnapshot
    ): CategoryMergeResult {

        val conflicts =
            mutableListOf<String>()

        /*
         * NAME
         */

        val localNameChanged =
            local.name != base.name

        val remoteNameChanged =
            remote.name != base.name

        val mergedName =
            when {

                localNameChanged &&
                        remoteNameChanged &&
                        local.name != remote.name -> {

                    conflicts += "name"
                    local.name
                }

                localNameChanged ->
                    local.name

                remoteNameChanged ->
                    remote.name

                else ->
                    base.name
            }

        /*
         * ICON
         */

        val localIconChanged =
            local.icon != base.icon

        val remoteIconChanged =
            remote.icon != base.icon

        val mergedIcon =
            when {

                localIconChanged &&
                        remoteIconChanged &&
                        local.icon != remote.icon -> {

                    conflicts += "icon"
                    local.icon
                }

                localIconChanged ->
                    local.icon

                remoteIconChanged ->
                    remote.icon

                else ->
                    base.icon
            }

        /*
         * DELETE
         */

        val localDeletedChanged =
            local.deleted != base.deleted

        val remoteDeletedChanged =
            remote.deleted != base.deleted

        val mergedDeleted =
            when {

                localDeletedChanged &&
                        remoteDeletedChanged &&
                        local.deleted != remote.deleted -> {

                    conflicts += "deleted"
                    local.deleted
                }

                localDeletedChanged ->
                    local.deleted

                remoteDeletedChanged ->
                    remote.deleted

                else ->
                    base.deleted
            }

        return CategoryMergeResult(
            merged = CategorySnapshot(
                name = mergedName,
                icon = mergedIcon,
                deleted = mergedDeleted
            ),
            conflictingFields = conflicts
        )
    }

    /*
     * =========================================================
     * SNAPSHOT
     * =========================================================
     */

    private fun categorySnapshot(
        name: String,
        icon: String,
        deleted: Boolean
    ): String {

        return json.encodeToString(
            CategorySnapshot.serializer(),
            CategorySnapshot(
                name = name,
                icon = icon,
                deleted = deleted
            )
        )
    }

    private fun decodeSnapshot(
        snapshot: String
    ): CategorySnapshot {

        return json.decodeFromString(
            CategorySnapshot.serializer(),
            snapshot
        )
    }

    /*
     * =========================================================
     * INTERNAL MODELS
     * =========================================================
     */

    @Serializable
    private data class CategorySnapshot(
        val name: String,
        val icon: String,
        val deleted: Boolean
    )

    private data class CategoryMergeResult(
        val merged: CategorySnapshot,
        val conflictingFields: List<String>
    )

    @Serializable
    private data class SupabaseCategoryInsert(
        @SerialName("user_id")
        val userId: String,

        val name: String,

        val icon: String,

        @SerialName("local_id")
        val localId: Int
    )

    /*
     * این مدل فقط deleted_at را تغییر می‌دهد.
     * بنابراین update عادی نام و آیکون نمی‌تواند
     * ناخواسته دسته‌ای را فعال یا حذف کند.
     */

    @Serializable
    private data class SupabaseCategoryStatusUpdate(
        @SerialName("deleted_at")
        val deletedAt: String?
    )
}