package com.example.yekdarsad.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(
        entry: TrackerEntry
    )

    @Query(
        """
        SELECT *
        FROM tracker_entries
        WHERE date = :date
        """
    )
    fun getByDate(
        date: String
    ): Flow<List<TrackerEntry>>

    @Query(
        """
        SELECT *
        FROM tracker_entries
        WHERE date = :date
        AND type = :type
        LIMIT 1
        """
    )
    fun getTracker(
        date: String,
        type: String
    ): Flow<TrackerEntry?>

    @Query(
        """
        DELETE FROM tracker_entries
        WHERE date = :date
        AND type = :type
        """
    )
    suspend fun delete(
        date: String,
        type: String
    )

    @Query(
        """
        DELETE FROM tracker_entries
        """
    )
    suspend fun clearAll()

}