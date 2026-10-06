package com.example.yekdarsad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yekdarsad.data.TrackerDao
import com.example.yekdarsad.data.TrackerEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class TrackerViewModel(

    private val dao: TrackerDao

) : ViewModel() {

    fun getEntries(

        date: String

    ): Flow<List<TrackerEntry>> {

        return dao.getByDate(date)

    }

    fun getTracker(

        date: String,

        type: String

    ): Flow<TrackerEntry?> {

        return dao.getTracker(

            date,

            type

        )

    }

    fun save(

        date: String,

        type: String,

        value: Double

    ) {

        viewModelScope.launch {

            dao.delete(

                date,

                type

            )

            dao.insert(

                TrackerEntry(

                    date = date,

                    type = type,

                    value = value

                )

            )

        }

    }

    fun saveCalories(

        date: String,

        calories: Double

    ) {

        save(

            date,

            "CALORIES",

            calories

        )

    }

    fun saveWater(

        date: String,

        water: Double

    ) {

        save(

            date,

            "WATER",

            water

        )

    }

    fun saveExpense(

        date: String,

        expense: Double

    ) {

        save(

            date,

            "EXPENSE",

            expense

        )

    }

    fun saveWeight(

        date: String,

        weight: Double

    ) {

        save(

            date,

            "WEIGHT",

            weight

        )

    }

    fun clearAll() {

        viewModelScope.launch {

            dao.clearAll()

        }

    }

}