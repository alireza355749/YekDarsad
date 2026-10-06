
package com.example.yekdarsad.data.sync

import android.content.Context
import com.example.yekdarsad.data.DatabaseProvider

object SleepSyncRunner {

    suspend fun run(context: Context) {
        val database = DatabaseProvider.getDatabase(context)

        val repository = SleepSyncRepository(
            sleepDao = database.sleepDao()
        )

        repository.syncSleep()
    }
}