package com.example.yekdarsad.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert
    suspend fun insert(
        task: Task
    ): Long

    @Transaction
    @Query(
        """
        SELECT *
        FROM tasks
        WHERE categoryId = :categoryId
          AND deleted = 0
        ORDER BY orderIndex ASC
        """
    )
    fun getTasksByCategory(
        categoryId: Int
    ): Flow<List<TaskWithDurations>>

    @Query(
        """
        SELECT *
        FROM tasks
        WHERE categoryId = :categoryId
          AND deleted = 0
        ORDER BY orderIndex ASC
        """
    )
    suspend fun getTasksByCategoryOnce(
        categoryId: Int
    ): List<Task>

    @Query(
        """
        SELECT *
        FROM tasks
        WHERE deleted = 0
        ORDER BY orderIndex ASC
        """
    )
    fun getAllTasks(): Flow<List<Task>>

    @Query(
        """
        SELECT *
        FROM tasks
        ORDER BY orderIndex ASC
        """
    )
    suspend fun getAllTasksOnce(): List<Task>

    @Query(
        """
        SELECT *
        FROM tasks
        ORDER BY orderIndex ASC
        """
    )
    suspend fun getAllTasksIncludingDeleted(): List<Task>

    @Query(
        """
        SELECT *
        FROM tasks
        WHERE id = :taskId
        LIMIT 1
        """
    )
    suspend fun getById(
        taskId: Int
    ): Task?

    @Query(
        """
        SELECT *
        FROM tasks
        WHERE cloudId = :cloudId
        LIMIT 1
        """
    )
    suspend fun getByCloudId(
        cloudId: String
    ): Task?

    @Query(
        """
        UPDATE tasks
        SET
            cloudId = :cloudId,
            syncBase = :syncBase,
            syncVersion = :syncVersion
        WHERE id = :taskId
        """
    )
    suspend fun updateSyncState(
        taskId: Int,
        cloudId: String,
        syncBase: String,
        syncVersion: Long
    )

    @Query(
        """
        UPDATE tasks
        SET
            cloudId = :cloudId,
            categoryId = :categoryId,
            title = :title,
            coefficient = :coefficient,
            type = :type,
            orderIndex = :orderIndex,
            caloriesPerHour = :caloriesPerHour,
            deleted = :deleted,
            syncBase = :syncBase,
            syncVersion = :syncVersion
        WHERE id = :taskId
        """
    )
    suspend fun applySyncedTask(
        taskId: Int,
        cloudId: String,
        categoryId: Int,
        title: String,
        coefficient: Double,
        type: String,
        orderIndex: Int,
        caloriesPerHour: Double,
        deleted: Boolean,
        syncBase: String,
        syncVersion: Long
    )

    @Query(
        """
        UPDATE tasks
        SET
            syncBase = :syncBase,
            syncVersion = :syncVersion
        WHERE id = :taskId
        """
    )
    suspend fun updateSyncSnapshot(
        taskId: Int,
        syncBase: String,
        syncVersion: Long
    )

    @Query(
        """
        UPDATE tasks
        SET deleted = 1
        WHERE id = :taskId
        """
    )
    suspend fun markDeleted(
        taskId: Int
    ): Int

    @Query(
        """
        DELETE FROM tasks
        WHERE id = :taskId
        """
    )
    suspend fun deleteById(
        taskId: Int
    )

    @Query(
        """
        UPDATE tasks
        SET
            title = :title,
            coefficient = :coefficient
        WHERE id = :taskId
        """
    )
    suspend fun updateTask(
        taskId: Int,
        title: String,
        coefficient: Double
    )

    @Query(
        """
        UPDATE tasks
        SET
            title = :title,
            coefficient = :coefficient,
            caloriesPerHour = :caloriesPerHour
        WHERE id = :taskId
        """
    )
    suspend fun updateTaskWithCalories(
        taskId: Int,
        title: String,
        coefficient: Double,
        caloriesPerHour: Double
    )

    @Query(
        """
        UPDATE tasks
        SET caloriesPerHour = :caloriesPerHour
        WHERE id = :taskId
        """
    )
    suspend fun updateCaloriesPerHour(
        taskId: Int,
        caloriesPerHour: Double
    )

    @Query(
        """
        UPDATE tasks
        SET orderIndex = :orderIndex
        WHERE id = :taskId
        """
    )
    suspend fun updateOrder(
        taskId: Int,
        orderIndex: Int
    )

    @Query(
        """
        UPDATE tasks
        SET
            categoryId = :categoryId,
            title = :title,
            coefficient = :coefficient,
            type = :type,
            orderIndex = :orderIndex,
            caloriesPerHour = :caloriesPerHour
        WHERE id = :taskId
        """
    )
    suspend fun updateTaskFull(
        taskId: Int,
        categoryId: Int,
        title: String,
        coefficient: Double,
        type: String,
        orderIndex: Int,
        caloriesPerHour: Double
    )
}