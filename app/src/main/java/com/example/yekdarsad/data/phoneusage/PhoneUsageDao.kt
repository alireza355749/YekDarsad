package com.example.yekdarsad.data.phoneusage

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PhoneUsageDao {

    // =====================================================
    // ثبت مجموع مصرف گوشی
    // =====================================================

    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun insertTotal(
        usage: PhoneUsageEntity
    )


    // =====================================================
    // ثبت مصرف اپلیکیشن‌ها
    // =====================================================

    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun insertApps(
        apps: List<PhoneAppUsageEntity>
    )


    // =====================================================
    // مجموع مصرف یک روز
    // =====================================================

    @Query(
        """
        SELECT *
        FROM phone_usage
        WHERE date = :date
        LIMIT 1
        """
    )
    fun getTotal(
        date: String
    ): Flow<PhoneUsageEntity?>


    // =====================================================
    // مصرف اپلیکیشن‌های یک روز
    // =====================================================

    @Query(
        """
        SELECT *
        FROM phone_app_usage
        WHERE date = :date
        ORDER BY minutes DESC
        """
    )
    fun getApps(
        date: String
    ): Flow<List<PhoneAppUsageEntity>>


    // =====================================================
    // مجموع مصرف گوشی در یک بازه
    // =====================================================

    @Query(
        """
        SELECT *
        FROM phone_usage
        WHERE date BETWEEN :startDate AND :endDate
        ORDER BY date ASC
        """
    )
    fun getTotalsBetweenDates(
        startDate: String,
        endDate: String
    ): Flow<List<PhoneUsageEntity>>


    // =====================================================
    // حذف مصرف اپلیکیشن‌های یک روز
    // =====================================================

    @Query(
        """
        DELETE FROM phone_app_usage
        WHERE date = :date
        """
    )
    suspend fun deleteApps(
        date: String
    )


    // =====================================================
    // حذف مجموع مصرف یک روز
    // =====================================================

    @Query(
        """
        DELETE FROM phone_usage
        WHERE date = :date
        """
    )
    suspend fun deleteTotal(
        date: String
    )
}