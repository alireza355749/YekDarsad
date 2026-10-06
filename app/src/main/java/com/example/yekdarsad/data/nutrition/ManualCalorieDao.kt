package com.example.yekdarsad.data.nutrition

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ManualCalorieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(
        entry: ManualCalorieEntry
    )

    // مجموع کالری‌های دستی؛ رکوردهای حذف‌شده محاسبه نمی‌شوند
    @Query(
        """
        SELECT COALESCE(SUM(calories), 0.0)
        FROM manual_calorie_entries
        WHERE date = :date
          AND deletedAt IS NULL
        """
    )
    fun getTotalCalories(
        date: String
    ): Flow<Double>

    // فهرست رکوردهای زنده یک روز
    @Query(
        """
        SELECT *
        FROM manual_calorie_entries
        WHERE date = :date
          AND deletedAt IS NULL
        ORDER BY createdAt DESC
        """
    )
    fun getEntries(
        date: String
    ): Flow<List<ManualCalorieEntry>>

    // تمام رکوردها برای سینک، شامل حذف‌شده‌ها
    @Query(
        "SELECT * FROM manual_calorie_entries ORDER BY id ASC"
    )
    suspend fun getAllEntriesForSync(): List<ManualCalorieEntry>

    // جست‌وجو بر اساس شناسه ابری
    @Query(
        """
        SELECT * FROM manual_calorie_entries
        WHERE cloudId = :cloudId
        LIMIT 1
        """
    )
    suspend fun getEntryByCloudId(
        cloudId: String
    ): ManualCalorieEntry?

    // حذف نرم یک رکورد
    @Query(
        """
        UPDATE manual_calorie_entries
        SET deletedAt = :deletedAt,
            updatedAt = :updatedAt
        WHERE id = :id
        """
    )
    suspend fun softDelete(
        id: Long,
        deletedAt: Long,
        updatedAt: Long
    )

    // حذف نرم تمام کالری‌های دستی یک روز
    @Query(
        """
        UPDATE manual_calorie_entries
        SET deletedAt = :deletedAt,
            updatedAt = :updatedAt
        WHERE date = :date
          AND deletedAt IS NULL
        """
    )
    suspend fun softDeleteAllForDate(
        date: String,
        deletedAt: Long,
        updatedAt: Long
    )
}