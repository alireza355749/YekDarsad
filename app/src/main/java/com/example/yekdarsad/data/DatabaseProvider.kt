package com.example.yekdarsad.data

import android.content.Context

object DatabaseProvider {

    fun getDatabase(
        context: Context
    ): AppDatabase {

        return AppDatabase.getDatabase(
            context.applicationContext
        )
    }
}