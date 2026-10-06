package com.example.yekdarsad.data.sync

import android.content.Context
import com.example.yekdarsad.data.DatabaseProvider

object TaskSyncRunner {

    suspend fun run(context: Context) {

        val database =
            DatabaseProvider.getDatabase(context)

        TaskSyncRepository(
            taskDao = database.taskDao(),
            categoryDao = database.categoryDao()
        ).syncTasks()
    }
}