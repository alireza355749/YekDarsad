package com.example.yekdarsad.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// ============================================================
// ROUTINE TYPE
// ============================================================

enum class RoutineType(
    val value: String,
    val title: String
) {
    WEEKLY(
        value = "WEEKLY",
        title = "هفتگی"
    ),

    MONTHLY(
        value = "MONTHLY",
        title = "ماهانه"
    );

    companion object {

        fun fromValue(
            value: String
        ): RoutineType {

            return entries.firstOrNull {
                it.value == value
            } ?: WEEKLY
        }
    }
}

// ============================================================
// ROUTINE ENTITY
// ============================================================

@Entity(
    tableName = "routines"
)
data class Routine(

    @PrimaryKey(
        autoGenerate = true
    )
    val id: Int = 0,

    // ========================================================
    // SUPABASE
    // ========================================================

    val cloudId: String? = null,

    // ========================================================
    // LOCAL RELATION
    // ========================================================

    val taskId: Int,

    // WEEKLY یا MONTHLY
    val type: String,

    // ========================================================
    // PERIOD
    // ========================================================

    // تاریخ شروع دوره به فرمت yyyy-MM-dd شمسی
    val startDate: String,

    // تاریخ پایان دوره به فرمت yyyy-MM-dd شمسی
    val endDate: String,

    // ========================================================
    // DURATION
    // ========================================================

    // مدت زمان روزانه برای فعالیت‌های TIME
    //
    // 0 = بدون مدت زمان / فعالیت CHECK
    val durationMinutes: Int = 0,

    // ========================================================
    // STATUS
    // ========================================================

    // false = غیرفعال
    // true  = فعال
    val enabled: Boolean = true,

    // نمایش این Routine در تب «در یک نگاه»
    // پیش‌فرض روشن است.
    val showInOverview: Boolean = true,

    // حذف منطقی برای Sync
    val deleted: Boolean = false,

    // ========================================================
    // SYNC
    // ========================================================

    val syncBase: String? = null,

    val syncVersion: Long = 0
)

// ============================================================
// ROUTINE DAO
// ============================================================

@Dao
interface RoutineDao {

    // ========================================================
    // INSERT
    // ========================================================

    @Insert
    suspend fun insert(
        routine: Routine
    ): Long

    // ========================================================
    // UPDATE
    // ========================================================

    @Update
    suspend fun update(
        routine: Routine
    )

    // ========================================================
    // OBSERVE
    // ========================================================

    @Query(
        """
        SELECT *
        FROM routines
        WHERE deleted = 0
        ORDER BY id ASC
        """
    )
    fun observeAll(): Flow<List<Routine>>

    // ========================================================
    // GET ALL
    // ========================================================

    @Query(
        """
        SELECT *
        FROM routines
        WHERE deleted = 0
        ORDER BY id ASC
        """
    )
    suspend fun getAllOnce(): List<Routine>

    // ========================================================
    // GET BY TASK
    // ========================================================

    @Query(
        """
        SELECT *
        FROM routines
        WHERE taskId = :taskId
          AND deleted = 0
        ORDER BY startDate ASC
        """
    )
    suspend fun getByTaskOnce(
        taskId: Int
    ): List<Routine>

    // ========================================================
    // GET BY ID
    // ========================================================

    @Query(
        """
        SELECT *
        FROM routines
        WHERE id = :id
        LIMIT 1
        """
    )
    suspend fun getById(
        id: Int
    ): Routine?

    // ========================================================
    // GET BY CLOUD ID
    // ========================================================

    @Query(
        """
        SELECT *
        FROM routines
        WHERE cloudId = :cloudId
        LIMIT 1
        """
    )
    suspend fun getByCloudId(
        cloudId: String
    ): Routine?

    // ========================================================
    // DUPLICATE CHECK
    // ========================================================

    @Query(
        """
        SELECT *
        FROM routines
        WHERE taskId = :taskId
          AND type = :type
          AND startDate = :startDate
          AND endDate = :endDate
          AND deleted = 0
        LIMIT 1
        """
    )
    suspend fun getExistingRoutine(
        taskId: Int,
        type: String,
        startDate: String,
        endDate: String
    ): Routine?

    // ========================================================
    // ENABLE / DISABLE
    // ========================================================

    @Query(
        """
        UPDATE routines
        SET enabled = :enabled
        WHERE id = :routineId
        """
    )
    suspend fun updateEnabled(
        routineId: Int,
        enabled: Boolean
    )

    // ========================================================
    // SHOW IN OVERVIEW
    // ========================================================

    @Query(
        """
        UPDATE routines
        SET showInOverview = :showInOverview
        WHERE id = :routineId
        """
    )
    suspend fun updateShowInOverview(
        routineId: Int,
        showInOverview: Boolean
    )

    // ========================================================
    // DELETE
    // ========================================================

    @Query(
        """
        UPDATE routines
        SET deleted = 1
        WHERE id = :routineId
        """
    )
    suspend fun markDeleted(
        routineId: Int
    )

    @Query(
        """
        UPDATE routines
        SET deleted = 1
        WHERE taskId = :taskId
        """
    )
    suspend fun markDeletedByTaskId(
        taskId: Int
    )

    // ========================================================
    // SYNC
    // ========================================================

    @Query(
        """
        UPDATE routines
        SET
            cloudId = :cloudId,
            syncBase = :syncBase,
            syncVersion = :syncVersion
        WHERE id = :routineId
        """
    )
    suspend fun updateSyncState(
        routineId: Int,
        cloudId: String,
        syncBase: String,
        syncVersion: Long
    )

    @Query(
        """
        UPDATE routines
        SET
            syncBase = :syncBase,
            syncVersion = :syncVersion
        WHERE id = :routineId
        """
    )
    suspend fun updateSyncSnapshot(
        routineId: Int,
        syncBase: String,
        syncVersion: Long
    )
}

// ============================================================
// JALALI DATE MODEL
// ============================================================

data class RoutineJalaliDate(
    val year: Int,
    val month: Int,
    val day: Int
) {

    fun toKey(): String {

        return buildString {

            append(year)
            append("-")

            append(
                month.toString().padStart(
                    2,
                    '0'
                )
            )

            append("-")

            append(
                day.toString().padStart(
                    2,
                    '0'
                )
            )
        }
    }
}

// ============================================================
// JALALI DATE UTILS
// ============================================================

object RoutineJalali {

    // ========================================================
    // GREGORIAN -> JALALI
    // ========================================================

    fun fromGregorian(
        year: Int,
        month: Int,
        day: Int
    ): RoutineJalaliDate {

        val gregorianMonthDays = intArrayOf(
            0,
            31,
            59,
            90,
            120,
            151,
            181,
            212,
            243,
            273,
            304,
            334
        )

        val adjustedYear =
            if (month > 2) {
                year + 1
            } else {
                year
            }

        var days =
            355666 +
                    (365 * year) +
                    ((adjustedYear + 3) / 4) -
                    ((adjustedYear + 99) / 100) +
                    ((adjustedYear + 399) / 400) +
                    day +
                    gregorianMonthDays[month - 1]

        var jalaliYear =
            -1595 +
                    (33 * (days / 12053))

        days %= 12053

        jalaliYear +=
            4 * (days / 1461)

        days %= 1461

        if (days > 365) {

            jalaliYear +=
                (days - 1) / 365

            days =
                (days - 1) % 365
        }

        val jalaliMonth: Int
        val jalaliDay: Int

        if (days < 186) {

            jalaliMonth =
                1 + days / 31

            jalaliDay =
                1 + days % 31

        } else {

            jalaliMonth =
                7 + (days - 186) / 30

            jalaliDay =
                1 + (days - 186) % 30
        }

        return RoutineJalaliDate(
            year = jalaliYear,
            month = jalaliMonth,
            day = jalaliDay
        )
    }

    // ========================================================
    // JALALI -> GREGORIAN
    // ========================================================

    fun toGregorian(
        year: Int,
        month: Int,
        day: Int
    ): java.time.LocalDate {

        var jy =
            year + 1595

        var days =
            -355668 +
                    (365 * jy) +
                    ((jy / 33) * 8) +
                    (((jy % 33) + 3) / 4)

        days += day

        if (month < 7) {

            days +=
                (month - 1) * 31

        } else {

            days +=
                ((month - 7) * 30) +
                        186
        }

        var gy =
            400 * (days / 146097)

        days %= 146097

        if (days > 36524) {

            days--

            gy +=
                100 * (days / 36524)

            days %= 36524

            if (days >= 365) {
                days++
            }
        }

        gy +=
            4 * (days / 1461)

        days %= 1461

        if (days > 365) {

            gy +=
                (days - 1) / 365

            days =
                (days - 1) % 365
        }

        val gd =
            days + 1

        val leap =
            gy % 4 == 0 &&
                    (
                            gy % 100 != 0 ||
                                    gy % 400 == 0
                            )

        val monthDays =
            intArrayOf(
                31,
                if (leap) 29 else 28,
                31,
                30,
                31,
                30,
                31,
                31,
                30,
                31,
                30,
                31
            )

        var remaining =
            gd

        var gm = 1

        while (
            gm <= 12 &&
            remaining > monthDays[gm - 1]
        ) {

            remaining -=
                monthDays[gm - 1]

            gm++
        }

        return java.time.LocalDate.of(
            gy,
            gm,
            remaining
        )
    }

    // ========================================================
    // KEY -> GREGORIAN
    // ========================================================

    fun keyToGregorian(
        key: String
    ): java.time.LocalDate {

        val parts =
            key.split("-")

        require(parts.size == 3)

        return toGregorian(
            year = parts[0].toInt(),
            month = parts[1].toInt(),
            day = parts[2].toInt()
        )
    }

    // ========================================================
    // CURRENT JALALI DATE
    // ========================================================

    fun today(): RoutineJalaliDate {

        val today =
            java.time.LocalDate.now(
                java.time.ZoneId.of(
                    "Asia/Tehran"
                )
            )

        return fromGregorian(
            year = today.year,
            month = today.monthValue,
            day = today.dayOfMonth
        )
    }

    // ========================================================
    // DAYS IN JALALI MONTH
    // ========================================================

    fun daysInMonth(
        year: Int,
        month: Int
    ): Int {

        if (month <= 6) {
            return 31
        }

        if (month <= 11) {
            return 30
        }

        return if (isLeapYear(year)) {
            30
        } else {
            29
        }
    }

    // ========================================================
    // JALALI LEAP YEAR
    // ========================================================

    fun isLeapYear(
        year: Int
    ): Boolean {

        val current =
            toGregorian(
                year,
                12,
                29
            )

        val next =
            toGregorian(
                year + 1,
                1,
                1
            )

        return current.plusDays(1) == next
    }

    // ========================================================
    // CURRENT WEEK
    // ========================================================

    fun currentWeekDates(): List<String> {

        val today =
            java.time.LocalDate.now(
                java.time.ZoneId.of(
                    "Asia/Tehran"
                )
            )

        val saturdayOffset =
            (
                    today.dayOfWeek.value - 6 + 7
                    ) % 7

        val saturday =
            today.minusDays(
                saturdayOffset.toLong()
            )

        return (0L..6L).map { offset ->

            val date =
                saturday.plusDays(offset)

            fromGregorian(
                year = date.year,
                month = date.monthValue,
                day = date.dayOfMonth
            ).toKey()
        }
    }

    // ========================================================
    // CURRENT MONTH
    // ========================================================

    fun currentMonthDates(): List<String> {

        val today =
            today()

        val days =
            daysInMonth(
                year = today.year,
                month = today.month
            )

        return (1..days).map { day ->

            RoutineJalaliDate(
                year = today.year,
                month = today.month,
                day = day
            ).toKey()
        }
    }

    // ========================================================
    // CURRENT PERIOD
    // ========================================================

    fun currentPeriod(
        type: RoutineType
    ): Pair<String, String> {

        return when (type) {

            RoutineType.WEEKLY -> {

                val dates =
                    currentWeekDates()

                Pair(
                    dates.first(),
                    dates.last()
                )
            }

            RoutineType.MONTHLY -> {

                val dates =
                    currentMonthDates()

                Pair(
                    dates.first(),
                    dates.last()
                )
            }
        }
    }
}