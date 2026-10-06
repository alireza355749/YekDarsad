package com.example.yekdarsad.ui.statistics.overall

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.yekdarsad.ui.theme.CardBackground
import com.example.yekdarsad.ui.theme.PrimaryGreen
import com.example.yekdarsad.viewmodel.StatisticsViewModel
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun OverallStatisticsScreen(
    statisticsViewModel: StatisticsViewModel
) {

    val statistics by statisticsViewModel
        .statistics
        .collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            )
    ) {

        // ====================================================
        // عنوان
        // ====================================================

        Text(
            text = "آمار کلی",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        // ====================================================
        // خلاصه کلی
        // ====================================================

        OverallStatisticsSummary(
            totalActivityHours =
                statistics.totalActivityHours
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        // ====================================================
        // عملکرد کلی
        //
        // امتیاز روزانه از 100
        // ====================================================

        OverallPerformanceChart(
            values =
                statistics.dailyScores,

            days =
                statistics.weekDays
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        // ====================================================
        // سایر نمودارهای کلی
        // ====================================================

        OverallStatisticsCharts(
            weekDays =
                statistics.weekDays,

            activityData =
                statistics.weeklyActivityHours,

            calorieData =
                statistics.calorieIntake,

            sleepData =
                statistics.sleepHours,

            phoneData =
                statistics.phoneUsageHours,

            leisureData =
                statistics.leisureHours,

            spiritualityData =
                statistics.spiritualityHours,

            exerciseData =
                statistics.exerciseHours,

            scoreData =
                emptyList()
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        // ====================================================
        // خلاصه‌های تکمیلی
        // ====================================================

        OverallAdditionalStats(
            averageSleepHours =
                statistics.averageSleepHours,

            weeklyScore =
                statistics.weeklyScore,

            averageCalories =
                statistics.averageCalories,

            totalCalories =
                statistics.totalCalories,

            activityValues =
                statistics.weeklyActivityHours,

            days =
                statistics.weekDays
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )
    }
}

// ============================================================
// OVERALL PERFORMANCE
// ============================================================

@Composable
private fun OverallPerformanceChart(
    values: List<Float>,
    days: List<String>
) {

    val accent =
        OverallScoreColor

    val maximum =
        100f

    val total =
        values.sum()

    val average =
        if (values.isNotEmpty()) {
            values.average().toFloat()
        } else {
            0f
        }

    val best =
        values.maxOrNull()
            ?: 0f

    OverallChartCard(
        title = "عملکرد کلی",
        accent = accent
    ) {

        // ====================================================
        // آمار عملکرد
        // ====================================================

        Row(
            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.spacedBy(5.dp)
        ) {

            PerformanceMiniStat(
                modifier =
                    Modifier.weight(1f),

                title =
                    "میانگین",

                value =
                    if (average > 0f) {
                        "${average.roundToInt()}"
                    } else {
                        "—"
                    },

                accent =
                    accent
            )

            PerformanceMiniStat(
                modifier =
                    Modifier.weight(1f),

                title =
                    "بهترین",

                value =
                    if (best > 0f) {
                        "${best.roundToInt()}"
                    } else {
                        "—"
                    },

                accent =
                    accent
            )

            PerformanceMiniStat(
                modifier =
                    Modifier.weight(1f),

                title =
                    "امتیاز هفته",

                value =
                    if (total > 0f) {
                        "${total.roundToInt()}"
                    } else {
                        "—"
                    },

                accent =
                    accent
            )
        }

        Spacer(
            modifier =
                Modifier.height(10.dp)
        )

        // ====================================================
        // نمودار
        // ====================================================

        PerformanceBars(
            values =
                values,

            days =
                days,

            maximum =
                maximum,

            accent =
                accent,

            height =
                125.dp
        )
    }
}

// ============================================================
// PERFORMANCE BARS
// ============================================================

@Composable
private fun PerformanceBars(
    values: List<Float>,
    days: List<String>,
    maximum: Float,
    accent: Color,
    height: androidx.compose.ui.unit.Dp
) {

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(height),

        horizontalArrangement =
            Arrangement.spacedBy(0.dp),

        verticalAlignment =
            Alignment.Bottom
    ) {

        (0 until 7).forEach { index ->

            val value =
                values.getOrNull(index)
                    ?: 0f

            PerformanceBar(
                modifier =
                    Modifier.weight(1f),

                value =
                    value,

                maximum =
                    maximum,

                day =
                    days.getOrNull(index)
                        ?: "",

                accent =
                    accent
            )
        }
    }
}

// ============================================================
// SINGLE PERFORMANCE BAR
// ============================================================

@Composable
private fun PerformanceBar(
    modifier: Modifier,
    value: Float,
    maximum: Float,
    day: String,
    accent: Color
) {

    val target =
        if (maximum > 0f) {
            (value / maximum)
                .coerceIn(0f, 1f)
        } else {
            0f
        }

    val progress by animateFloatAsState(
        targetValue =
            target,

        animationSpec =
            tween(
                durationMillis = 500,
                easing =
                    FastOutSlowInEasing
            ),

        label =
            "performance_bar"
    )

    Column(
        modifier =
            modifier.fillMaxHeight(),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Bottom
    ) {

        if (value > 0f) {

            Text(
                text =
                    value.roundToInt().toString(),

                style =
                    MaterialTheme
                        .typography
                        .labelSmall,

                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )

            Spacer(
                modifier =
                    Modifier.height(2.dp)
            )
        }

        Box(
            modifier =
                Modifier
                    .fillMaxWidth(0.72f)
                    .fillMaxHeight(
                        0.68f * progress
                    )
                    .clip(
                        RoundedCornerShape(
                            topStart = 7.dp,
                            topEnd = 7.dp
                        )
                    )
                    .background(
                        if (value > 0f) {
                            accent.copy(
                                alpha = 0.92f
                            )
                        } else {
                            accent.copy(
                                alpha = 0.08f
                            )
                        }
                    )
        )

        Spacer(
            modifier =
                Modifier.height(4.dp)
        )

        OverallDayLabel(
            day =
                day,

            hasData =
                value > 0f
        )
    }
}

// ============================================================
// PERFORMANCE MINI STAT
// ============================================================

@Composable
private fun PerformanceMiniStat(
    modifier: Modifier,
    title: String,
    value: String,
    accent: Color
) {

    Column(
        modifier =
            modifier
                .clip(
                    RoundedCornerShape(10.dp)
                )
                .background(
                    accent.copy(
                        alpha = 0.07f
                    )
                )
                .padding(
                    horizontal = 7.dp,
                    vertical = 6.dp
                )
    ) {

        Text(
            text =
                title,

            style =
                MaterialTheme
                    .typography
                    .labelSmall,

            color =
                MaterialTheme
                    .colorScheme
                    .onSurfaceVariant,

            maxLines = 1
        )

        Spacer(
            modifier =
                Modifier.height(2.dp)
        )

        Text(
            text =
                value,

            style =
                MaterialTheme
                    .typography
                    .labelSmall,

            maxLines = 1
        )
    }
}