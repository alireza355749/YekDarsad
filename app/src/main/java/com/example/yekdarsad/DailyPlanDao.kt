package com.example.yekdarsad.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyPlanDao {

    @Insert
    suspend fun insert(
        plan: DailyPlan
    )

    @Transaction
    @Query(
        """
        SELECT daily_plans.*
        FROM daily_plans
        INNER JOIN tasks
            ON tasks.id = daily_plans.taskId
        WHERE daily_plans.date = :date
          AND daily_plans.deleted = 0
          AND tasks.deleted = 0
        ORDER BY daily_plans.orderIndex ASC
        """
    )
    fun getByDate(
        date: String
    ): Flow<List<DailyPlanWithTask>>

    @Query(
        """
        SELECT *
        FROM daily_plans
        """
    )
    suspend fun getAllPlansOnce(): List<DailyPlan>

    @Query(
        """
        SELECT *
        FROM daily_plans
        WHERE completed = 1
          AND deleted = 0
        """
    )
    fun getAllCompletedPlans(): Flow<List<DailyPlan>>

    @Query(
        """
        SELECT
            daily_plans.date
            || '|'
            || categories.name
            || '|'
            || daily_plans.actualMinutes
        FROM daily_plans
        INNER JOIN tasks
            ON tasks.id = daily_plans.taskId
        INNER JOIN categories
            ON categories.id = tasks.categoryId
        WHERE daily_plans.actualMinutes > 0
          AND daily_plans.deleted = 0
          AND tasks.deleted = 0
          AND categories.deleted = 0
        """
    )
    fun getCategoryStatisticsRaw(): Flow<List<String>>

    @Query(
        """
        SELECT *
        FROM daily_plans
        WHERE date = :date
          AND deleted = 0
        """
    )
    fun getAllPlansOfDate(
        date: String
    ): Flow<List<DailyPlan>>

    @Query(
        """
        SELECT daily_plans.*
        FROM daily_plans
        INNER JOIN tasks
            ON tasks.id = daily_plans.taskId
        WHERE daily_plans.dayRegistered = 1
          AND daily_plans.deleted = 0
          AND tasks.deleted = 0
        """
    )
    fun getAllRegisteredPlans(): Flow<List<DailyPlanWithTask>>

    @Query(
        """
        SELECT EXISTS(
            SELECT 1
            FROM daily_plans
            WHERE date = :date
              AND dayRegistered = 1
              AND deleted = 0
        )
        """
    )
    fun isDayRegistered(
        date: String
    ): Flow<Boolean>

    // ==========================================
    // ROUTINE
    // ==========================================

    // حذف نرم فقط DailyPlanهایی که توسط یک Routine
    // مشخص ساخته شده‌اند.
    //
    // برنامه‌های دستی همان Task دست‌نخورده می‌مانند.
    @Query(
        """
        UPDATE daily_plans
        SET deleted = 1
        WHERE routineId = :routineId
          AND deleted = 0
        """
    )
    suspend fun markDeletedByRoutineId(
        routineId: Int
    )

    // بررسی می‌کند آیا برای این Routine و این تاریخ
    // یک DailyPlan فعال وجود دارد یا نه.
    @Query(
        """
        SELECT *
        FROM daily_plans
        WHERE routineId = :routineId
          AND date = :date
          AND deleted = 0
        LIMIT 1
        """
    )
    suspend fun getRoutinePlanByDate(
        routineId: Int,
        date: String
    ): DailyPlan?

    // ==========================================
    // SYNC
    // ==========================================

    @Query(
        """
        SELECT *
        FROM daily_plans
        WHERE cloudId = :cloudId
        LIMIT 1
        """
    )
    suspend fun getByCloudId(
        cloudId: String
    ): DailyPlan?

    @Query(
        """
        UPDATE daily_plans
        SET
            cloudId = :cloudId,
            syncBase = :syncBase,
            syncVersion = :syncVersion
        WHERE id = :planId
        """
    )
    suspend fun updateSyncState(
        planId: Int,
        cloudId: String,
        syncBase: String,
        syncVersion: Long
    )

    @Query(
        """
        UPDATE daily_plans
        SET
            cloudId = :cloudId,
            taskId = :taskId,
            date = :date,
            plannedMinutes = :plannedMinutes,
            actualMinutes = :actualMinutes,
            earnedScore = :earnedScore,
            completed = :completed,
            exerciseCalories = :exerciseCalories,
            dayRegistered = :dayRegistered,
            orderIndex = :orderIndex,
            note = :note,
            deleted = :deleted,
            syncBase = :syncBase,
            syncVersion = :syncVersion
        WHERE id = :planId
        """
    )
    suspend fun applySyncedDailyPlan(
        planId: Int,
        cloudId: String,
        taskId: Int,
        date: String,
        plannedMinutes: Int,
        actualMinutes: Int,
        earnedScore: Double,
        completed: Boolean,
        exerciseCalories: Double,
        dayRegistered: Boolean,
        orderIndex: Int,
        note: String,
        deleted: Boolean,
        syncBase: String,
        syncVersion: Long
    )

    @Query(
        """
        UPDATE daily_plans
        SET
            syncBase = :syncBase,
            syncVersion = :syncVersion
        WHERE id = :planId
        """
    )
    suspend fun updateSyncSnapshot(
        planId: Int,
        syncBase: String,
        syncVersion: Long
    )

    @Query(
        """
        UPDATE daily_plans
        SET deleted = 1
        WHERE id = :planId
        """
    )
    suspend fun markDeleted(
        planId: Int
    )

    @Query(
        """
        UPDATE daily_plans
        SET deleted = 1
        WHERE taskId = :taskId
        """
    )
    suspend fun markDeletedByTaskId(
        taskId: Int
    )

    // ==========================================
    // EXISTING OPERATIONS
    // ==========================================

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
            completed = :completed,
            exerciseCalories = :exerciseCalories
        WHERE id = :id
        """
    )
    suspend fun updateProgress(
        id: Int,
        actualMinutes: Int,
        earnedScore: Double,
        completed: Boolean,
        exerciseCalories: Double
    )

    @Query(
        """
        UPDATE daily_plans
        SET
            taskId = :taskId,
            date = :date,
            plannedMinutes = :plannedMinutes,
            actualMinutes = :actualMinutes,
            earnedScore = :earnedScore,
            completed = :completed,
            exerciseCalories = :exerciseCalories,
            dayRegistered = :dayRegistered,
            orderIndex = :orderIndex,
            note = :note
        WHERE id = :id
        """
    )
    suspend fun updateFull(
        id: Int,
        taskId: Int,
        date: String,
        plannedMinutes: Int,
        actualMinutes: Int,
        earnedScore: Double,
        completed: Boolean,
        exerciseCalories: Double,
        dayRegistered: Boolean,
        orderIndex: Int,
        note: String
    )

    @Query(
        """
        UPDATE daily_plans
        SET dayRegistered = :registered
        WHERE date = :date
        """
    )
    suspend fun updateDayRegistered(
        date: String,
        registered: Boolean
    )

    @Query(
        """
        UPDATE daily_plans
        SET orderIndex = :orderIndex
        WHERE id = :id
        """
    )
    suspend fun updateOrder(
        id: Int,
        orderIndex: Int
    )

    @Query(
        """
        DELETE FROM daily_plans
        WHERE id = :id
        """
    )
    suspend fun deleteById(
        id: Int
    )

    @Query(
        """
        DELETE FROM daily_plans
        WHERE taskId = :taskId
        """
    )
    suspend fun deleteByTaskId(
        taskId: Int
    )

    @Query(
        """
        SELECT COALESCE(
            SUM(
                daily_plans.actualMinutes *
                tasks.caloriesPerHour / 60.0
            ),
            0.0
        )
        FROM daily_plans
        INNER JOIN tasks
            ON tasks.id = daily_plans.taskId
        WHERE daily_plans.date = :date
          AND daily_plans.deleted = 0
          AND tasks.deleted = 0
          AND tasks.caloriesPerHour > 0
        """
    )
    fun getExerciseCalories(
        date: String
    ): Flow<Double>

    @Query(
        """
        UPDATE daily_plans
        SET taskId = :taskId
        WHERE id = :planId
        """
    )
    suspend fun updateTaskId(
        planId: Int,
        taskId: Int
    )
}