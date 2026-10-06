package com.example.yekdarsad.data.sleep

import kotlinx.coroutines.flow.Flow

class SleepRepository(
    private val dao: SleepDao
) {

    fun getSleep(
        date: String
    ): Flow<SleepEntry?> {

        return dao.getByDate(date)
    }

    suspend fun save(
        entry: SleepEntry
    ) {

        dao.insert(entry)
    }

    suspend fun delete(
        date: String
    ) {

        dao.deleteForDate(date)
    }
}