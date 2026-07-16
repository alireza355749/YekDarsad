package com.example.yekdarsad.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyTaskDao {

    @Insert
    suspend fun insert(dailyTask: DailyTask)


    @Query("SELECT * FROM daily_tasks ORDER BY id DESC")
    fun getAll(): Flow<List<DailyTask>>

}