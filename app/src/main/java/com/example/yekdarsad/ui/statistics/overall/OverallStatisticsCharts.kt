package com.example.yekdarsad.ui.statistics.overall

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max

// ============================================================
// ALL OVERALL CHARTS
// ============================================================

@Composable
fun OverallStatisticsCharts(
    weekDays: List<String>,
    activityData: List<Float>,
    calorieData: List<Float>,
    sleepData: List<Float>,
    phoneData: List<Float>,
    leisureData: List<Float>,
    spiritualityData: List<Float>,
    exerciseData: List<Float>,
    scoreData: List<Float>
) {

    Column(
        modifier =
            Modifier.fillMaxWidth(),

        verticalArrangement =
            Arrangement.spacedBy(10.dp)
    ) {

        // ====================================================
        // فعالیت
        // ====================================================

        ActivityOverviewChart(
            values =
                activityData,

            days =
                weekDays
        )

        // ====================================================
        // کالری + خواب
        // ====================================================

        Row(
            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.spacedBy(9.dp)
        ) {

            CompactContinuousChart(
                modifier =
                    Modifier.weight(1f),

                title =
                    "کالری",

                values =
                    calorieData,

                days =
                    weekDays,

                unit =
                    "کالری",

                accent =
                    OverallCaloriesColor,

                formatter =
                    ::formatNumber
            )

            CompactContinuousChart(
                modifier =
                    Modifier.weight(1f),

                title =
                    "خواب",

                values =
                    sleepData,

                days =
                    weekDays,

                unit =
                    "زمان",

                accent =
                    OverallSleepColor,

                formatter =
                    ::formatHours
            )
        }

        // ====================================================
        // موبایل + تفریح
        // ====================================================

        Row(
            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.spacedBy(9.dp)
        ) {

            CompactContinuousChart(
                modifier =
                    Modifier.weight(1f),

                title =
                    "موبایل",

                values =
                    phoneData,

                days =
                    weekDays,

                unit =
                    "زمان",

                accent =
                    OverallPhoneColor,

                formatter =
                    ::formatHours
            )

            CompactBarChart(
                modifier =
                    Modifier.weight(1f),

                title =
                    "تفریح",

                values =
                    leisureData,

                days =
                    weekDays,

                unit =
                    "زمان",

                accent =
                    OverallLeisureColor,

                formatter =
                    ::formatHours
            )
        }

        // ====================================================
        // معنویت + ورزش
        // ====================================================

        Row(
            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.spacedBy(9.dp)
        ) {

            CompactBarChart(
                modifier =
                    Modifier.weight(1f),

                title =
                    "معنویت",

                values =
                    spiritualityData,

                days =
                    weekDays,

                unit =
                    "زمان",

                accent =
                    OverallSpiritualityColor,

                formatter =
                    ::formatHours
            )

            CompactContinuousChart(
                modifier =
                    Modifier.weight(1f),

                title =
                    "ورزش",

                values =
                    exerciseData,

                days =
                    weekDays,

                unit =
                    "زمان",

                accent =
                    OverallExerciseColor,

                formatter =
                    ::formatHours
            )
        }

        // ====================================================
        // امتیاز
        // ====================================================

        CompactContinuousChart(
            modifier =
                Modifier.fillMaxWidth(),

            title =
                "امتیاز روزانه",

            values =
                scoreData,

            days =
                weekDays,

            unit =
                "امتیاز",

            accent =
                OverallScoreColor,

            formatter =
                ::formatNumber
        )
    }
}

// ============================================================
// ACTIVITY
// ============================================================

@Composable
private fun ActivityOverviewChart(
    values: List<Float>,
    days: List<String>
) {

    OverallChartCard(
        title =
            "فعالیت",

        accent =
            OverallActivityColor
    ) {

        OverallChartStats(
            values =
                values,

            unit =
                "زمان",

            accent =
                OverallActivityColor,

            formatter =
                ::formatHours
        )

        Spacer(
            modifier =
                Modifier.height(9.dp)
        )

        DenseActivityBars(
            values =
                values,

            days =
                days,

            accent =
                OverallActivityColor,

            height =
                120.dp
        )
    }
}

// ============================================================
// COMPACT BAR
// ============================================================

@Composable
private fun CompactBarChart(
    modifier: Modifier,
    title: String,
    values: List<Float>,
    days: List<String>,
    unit: String,
    accent: Color,
    formatter: (Float) -> String
) {

    OverallChartCard(
        modifier =
            modifier,

        title =
            title,

        accent =
            accent
    ) {

        OverallChartStats(
            values =
                values,

            unit =
                unit,

            accent =
                accent,

            formatter =
                formatter
        )

        Spacer(
            modifier =
                Modifier.height(6.dp)
        )

        DenseActivityBars(
            values =
                values,

            days =
                days,

            accent =
                accent,

            height =
                82.dp,

            formatter =
                formatter
        )
    }
}

// ============================================================
// DENSE BARS
// ============================================================

@Composable
private fun DenseActivityBars(
    values: List<Float>,
    days: List<String>,
    accent: Color,
    height: Dp,
    formatter: (Float) -> String = ::formatNumber
) {

    val maximum =
        max(
            values.maxOrNull() ?: 0f,
            1f
        )

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

        values.forEachIndexed { index, value ->

            DenseActivityBar(
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
                    accent,

                formatter =
                    formatter
            )
        }
    }
}

// ============================================================
// SINGLE BAR
// ============================================================

@Composable
private fun DenseActivityBar(
    modifier: Modifier,
    value: Float,
    maximum: Float,
    day: String,
    accent: Color,
    formatter: (Float) -> String
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
            "overall_bar"
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

            androidx.compose.material3.Text(
                text =
                    formatter(value),

                style =
                    androidx.compose.material3.MaterialTheme
                        .typography
                        .labelSmall,

                color =
                    androidx.compose.material3.MaterialTheme
                        .colorScheme
                        .onSurfaceVariant,

                maxLines =
                    1
            )

            Spacer(
                modifier =
                    Modifier.height(2.dp)
            )
        }

        Box(
            modifier =
                Modifier
                    .fillMaxWidth(0.82f)
                    .fillMaxHeight(
                        0.68f * progress
                    )
                    .clip(
                        RoundedCornerShape(
                            topStart = 6.dp,
                            topEnd = 6.dp
                        )
                    )
                    .background(
                        if (value > 0f) {
                            accent.copy(alpha = 0.92f)
                        } else {
                            accent.copy(alpha = 0.08f)
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
// CONTINUOUS LINE CHART
// ============================================================

@Composable
private fun CompactContinuousChart(
    modifier: Modifier,
    title: String,
    values: List<Float>,
    days: List<String>,
    unit: String,
    accent: Color,
    formatter: (Float) -> String
) {

    OverallChartCard(
        modifier =
            modifier,

        title =
            title,

        accent =
            accent
    ) {

        OverallChartStats(
            values =
                values,

            unit =
                unit,

            accent =
                accent,

            formatter =
                formatter
        )

        Spacer(
            modifier =
                Modifier.height(5.dp)
        )

        ContinuousLineChart(
            values =
                values,

            days =
                days,

            accent =
                accent,

            height =
                82.dp,

            formatter =
                formatter
        )
    }
}

// ============================================================
// CONTINUOUS LINE
// ============================================================

@Composable
private fun ContinuousLineChart(
    values: List<Float>,
    days: List<String>,
    accent: Color,
    height: Dp,
    formatter: (Float) -> String
) {

    val maximum =
        max(
            values.maxOrNull() ?: 0f,
            1f
        )

    Column(
        modifier =
            Modifier.fillMaxWidth()
    ) {

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(height)
        ) {

            // ------------------------------------------------
            // Grid
            // ------------------------------------------------

            Canvas(
                modifier =
                    Modifier.fillMaxSize()
            ) {

                val first =
                    size.height * 0.25f

                val second =
                    size.height * 0.50f

                val third =
                    size.height * 0.75f

                drawLine(
                    color =
                        OverallGridColor,

                    start =
                        Offset(
                            0f,
                            first
                        ),

                    end =
                        Offset(
                            size.width,
                            first
                        ),

                    strokeWidth =
                        1f
                )

                drawLine(
                    color =
                        OverallGridColor,

                    start =
                        Offset(
                            0f,
                            second
                        ),

                    end =
                        Offset(
                            size.width,
                            second
                        ),

                    strokeWidth =
                        1f
                )

                drawLine(
                    color =
                        OverallGridColor,

                    start =
                        Offset(
                            0f,
                            third
                        ),

                    end =
                        Offset(
                            size.width,
                            third
                        ),

                    strokeWidth =
                        1f
                )
            }

            // ------------------------------------------------
            // Line
            // ------------------------------------------------

            Canvas(
                modifier =
                    Modifier.fillMaxSize()
            ) {

                if (values.isEmpty()) {
                    return@Canvas
                }

                val horizontalPadding =
                    5.dp.toPx()

                val usableWidth =
                    size.width -
                            horizontalPadding * 2f

                val usableHeight =
                    size.height -
                            10.dp.toPx()

                val points =
                    values.mapIndexed { index, value ->

                        val x =
                            if (values.size == 1) {

                                size.width / 2f

                            } else {

                                horizontalPadding +
                                        usableWidth *
                                        index /
                                        (values.size - 1)
                            }

                        val normalized =
                            (
                                    value / maximum
                                    ).coerceIn(
                                    0f,
                                    1f
                                )

                        val y =
                            size.height -
                                    5.dp.toPx() -
                                    normalized *
                                    usableHeight

                        Offset(
                            x,
                            y
                        )
                    }

                if (points.size >= 2) {

                    val path =
                        Path()

                    path.moveTo(
                        points.first().x,
                        points.first().y
                    )

                    for (
                    index in
                    1 until points.size
                    ) {

                        val previous =
                            points[index - 1]

                        val current =
                            points[index]

                        val centerX =
                            (
                                    previous.x +
                                            current.x
                                    ) / 2f

                        path.cubicTo(
                            centerX,
                            previous.y,

                            centerX,
                            current.y,

                            current.x,
                            current.y
                        )
                    }

                    // سایه نرم
                    drawPath(
                        path =
                            path,

                        color =
                            accent.copy(
                                alpha = 0.15f
                            ),

                        style =
                            Stroke(
                                width =
                                    7.dp.toPx(),

                                cap =
                                    StrokeCap.Round,

                                join =
                                    StrokeJoin.Round
                            )
                    )

                    // خط اصلی
                    drawPath(
                        path =
                            path,

                        color =
                            accent,

                        style =
                            Stroke(
                                width =
                                    2.4.dp.toPx(),

                                cap =
                                    StrokeCap.Round,

                                join =
                                    StrokeJoin.Round
                            )
                    )
                }

                // ------------------------------------------------
                // نقاط
                // ------------------------------------------------

                points.forEachIndexed { index, point ->

                    val value =
                        values[index]

                    if (value > 0f) {

                        drawCircle(
                            color =
                                Color.White,

                            radius =
                                4.8.dp.toPx(),

                            center =
                                point
                        )

                        drawCircle(
                            color =
                                accent,

                            radius =
                                3.dp.toPx(),

                            center =
                                point
                        )
                    }
                }
            }
        }

        Spacer(
            modifier =
                Modifier.height(3.dp)
        )

        // ------------------------------------------------
        // روزها
        // ------------------------------------------------

        Row(
            modifier =
                Modifier.fillMaxWidth()
        ) {

            days.forEachIndexed { index, day ->

                val value =
                    values.getOrNull(index)
                        ?: 0f

                Box(
                    modifier =
                        Modifier.weight(1f),

                    contentAlignment =
                        Alignment.Center
                ) {

                    OverallDayLabel(
                        day =
                            day,

                        hasData =
                            value > 0f
                    )
                }
            }
        }

        // ------------------------------------------------
        // تغییر روند
        // ------------------------------------------------

        OverallTrendStats(
            values =
                values,

            formatter =
                formatter,

            accent =
                accent
        )
    }
}

// ============================================================
// TREND STATS
// ============================================================

@Composable
private fun OverallTrendStats(
    values: List<Float>,
    formatter: (Float) -> String,
    accent: Color
) {

    val first =
        values.firstOrNull {
            it > 0f
        } ?: 0f

    val last =
        values.lastOrNull {
            it > 0f
        } ?: 0f

    if (
        first <= 0f ||
        last <= 0f
    ) {
        return
    }

    val change =
        last - first

    Row(
        modifier =
            Modifier.fillMaxWidth(),

        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {

        androidx.compose.material3.Text(
            text =
                "شروع ${formatter(first)}",

            style =
                androidx.compose.material3.MaterialTheme
                    .typography
                    .labelSmall,

            color =
                androidx.compose.material3.MaterialTheme
                    .colorScheme
                    .onSurfaceVariant
        )

        val changeText =
            when {

                change > 0f ->
                    "+${formatter(change)}"

                change < 0f ->
                    formatter(change)

                else ->
                    "ثابت"
            }

        androidx.compose.material3.Text(
            text =
                changeText,

            style =
                androidx.compose.material3.MaterialTheme
                    .typography
                    .labelSmall,

            color =
                if (change == 0f) {
                    androidx.compose.material3.MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
                } else {
                    accent
                }
        )

        androidx.compose.material3.Text(
            text =
                "آخر ${formatter(last)}",

            style =
                androidx.compose.material3.MaterialTheme
                    .typography
                    .labelSmall,

            color =
                androidx.compose.material3.MaterialTheme
                    .colorScheme
                    .onSurfaceVariant
        )
    }
}