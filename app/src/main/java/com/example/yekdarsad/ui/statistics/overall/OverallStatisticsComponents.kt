package com.example.yekdarsad.ui.statistics.overall

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.yekdarsad.ui.theme.PrimaryGreen
import com.example.yekdarsad.ui.theme.TextSecondary
import kotlin.math.roundToInt

// ============================================================
// COLORS
// ============================================================

internal val OverallActivityColor =
    PrimaryGreen

internal val OverallCaloriesColor =
    Color(0xFFFF8A3D)

internal val OverallSleepColor =
    Color(0xFF8E7CFF)

internal val OverallPhoneColor =
    Color(0xFF42A5F5)

internal val OverallLeisureColor =
    Color(0xFFFFC107)

internal val OverallSpiritualityColor =
    Color(0xFF43A047)

internal val OverallExerciseColor =
    Color(0xFF26A69A)

internal val OverallScoreColor =
    Color(0xFFE94E77)

internal val OverallMissingColor =
    Color(0xFFEF5350)

internal val OverallGridColor =
    Color(0xFF9E9E9E).copy(
        alpha = 0.10f
    )

// ============================================================
// CARD BORDER
// ============================================================

private val OverallCardBorderColor =
    Color(0xFFE8E8E8)

// ============================================================
// SUMMARY
// ============================================================

@Composable
fun OverallStatisticsSummary(
    totalActivityHours: Float
) {

    val average =
        if (totalActivityHours > 0f) {
            totalActivityHours / 7f
        } else {
            0f
        }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(9.dp)
        ) {

            OverallSummaryCard(
                modifier =
                    Modifier.weight(1f),

                title =
                    "فعالیت هفته",

                value =
                    formatHours(totalActivityHours),

                accent =
                    OverallActivityColor
            )

            OverallSummaryCard(
                modifier =
                    Modifier.weight(1f),

                title =
                    "میانگین روزانه",

                value =
                    formatHours(average),

                accent =
                    OverallActivityColor
            )
        }
    }
}

// ============================================================
// SUMMARY CARD
// ============================================================

@Composable
private fun OverallSummaryCard(
    modifier: Modifier,
    title: String,
    value: String,
    accent: Color
) {

    Column(
        modifier = modifier
            .clip(
                RoundedCornerShape(17.dp)
            )
            .background(
                Color.White
            )
            .border(
                width = 1.dp,
                color = OverallCardBorderColor,
                shape = RoundedCornerShape(17.dp)
            )
            .padding(
                horizontal = 13.dp,
                vertical = 12.dp
            )
    ) {

        Row(
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(accent)
            )

            Spacer(
                modifier = Modifier.width(6.dp)
            )

            Text(
                text = title,
                style =
                    MaterialTheme.typography.labelMedium,
                color =
                    TextSecondary
            )
        }

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = value,
            style =
                MaterialTheme.typography.titleMedium
        )
    }
}

// ============================================================
// ADDITIONAL STATS
// ============================================================

@Composable
fun OverallAdditionalStats(
    averageSleepHours: Float,
    weeklyScore: Float,
    averageCalories: Float,
    totalCalories: Float,
    activityValues: List<Float>,
    days: List<String>
) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement =
            Arrangement.spacedBy(9.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(9.dp)
        ) {

            OverallStatCard(
                modifier =
                    Modifier.weight(1f),

                title =
                    "میانگین خواب",

                value =
                    if (averageSleepHours > 0f) {
                        formatHours(
                            averageSleepHours
                        )
                    } else {
                        "—"
                    }
            )

            OverallStatCard(
                modifier =
                    Modifier.weight(1f),

                title =
                    "امتیاز هفته",

                value =
                    if (weeklyScore > 0f) {
                        formatNumber(
                            weeklyScore
                        )
                    } else {
                        "—"
                    }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(9.dp)
        ) {

            OverallStatCard(
                modifier =
                    Modifier.weight(1f),

                title =
                    "میانگین کالری",

                value =
                    if (averageCalories > 0f) {
                        "${formatNumber(averageCalories)} کالری"
                    } else {
                        "—"
                    }
            )

            OverallStatCard(
                modifier =
                    Modifier.weight(1f),

                title =
                    "کالری هفته",

                value =
                    if (totalCalories > 0f) {
                        "${formatNumber(totalCalories)} کالری"
                    } else {
                        "—"
                    }
            )
        }

        OverallStatCard(
            modifier =
                Modifier.fillMaxWidth(),

            title =
                "بهترین روز فعالیت",

            value =
                findBestDay(
                    values =
                        activityValues,

                    days =
                        days
                )
        )
    }
}

// ============================================================
// SIMPLE STAT CARD
// ============================================================

@Composable
fun OverallStatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .clip(
                RoundedCornerShape(16.dp)
            )
            .background(
                Color.White
            )
            .border(
                width = 1.dp,
                color = OverallCardBorderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(
                horizontal = 13.dp,
                vertical = 11.dp
            )
    ) {

        Text(
            text = title,
            style =
                MaterialTheme.typography.labelMedium,
            color =
                TextSecondary
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = value,
            style =
                MaterialTheme.typography.titleMedium
        )
    }
}

// ============================================================
// CHART CARD
// ============================================================

@Composable
internal fun OverallChartCard(
    modifier: Modifier = Modifier,
    title: String,
    accent: Color,
    content: @Composable ColumnScope.() -> Unit
) {

    Column(
        modifier = modifier
            .clip(
                RoundedCornerShape(19.dp)
            )
            .background(
                Color.White
            )
            .border(
                width = 1.dp,
                color = OverallCardBorderColor,
                shape = RoundedCornerShape(19.dp)
            )
            .padding(13.dp)
    ) {

        Row(
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(accent)
            )

            Spacer(
                modifier = Modifier.width(7.dp)
            )

            Text(
                text = title,
                style =
                    MaterialTheme.typography.titleSmall
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        content()
    }
}

// ============================================================
// CHART STATS
// ============================================================

@Composable
internal fun OverallChartStats(
    values: List<Float>,
    unit: String,
    accent: Color,
    formatter: (Float) -> String
) {

    val positiveValues =
        values.filter {
            it > 0f
        }

    val total =
        values.sum()

    val average =
        if (positiveValues.isNotEmpty()) {
            positiveValues.average().toFloat()
        } else {
            0f
        }

    val maximum =
        positiveValues.maxOrNull()
            ?: 0f

    Row(
        modifier =
            Modifier.fillMaxWidth(),

        horizontalArrangement =
            Arrangement.spacedBy(5.dp)
    ) {

        OverallMiniStat(
            modifier =
                Modifier.weight(1f),

            title =
                "مجموع",

            value =
                if (total > 0f) {
                    "${formatter(total)} $unit"
                } else {
                    "—"
                },

            accent =
                accent
        )

        OverallMiniStat(
            modifier =
                Modifier.weight(1f),

            title =
                "میانگین",

            value =
                if (average > 0f) {
                    "${formatter(average)} $unit"
                } else {
                    "—"
                },

            accent =
                accent
        )

        OverallMiniStat(
            modifier =
                Modifier.weight(1f),

            title =
                "بیشترین",

            value =
                if (maximum > 0f) {
                    "${formatter(maximum)} $unit"
                } else {
                    "—"
                },

            accent =
                accent
        )
    }
}

// ============================================================
// MINI STAT
// ============================================================

@Composable
private fun OverallMiniStat(
    modifier: Modifier,
    title: String,
    value: String,
    accent: Color
) {

    Column(
        modifier = modifier
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
            text = title,
            style =
                MaterialTheme.typography.labelSmall,
            color =
                TextSecondary,
            maxLines = 1
        )

        Spacer(
            modifier = Modifier.height(2.dp)
        )

        Text(
            text = value,
            style =
                MaterialTheme.typography.labelSmall,
            maxLines = 1
        )
    }
}

// ============================================================
// DAY LABEL
// ============================================================

@Composable
internal fun OverallDayLabel(
    day: String,
    hasData: Boolean
) {

    if (hasData) {

        Text(
            text = day,
            style =
                MaterialTheme.typography.labelSmall,
            color =
                TextSecondary
        )

        return
    }

    Row(
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(15.dp)
                .clip(CircleShape)
                .background(
                    OverallMissingColor.copy(
                        alpha = 0.14f
                    )
                ),
            contentAlignment =
                Alignment.Center
        ) {

            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(
                        OverallMissingColor
                    )
            )
        }

        Spacer(
            modifier = Modifier.width(3.dp)
        )

        Text(
            text = day,
            style =
                MaterialTheme.typography.labelSmall,
            color =
                OverallMissingColor
        )
    }
}

// ============================================================
// BEST DAY
// ============================================================

private fun findBestDay(
    values: List<Float>,
    days: List<String>
): String {

    if (values.isEmpty()) {
        return "—"
    }

    val index =
        values.indices.maxByOrNull {
            values[it]
        } ?: return "—"

    val value =
        values[index]

    if (value <= 0f) {
        return "—"
    }

    val day =
        days.getOrNull(index)
            ?: "—"

    return "$day • ${formatHours(value)}"
}

// ============================================================
// FORMAT NUMBER
// ============================================================

internal fun formatNumber(
    value: Float
): String {

    if (value <= 0f) {
        return "—"
    }

    return if (
        value == value.toInt().toFloat()
    ) {

        value.toInt().toString()

    } else {

        String.format(
            "%.1f",
            value
        )
    }
}

// ============================================================
// FORMAT HOURS
// ============================================================

internal fun formatHours(
    hours: Float
): String {

    if (hours <= 0f) {
        return "—"
    }

    val totalMinutes =
        (hours * 60f)
            .roundToInt()

    val wholeHours =
        totalMinutes / 60

    val minutes =
        totalMinutes % 60

    return when {

        wholeHours == 0 ->
            "$minutes دقیقه"

        minutes == 0 ->
            "$wholeHours ساعت"

        else ->
            "$wholeHours ساعت و $minutes دقیقه"
    }
}