package com.example.yekdarsad.data.sync

import android.content.Context
import com.example.yekdarsad.data.DatabaseProvider

object CategorySyncRunner {

    suspend fun run(context: Context) {

        val database =
            DatabaseProvider.getDatabase(context)

        /*
         * =====================================================
         * CATEGORY SYNC
         * =====================================================
         */

        CategorySyncRepository(
            database.categoryDao()
        ).syncCategories()

        /*
         * =====================================================
         * OVERVIEW NOTE SYNC
         * =====================================================
         */

        OverviewNoteSyncRepository(
            database.overviewNoteDao()
        ).syncNotes()

        /*
         * =====================================================
         * DAILY NOTE SYNC
         * =====================================================
         */

        DailyNoteSyncRepository(
            database.dailyNoteDao()
        ).syncNotes()
    }
}