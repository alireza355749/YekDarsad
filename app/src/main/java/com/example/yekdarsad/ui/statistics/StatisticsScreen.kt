package com.example.yekdarsad.ui.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.yekdarsad.ui.statistics.categories.CategoryStatisticsDetailScreen
import com.example.yekdarsad.ui.statistics.categories.CategoryStatisticsScreen
import com.example.yekdarsad.ui.statistics.overall.OverallStatisticsScreen
import com.example.yekdarsad.ui.theme.CardBackground
import com.example.yekdarsad.ui.theme.PrimaryGreen
import com.example.yekdarsad.viewmodel.StatisticsViewModel

@Composable
fun StatisticsScreen(
    statisticsViewModel: StatisticsViewModel
) {

    var selectedMode by remember {
        mutableStateOf(StatisticsMode.OVERALL)
    }

    var selectedCategoryId by remember {
        mutableStateOf<Int?>(null)
    }

    var selectedCategoryName by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        if (selectedCategoryId == null) {

            StatisticsModeSelector(
                selectedMode = selectedMode,
                onModeSelected = {
                    selectedMode = it
                }
            )
        }

        if (selectedCategoryId != null) {

            CategoryStatisticsDetailScreen(
                categoryId = selectedCategoryId!!,
                categoryName = selectedCategoryName,
                onBack = {
                    selectedCategoryId = null
                    selectedCategoryName = ""
                }
            )

        } else {

            when (selectedMode) {

                StatisticsMode.OVERALL -> {

                    OverallStatisticsScreen(
                        statisticsViewModel = statisticsViewModel
                    )
                }

                StatisticsMode.CATEGORY -> {

                    CategoryStatisticsScreen(
                        onCategorySelected = { id, name ->
                            selectedCategoryId = id
                            selectedCategoryName = name
                        }
                    )
                }
            }
        }
    }
}

// ============================================================
// STATISTICS MODE SELECTOR
// ============================================================

@Composable
private fun StatisticsModeSelector(
    selectedMode: StatisticsMode,
    onModeSelected: (StatisticsMode) -> Unit
) {

    var expanded by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 20.dp,
                vertical = 6.dp
            )
    ) {

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
        ) {

            // =================================================
            // MAIN SELECTOR
            // =================================================

            Row(
                modifier = Modifier
                    .width(215.dp)
                    .height(42.dp)
                    .clip(
                        RoundedCornerShape(14.dp)
                    )
                    .background(
                        PrimaryGreen.copy(
                            alpha = 0.09f
                        )
                    )
                    .clickable {
                        expanded = !expanded
                    }
                    .padding(
                        horizontal = 10.dp
                    ),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                SelectorIcon(
                    mode = selectedMode
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = selectedMode.title,

                    modifier = Modifier.weight(1f),

                    style =
                        MaterialTheme.typography.labelMedium,

                    maxLines = 1
                )

                Spacer(
                    modifier = Modifier.width(5.dp)
                )

                Text(
                    text =
                        if (expanded) "⌃" else "⌄",

                    style =
                        MaterialTheme.typography.titleMedium,

                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant
                )
            }

            // =================================================
            // DROPDOWN
            // =================================================

            DropdownMenu(
                expanded = expanded,

                onDismissRequest = {
                    expanded = false
                },

                modifier = Modifier
                    .width(235.dp)
                    .background(CardBackground)
            ) {

                // ---------------------------------------------
                // آمار کلی
                // ---------------------------------------------

                DropdownMenuItem(
                    text = {

                        Text(
                            text = "آمار کلی",

                            color =
                                if (
                                    selectedMode ==
                                    StatisticsMode.OVERALL
                                ) {
                                    PrimaryGreen
                                } else {
                                    MaterialTheme.colorScheme
                                        .onSurface
                                }
                        )
                    },

                    leadingIcon = {

                        SelectorIcon(
                            mode =
                                StatisticsMode.OVERALL
                        )
                    },

                    onClick = {

                        onModeSelected(
                            StatisticsMode.OVERALL
                        )

                        expanded = false
                    }
                )

                // ---------------------------------------------
                // آمار با دسته‌بندی
                // ---------------------------------------------

                DropdownMenuItem(
                    text = {

                        Text(
                            text = "آمار با دسته‌بندی",

                            color =
                                if (
                                    selectedMode ==
                                    StatisticsMode.CATEGORY
                                ) {
                                    PrimaryGreen
                                } else {
                                    MaterialTheme.colorScheme
                                        .onSurface
                                },

                            maxLines = 1
                        )
                    },

                    leadingIcon = {

                        SelectorIcon(
                            mode =
                                StatisticsMode.CATEGORY
                        )
                    },

                    onClick = {

                        onModeSelected(
                            StatisticsMode.CATEGORY
                        )

                        expanded = false
                    }
                )
            }
        }
    }
}

// ============================================================
// SELECTOR ICON
// ============================================================

@Composable
private fun SelectorIcon(
    mode: StatisticsMode
) {

    val accent =
        PrimaryGreen

    Box(
        modifier = Modifier
            .size(23.dp)
            .clip(CircleShape)
            .background(
                accent.copy(
                    alpha = 0.13f
                )
            ),

        contentAlignment =
            Alignment.Center
    ) {

        when (mode) {

            StatisticsMode.OVERALL -> {

                Row(
                    modifier = Modifier
                        .height(13.dp),
                    horizontalArrangement =
                        Arrangement.spacedBy(2.dp),
                    verticalAlignment =
                        Alignment.Bottom
                ) {

                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(6.dp)
                            .clip(
                                RoundedCornerShape(2.dp)
                            )
                            .background(accent)
                    )

                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(10.dp)
                            .clip(
                                RoundedCornerShape(2.dp)
                            )
                            .background(accent)
                    )

                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(8.dp)
                            .clip(
                                RoundedCornerShape(2.dp)
                            )
                            .background(accent)
                    )
                }
            }

            StatisticsMode.CATEGORY -> {

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(accent)
                    )

                    Row(
                        horizontalArrangement =
                            Arrangement.spacedBy(4.dp)
                    ) {

                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(accent)
                        )

                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(accent)
                        )
                    }
                }
            }
        }
    }
}