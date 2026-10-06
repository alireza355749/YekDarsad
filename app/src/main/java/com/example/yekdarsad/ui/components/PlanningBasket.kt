package com.example.yekdarsad.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.example.yekdarsad.viewmodel.PlanningViewModel
import kotlinx.coroutines.delay

// =========================================================
// Colors
// =========================================================

private val BasketTurquoise = Color(0xFF00A6A6)

private val BasketBackground = Color.White

private val BasketText = Color(0xFF30353A)

private val BasketSecondaryText = Color(0xFF7A828A)

private val BasketDivider = Color(0xFFE1E5E8)

private val BasketDelete = Color(0xFFD45C5C)


// =========================================================
// Planning Basket
// =========================================================

@Composable
fun PlanningBasket(
    totalMinutes: Int,
    planningViewModel: PlanningViewModel,
    selectedDate: String,
    modifier: Modifier = Modifier
) {

    // =====================================================
    // State
    // =====================================================

    var oldMinutes by remember {
        mutableIntStateOf(totalMinutes)
    }

    var pop by remember {
        mutableStateOf(false)
    }

    var showPanel by remember {
        mutableStateOf(false)
    }

    // =====================================================
    // Animation when minutes change
    // =====================================================

    LaunchedEffect(totalMinutes) {

        if (totalMinutes != oldMinutes) {

            oldMinutes = totalMinutes

            pop = true

            delay(120)

            pop = false
        }
    }

    // =====================================================
    // Basket rotation
    // =====================================================

    val rotation by animateFloatAsState(
        targetValue =
            if (showPanel) {
                180f
            } else {
                0f
            },

        animationSpec =
            tween(
                durationMillis = 280
            ),

        label = "basketRotation"
    )

    // =====================================================
    // Basket scale
    // =====================================================

    val scale by animateFloatAsState(
        targetValue =
            if (pop) {
                1.14f
            } else {
                1f
            },

        animationSpec =
            tween(150),

        label = "basketScale"
    )

    // =====================================================
    // Anchor
    // =====================================================

    Box(
        modifier = modifier
    ) {

        // =================================================
        // Basket + Time
        // کل این قسمت سفید است
        // =================================================

        Row(
            modifier =
                Modifier
                    .background(
                        color = BasketBackground,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable {
                        showPanel = !showPanel
                    }
                    .padding(
                        horizontal = 8.dp,
                        vertical = 5.dp
                    ),

            verticalAlignment =
                Alignment.CenterVertically,

            horizontalArrangement =
                Arrangement.Center
        ) {

            // ---------------------------------------------
            // Basket Icon
            // ---------------------------------------------

            Box(
                modifier =
                    Modifier.size(22.dp),

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Inventory2,

                    contentDescription =
                        "سبد برنامه‌ریزی",

                    tint =
                        BasketTurquoise,

                    modifier =
                        Modifier
                            .size(22.dp)
                            .scale(scale)
                            .rotate(rotation)
                )
            }

            Spacer(
                modifier =
                    Modifier.width(5.dp)
            )

            // ---------------------------------------------
            // Total Time
            // ---------------------------------------------

            Text(
                text =
                    formatMinutes(totalMinutes),

                color =
                    BasketTurquoise,

                style =
                    MaterialTheme
                        .typography
                        .labelMedium,

                fontWeight =
                    FontWeight.Bold
            )
        }

        // =================================================
        // Popup
        // =================================================

        if (showPanel) {

            Popup(
                onDismissRequest = {
                    showPanel = false
                },

                popupPositionProvider =
                    rememberBasketPopupPositionProvider(),

                properties =
                    PopupProperties(
                        focusable = true,
                        dismissOnBackPress = true,
                        dismissOnClickOutside = true
                    )
            ) {

                BasketPanel(
                    totalMinutes =
                        totalMinutes,

                    planningViewModel =
                        planningViewModel,

                    selectedDate =
                        selectedDate
                )
            }
        }
    }
}


// =========================================================
// Popup Position Provider
// =========================================================

@Composable
private fun rememberBasketPopupPositionProvider():
        PopupPositionProvider {

    return remember {

        object : PopupPositionProvider {

            override fun calculatePosition(
                anchorBounds: IntRect,
                windowSize: IntSize,
                layoutDirection: LayoutDirection,
                popupContentSize: IntSize
            ): IntOffset {

                val x =
                    anchorBounds.right -
                            popupContentSize.width

                val y =
                    anchorBounds.bottom

                val safeX =
                    x.coerceIn(
                        0,
                        (
                                windowSize.width -
                                        popupContentSize.width
                                ).coerceAtLeast(0)
                    )

                val safeY =
                    y.coerceIn(
                        0,
                        (
                                windowSize.height -
                                        popupContentSize.height
                                ).coerceAtLeast(0)
                    )

                return IntOffset(
                    x = safeX,
                    y = safeY
                )
            }
        }
    }
}


// =========================================================
// Basket Panel
// =========================================================

@Composable
private fun BasketPanel(
    totalMinutes: Int,
    planningViewModel: PlanningViewModel,
    selectedDate: String
) {

    val plans by planningViewModel
        .getPlansForDate(selectedDate)
        .collectAsState(
            initial = emptyList()
        )

    Box(
        modifier =
            Modifier
                .width(185.dp)
                .background(
                    color =
                        BasketBackground,

                    shape =
                        RoundedCornerShape(
                            12.dp
                        )
                )
                .padding(
                    horizontal = 7.dp,
                    vertical = 7.dp
                )
    ) {

        Column(
            modifier =
                Modifier.fillMaxWidth()
        ) {

            // =================================================
            // Header
            // =================================================

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Inventory2,

                    contentDescription =
                        null,

                    tint =
                        BasketTurquoise,

                    modifier =
                        Modifier.size(15.dp)
                )

                Spacer(
                    modifier =
                        Modifier.width(5.dp)
                )

                Text(
                    text =
                        formatMinutes(totalMinutes),

                    style =
                        MaterialTheme
                            .typography
                            .labelSmall,

                    color =
                        BasketTurquoise,

                    fontWeight =
                        FontWeight.Bold
                )
            }

            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )

            // =================================================
            // Empty
            // =================================================

            if (plans.isEmpty()) {

                Text(
                    text =
                        "لیست برنامه ریزی شما خالی است",

                    style =
                        MaterialTheme
                            .typography
                            .labelSmall,

                    color =
                        BasketSecondaryText,

                    modifier =
                        Modifier.padding(
                            vertical = 6.dp
                        )
                )

            } else {

                // =================================================
                // Task List
                // =================================================

                LazyColumn(
                    modifier =
                        Modifier.height(
                            minOf(
                                plans.size * 30,
                                300
                            ).dp
                        )
                ) {

                    itemsIndexed(
                        items = plans,

                        key = { _, plan ->
                            plan.dailyPlan.id
                        }
                    ) { index, plan ->

                        BasketTaskRow(

                            title =
                                plan.task.title,

                            minutes =
                                plan.dailyPlan
                                    .plannedMinutes,

                            onDelete = {

                                planningViewModel
                                    .deletePlan(
                                        plan.dailyPlan.id
                                    )
                            }
                        )

                        // =================================================
                        // Divider
                        // =================================================

                        if (
                            index <
                            plans.lastIndex
                        ) {

                            Divider(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            start = 9.dp,
                                            end = 2.dp
                                        ),

                                thickness =
                                    0.5.dp,

                                color =
                                    BasketDivider
                            )
                        }
                    }
                }
            }
        }
    }
}


// =========================================================
// Task Row
// =========================================================

@Composable
private fun BasketTaskRow(
    title: String,
    minutes: Int,
    onDelete: () -> Unit
) {

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(30.dp),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        // =================================================
        // Dot
        // =================================================

        Box(
            modifier =
                Modifier
                    .size(4.dp)
                    .background(
                        color =
                            BasketTurquoise,

                        shape =
                            RoundedCornerShape(
                                50
                            )
                    )
        )

        Spacer(
            modifier =
                Modifier.width(5.dp)
        )

        // =================================================
        // Title
        // =================================================

        Text(
            text =
                title,

            modifier =
                Modifier.weight(1f),

            style =
                MaterialTheme
                    .typography
                    .labelSmall,

            color =
                BasketText,

            maxLines = 1
        )

        Spacer(
            modifier =
                Modifier.width(3.dp)
        )

        // =================================================
        // Minutes
        // =================================================

        Text(
            text =
                "${minutes}د",

            style =
                MaterialTheme
                    .typography
                    .labelSmall,

            color =
                BasketSecondaryText
        )

        Spacer(
            modifier =
                Modifier.width(2.dp)
        )

        // =================================================
        // Delete
        // =================================================

        Icon(
            imageVector =
                Icons.Default.DeleteOutline,

            contentDescription =
                "حذف",

            tint =
                BasketDelete,

            modifier =
                Modifier
                    .size(16.dp)
                    .clickable {
                        onDelete()
                    }
        )
    }
}


// =========================================================
// Format Minutes
// =========================================================

private fun formatMinutes(
    minutes: Int
): String {

    val hours =
        minutes / 60

    val mins =
        minutes % 60

    return "%02d:%02d".format(
        hours,
        mins
    )
}