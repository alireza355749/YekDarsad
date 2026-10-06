package com.example.yekdarsad.data.nutrition

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface NutritionDao {

    // ثبت یا به‌روزرسانی وعده
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodEntry(
        entry: FoodEntry
    )

    // دریافت وعده‌های زنده یک روز
    @Transaction
    @Query(
        """
        SELECT *
        FROM food_entries
        WHERE date = :date
          AND deletedAt IS NULL
        ORDER BY createdAt ASC
        """
    )
    fun getFoodEntries(
        date: String
    ): Flow<List<FoodEntryWithFood>>

    // دریافت وعده‌های زنده یک بازه
    @Transaction
    @Query(
        """
        SELECT *
        FROM food_entries
        WHERE date BETWEEN :startDate AND :endDate
          AND deletedAt IS NULL
        ORDER BY date ASC, createdAt ASC
        """
    )
    fun getFoodEntriesBetweenDates(
        startDate: String,
        endDate: String
    ): Flow<List<FoodEntryWithFood>>

    // دریافت تمام رکوردها برای سینک، شامل حذف‌شده‌ها
    @Query("SELECT * FROM food_entries ORDER BY id ASC")
    suspend fun getAllEntriesForSync(): List<FoodEntry>

    // پیدا کردن رکورد با شناسه ابری
    @Query(
        """
        SELECT * FROM food_entries
        WHERE cloudId = :cloudId
        LIMIT 1
        """
    )
    suspend fun getEntryByCloudId(
        cloudId: String
    ): FoodEntry?

    // علامت‌گذاری یک وعده به‌عنوان حذف‌شده
    @Query(
        """
        UPDATE food_entries
        SET deletedAt = :deletedAt,
            updatedAt = :updatedAt
        WHERE id = :id
        """
    )
    suspend fun softDeleteFoodEntry(
        id: Long,
        deletedAt: Long,
        updatedAt: Long
    )

    // علامت‌گذاری تمام وعده‌های یک روز به‌عنوان حذف‌شده
    @Query(
        """
        UPDATE food_entries
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

    // تنظیمات تغذیه
    @Query(
        """
        SELECT *
        FROM nutrition_settings
        WHERE id = 1
        LIMIT 1
        """
    )
    suspend fun getSettings(): NutritionSettings?

    // ذخیره تنظیمات
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(
        settings: NutritionSettings
    )
}