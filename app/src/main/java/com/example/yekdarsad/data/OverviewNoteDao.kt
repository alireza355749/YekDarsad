package com.example.yekdarsad.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OverviewNoteDao {

    @Query(
        """
        SELECT *
        FROM overview_notes
        WHERE periodKey = :periodKey
        LIMIT 1
        """
    )
    fun getNote(
        periodKey: String
    ): Flow<OverviewNote?>

    @Query(
        """
        SELECT *
        FROM overview_notes
        """
    )
    suspend fun getAllNotes(): List<OverviewNote>

    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun saveNote(
        note: OverviewNote
    )

    @Query(
        """
        DELETE FROM overview_notes
        WHERE periodKey = :periodKey
        """
    )
    suspend fun deleteNote(
        periodKey: String
    )
}