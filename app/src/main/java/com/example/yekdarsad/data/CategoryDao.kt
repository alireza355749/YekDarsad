package com.example.yekdarsad.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.yekdarsad.data.sync.SyncConflict
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert
    suspend fun insert(category: Category): Long

    @Update
    suspend fun update(category: Category)

    @Delete
    suspend fun delete(category: Category)

    @Query("SELECT * FROM categories WHERE deleted = 0")
    suspend fun getAll(): List<Category>

    @Query("SELECT * FROM categories WHERE deleted = 0")
    fun observeAll(): Flow<List<Category>>

    @Query("SELECT * FROM categories")
    suspend fun getAllIncludingDeleted(): List<Category>

    @Query("SELECT * FROM categories WHERE cloudId = :cloudId LIMIT 1")
    suspend fun getByCloudId(cloudId: String): Category?

    @Query("UPDATE categories SET cloudId = :cloudId WHERE id = :categoryId")
    suspend fun updateCloudId(categoryId: Int, cloudId: String)

    // ==========================================
    // SYNC
    // ==========================================

    @Query("""
        UPDATE categories
        SET cloudId = :cloudId,
            syncBase = :syncBase,
            syncVersion = :syncVersion
        WHERE id = :categoryId
    """)
    suspend fun updateSyncState(
        categoryId: Int,
        cloudId: String,
        syncBase: String,
        syncVersion: Long
    )

    @Query("""
        UPDATE categories
        SET cloudId = :cloudId,
            name = :name,
            icon = :icon,
            deleted = :deleted,
            syncBase = :syncBase,
            syncVersion = :syncVersion
        WHERE id = :categoryId
    """)
    suspend fun applySyncedCategory(
        categoryId: Int,
        cloudId: String,
        name: String,
        icon: String,
        deleted: Boolean,
        syncBase: String,
        syncVersion: Long
    )

    @Query("""
        UPDATE categories
        SET syncBase = :syncBase,
            syncVersion = :syncVersion
        WHERE id = :categoryId
    """)
    suspend fun updateSyncSnapshot(
        categoryId: Int,
        syncBase: String,
        syncVersion: Long
    )

    // ==========================================
    // DELETE
    // ==========================================

    @Query("UPDATE categories SET deleted = 1 WHERE id = :categoryId")
    suspend fun markDeleted(categoryId: Int)

    @Query("DELETE FROM categories WHERE id = :categoryId")
    suspend fun permanentlyDelete(categoryId: Int)

    // ==========================================
    // SYNC CONFLICTS
    // ==========================================

    @Insert
    suspend fun insertSyncConflict(conflict: SyncConflict): Long

    @Query("""
        SELECT * FROM sync_conflicts
        WHERE resolved = 0
        ORDER BY createdAt ASC
    """)
    suspend fun getUnresolvedSyncConflicts(): List<SyncConflict>

    @Query("""
        SELECT * FROM sync_conflicts
        WHERE entityType = :entityType
          AND entityId = :entityId
          AND resolved = 0
        ORDER BY createdAt DESC
        LIMIT 1
    """)
    suspend fun getUnresolvedSyncConflict(
        entityType: String,
        entityId: String
    ): SyncConflict?

    @Query("""
        UPDATE sync_conflicts
        SET resolved = 1,
            resolvedAt = :resolvedAt
        WHERE id = :conflictId
    """)
    suspend fun resolveSyncConflict(
        conflictId: Long,
        resolvedAt: Long = System.currentTimeMillis()
    )

    @Query("""
        DELETE FROM sync_conflicts
        WHERE resolved = 1
    """)
    suspend fun deleteResolvedSyncConflicts()
}