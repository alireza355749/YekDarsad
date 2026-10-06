package com.example.yekdarsad.ui.overview

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yekdarsad.data.Category
import com.example.yekdarsad.data.DailyPlanWithTask
import com.example.yekdarsad.ui.theme.PrimaryGreen
import com.example.yekdarsad.ui.theme.PrimaryGreenLight
import com.example.yekdarsad.ui.theme.TextSecondary

data class CategoryTimeSummaryItem(
    val categoryId: Int,
    val categoryName: String,
    val totalMinutes: Int
)

@Composable
fun CategoryTimeSummaryButton(
    plans: List<DailyPlanWithTask>,
    categories: List<Category>,
    modifier: Modifier = Modifier
) {

    var expanded by remember {
        mutableStateOf(false)
    }

    val summary = remember(
        plans,
        categories
    ) {

        val categoryMap =
            categories.associateBy {
                it.id
            }

        plans
            .filter {
                it.task.type == "TIME" &&
                        it.task.coefficient != 0.0 &&
                        it.dailyPlan.plannedMinutes > 0
            }
            .groupBy {
                it.task.categoryId
            }
            .mapNotNull { (categoryId, categoryPlans) ->

                val category =
                    categoryMap[categoryId]
                        ?: return@mapNotNull null

                val totalMinutes =
                    categoryPlans.sumOf {
                        it.dailyPlan.plannedMinutes
                    }

                if (totalMinutes <= 0) {

                    null

                } else {

                    CategoryTimeSummaryItem(
                        categoryId =
                            categoryId,

                        categoryName =
                            category.name,

                        totalMinutes =
                            totalMinutes
                    )
                }
            }
            .sortedByDescending {
                it.totalMinutes
            }
    }

    Box(
        modifier = modifier
    ) {

        IconButton(
            onClick = {
                expanded = !expanded
            },
            modifier = Modifier.size(34.dp)
        ) {

            Icon(
                imageVector =
                    Icons.Default.Category,

                contentDescription =
                    "جمع زمان دسته‌بندی‌ها",

                modifier =
                    Modifier.size(19.dp),

                tint =
                    if (expanded) {
                        PrimaryGreen
                    } else {
                        TextSecondary
                    }
            )
        }

        if (expanded) {

            CategoryTimeSummaryPopup(
                summary =
                    summary,

                onClose = {
                    expanded = false
                }
            )
        }
    }
}

@Composable
private fun CategoryTimeSummaryPopup(
    summary: List<CategoryTimeSummaryItem>,
    onClose: () -> Unit
) {

    androidx.compose.ui.window.Popup(
        alignment =
            Alignment.TopCenter,

        onDismissRequest =
            onClose,

        properties =
            androidx.compose.ui.window.PopupProperties(
                focusable = true,
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
    ) {

        Column(
            modifier =
                Modifier
                    .fillMaxWidth(0.78f)
                    .clip(
                        RoundedCornerShape(14.dp)
                    )
                    .background(
                        MaterialTheme
                            .colorScheme
                            .surface
                    )
                    .border(
                        width = 0.8.dp,

                        color =
                            PrimaryGreen.copy(
                                alpha = 0.25f
                            ),

                        shape =
                            RoundedCornerShape(14.dp)
                    )
                    .padding(
                        horizontal = 12.dp,
                        vertical = 9.dp
                    )
        ) {

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text =
                        "زمان برنامه‌ریزی‌شده",

                    modifier =
                        Modifier.weight(1f),

                    fontSize =
                        11.sp,

                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurface
                )

                IconButton(
                    onClick =
                        onClose,

                    modifier =
                        Modifier.size(25.dp)
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Close,

                        contentDescription =
                            "بستن",

                        modifier =
                            Modifier.size(15.dp),

                        tint =
                            TextSecondary
                    )
                }
            }

            if (summary.isEmpty()) {

                Text(
                    text =
                        "برنامه زمانی ثبت نشده",

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                vertical = 12.dp
                            ),

                    fontSize =
                        9.sp,

                    color =
                        TextSecondary
                )

            } else {

                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .heightIn(
                                max = 260.dp
                            ),

                    verticalArrangement =
                        Arrangement.spacedBy(5.dp)
                ) {

                    items(
                        items =
                            summary,

                        key = {
                            it.categoryId
                        }
                    ) { item ->

                        CategoryTimeSummaryRow(
                            item =
                                item
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryTimeSummaryRow(
    item: CategoryTimeSummaryItem
) {

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(7.dp)
                )
                .background(
                    PrimaryGreenLight.copy(
                        alpha = 0.10f
                    )
                )
                .padding(
                    horizontal = 8.dp,
                    vertical = 6.dp
                ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Text(
            text =
                item.categoryName,

            modifier =
                Modifier.weight(1f),

            fontSize =
                10.sp,

            maxLines =
                1,

            color =
                MaterialTheme
                    .colorScheme
                    .onSurface
        )

        Text(
            text =
                formatMinutesPersian(
                    item.totalMinutes
                ),

            fontSize =
                10.sp,

            color =
                PrimaryGreen
        )
    }
}

private fun formatMinutesPersian(
    minutes: Int
): String {

    val hours =
        minutes / 60

    val remainingMinutes =
        minutes % 60

    return if (hours > 0) {

        "${toPersianDigitsOverview(
            hours.toString()
        )}:" +
                toPersianDigitsOverview(
                    remainingMinutes
                        .toString()
                        .padStart(2, '0')
                )

    } else {

        "${toPersianDigitsOverview(
            remainingMinutes.toString()
        )} دقیقه"
    }
}

private fun toPersianDigitsOverview(
    value: String
): String {

    return value
        .replace("0", "۰")
        .replace("1", "۱")
        .replace("2", "۲")
        .replace("3", "۳")
        .replace("4", "۴")
        .replace("5", "۵")
        .replace("6", "۶")
        .replace("7", "۷")
        .replace("8", "۸")
        .replace("9", "۹")
}