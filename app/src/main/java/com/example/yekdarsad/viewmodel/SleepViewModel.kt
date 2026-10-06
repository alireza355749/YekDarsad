
package com.example.yekdarsad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yekdarsad.data.sleep.SleepDao
import com.example.yekdarsad.data.sleep.SleepEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SleepViewModel(
    private val dao: SleepDao
) : ViewModel() {

    fun getEntry(date: String): Flow<SleepEntry?> {
        return dao.getByDate(date)
    }

    fun saveSleep(
        date: String,
        sleepStartHour: Int,
        sleepStartMinute: Int,
        wakeHour: Int,
        wakeMinute: Int,
        napsMinutes: Int
    ) {
        val safeSleepStartHour = sleepStartHour.coerceIn(0, 23)
        val safeSleepStartMinute = sleepStartMinute.coerceIn(0, 59)
        val safeWakeHour = wakeHour.coerceIn(0, 23)
        val safeWakeMinute = wakeMinute.coerceIn(0, 59)
        val safeNapsMinutes = napsMinutes.coerceAtLeast(0)

        viewModelScope.launch {
            dao.insert(
                SleepEntry(
                    date = date,
                    sleepStartHour = safeSleepStartHour,
                    sleepStartMinute = safeSleepStartMinute,
                    wakeHour = safeWakeHour,
                    wakeMinute = safeWakeMinute,
                    napsMinutes = safeNapsMinutes,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun deleteSleep(date: String) {
        viewModelScope.launch {
            dao.deleteForDate(date)
        }
    }

    fun getNightSleepMinutes(entry: SleepEntry?): Int {
        if (entry == null) return 0

        val start =
            entry.sleepStartHour * 60 + entry.sleepStartMinute

        val wake =
            entry.wakeHour * 60 + entry.wakeMinute

        return if (wake >= start) {
            wake - start
        } else {
            (24 * 60 - start) + wake
        }
    }

    fun getTotalSleepMinutes(entry: SleepEntry?): Int {
        if (entry == null) return 0

        return getNightSleepMinutes(entry) +
                entry.napsMinutes.coerceAtLeast(0)
    }
}