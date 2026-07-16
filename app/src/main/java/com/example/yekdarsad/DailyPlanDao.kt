package com.example.yekdarsad.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyPlanDao {

    @Insert
    suspend fun insert(plan: DailyPlan)

    @Transaction
    @Query(
        "SELECT * FROM daily_plans WHERE date = :date"
    )
    fun getByDate(date: String): Flow<List<DailyPlanWithTask>>

    @Query(
        """
        UPDATE daily_plans
        SET completed = :completed
        WHERE id = :id
        """
    )
    suspend fun updateCompleted(
        id: Int,
        completed: Boolean
    )

    @Query(
        """
        UPDATE daily_plans
        SET
            actualMinutes = :actualMinutes,
            earnedScore = :earnedScore,
            completed = :completed
        WHERE id = :id
        """
    )
    suspend fun updateProgress(
        id: Int,
        actualMinutes: Int,
        earnedScore: Double,
        completed: Boolean
    )

    @Query(
        "DELETE FROM daily_plans WHERE id = :id"
    )
    suspend fun deleteById(id: Int)

}