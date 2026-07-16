package com.example.yekdarsad.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert
    suspend fun insert(task: Task): Long

    @Transaction
    @Query(
        "SELECT * FROM tasks WHERE categoryId = :categoryId"
    )
    fun getTasksByCategory(
        categoryId: Int
    ): Flow<List<TaskWithDurations>>

    @Query(
        "SELECT * FROM tasks ORDER BY title"
    )
    fun getAllTasks(): Flow<List<Task>>

    @Query(
        "DELETE FROM tasks WHERE id = :taskId"
    )
    suspend fun deleteById(taskId: Int)

}