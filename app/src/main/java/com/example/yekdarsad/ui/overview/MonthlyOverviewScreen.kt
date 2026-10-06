package com.example.yekdarsad.ui.overview

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.yekdarsad.data.Category
import com.example.yekdarsad.data.DailyPlanWithTask
import com.example.yekdarsad.data.DatabaseProvider
import com.example.yekdarsad.ui.theme.PrimaryGreen
import com.example.yekdarsad.ui.theme.PrimaryGreenLight
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

// ============================================================
// MONTHLY OVERVIEW SCREEN
// ============================================================

@Composable
fun MonthlyOverviewScreen(

    onCategoryTotalsChanged: (
        List<OverviewCategoryTime>
    ) -> Unit = {},

    onDateSelected: (
        LocalDate
    ) -> Unit = {},

    onDisplayedMonthChanged: (
        Int,
        Int
    ) -> Unit = { _, _ -> }

) {

    // ========================================================
    // CONTEXT / DATABASE
    // ========================================================

    val context =
        LocalContext.current

    val database =
        remember {
            DatabaseProvider.getDatabase(context)
        }

    val dailyPlanDao =
        database.dailyPlanDao()

    val categoryDao =
        database.categoryDao()

    // ========================================================
    // TODAY
    // ========================================================

    val today =
        remember {

            LocalDate.now(
                ZoneId.of("Asia/Tehran")
            )
        }

    // ========================================================
    // TODAY JALALI
    // ========================================================

    val todayJalali =
        remember(today) {

            gregorianToJalali(
                today.year,
                today.monthValue,
                today.dayOfMonth
            )
        }

    val currentJalaliYear =
        todayJalali[0]

    val currentJalaliMonth =
        todayJalali[1]

    // ========================================================
    // DISPLAYED MONTH OFFSET
    //
    // 0  = ماه جاری
    // -1 = ماه قبل
    // +1 = ماه بعد
    //
    // این روش باعث می‌شود عبور از سال و فروردین/اسفند
    // کاملاً امن باشد.
    // ========================================================

    var monthOffset by remember {

        mutableStateOf(0)
    }

    // ========================================================
    // CALCULATE DISPLAYED JALALI MONTH
    // ========================================================

    val displayedMonth =
        remember(
            currentJalaliYear,
            currentJalaliMonth,
            monthOffset
        ) {

            calculateJalaliMonthByOffset(
                baseYear =
                    currentJalaliYear,

                baseMonth =
                    currentJalaliMonth,

                offset =
                    monthOffset
            )
        }

    val displayedJalaliYear =
        displayedMonth.first

    val displayedJalaliMonth =
        displayedMonth.second

    // ========================================================
    // SEND DISPLAYED MONTH TO PARENT
    // ========================================================

    LaunchedEffect(
        displayedJalaliYear,
        displayedJalaliMonth
    ) {

        onDisplayedMonthChanged(
            displayedJalaliYear,
            displayedJalaliMonth
        )
    }

    // ========================================================
    // SELECTED DATE
    // ========================================================

    var selectedDate by remember {

        mutableStateOf<LocalDate?>(null)
    }

    // ========================================================
    // ACTIVITY POPUP
    // ========================================================

    var activityPopupVisible by remember {

        mutableStateOf(false)
    }

    // ========================================================
    // CATEGORIES
    // ========================================================

    var categories by remember {

        mutableStateOf<List<Category>>(
            emptyList()
        )
    }

    // ========================================================
    // PLANS BY DATE
    // ========================================================

    val plansByDate =
        remember {

            mutableStateMapOf<
                    String,
                    List<DailyPlanWithTask>
                    >()
        }

    // ========================================================
    // LOAD CATEGORIES
    // ========================================================

    LaunchedEffect(Unit) {

        try {

            categories =
                categoryDao.getAll()

        } catch (
            e: Exception
        ) {

            categories =
                emptyList()
        }
    }

    // ========================================================
    // MONTH DAYS
    // ========================================================

    val daysInMonth =
        remember(
            displayedJalaliYear,
            displayedJalaliMonth
        ) {

            daysInJalaliMonth(
                displayedJalaliYear,
                displayedJalaliMonth
            )
        }

    // ========================================================
    // FIRST GREGORIAN DATE
    // ========================================================

    val firstGregorianDate =
        remember(
            displayedJalaliYear,
            displayedJalaliMonth
        ) {

            jalaliToGregorian(
                displayedJalaliYear,
                displayedJalaliMonth,
                1
            )
        }

    // ========================================================
    // START OFFSET
    // ========================================================

    val startOffset =
        remember(
            firstGregorianDate
        ) {

            saturdayBasedDayIndex(
                firstGregorianDate
            )
        }

    // ========================================================
    // TOTAL CELLS
    // ========================================================

    val totalCells =
        remember(
            startOffset,
            daysInMonth
        ) {

            calculateCalendarCells(
                startOffset =
                    startOffset,

                daysInMonth =
                    daysInMonth
            )
        }

    // ========================================================
    // CALENDAR DATES
    // ========================================================

    val calendarDates =
        remember(
            displayedJalaliYear,
            displayedJalaliMonth,
            startOffset,
            daysInMonth,
            totalCells
        ) {

            buildMonthlyCalendarDates(
                year =
                    displayedJalaliYear,

                month =
                    displayedJalaliMonth,

                startOffset =
                    startOffset,

                daysInMonth =
                    daysInMonth,

                totalCells =
                    totalCells
            )
        }

    // ========================================================
    // LOAD ACTIVITIES
    // ========================================================

    LaunchedEffect(
        displayedJalaliYear,
        displayedJalaliMonth
    ) {

        plansByDate.clear()

        calendarDates
            .filterNotNull()
            .forEach { date ->

                launch {

                    dailyPlanDao
                        .getByDate(
                            date.toString()
                        )
                        .collect { plans ->

                            plansByDate[
                                date.toString()
                            ] =
                                plans.filter {

                                    it.task.coefficient != 0.0
                                }
                        }
                }
            }
    }

    // ========================================================
    // CLOSE POPUP WHEN MONTH CHANGES
    // ========================================================

    LaunchedEffect(
        displayedJalaliYear,
        displayedJalaliMonth
    ) {

        selectedDate =
            null

        activityPopupVisible =
            false
    }

    // ========================================================
    // MONTHLY CATEGORY TOTALS
    // ========================================================

    LaunchedEffect(
        plansByDate.toMap(),
        categories
    ) {

        if (
            categories.isEmpty()
        ) {

            onCategoryTotalsChanged(
                emptyList()
            )

            return@LaunchedEffect
        }

        val monthlyTotals =
            buildMonthlyCategoryTotals(
                plansByDate =
                    plansByDate,

                categories =
                    categories
            )

        val overviewTotals =
            monthlyTotals.map { item ->

                OverviewCategoryTime(

                    category =
                        item.category,

                    minutes =
                        item.totalMinutes
                )
            }

        onCategoryTotalsChanged(
            overviewTotals
        )
    }

    // ========================================================
    // ROOT
    // ========================================================

    Box(

        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Color.White
                )
                .padding(
                    horizontal = 8.dp,
                    vertical = 6.dp
                )

    ) {

        Column(

            modifier =
                Modifier.fillMaxSize()

        ) {

            // ==================================================
            // HEADER
            // ==================================================

            MonthlyHeader(

                year =
                    displayedJalaliYear,

                month =
                    displayedJalaliMonth,

                // ----------------------------------------------
                // PREVIOUS MONTH
                // ----------------------------------------------

                onPreviousMonth = {

                    activityPopupVisible =
                        false

                    selectedDate =
                        null

                    monthOffset--
                },

                // ----------------------------------------------
                // NEXT MONTH
                // ----------------------------------------------

                onNextMonth = {

                    activityPopupVisible =
                        false

                    selectedDate =
                        null

                    monthOffset++
                }
            )

            // ==================================================
            // CURRENT MONTH BUTTON
            // ==================================================

            if (
                monthOffset != 0
            ) {

                Spacer(
                    modifier =
                        Modifier.height(
                            5.dp
                        )
                )

                Box(

                    modifier =
                        Modifier
                            .align(
                                Alignment.CenterHorizontally
                            )
                            .clip(
                                RoundedCornerShape(
                                    50.dp
                                )
                            )
                            .background(
                                PrimaryGreenLight.copy(
                                    alpha = 0.45f
                                )
                            )
                            .border(
                                width = 0.6.dp,

                                color =
                                    PrimaryGreen.copy(
                                        alpha = 0.25f
                                    ),

                                shape =
                                    RoundedCornerShape(
                                        50.dp
                                    )
                            )
                            .clickable {

                                monthOffset =
                                    0

                                selectedDate =
                                    null

                                activityPopupVisible =
                                    false
                            }
                            .padding(
                                horizontal = 12.dp,
                                vertical = 4.dp
                            )

                ) {

                    Text(

                        text =
                            "برو به ماه جاری",

                        fontSize =
                            8.sp,

                        fontWeight =
                            FontWeight.Medium,

                        color =
                            PrimaryGreen
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(
                        5.dp
                    )
            )

            // ==================================================
            // WEEK HEADER
            // ==================================================

            MonthlyWeekHeader()

            Spacer(
                modifier =
                    Modifier.height(
                        2.dp
                    )
            )

            // ==================================================
            // CALENDAR
            // ==================================================

            Box(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)

            ) {

                LazyColumn(

                    modifier =
                        Modifier.fillMaxSize(),

                    verticalArrangement =
                        Arrangement.spacedBy(
                            2.dp
                        )

                ) {

                    items(
                        count =
                            totalCells / 7
                    ) { weekIndex ->

                        Row(

                            modifier =
                                Modifier.fillMaxWidth(),

                            horizontalArrangement =
                                Arrangement.spacedBy(
                                    2.dp
                                )

                        ) {

                            repeat(7) { dayIndex ->

                                val index =
                                    weekIndex * 7 +
                                            dayIndex

                                val date =
                                    calendarDates[index]

                                val plans =
                                    date
                                        ?.let {

                                            plansByDate[
                                                it.toString()
                                            ].orEmpty()

                                        }
                                        .orEmpty()

                                MonthlyDayCell(

                                    date =
                                        date,

                                    plans =
                                        plans,

                                    isToday =
                                        date == today,

                                    isSelected =
                                        date ==
                                                selectedDate,

                                    // ----------------------------------
                                    // SHORT CLICK
                                    // ----------------------------------

                                    onClick = {

                                        if (
                                            date != null
                                        ) {

                                            selectedDate =
                                                date

                                            activityPopupVisible =
                                                true
                                        }
                                    },

                                    // ----------------------------------
                                    // LONG PRESS
                                    // ----------------------------------

                                    onLongPress = {
                                            selectedDateForToday ->

                                        activityPopupVisible =
                                            false

                                        selectedDate =
                                            null

                                        onDateSelected(
                                            selectedDateForToday
                                        )
                                    },

                                    modifier =
                                        Modifier.weight(
                                            1f
                                        )
                                )
                            }
                        }
                    }
                }

                // =================================================
                // ACTIVITY POPUP
                // =================================================

                if (
                    activityPopupVisible &&
                    selectedDate != null
                ) {

                    val popupDate =
                        selectedDate!!

                    val popupPlans =
                        plansByDate[
                            popupDate.toString()
                        ].orEmpty()

                    Popup(

                        alignment =
                            Alignment.TopCenter,

                        onDismissRequest = {

                            activityPopupVisible =
                                false

                            selectedDate =
                                null
                        },

                        properties =
                            PopupProperties(

                                focusable =
                                    true,

                                dismissOnBackPress =
                                    true,

                                dismissOnClickOutside =
                                    true
                            )

                    ) {

                        MonthlyTaskPopup(

                            date =
                                popupDate,

                            plans =
                                popupPlans,

                            onDismiss = {

                                activityPopupVisible =
                                    false

                                selectedDate =
                                    null
                            }
                        )
                    }
                }
            }
        }
    }
}

// ============================================================
// JALALI MONTH NAVIGATION
// ============================================================
//
// این تابع عمداً مستقل از تبدیل Gregorian است.
//
// مثال:
//
// 1405/01 + (-1) = 1404/12
// 1405/01 + (+1) = 1405/02
// 1405/12 + (+1) = 1406/01
//
// بنابراین مشکل فروردین/اسفند ایجاد نمی‌شود.
// ============================================================

private fun calculateJalaliMonthByOffset(

    baseYear: Int,

    baseMonth: Int,

    offset: Int

): Pair<Int, Int> {

    val baseIndex =
        baseYear * 12 +
                (baseMonth - 1)

    val targetIndex =
        baseIndex + offset

    val targetYear =
        Math.floorDiv(
            targetIndex,
            12
        )

    val targetMonth =
        Math.floorMod(
            targetIndex,
            12
        ) + 1

    return Pair(
        targetYear,
        targetMonth
    )
}