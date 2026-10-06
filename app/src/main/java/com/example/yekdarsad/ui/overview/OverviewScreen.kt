package com.example.yekdarsad.ui.overview

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.yekdarsad.data.DatabaseProvider
import com.example.yekdarsad.data.OverviewNote
import com.example.yekdarsad.ui.theme.CardBackground
import com.example.yekdarsad.ui.theme.PrimaryGreen
import com.example.yekdarsad.ui.theme.PrimaryGreenLight
import com.example.yekdarsad.ui.theme.TextSecondary
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

// ============================================================
// OVERVIEW SCREEN
// ============================================================

@Composable
fun OverviewScreen(

    weekOffset: Int = 0,

    showCategorySummary: Boolean = false,

    onCategorySummaryClose: () -> Unit = {},

    onDateSelected: (LocalDate) -> Unit = {},

    overviewMode: OverviewMode = OverviewMode.WEEKLY,

    onModeChange: (OverviewMode) -> Unit = {},

    onWeekOffsetChange: (Int) -> Unit = {}

) {

    // ========================================================
    // DATABASE
    // ========================================================

    val context =
        LocalContext.current

    val database =
        remember {
            DatabaseProvider.getDatabase(context)
        }

    val overviewNoteDao =
        database.overviewNoteDao()

    // ========================================================
    // COROUTINE SCOPE
    // ========================================================

    val scope =
        rememberCoroutineScope()

    // ========================================================
    // SUMMARY
    // ========================================================

    var summaryVisible by rememberSaveable {

        mutableStateOf(
            showCategorySummary
        )
    }

    var summaryTotals by remember {

        mutableStateOf<List<OverviewCategoryTime>>(
            emptyList()
        )
    }

    LaunchedEffect(
        showCategorySummary
    ) {

        summaryVisible =
            showCategorySummary
    }

    LaunchedEffect(
        overviewMode
    ) {

        summaryVisible =
            false

        summaryTotals =
            emptyList()
    }

    // ========================================================
    // NOTE
    // ========================================================

    var noteVisible by rememberSaveable {

        mutableStateOf(false)
    }

    var noteText by remember {

        mutableStateOf("")
    }

    var noteLoading by remember {

        mutableStateOf(false)
    }

    // ========================================================
    // NOTE KEY
    // ========================================================

    var noteKey by remember {

        mutableStateOf("")
    }

    // ========================================================
    // NOTE SAVE JOB
    // ========================================================

    var noteSaveJob by remember {

        mutableStateOf<Job?>(null)
    }

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

    // ========================================================
    // DISPLAYED MONTH
    // ========================================================

    var displayedMonthlyYear by remember {

        mutableStateOf<Int?>(null)
    }

    var displayedMonthlyMonth by remember {

        mutableStateOf<Int?>(null)
    }

    // ========================================================
    // WEEKLY NOTE KEY
    // ========================================================

    val weeklyNoteKey =
        remember(
            weekOffset
        ) {

            val saturday =
                getSaturdayOfWeek(
                    today
                ).plusDays(
                    weekOffset * 7L
                )

            val jalali =
                gregorianToJalali(
                    saturday.year,
                    saturday.monthValue,
                    saturday.dayOfMonth
                )

            "WEEKLY_${jalali[0]}_${jalali[1]}_${jalali[2]}"
        }

    // ========================================================
    // CURRENT NOTE KEY
    // ========================================================

    LaunchedEffect(
        overviewMode,
        weeklyNoteKey,
        displayedMonthlyYear,
        displayedMonthlyMonth
    ) {

        val newKey =
            if (
                overviewMode ==
                OverviewMode.WEEKLY
            ) {

                weeklyNoteKey

            } else {

                val year =
                    displayedMonthlyYear
                        ?: todayJalali[0]

                val month =
                    displayedMonthlyMonth
                        ?: todayJalali[1]

                "MONTHLY_${year}_${month}"
            }

        if (
            newKey != noteKey
        ) {

            noteSaveJob?.cancel()

            noteKey =
                newKey

            noteText =
                ""

            noteLoading =
                true
        }
    }

    // ========================================================
    // NOTE TITLE
    // ========================================================

    val noteTitle =
        if (
            overviewMode ==
            OverviewMode.WEEKLY
        ) {

            "یادداشت هفتگی"

        } else {

            "یادداشت ماهانه"
        }

    // ========================================================
    // LOAD NOTE
    // ========================================================

    LaunchedEffect(
        noteKey,
        noteVisible
    ) {

        if (
            noteKey.isBlank()
        ) {

            noteText =
                ""

            noteLoading =
                false

            return@LaunchedEffect
        }

        noteLoading =
            true

        try {

            val note =
                overviewNoteDao
                    .getNote(
                        noteKey
                    )
                    .first()

            noteText =
                note?.text.orEmpty()

        } catch (
            _: Exception
        ) {

            noteText =
                ""

        } finally {

            noteLoading =
                false
        }
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

    ) {

        Column(

            modifier =
                Modifier.fillMaxSize()

        ) {

            // ==================================================
            // HEADER
            // ==================================================

            Row(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 12.dp,
                            vertical = 5.dp
                        ),

                verticalAlignment =
                    Alignment.CenterVertically

            ) {

                // ==================================================
                // NOTE BUTTON
                // ==================================================

                IconButton(

                    onClick = {

                        noteVisible =
                            true
                    },

                    modifier =
                        Modifier
                            .width(34.dp)
                            .height(34.dp)

                ) {

                    Icon(

                        imageVector =
                            Icons.Default.EditNote,

                        contentDescription =
                            "یادداشت",

                        modifier =
                            Modifier.size(
                                20.dp
                            ),

                        tint =
                            if (
                                noteText.isNotBlank()
                            ) {

                                PrimaryGreen

                            } else {

                                PrimaryGreen.copy(
                                    alpha = 0.70f
                                )
                            }
                    )
                }

                // ==================================================
                // MODE SWITCH
                // ==================================================

                Box(

                    modifier =
                        Modifier.weight(
                            1f
                        ),

                    contentAlignment =
                        Alignment.Center

                ) {

                    Row(

                        modifier =
                            Modifier
                                .clip(
                                    RoundedCornerShape(
                                        50.dp
                                    )
                                )
                                .background(
                                    PrimaryGreenLight.copy(
                                        alpha = 0.35f
                                    )
                                )
                                .padding(
                                    2.dp
                                ),

                        verticalAlignment =
                            Alignment.CenterVertically

                    ) {

                        // ==========================================
                        // WEEKLY
                        // ==========================================

                        OverviewModeItem(

                            text =
                                "هفتگی",

                            icon =
                                Icons.Default.PieChart,

                            selected =
                                overviewMode ==
                                        OverviewMode.WEEKLY,

                            onClick = {

                                noteVisible =
                                    false

                                noteSaveJob?.cancel()

                                onModeChange(
                                    OverviewMode.WEEKLY
                                )
                            }
                        )

                        // ==========================================
                        // MONTHLY
                        // ==========================================

                        OverviewModeItem(

                            text =
                                "ماهانه",

                            icon =
                                Icons.Default.CalendarMonth,

                            selected =
                                overviewMode ==
                                        OverviewMode.MONTHLY,

                            onClick = {

                                noteVisible =
                                    false

                                noteSaveJob?.cancel()

                                onModeChange(
                                    OverviewMode.MONTHLY
                                )
                            }
                        )
                    }
                }

                // ==================================================
                // SUMMARY
                // ==================================================

                IconButton(

                    onClick = {

                        summaryVisible =
                            !summaryVisible
                    },

                    modifier =
                        Modifier
                            .width(34.dp)
                            .height(34.dp)

                ) {

                    Icon(

                        imageVector =
                            Icons.Default.Summarize,

                        contentDescription =
                            "خلاصه",

                        modifier =
                            Modifier.size(
                                19.dp
                            ),

                        tint =
                            if (
                                summaryVisible
                            ) {

                                PrimaryGreen

                            } else {

                                PrimaryGreen.copy(
                                    alpha = 0.70f
                                )
                            }
                    )
                }
            }

            // ==================================================
            // CONTENT
            // ==================================================

            Box(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)

            ) {

                // ==================================================
                // WEEKLY
                // ==================================================

                if (
                    overviewMode ==
                    OverviewMode.WEEKLY
                ) {

                    WeeklyOverviewScreen(

                        weekOffset =
                            weekOffset,

                        onWeekOffsetChange = {
                                offset ->

                            onWeekOffsetChange(
                                offset
                            )
                        },

                        onDateSelected = {
                                date ->

                            onDateSelected(
                                date
                            )
                        },

                        onCategoryTotalsChanged = {
                                totals ->

                            summaryTotals =
                                totals
                        }
                    )

                } else {

                    // ==================================================
                    // MONTHLY
                    // ==================================================

                    MonthlyOverviewScreen(

                        onCategoryTotalsChanged = {
                                totals ->

                            summaryTotals =
                                totals
                        },

                        onDateSelected = {
                                date ->

                            onDateSelected(
                                date
                            )
                        },

                        onDisplayedMonthChanged = {
                                year,
                                month ->

                            displayedMonthlyYear =
                                year

                            displayedMonthlyMonth =
                                month
                        }
                    )
                }

                // ==================================================
                // SUMMARY POPUP
                // ==================================================

                if (
                    summaryVisible
                ) {

                    Popup(

                        alignment =
                            Alignment.TopCenter,

                        onDismissRequest = {

                            summaryVisible =
                                false

                            onCategorySummaryClose()
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

                        OverviewSummaryPopup(

                            title =
                                if (
                                    overviewMode ==
                                    OverviewMode.WEEKLY
                                ) {

                                    "خلاصه هفته"

                                } else {

                                    "خلاصه ماه"
                                },

                            totals =
                                summaryTotals,

                            onClose = {

                                summaryVisible =
                                    false

                                onCategorySummaryClose()
                            }
                        )
                    }
                }

                // ==================================================
                // NOTE POPUP
                // ==================================================

                if (
                    noteVisible
                ) {

                    OverviewNoteEditorPopup(

                        title =
                            noteTitle,

                        initialText =
                            noteText,

                        loading =
                            noteLoading,

                        // ------------------------------------------
                        // CLOSE
                        // ------------------------------------------

                        onDismiss = {

                            noteSaveJob?.cancel()

                            noteVisible =
                                false
                        },

                        // ------------------------------------------
                        // TEXT CHANGE
                        // ------------------------------------------

                        onTextChanged = {
                                newText ->

                            noteText =
                                newText

                            if (
                                noteKey.isBlank()
                            ) {

                                return@OverviewNoteEditorPopup
                            }

                            noteSaveJob?.cancel()

                            val keyAtTypingTime =
                                noteKey

                            noteSaveJob =
                                scope.launch {

                                    delay(500)

                                    if (
                                        noteKey !=
                                        keyAtTypingTime
                                    ) {

                                        return@launch
                                    }

                                    val cleanText =
                                        newText.trim()

                                    if (
                                        cleanText.isBlank()
                                    ) {

                                        overviewNoteDao
                                            .deleteNote(
                                                keyAtTypingTime
                                            )

                                    } else {

                                        overviewNoteDao
                                            .saveNote(

                                                OverviewNote(

                                                    periodKey =
                                                        keyAtTypingTime,

                                                    text =
                                                        cleanText
                                                )
                                            )
                                    }
                                }
                        }
                    )
                }
            }
        }
    }
}

// ============================================================
// NOTE EDITOR POPUP
// ============================================================

@Composable
private fun OverviewNoteEditorPopup(

    title: String,

    initialText: String,

    loading: Boolean,

    onDismiss: () -> Unit,

    onTextChanged: (String) -> Unit

) {

    var text by remember(
        initialText
    ) {

        mutableStateOf(
            initialText
        )
    }

    val keyboardController =
        LocalSoftwareKeyboardController.current

    LaunchedEffect(
        initialText
    ) {

        text =
            initialText
    }

    Box(

        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Color.Black.copy(
                        alpha = 0.10f
                    )
                )
                .clickable(

                    indication = null,

                    interactionSource =
                        remember {
                            MutableInteractionSource()
                        }

                ) {

                    keyboardController?.hide()

                    onDismiss()
                }

    ) {

        Box(

            modifier =
                Modifier
                    .align(
                        Alignment.Center
                    )
                    .fillMaxWidth(
                        0.88f
                    )
                    .clip(
                        RoundedCornerShape(
                            22.dp
                        )
                    )
                    .background(
                        CardBackground.copy(
                            alpha = 0.98f
                        )
                    )
                    .border(
                        width = 0.8.dp,

                        color =
                            PrimaryGreen.copy(
                                alpha = 0.20f
                            ),

                        shape =
                            RoundedCornerShape(
                                22.dp
                            )
                    )
                    .clickable(

                        indication = null,

                        interactionSource =
                            remember {
                                MutableInteractionSource()
                            }

                    ) {
                        // جلوگیری از بسته شدن Popup
                    }
                    .padding(
                        16.dp
                    )

        ) {

            CompositionLocalProvider(

                LocalLayoutDirection provides
                        LayoutDirection.Rtl

            ) {

                Column {

                    Row(

                        modifier =
                            Modifier.fillMaxWidth(),

                        verticalAlignment =
                            Alignment.CenterVertically

                    ) {

                        Icon(

                            imageVector =
                                Icons.Default.EditNote,

                            contentDescription =
                                null,

                            modifier =
                                Modifier.size(
                                    23.dp
                                ),

                            tint =
                                PrimaryGreen
                        )

                        Spacer(
                            modifier =
                                Modifier.width(
                                    7.dp
                                )
                        )

                        Text(

                            text =
                                title,

                            modifier =
                                Modifier.weight(
                                    1f
                                ),

                            fontSize =
                                15.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                Color.Black,

                            textAlign =
                                TextAlign.Right
                        )

                        IconButton(

                            onClick = {

                                keyboardController?.hide()

                                onDismiss()
                            },

                            modifier =
                                Modifier.size(
                                    32.dp
                                )

                        ) {

                            Icon(

                                imageVector =
                                    Icons.Default.Close,

                                contentDescription =
                                    "بستن",

                                modifier =
                                    Modifier.size(
                                        19.dp
                                    ),

                                tint =
                                    TextSecondary
                            )
                        }
                    }

                    Spacer(
                        modifier =
                            Modifier.height(
                                12.dp
                            )
                    )

                    Box(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(
                                    190.dp
                                )
                                .clip(
                                    RoundedCornerShape(
                                        15.dp
                                    )
                                )
                                .background(
                                    Color.White.copy(
                                        alpha = 0.90f
                                    )
                                )
                                .border(
                                    width = 0.7.dp,

                                    color =
                                        PrimaryGreen.copy(
                                            alpha = 0.13f
                                        ),

                                    shape =
                                        RoundedCornerShape(
                                            15.dp
                                        )
                                )
                                .padding(
                                    12.dp
                                )

                    ) {

                        if (
                            loading
                        ) {

                            Text(

                                text =
                                    "در حال بارگذاری...",

                                modifier =
                                    Modifier.fillMaxWidth(),

                                fontSize =
                                    11.sp,

                                color =
                                    TextSecondary,

                                textAlign =
                                    TextAlign.Right
                            )

                        } else {

                            BasicTextField(

                                value =
                                    text,

                                onValueChange = {
                                        newText ->

                                    text =
                                        newText

                                    onTextChanged(
                                        newText
                                    )
                                },

                                modifier =
                                    Modifier.fillMaxSize(),

                                textStyle =
                                    TextStyle(

                                        fontSize =
                                            12.sp,

                                        color =
                                            Color.Black,

                                        lineHeight =
                                            21.sp,

                                        textAlign =
                                            TextAlign.Right
                                    ),

                                cursorBrush =
                                    SolidColor(
                                        PrimaryGreen
                                    ),

                                decorationBox = {
                                        innerTextField ->

                                    innerTextField()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ============================================================
// SATURDAY
// ============================================================

private fun getSaturdayOfWeek(
    date: LocalDate
): LocalDate {

    val daysFromSaturday =
        (date.dayOfWeek.value + 1) % 7

    return date.minusDays(
        daysFromSaturday.toLong()
    )
}

// ============================================================
// MODE ITEM
// ============================================================

@Composable
private fun OverviewModeItem(

    text: String,

    icon: androidx.compose.ui.graphics.vector.ImageVector,

    selected: Boolean,

    onClick: () -> Unit

) {

    Row(

        modifier =
            Modifier
                .clip(
                    RoundedCornerShape(
                        50.dp
                    )
                )
                .background(

                    if (
                        selected
                    ) {

                        PrimaryGreen

                    } else {

                        Color.Transparent
                    }
                )
                .clickable {

                    onClick()
                }
                .padding(

                    horizontal = 10.dp,

                    vertical = 5.dp
                ),

        verticalAlignment =
            Alignment.CenterVertically

    ) {

        Icon(

            imageVector =
                icon,

            contentDescription =
                null,

            modifier =
                Modifier.size(
                    14.dp
                ),

            tint =
                if (
                    selected
                ) {

                    Color.White

                } else {

                    PrimaryGreen
                }
        )

        Spacer(
            modifier =
                Modifier.width(
                    4.dp
                )
        )

        Text(

            text =
                text,

            fontSize =
                9.sp,

            fontWeight =
                FontWeight.Medium,

            color =
                if (
                    selected
                ) {

                    Color.White

                } else {

                    PrimaryGreen
                }
        )
    }
}