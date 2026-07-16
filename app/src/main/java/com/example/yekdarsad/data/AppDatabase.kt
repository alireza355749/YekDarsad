package com.example.yekdarsad.data

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
    entities = [
        Category::class,
        Task::class,
        DailyTask::class,
        ActivityDuration::class,
        DailyPlan::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {


    abstract fun categoryDao(): CategoryDao


    abstract fun taskDao(): TaskDao


    abstract fun dailyTaskDao(): DailyTaskDao


    abstract fun activityDurationDao(): ActivityDurationDao

    abstract fun dailyPlanDao(): DailyPlanDao
}