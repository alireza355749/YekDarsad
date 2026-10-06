package com.example.yekdarsad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yekdarsad.data.expense.ExpenseEntry
import com.example.yekdarsad.data.expense.ExpenseRepository
import com.example.yekdarsad.data.expense.ExpenseType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ExpenseViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    fun getEntriesForDate(
        date: String
    ): Flow<List<ExpenseEntry>> {
        return repository.getEntriesForDate(date)
    }

    fun getTotalExpensesForDate(
        date: String
    ): Flow<Long> {
        return repository.getTotalExpensesForDate(date)
    }

    fun getTotalIncomeForDate(
        date: String
    ): Flow<Long> {
        return repository.getTotalIncomeForDate(date)
    }

    fun addExpense(
        date: String,
        amount: Long,
        category: String,
        title: String,
        description: String = ""
    ) {
        if (amount <= 0) return

        viewModelScope.launch {
            repository.insert(
                ExpenseEntry(
                    date = date,
                    type = ExpenseType.EXPENSE,
                    amount = amount,
                    category = category,
                    title = title,
                    description = description
                )
            )
        }
    }

    fun addIncome(
        date: String,
        amount: Long,
        category: String,
        title: String,
        description: String = ""
    ) {
        if (amount <= 0) return

        viewModelScope.launch {
            repository.insert(
                ExpenseEntry(
                    date = date,
                    type = ExpenseType.INCOME,
                    amount = amount,
                    category = category,
                    title = title,
                    description = description
                )
            )
        }
    }

    fun deleteEntry(
        entry: ExpenseEntry
    ) {
        viewModelScope.launch {
            repository.delete(entry)
        }
    }
}