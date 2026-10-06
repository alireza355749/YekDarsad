package com.example.yekdarsad.data.sync

import android.content.Context
import com.example.yekdarsad.data.DatabaseProvider

object ExpenseSyncRunner {

    suspend fun run(context: Context) {
        val database = DatabaseProvider.getDatabase(context)

        val repository = ExpenseSyncRepository(
            database.expenseDao()
        )

        repository.syncExpenses()
    }
}