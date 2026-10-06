package com.example.yekdarsad.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDurationDao {

    @Insert
    suspend fun insert(
        duration: ActivityDuration
    )

    @Query(
        "SELECT * FROM activity_durations WHERE taskId = :taskId"
    )
    fun getByTask(
        taskId: Int
    ): Flow<List<ActivityDuration>>

    @Query(
        "DELETE FROM activity_durations WHERE taskId = :taskId"
    )
    suspend fun deleteByTask(
        taskId: Int
    )

    @Query(
        "DELETE FROM activity_durations WHERE taskId = :taskId"
    )
    suspend fun deleteByTaskId(
        taskId: Int
    )
}