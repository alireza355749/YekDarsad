package com.example.yekdarsad.data.expense

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Query("""
        SELECT * FROM expense_entries
        WHERE date = :date
        AND deletedAt IS NULL
        ORDER BY createdAt DESC
    """)
    fun getEntriesForDate(
        date: String
    ): Flow<List<ExpenseEntry>>

    @Query("""
        SELECT COALESCE(SUM(amount), 0)
        FROM expense_entries
        WHERE date = :date
        AND type = 'EXPENSE'
        AND deletedAt IS NULL
    """)
    fun getTotalExpensesForDate(
        date: String
    ): Flow<Long>

    @Query("""
        SELECT COALESCE(SUM(amount), 0)
        FROM expense_entries
        WHERE date = :date
        AND type = 'INCOME'
        AND deletedAt IS NULL
    """)
    fun getTotalIncomeForDate(
        date: String
    ): Flow<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(
        entry: ExpenseEntry
    )

    @Query("""
        UPDATE expense_entries
        SET deletedAt = :deletedAt,
            updatedAt = :updatedAt
        WHERE id = :id
    """)
    suspend fun softDelete(
        id: Long,
        deletedAt: Long,
        updatedAt: Long
    )

    @Query("""
        UPDATE expense_entries
        SET deletedAt = :deletedAt,
            updatedAt = :updatedAt
        WHERE date = :date
        AND deletedAt IS NULL
    """)
    suspend fun softDeleteAllForDate(
        date: String,
        deletedAt: Long,
        updatedAt: Long
    )

    @Query("""
        SELECT * FROM expense_entries
        ORDER BY createdAt DESC
    """)
    suspend fun getAllEntriesForSync(): List<ExpenseEntry>

    @Query("""
        SELECT * FROM expense_entries
        WHERE cloudId = :cloudId
        LIMIT 1
    """)
    suspend fun getEntryByCloudId(
        cloudId: String
    ): ExpenseEntry?

    @Query("""
        SELECT * FROM expense_entries
        WHERE id = :id
        LIMIT 1
    """)
    suspend fun getEntryById(
        id: Long
    ): ExpenseEntry?
}