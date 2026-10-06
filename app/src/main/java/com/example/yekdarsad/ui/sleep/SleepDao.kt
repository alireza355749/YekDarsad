
package com.example.yekdarsad.data.sleep

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SleepDao {

    @Query(
        """
        SELECT *
        FROM sleep_entries
        WHERE date = :date
        LIMIT 1
        """
    )
    fun getByDate(date: String): Flow<SleepEntry?>

    @Query(
        """
        SELECT *
        FROM sleep_entries
        WHERE date BETWEEN :startDate AND :endDate
        ORDER BY date ASC
        """
    )
    fun getBetweenDates(
        startDate: String,
        endDate: String
    ): Flow<List<SleepEntry>>

    @Query(
        """
        SELECT *
        FROM sleep_entries
        ORDER BY date ASC
        """
    )
    suspend fun getAllEntriesForSync(): List<SleepEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: SleepEntry)

    @Query("DELETE FROM sleep_entries WHERE date = :date")
    suspend fun deleteForDate(date: String)

    @Query("DELETE FROM sleep_entries")
    suspend fun clearAll()
}