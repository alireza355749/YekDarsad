package com.example.yekdarsad.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyNoteDao {

    @Query(
        "SELECT * FROM daily_notes WHERE date = :date"
    )
    fun getNote(
        date: String
    ): Flow<DailyNote?>

    @Query("SELECT * FROM daily_notes")
    suspend fun getAllNotes(): List<DailyNote>

    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun saveNote(
        note: DailyNote
    )
}