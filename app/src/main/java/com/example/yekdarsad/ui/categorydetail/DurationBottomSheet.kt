package com.example.yekdarsad.ui.categorydetail

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.yekdarsad.data.TaskWithDurations
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DurationBottomSheet(
    task: TaskWithDurations,
    sheetState: SheetState,
    categoryColor: Color,
    onDismiss: () -> Unit,
    onDurationSelected: (Int) -> Unit,
    onItemSelected: () -> Unit
) {

    // =====================================================
    // Durations
    // =====================================================

    val durations =
        listOf(
            15,
            30,
            45,
            60,
            75,
            90,
            105,
            120
        )

    // =====================================================
    // Item Added Message
    // =====================================================

    var showItemAddedMessage by remember {
        mutableStateOf(false)
    }

    val messageAlpha by animateFloatAsState(

        targetValue =
            if (showItemAddedMessage) {
                1f
            } else {
                0f
            },

        animationSpec =
            tween(
                durationMillis = 250
            ),

        label =
            "item_added_message"
    )

    // =====================================================
    // Item Selected Flow
    // =====================================================
    //
    // ترتیب:
    //
    // 1. کاربر روی «موردی» می‌زند
    // 2. پیام نمایش داده می‌شود
    // 3. کمی صبر
    // 4. پیام محو می‌شود
    // 5. onItemSelected اجرا می‌شود
    // 6. BottomSheet توسط صفحه اصلی بسته می‌شود
    //
    // =====================================================

    LaunchedEffect(showItemAddedMessage) {

        if (showItemAddedMessage) {

            // پیام مدتی دیده شود
            delay(900)

            // شروع محو شدن
            showItemAddedMessage = false

            // صبر برای کامل شدن محو شدن
            delay(250)

            // حالا BottomSheet بسته شود
            onDismiss()
        }
    }

    // =====================================================
    // Bottom Sheet
    // =====================================================

    ModalBottomSheet(

        onDismissRequest =
            onDismiss,

        sheetState =
            sheetState,

        shape =
            RoundedCornerShape(
                topStart = 26.dp,
                topEnd = 26.dp
            ),

        containerColor =
            MaterialTheme
                .colorScheme
                .surface
    ) {

        Box(
            modifier =
                Modifier.fillMaxWidth()
        ) {

            Column(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 18.dp,
                            end = 18.dp,
                            bottom = 20.dp
                        )
            ) {

                // =================================================
                // Header
                // =================================================

                Row(

                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.SpaceBetween,

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Column(
                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Text(

                            text =
                                task.task.title,

                            style =
                                MaterialTheme
                                    .typography
                                    .titleMedium
                        )

                        Spacer(
                            modifier =
                                Modifier.height(1.dp)
                        )

                        Text(

                            text =
                                "نحوه ثبت فعالیت",

                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall,

                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant
                        )
                    }

                    TextButton(
                        onClick =
                            onDismiss,

                        enabled =
                            !showItemAddedMessage
                    ) {

                        Text("بستن")
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                // =================================================
                // موردی
                // =================================================

                FilterChip(

                    selected =
                        false,

                    onClick = {

                        if (!showItemAddedMessage) {

                            // همین لحظه مورد واقعاً ثبت می‌شود
                            onItemSelected()

                            // بعد پیام نمایش داده می‌شود
                            showItemAddedMessage = true
                        }
                    },

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(40.dp),

                    label = {

                        Text(

                            text =
                                "موردی",

                            style =
                                MaterialTheme
                                    .typography
                                    .labelMedium
                        )
                    },

                    shape =
                        RoundedCornerShape(11.dp),

                    colors =
                        FilterChipDefaults
                            .filterChipColors(

                                containerColor =
                                    categoryColor.copy(
                                        alpha = 0.10f
                                    ),

                                labelColor =
                                    MaterialTheme
                                        .colorScheme
                                        .onSurface
                            ),

                    border =
                        FilterChipDefaults
                            .filterChipBorder(

                                enabled =
                                    !showItemAddedMessage,

                                selected =
                                    false,

                                borderColor =
                                    categoryColor.copy(
                                        alpha = 0.20f
                                    )
                            )
                )

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                // =================================================
                // عنوان مدت زمان
                // =================================================

                Text(

                    text =
                        "انتخاب مدت زمان",

                    style =
                        MaterialTheme
                            .typography
                            .labelMedium,

                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                )

                Spacer(
                    modifier =
                        Modifier.height(7.dp)
                )

                // =================================================
                // Duration Grid
                // =================================================

                val rows =
                    durations.chunked(4)

                rows.forEachIndexed {
                        rowIndex,
                        rowDurations ->

                    Row(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(38.dp),

                        horizontalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {

                        rowDurations.forEach { minutes ->

                            Box(

                                modifier =
                                    Modifier
                                        .weight(1f)
                                        .height(38.dp)
                            ) {

                                FilterChip(

                                    selected =
                                        false,

                                    onClick = {

                                        onDurationSelected(
                                            minutes
                                        )
                                    },

                                    modifier =
                                        Modifier.fillMaxSize(),

                                    label = {

                                        Text(

                                            text =
                                                "$minutes دقیقه",

                                            style =
                                                MaterialTheme
                                                    .typography
                                                    .labelSmall,

                                            maxLines =
                                                1
                                        )
                                    },

                                    shape =
                                        RoundedCornerShape(
                                            11.dp
                                        ),

                                    colors =
                                        FilterChipDefaults
                                            .filterChipColors(

                                                containerColor =
                                                    categoryColor.copy(
                                                        alpha = 0.10f
                                                    ),

                                                labelColor =
                                                    MaterialTheme
                                                        .colorScheme
                                                        .onSurface
                                            ),

                                    border =
                                        FilterChipDefaults
                                            .filterChipBorder(

                                                enabled =
                                                    true,

                                                selected =
                                                    false,

                                                borderColor =
                                                    categoryColor.copy(
                                                        alpha = 0.20f
                                                    )
                                            )
                                )
                            }
                        }

                        // =================================================
                        // خانه‌های خالی ردیف آخر
                        // =================================================

                        repeat(
                            4 - rowDurations.size
                        ) {

                            Spacer(
                                modifier =
                                    Modifier.weight(1f)
                            )
                        }
                    }

                    if (
                        rowIndex <
                        rows.lastIndex
                    ) {

                        Spacer(
                            modifier =
                                Modifier.height(7.dp)
                        )
                    }
                }
            }

            // =====================================================
            // پیام «۱ مورد اضافه شد»
            // =====================================================

            if (messageAlpha > 0f) {

                Box(

                    modifier =
                        Modifier
                            .align(
                                Alignment.BottomCenter
                            )
                            .padding(
                                bottom = 18.dp
                            )
                            .alpha(
                                messageAlpha
                            )
                            .background(

                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .inverseSurface,

                                shape =
                                    RoundedCornerShape(
                                        14.dp
                                    )
                            )
                            .padding(
                                horizontal = 18.dp,
                                vertical = 10.dp
                            ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(

                        text =
                            "۱ مورد اضافه شد",

                        color =
                            MaterialTheme
                                .colorScheme
                                .inverseOnSurface,

                        style =
                            MaterialTheme
                                .typography
                                .labelLarge
                    )
                }
            }
        }
    }
}