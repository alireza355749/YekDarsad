package com.example.yekdarsad.ui.overview

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yekdarsad.data.DailyPlanWithTask
import com.example.yekdarsad.ui.theme.PrimaryGreen
import com.example.yekdarsad.ui.theme.PrimaryGreenLight
import com.example.yekdarsad.ui.theme.TextSecondary
import java.time.LocalDate

// ============================================================
// MONTHLY HEADER
// ============================================================

@Composable
fun MonthlyHeader(
    year: Int,
    month: Int,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(13.dp)
            )
            .background(
                MaterialTheme
                    .colorScheme
                    .surface
            )
            .border(
                width = 0.7.dp,
                color =
                    MaterialTheme
                        .colorScheme
                        .outline
                        .copy(alpha = 0.12f),
                shape =
                    RoundedCornerShape(13.dp)
            )
            .padding(
                horizontal = 4.dp,
                vertical = 3.dp
            ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        IconButton(
            onClick = onPreviousMonth,
            modifier = Modifier.size(34.dp)
        ) {

            Icon(
                imageVector =
                    Icons.Default.ChevronLeft,

                contentDescription =
                    "ماه قبل",

                tint =
                    TextSecondary,

                modifier =
                    Modifier.size(19.dp)
            )
        }

        Column(
            modifier =
                Modifier.weight(1f),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text =
                    persianMonthName(month),

                fontSize =
                    14.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    MaterialTheme
                        .colorScheme
                        .onSurface
            )

            Text(
                text =
                    toPersianDigits(
                        year.toString()
                    ),

                fontSize =
                    8.sp,

                color =
                    TextSecondary
            )
        }

        IconButton(
            onClick = onNextMonth,
            modifier = Modifier.size(34.dp)
        ) {

            Icon(
                imageVector =
                    Icons.Default.ChevronRight,

                contentDescription =
                    "ماه بعد",

                tint =
                    TextSecondary,

                modifier =
                    Modifier.size(19.dp)
            )
        }
    }
}

// ============================================================
// WEEK HEADER
// ============================================================

@Composable
fun MonthlyWeekHeader() {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    topStart = 10.dp,
                    topEnd = 10.dp
                )
            )
            .background(
                PrimaryGreenLight.copy(
                    alpha = 0.55f
                )
            )
            .border(
                width = 0.7.dp,

                color =
                    PrimaryGreen.copy(
                        alpha = 0.18f
                    ),

                shape =
                    RoundedCornerShape(
                        topStart = 10.dp,
                        topEnd = 10.dp
                    )
            )
            .padding(
                vertical = 7.dp
            )
    ) {

        listOf(
            "شنبه",
            "یکشنبه",
            "دوشنبه",
            "سه‌شنبه",
            "چهارشنبه",
            "پنجشنبه",
            "جمعه"
        ).forEach { dayName ->

            Text(
                text =
                    dayName,

                modifier =
                    Modifier.weight(1f),

                fontSize =
                    8.sp,

                fontWeight =
                    FontWeight.Bold,

                textAlign =
                    TextAlign.Center,

                color =
                    PrimaryGreen
            )
        }
    }
}

// ============================================================
// MONTHLY DAY CELL
// ============================================================

@Composable
fun MonthlyDayCell(
    date: LocalDate?,
    plans: List<DailyPlanWithTask>,
    isToday: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongPress: (LocalDate) -> Unit = {},
    modifier: Modifier
) {

    if (date == null) {

        Box(
            modifier =
                modifier.height(88.dp)
        )

        return
    }

    val shape =
        RoundedCornerShape(9.dp)

    Column(
        modifier = modifier
            .height(88.dp)

            /*
             * فقط همین Handler مسئول
             * تشخیص لمس کوتاه و طولانی است.
             *
             * Popup اینجا ساخته نمی‌شود.
             * Popup فقط در MonthlyOverviewScreen ساخته می‌شود.
             */
            .dayLongPressHandler(

                date =
                    date,

                onDateTapped = {

                    onClick()
                },

                onDateLongPressed = {

                    onLongPress(it)
                }
            )

            .clip(shape)

            .background(
                when {

                    isSelected ->
                        PrimaryGreenLight.copy(
                            alpha = 0.34f
                        )

                    isToday ->
                        PrimaryGreenLight.copy(
                            alpha = 0.20f
                        )

                    else ->
                        MaterialTheme
                            .colorScheme
                            .surface
                            .copy(
                                alpha = 0.55f
                            )
                }
            )

            .border(
                width =
                    when {

                        isSelected ->
                            1.3.dp

                        isToday ->
                            1.dp

                        else ->
                            0.6.dp
                    },

                color =
                    when {

                        isSelected ->
                            PrimaryGreen

                        isToday ->
                            PrimaryGreen.copy(
                                alpha = 0.65f
                            )

                        else ->
                            MaterialTheme
                                .colorScheme
                                .outline
                                .copy(
                                    alpha = 0.13f
                                )
                    },

                shape =
                    shape
            )

            .padding(
                horizontal = 4.dp,
                vertical = 4.dp
            )
    ) {

        // ----------------------------------------------------
        // شماره روز
        // ----------------------------------------------------

        Box(
            modifier =
                Modifier.fillMaxWidth(),

            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text =
                    toPersianDigits(
                        jalaliDayFromGregorian(
                            date
                        ).toString()
                    ),

                fontSize =
                    12.sp,

                fontWeight =
                    if (
                        isSelected ||
                        isToday
                    ) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Medium
                    },

                color =
                    if (
                        isSelected ||
                        isToday
                    ) {
                        PrimaryGreen
                    } else {
                        MaterialTheme
                            .colorScheme
                            .onSurface
                    }
            )
        }

        Spacer(
            modifier =
                Modifier.height(2.dp)
        )

        // ----------------------------------------------------
        // فعالیت‌های داخل سلول
        // ----------------------------------------------------

        if (plans.isEmpty()) {

            Text(
                text =
                    "—",

                modifier =
                    Modifier.fillMaxWidth(),

                textAlign =
                    TextAlign.Center,

                fontSize =
                    9.sp,

                color =
                    TextSecondary.copy(
                        alpha = 0.45f
                    )
            )

        } else {

            plans
                .take(3)
                .forEach { plan ->

                    Text(
                        text =
                            "• ${plan.task.title}",

                        modifier =
                            Modifier.fillMaxWidth(),

                        maxLines =
                            1,

                        overflow =
                            TextOverflow.Ellipsis,

                        fontSize =
                            7.sp,

                        lineHeight =
                            9.sp,

                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurface
                    )
                }

            if (plans.size > 3) {

                Text(
                    text =
                        "+${
                            toPersianDigits(
                                (
                                        plans.size - 3
                                        ).toString()
                            )
                        } مورد",

                    modifier =
                        Modifier.fillMaxWidth(),

                    textAlign =
                        TextAlign.Center,

                    fontSize =
                        7.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        PrimaryGreen
                )
            }
        }
    }
}

// ============================================================
// MONTHLY ACTIVITY POPUP
// ============================================================

@Composable
fun MonthlyTaskPopup(
    date: LocalDate,
    plans: List<DailyPlanWithTask>,
    onDismiss: () -> Unit
) {

    val shape =
        RoundedCornerShape(14.dp)

    Column(
        modifier =
            Modifier
                .width(230.dp)
                .clip(shape)
                .background(
                    MaterialTheme
                        .colorScheme
                        .surface
                )
                .border(
                    width = 0.8.dp,

                    color =
                        PrimaryGreen.copy(
                            alpha = 0.20f
                        ),

                    shape =
                        shape
                )
                .padding(
                    horizontal = 12.dp,
                    vertical = 10.dp
                )
    ) {

        // ----------------------------------------------------
        // Header
        // ----------------------------------------------------

        Row(
            modifier =
                Modifier.fillMaxWidth(),

            verticalAlignment =
                Alignment.CenterVertically,

            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Column {

                Text(
                    text =
                        "فعالیت‌های روز",

                    fontSize =
                        13.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurface
                )

                Spacer(
                    modifier =
                        Modifier.height(1.dp)
                )

                val jalali =
                    gregorianToJalali(
                        date.year,
                        date.monthValue,
                        date.dayOfMonth
                    )

                Text(
                    text =
                        "${
                            persianMonthName(
                                jalali[1]
                            )
                        } ${
                            toPersianDigits(
                                jalali[2].toString()
                            )
                        }",

                    fontSize =
                        9.sp,

                    color =
                        TextSecondary
                )
            }

            Text(
                text =
                    "${
                        toPersianDigits(
                            plans.size.toString()
                        )
                    } فعالیت",

                fontSize =
                    9.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    PrimaryGreen
            )
        }

        Spacer(
            modifier =
                Modifier.height(7.dp)
        )

        // ----------------------------------------------------
        // لیست ساده فعالیت‌ها
        // ----------------------------------------------------

        if (plans.isEmpty()) {

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = 14.dp
                        ),

                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text =
                        "برای این روز فعالیتی ثبت نشده",

                    fontSize =
                        10.sp,

                    color =
                        TextSecondary
                )
            }

        } else {

            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(
                            if (plans.size > 6)
                                220.dp
                            else
                                (plans.size * 39).dp
                        ),

                verticalArrangement =
                    Arrangement.spacedBy(1.dp)
            ) {

                itemsIndexed(
                    items = plans
                ) { index, plan ->

                    MonthlyActivityListItem(
                        plan =
                            plan,

                        index =
                            index
                    )
                }
            }
        }
    }
}

// ============================================================
// SIMPLE ACTIVITY LIST ITEM
// بدون کارت، بدون پس‌زمینه، بدون ارتفاع اضافه
// ============================================================

@Composable
private fun MonthlyActivityListItem(
    plan: DailyPlanWithTask,
    index: Int
) {

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 4.dp
                ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        // ----------------------------------------------------
        // شماره
        // ----------------------------------------------------

        Text(
            text =
                "${toPersianDigits(
                    (index + 1).toString()
                )}.",

            modifier =
                Modifier.width(20.dp),

            fontSize =
                10.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                PrimaryGreen,

            textAlign =
                TextAlign.Center
        )

        Spacer(
            modifier =
                Modifier.width(6.dp)
        )

        // ----------------------------------------------------
        // نام فعالیت
        // ----------------------------------------------------

        Text(
            text =
                plan.task.title,

            modifier =
                Modifier.weight(1f),

            fontSize =
                10.sp,

            fontWeight =
                FontWeight.Medium,

            maxLines =
                1,

            overflow =
                TextOverflow.Ellipsis,

            color =
                MaterialTheme
                    .colorScheme
                    .onSurface
        )

        Spacer(
            modifier =
                Modifier.width(7.dp)
        )

        // ----------------------------------------------------
        // زمان / ضریب
        // ----------------------------------------------------

        Text(
            text =
                if (
                    plan.task.type == "TIME"
                ) {

                    "${
                        toPersianDigits(
                            plan.dailyPlan
                                .plannedMinutes
                                .toString()
                        )
                    } دقیقه"

                } else {

                    "ضریب ${
                        toPersianDigits(
                            plan.task.coefficient
                                .toString()
                        )
                    }"
                },

            fontSize =
                11.sp,

            fontWeight =
                FontWeight.Bold,

            color =
                PrimaryGreen,

            maxLines =
                1
        )
    }
}