package com.example.yekdarsad.ui.overview

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.yekdarsad.data.Category
import com.example.yekdarsad.data.DailyPlanWithTask
import com.example.yekdarsad.data.DatabaseProvider
import com.example.yekdarsad.ui.theme.CardBackground
import com.example.yekdarsad.ui.theme.PrimaryGreen
import com.example.yekdarsad.ui.theme.PrimaryGreenLight
import com.example.yekdarsad.ui.theme.TextSecondary
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun WeeklyOverviewScreen(
    weekOffset: Int = 0,
    onWeekOffsetChange: (Int) -> Unit = {},
    onDateSelected: (LocalDate) -> Unit = {},
    onCategoryTotalsChanged: (List<OverviewCategoryTime>) -> Unit = {}
) {

    val context = LocalContext.current

    val database = remember {
        DatabaseProvider.getDatabase(context)
    }

    val dailyPlanDao = database.dailyPlanDao()
    val categoryDao = database.categoryDao()

    var selectedWeekOffset by remember(weekOffset) {
        mutableStateOf(weekOffset)
    }

    val today = remember {
        LocalDate.now(
            ZoneId.of("Asia/Tehran")
        )
    }

    val categoriesState = remember {
        mutableStateOf<List<Category>>(emptyList())
    }

    LaunchedEffect(Unit) {

        categoriesState.value =
            categoryDao.getAll()
    }

    val categories = categoriesState.value

    val currentWeekStart = remember(today) {

        val daysFromSaturday =
            when (today.dayOfWeek) {

                DayOfWeek.SATURDAY -> 0L
                DayOfWeek.SUNDAY -> 1L
                DayOfWeek.MONDAY -> 2L
                DayOfWeek.TUESDAY -> 3L
                DayOfWeek.WEDNESDAY -> 4L
                DayOfWeek.THURSDAY -> 5L
                DayOfWeek.FRIDAY -> 6L
            }

        today.minusDays(
            daysFromSaturday
        )
    }

    val startOfWeek = remember(
        currentWeekStart,
        selectedWeekOffset
    ) {

        currentWeekStart.plusWeeks(
            selectedWeekOffset.toLong()
        )
    }

    val weekDates = remember(startOfWeek) {

        (0L..6L).map { offset ->
            startOfWeek.plusDays(offset)
        }
    }

    val dayNames = remember {

        listOf(
            "شنبه",
            "یکشنبه",
            "دوشنبه",
            "سه‌شنبه",
            "چهارشنبه",
            "پنجشنبه",
            "جمعه"
        )
    }

    val plansByDate =
        remember {

            mutableStateMapOf<
                    String,
                    List<DailyPlanWithTask>
                    >()
        }

    LaunchedEffect(startOfWeek) {

        plansByDate.clear()

        weekDates.forEach { date ->

            val dateKey =
                date.toString()

            launch {

                dailyPlanDao
                    .getByDate(dateKey)
                    .collect { plans ->

                        plansByDate[dateKey] =
                            plans
                    }
            }
        }
    }

    LaunchedEffect(
        plansByDate.toMap(),
        categories
    ) {

        onCategoryTotalsChanged(
            calculateCategoryTimes(
                plansByDate = plansByDate,
                categories = categories
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color.White
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    horizontal = 8.dp,
                    vertical = 6.dp
                )
        ) {

            Column(
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
                                .copy(
                                    alpha = 0.12f
                                ),
                        shape =
                            RoundedCornerShape(13.dp)
                    )
                    .padding(
                        horizontal = 5.dp,
                        vertical = 5.dp
                    )
            ) {

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    IconButton(
                        onClick = {

                            selectedWeekOffset--

                            onWeekOffsetChange(
                                selectedWeekOffset
                            )
                        },
                        modifier = Modifier
                            .width(34.dp)
                            .height(34.dp)
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.ArrowBack,
                            contentDescription =
                                "هفته قبل",
                            tint =
                                TextSecondary
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
                                weeklyTitle(
                                    selectedWeekOffset
                                ),
                            fontSize =
                                14.sp,
                            fontWeight =
                                FontWeight.Bold,
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurface
                        )

                        Spacer(
                            modifier =
                                Modifier.height(2.dp)
                        )

                        Text(
                            text =
                                "${
                                    jalaliDateWeekly(
                                        startOfWeek
                                    )
                                } تا ${
                                    jalaliDateWeekly(
                                        weekDates.last()
                                    )
                                }",
                            fontSize =
                                9.sp,
                            color =
                                TextSecondary
                        )
                    }

                    IconButton(
                        onClick = {

                            selectedWeekOffset++

                            onWeekOffsetChange(
                                selectedWeekOffset
                            )
                        },
                        modifier = Modifier
                            .width(34.dp)
                            .height(34.dp)
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.ArrowForward,
                            contentDescription =
                                "هفته بعد",
                            tint =
                                TextSecondary
                        )
                    }
                }

                if (selectedWeekOffset != 0) {

                    Spacer(
                        modifier =
                            Modifier.height(3.dp)
                    )

                    Box(
                        modifier = Modifier
                            .align(
                                Alignment.CenterHorizontally
                            )
                            .clip(
                                RoundedCornerShape(50.dp)
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
                                    RoundedCornerShape(50.dp)
                            )
                            .clickable {

                                selectedWeekOffset = 0

                                onWeekOffsetChange(0)
                            }
                            .padding(
                                horizontal = 12.dp,
                                vertical = 4.dp
                            )
                    ) {

                        Text(
                            text =
                                "برو به هفته جاری",
                            fontSize =
                                8.sp,
                            fontWeight =
                                FontWeight.Medium,
                            color =
                                PrimaryGreen
                        )
                    }
                }
            }

            Spacer(
                modifier =
                    Modifier.height(7.dp)
            )

            WeeklyTableHeader()

            Column(
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                weekDates.forEachIndexed {
                        index,
                        date ->

                    val visiblePlans =
                        plansByDate[
                            date.toString()
                        ]
                            .orEmpty()
                            .filter {
                                it.task.coefficient != 0.0
                            }

                    WeekDayRowWeekly(
                        dayName =
                            dayNames[index],
                        date =
                            date,
                        plans =
                            visiblePlans,
                        isToday =
                            date == today,
                        isLast =
                            index ==
                                    weekDates.lastIndex,
                        categories =
                            categories,
                        onDateSelected =
                            onDateSelected
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )
        }
    }
}