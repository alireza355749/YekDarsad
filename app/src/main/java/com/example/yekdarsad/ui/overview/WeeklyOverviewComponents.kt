package com.example.yekdarsad.ui.overview

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yekdarsad.data.Category
import com.example.yekdarsad.data.DailyPlanWithTask
import com.example.yekdarsad.ui.theme.PrimaryGreen
import com.example.yekdarsad.ui.theme.PrimaryGreenLight
import com.example.yekdarsad.ui.theme.TextSecondary

// ============================================================
// HEADER جدول
// ============================================================

@Composable
fun WeeklyTableHeader() {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    topStart = 9.dp,
                    topEnd = 9.dp
                )
            )
            .background(
                PrimaryGreenLight.copy(
                    alpha = 0.55f
                )
            )
            .border(
                width = 0.7.dp,
                color = PrimaryGreen.copy(
                    alpha = 0.18f
                ),
                shape = RoundedCornerShape(
                    topStart = 9.dp,
                    topEnd = 9.dp
                )
            )
            .padding(
                vertical = 7.dp
            ),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Text(
            text = "روز",
            modifier = Modifier
                .width(64.dp)
                .padding(start = 6.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryGreen
        )

        Text(
            text = "برنامه",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryGreen
        )
    }
}

// ============================================================
// خلاصه هفتگی
// ============================================================

@Composable
fun WeeklyCategorySummary(
    totalTasks: Int,
    completedTasks: Int,
    completionPercent: Int,
    totalMinutes: Int,
    categoryCounts: List<Pair<Int, Int>>,
    onClose: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(13.dp)
            )
            .background(
                MaterialTheme.colorScheme.surface
            )
            .border(
                width = 0.8.dp,
                color = PrimaryGreen.copy(
                    alpha = 0.22f
                ),
                shape = RoundedCornerShape(13.dp)
            )
            .padding(10.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "خلاصه هفته",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme
                        .colorScheme
                        .onSurface
                )

                Text(
                    text = "نمای کلی عملکرد این هفته",
                    fontSize = 8.sp,
                    color = TextSecondary
                )
            }

            Box(
                modifier = Modifier
                    .size(27.dp)
                    .clip(
                        RoundedCornerShape(50.dp)
                    )
                    .clickable {
                        onClose()
                    },
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Close,
                    contentDescription =
                        "بستن خلاصه",
                    modifier =
                        Modifier.size(16.dp),
                    tint = TextSecondary
                )
            }
        }

        Spacer(
            modifier = Modifier.height(9.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(5.dp)
        ) {

            WeeklySummaryStat(
                modifier = Modifier.weight(1f),
                title = "کل برنامه",
                value =
                    toPersianDigitsWeekly(
                        totalTasks.toString()
                    )
            )

            WeeklySummaryStat(
                modifier = Modifier.weight(1f),
                title = "انجام شده",
                value =
                    toPersianDigitsWeekly(
                        completedTasks.toString()
                    )
            )

            WeeklySummaryStat(
                modifier = Modifier.weight(1f),
                title = "عملکرد",
                value =
                    "${
                        toPersianDigitsWeekly(
                            completionPercent.toString()
                        )
                    }%"
            )

            WeeklySummaryStat(
                modifier = Modifier.weight(1f),
                title = "زمان",
                value =
                    formatMinutesWeekly(
                        totalMinutes
                    )
            )
        }

        if (categoryCounts.isNotEmpty()) {

            Spacer(
                modifier = Modifier.height(9.dp)
            )

            Text(
                text = "توزیع برنامه‌ها",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme
                    .colorScheme
                    .onSurface
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(
                        rememberScrollState()
                    ),
                horizontalArrangement =
                    Arrangement.spacedBy(5.dp)
            ) {

                categoryCounts.forEach {
                        (categoryId, count)
                    ->

                    Box(
                        modifier = Modifier
                            .clip(
                                RoundedCornerShape(8.dp)
                            )
                            .background(
                                PrimaryGreenLight.copy(
                                    alpha = 0.30f
                                )
                            )
                            .border(
                                width = 0.6.dp,
                                color = PrimaryGreen.copy(
                                    alpha = 0.18f
                                ),
                                shape =
                                    RoundedCornerShape(8.dp)
                            )
                            .padding(
                                horizontal = 8.dp,
                                vertical = 5.dp
                            )
                    ) {

                        Column(
                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {

                            Text(
                                text =
                                    "دسته ${
                                        toPersianDigitsWeekly(
                                            categoryId.toString()
                                        )
                                    }",
                                fontSize = 8.sp,
                                color = MaterialTheme
                                    .colorScheme
                                    .onSurface
                            )

                            Text(
                                text =
                                    "${
                                        toPersianDigitsWeekly(
                                            count.toString()
                                        )
                                    } برنامه",
                                fontSize = 7.sp,
                                color = PrimaryGreen,
                                fontWeight =
                                    FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

// ============================================================
// کارت آمار کوچک
// ============================================================

@Composable
fun WeeklySummaryStat(
    modifier: Modifier,
    title: String,
    value: String
) {

    Column(
        modifier = modifier
            .clip(
                RoundedCornerShape(9.dp)
            )
            .background(
                PrimaryGreenLight.copy(
                    alpha = 0.18f
                )
            )
            .border(
                width = 0.6.dp,
                color = PrimaryGreen.copy(
                    alpha = 0.13f
                ),
                shape = RoundedCornerShape(9.dp)
            )
            .padding(
                vertical = 7.dp
            ),
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryGreen
        )

        Spacer(
            modifier = Modifier.height(2.dp)
        )

        Text(
            text = title,
            fontSize = 7.sp,
            color = TextSecondary
        )
    }
}

// ============================================================
// ردیف روز
// ============================================================

@Composable
fun WeekDayRowWeekly(
    dayName: String,
    date: java.time.LocalDate,
    plans: List<DailyPlanWithTask>,
    isToday: Boolean,
    isLast: Boolean,
    categories: List<Category>,
    onDateSelected: (java.time.LocalDate) -> Unit
) {

    val horizontalScroll =
        rememberScrollState()

    val rowShape =
        if (isLast) {
            RoundedCornerShape(
                bottomStart = 9.dp,
                bottomEnd = 9.dp
            )
        } else {
            RoundedCornerShape(0.dp)
        }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(rowShape)
            .background(
                if (isToday) {
                    PrimaryGreenLight.copy(
                        alpha = 0.16f
                    )
                } else {
                    MaterialTheme
                        .colorScheme
                        .surface
                        .copy(alpha = 0.55f)
                }
            )
            .border(
                width =
                    if (isToday) 1.dp else 0.7.dp,
                color =
                    if (isToday) {
                        PrimaryGreen.copy(
                            alpha = 0.50f
                        )
                    } else {
                        MaterialTheme
                            .colorScheme
                            .outline
                            .copy(alpha = 0.16f)
                    },
                shape = rowShape
            )
            .clickable {
                onDateSelected(date)
            }
            .padding(
                vertical = 5.dp
            ),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        // ========================================================
        // روز
        // ========================================================

        Column(
            modifier = Modifier
                .width(64.dp)
                .padding(horizontal = 3.dp),
            horizontalAlignment =
                Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.Center
        ) {

            Text(
                text = dayName,
                fontSize = 10.sp,
                fontWeight =
                    if (isToday) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Medium
                    },
                maxLines = 1,
                overflow =
                    TextOverflow.Ellipsis,
                color =
                    if (isToday) {
                        PrimaryGreen
                    } else {
                        MaterialTheme
                            .colorScheme
                            .onSurface
                    }
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(5.dp)
                    )
                    .background(
                        if (isToday) {
                            PrimaryGreen
                        } else {
                            MaterialTheme
                                .colorScheme
                                .outline
                                .copy(alpha = 0.06f)
                        }
                    )
                    .border(
                        width = 0.7.dp,
                        color =
                            if (isToday) {
                                PrimaryGreen
                            } else {
                                MaterialTheme
                                    .colorScheme
                                    .outline
                                    .copy(alpha = 0.20f)
                            },
                        shape =
                            RoundedCornerShape(5.dp)
                    )
                    .padding(
                        horizontal = 7.dp,
                        vertical = 2.dp
                    )
            ) {

                Text(
                    text =
                        jalaliDayWeekly(date),
                    fontSize = 12.sp,
                    lineHeight = 13.sp,
                    fontWeight =
                        if (isToday) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Medium
                        },
                    color =
                        if (isToday) {
                            Color.White
                        } else {
                            MaterialTheme
                                .colorScheme
                                .onSurface
                        }
                )
            }
        }

        // ========================================================
        // جداکننده
        // ========================================================

        Box(
            modifier = Modifier
                .width(1.dp)
                .height(44.dp)
                .background(
                    MaterialTheme
                        .colorScheme
                        .outline
                        .copy(
                            alpha = 0.14f
                        )
                )
        )

        Spacer(
            modifier = Modifier.width(5.dp)
        )

        // ========================================================
        // برنامه‌ها
        // ========================================================

        if (plans.isEmpty()) {

            Text(
                text = "برنامه‌ای ثبت نشده",
                fontSize = 9.sp,
                color = TextSecondary,
                modifier = Modifier.padding(
                    start = 2.dp
                )
            )

        } else {

            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(
                        horizontalScroll
                    ),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                val columns =
                    (plans.size + 1) / 2

                Column(
                    verticalArrangement =
                        Arrangement.spacedBy(3.dp)
                ) {

                    // =================================================
                    // ردیف اول
                    // =================================================

                    Row(
                        horizontalArrangement =
                            Arrangement.spacedBy(3.dp)
                    ) {

                        for (
                        columnIndex
                        in 0 until columns
                        ) {

                            val planIndex =
                                columnIndex * 2

                            if (
                                planIndex < plans.size
                            ) {

                                OverviewTaskItemWeekly(
                                    plan =
                                        plans[planIndex],
                                    number =
                                        planIndex + 1,
                                    categories =
                                        categories
                                )
                            }
                        }
                    }

                    // =================================================
                    // ردیف دوم
                    // =================================================

                    if (plans.size > 1) {

                        Row(
                            horizontalArrangement =
                                Arrangement.spacedBy(3.dp)
                        ) {

                            for (
                            columnIndex
                            in 0 until columns
                            ) {

                                val planIndex =
                                    columnIndex * 2 + 1

                                if (
                                    planIndex < plans.size
                                ) {

                                    OverviewTaskItemWeekly(
                                        plan =
                                            plans[planIndex],
                                        number =
                                            planIndex + 1,
                                        categories =
                                            categories
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================
// کارت کوچک تسک
// ============================================================

@Composable
fun OverviewTaskItemWeekly(
    plan: DailyPlanWithTask,
    number: Int,
    categories: List<Category>
) {

    val task = plan.task

    val dailyPlan = plan.dailyPlan

    val categoryName =
        categories
            .firstOrNull {
                it.id == task.categoryId
            }
            ?.name
            ?: ""

    val colors =
        com.example.yekdarsad.ui.theme.categoryColors(
            categoryName
        )

    Row(
        modifier = Modifier
            .width(112.dp)
            .height(35.dp)
            .clip(
                RoundedCornerShape(7.dp)
            )
            .background(
                colors.background
            )
            .border(
                width = 0.8.dp,
                color = colors.accent.copy(
                    alpha = 0.28f
                ),
                shape = RoundedCornerShape(7.dp)
            )
            .padding(
                horizontal = 5.dp,
                vertical = 3.dp
            ),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        // ========================================================
        // شماره
        // ========================================================

        Box(
            modifier = Modifier
                .size(23.dp)
                .clip(
                    RoundedCornerShape(6.dp)
                )
                .background(
                    colors.iconBackground
                ),
            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text =
                    toPersianDigitsWeekly(
                        number.toString()
                    ),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = colors.accent
            )
        }

        Spacer(
            modifier = Modifier.width(5.dp)
        )

        // ========================================================
        // عنوان
        // ========================================================

        Text(
            text = task.title,
            fontSize = 8.5.sp,
            lineHeight = 9.5.sp,
            maxLines = 2,
            overflow =
                TextOverflow.Ellipsis,
            fontWeight =
                FontWeight.Medium,
            textAlign =
                TextAlign.Start,
            modifier =
                Modifier.weight(1f),
            color = colors.accent
        )

        // ========================================================
        // زمان
        // ========================================================

        if (
            task.type == "TIME" &&
            dailyPlan.plannedMinutes > 0
        ) {

            Spacer(
                modifier = Modifier.width(4.dp)
            )

            Text(
                text =
                    "${
                        toPersianDigitsWeekly(
                            dailyPlan.plannedMinutes
                                .toString()
                        )
                    }د",
                fontSize = 10.sp,
                lineHeight = 11.sp,
                maxLines = 1,
                fontWeight =
                    FontWeight.Bold,
                textAlign =
                    TextAlign.Center,
                color = colors.accent
            )
        }
    }
}