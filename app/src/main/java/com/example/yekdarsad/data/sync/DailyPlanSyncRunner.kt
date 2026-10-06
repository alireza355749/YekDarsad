package com.example.yekdarsad.data.sync

import android.content.Context
import android.util.Log
import com.example.yekdarsad.data.DatabaseProvider

object DailyPlanSyncRunner {

    suspend fun run(context: Context) {
        Log.d("DailyPlanSync", "RUNNER: starting")

        val database = DatabaseProvider.getDatabase(context)

        DailyPlanSyncRepository(
            dailyPlanDao = database.dailyPlanDao(),
            taskDao = database.taskDao(),
            categoryDao = database.categoryDao()
        ).syncDailyPlans()

        Log.d("DailyPlanSync", "RUNNER: finished")
    }
}