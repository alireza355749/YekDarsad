package com.example.yekdarsad.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.yekdarsad.data.expense.ExpenseDao
import com.example.yekdarsad.data.expense.ExpenseEntry
import com.example.yekdarsad.data.nutrition.CustomFood
import com.example.yekdarsad.data.nutrition.Food
import com.example.yekdarsad.data.nutrition.FoodDao
import com.example.yekdarsad.data.nutrition.FoodEntry
import com.example.yekdarsad.data.nutrition.ManualCalorieDao
import com.example.yekdarsad.data.nutrition.ManualCalorieEntry
import com.example.yekdarsad.data.nutrition.NutritionConverters
import com.example.yekdarsad.data.nutrition.NutritionDao
import com.example.yekdarsad.data.nutrition.NutritionSettings
import com.example.yekdarsad.data.phoneusage.PhoneAppUsageEntity
import com.example.yekdarsad.data.phoneusage.PhoneUsageDao
import com.example.yekdarsad.data.phoneusage.PhoneUsageEntity
import com.example.yekdarsad.data.sleep.SleepDao
import com.example.yekdarsad.data.sleep.SleepEntry
import com.example.yekdarsad.data.sync.SyncConflict

@Database(
    entities = [
        Category::class,
        Task::class,
        DailyTask::class,
        ActivityDuration::class,
        DailyPlan::class,
        TrackerEntry::class,
        DailyNote::class,
        OverviewNote::class,
        PhoneUsageEntity::class,
        PhoneAppUsageEntity::class,
        Food::class,
        FoodEntry::class,
        CustomFood::class,
        NutritionSettings::class,
        ManualCalorieEntry::class,
        SleepEntry::class,
        ExpenseEntry::class,
        SyncConflict::class,
        Routine::class
    ],
    version = 29,
    exportSchema = false
)
@TypeConverters(NutritionConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun taskDao(): TaskDao
    abstract fun dailyTaskDao(): DailyTaskDao
    abstract fun activityDurationDao(): ActivityDurationDao
    abstract fun dailyPlanDao(): DailyPlanDao
    abstract fun trackerDao(): TrackerDao
    abstract fun dailyNoteDao(): DailyNoteDao
    abstract fun overviewNoteDao(): OverviewNoteDao
    abstract fun phoneUsageDao(): PhoneUsageDao
    abstract fun foodDao(): FoodDao
    abstract fun nutritionDao(): NutritionDao
    abstract fun manualCalorieDao(): ManualCalorieDao
    abstract fun sleepDao(): SleepDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun routineDao(): RoutineDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_21_22 =
            object : Migration(21, 22) {

                override fun migrate(
                    database: SupportSQLiteDatabase
                ) {
                    database.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS sync_conflicts (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            entityType TEXT NOT NULL,
                            entityId TEXT NOT NULL,
                            baseSnapshot TEXT NOT NULL,
                            localSnapshot TEXT NOT NULL,
                            remoteSnapshot TEXT NOT NULL,
                            conflictingFields TEXT NOT NULL,
                            createdAt INTEGER NOT NULL,
                            resolved INTEGER NOT NULL,
                            resolvedAt INTEGER
                        )
                        """.trimIndent()
                    )
                }
            }

        private val MIGRATION_22_23 =
            object : Migration(22, 23) {

                override fun migrate(
                    database: SupportSQLiteDatabase
                ) {
                    database.execSQL(
                        """
                        ALTER TABLE daily_notes
                        ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0
                        """.trimIndent()
                    )
                }
            }

        private val MIGRATION_23_24 =
            object : Migration(23, 24) {

                override fun migrate(
                    database: SupportSQLiteDatabase
                ) {
                    database.execSQL(
                        """
                        ALTER TABLE food_entries
                        ADD COLUMN cloudId TEXT DEFAULT NULL
                        """.trimIndent()
                    )

                    database.execSQL(
                        """
                        ALTER TABLE food_entries
                        ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0
                        """.trimIndent()
                    )

                    database.execSQL(
                        """
                        ALTER TABLE food_entries
                        ADD COLUMN deletedAt INTEGER DEFAULT NULL
                        """.trimIndent()
                    )

                    database.execSQL(
                        """
                        ALTER TABLE manual_calorie_entries
                        ADD COLUMN cloudId TEXT DEFAULT NULL
                        """.trimIndent()
                    )

                    database.execSQL(
                        """
                        ALTER TABLE manual_calorie_entries
                        ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0
                        """.trimIndent()
                    )

                    database.execSQL(
                        """
                        ALTER TABLE manual_calorie_entries
                        ADD COLUMN deletedAt INTEGER DEFAULT NULL
                        """.trimIndent()
                    )
                }
            }

        private val MIGRATION_24_25 =
            object : Migration(24, 25) {

                override fun migrate(
                    database: SupportSQLiteDatabase
                ) {
                    database.execSQL(
                        """
                        ALTER TABLE sleep_entries
                        ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0
                        """.trimIndent()
                    )
                }
            }

        private val MIGRATION_25_26 =
            object : Migration(25, 26) {

                override fun migrate(
                    database: SupportSQLiteDatabase
                ) {
                    database.execSQL(
                        """
                        ALTER TABLE expense_entries
                        ADD COLUMN cloudId TEXT DEFAULT NULL
                        """.trimIndent()
                    )

                    database.execSQL(
                        """
                        ALTER TABLE expense_entries
                        ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0
                        """.trimIndent()
                    )

                    database.execSQL(
                        """
                        ALTER TABLE expense_entries
                        ADD COLUMN deletedAt INTEGER DEFAULT NULL
                        """.trimIndent()
                    )
                }
            }

        private val MIGRATION_26_27 =
            object : Migration(26, 27) {

                override fun migrate(
                    database: SupportSQLiteDatabase
                ) {

                    database.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS routines (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            cloudId TEXT,
                            taskId INTEGER NOT NULL,
                            type TEXT NOT NULL,
                            startDate TEXT NOT NULL,
                            endDate TEXT NOT NULL,
                            enabled INTEGER NOT NULL,
                            deleted INTEGER NOT NULL,
                            syncBase TEXT,
                            syncVersion INTEGER NOT NULL
                        )
                        """.trimIndent()
                    )
                }
            }

        private val MIGRATION_27_28 =
            object : Migration(27, 28) {

                override fun migrate(
                    database: SupportSQLiteDatabase
                ) {

                    database.execSQL(
                        """
                        ALTER TABLE routines
                        ADD COLUMN showInOverview INTEGER NOT NULL DEFAULT 1
                        """.trimIndent()
                    )
                }
            }

        private val MIGRATION_28_29 =
            object : Migration(28, 29) {

                override fun migrate(
                    database: SupportSQLiteDatabase
                ) {

                    database.execSQL(
                        """
                        ALTER TABLE daily_plans
                        ADD COLUMN routineId INTEGER DEFAULT NULL
                        """.trimIndent()
                    )
                }
            }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "yekdarsad_database"
                )
                    .addMigrations(
                        MIGRATION_21_22,
                        MIGRATION_22_23,
                        MIGRATION_23_24,
                        MIGRATION_24_25,
                        MIGRATION_25_26,
                        MIGRATION_26_27,
                        MIGRATION_27_28,
                        MIGRATION_28_29
                    )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}