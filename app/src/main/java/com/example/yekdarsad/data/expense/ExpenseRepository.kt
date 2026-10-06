package com.example.yekdarsad.data.expense

import kotlinx.coroutines.flow.Flow

class ExpenseRepository(
    private val expenseDao: ExpenseDao
) {

    fun getEntriesForDate(
        date: String
    ): Flow<List<ExpenseEntry>> {
        return expenseDao.getEntriesForDate(date)
    }

    fun getTotalExpensesForDate(
        date: String
    ): Flow<Long> {
        return expenseDao.getTotalExpensesForDate(date)
    }

    fun getTotalIncomeForDate(
        date: String
    ): Flow<Long> {
        return expenseDao.getTotalIncomeForDate(date)
    }

    suspend fun insert(
        entry: ExpenseEntry
    ) {
        expenseDao.insert(entry)
    }

    suspend fun delete(
        entry: ExpenseEntry
    ) {
        val now = System.currentTimeMillis()

        expenseDao.softDelete(
            id = entry.id,
            deletedAt = now,
            updatedAt = now
        )
    }

    suspend fun deleteAllForDate(
        date: String
    ) {
        val now = System.currentTimeMillis()

        expenseDao.softDeleteAllForDate(
            date = date,
            deletedAt = now,
            updatedAt = now
        )
    }

    suspend fun getAllEntriesForSync(): List<ExpenseEntry> {
        return expenseDao.getAllEntriesForSync()
    }

    suspend fun getEntryByCloudId(
        cloudId: String
    ): ExpenseEntry? {
        return expenseDao.getEntryByCloudId(cloudId)
    }

    suspend fun getEntryById(
        id: Long
    ): ExpenseEntry? {
        return expenseDao.getEntryById(id)
    }
}