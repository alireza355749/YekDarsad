
package com.example.yekdarsad.ui.todaytracker

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.yekdarsad.data.DailyPlanWithTask
import com.example.yekdarsad.ui.components.DailyPlanCard
import com.example.yekdarsad.ui.components.EmptyTodayView
import kotlinx.coroutines.delay
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun TodayTasksSection(

    plans: List<DailyPlanWithTask>,

    locked: Boolean,

    // =====================================================
    // اولویت‌های نمایشی
    //
    // فقط از BottomSheet تغییر می‌کنند.
    // اینجا فقط برای نمایش پرچم استفاده می‌شوند.
    // =====================================================

    priorityItems: Set<Int>,

    onCheckedChange:
        (DailyPlanWithTask, Boolean) -> Unit,

    onTimeClick:
        (DailyPlanWithTask) -> Unit,

    onDeleteClick:
        (DailyPlanWithTask) -> Unit,

    onProgressSave:
        (DailyPlanWithTask, Int) -> Unit,

    onOrderChanged:
        (List<DailyPlanWithTask>) -> Unit

) {

    // =====================================================
    // اگر فعالیتی وجود ندارد
    // =====================================================

    if (plans.isEmpty()) {

        EmptyTodayView()

        return

    }

    // =====================================================
    // List
    // =====================================================

    var list by remember(plans) {

        mutableStateOf(

            plans
                .map {
                    TodayListItem.Task(it)
                }
                .toMutableList<TodayListItem>()

        )

    }

    // =====================================================
    // Lazy List
    // =====================================================

    val lazyListState =
        rememberLazyListState()

    // =====================================================
    // Reorder
    // =====================================================

    val reorderState =
        rememberReorderableLazyListState(

            lazyListState =
                lazyListState,

            onMove = { from, to ->

                if (!locked) {

                    // =================================================
                    // تأخیر قبل از جابه‌جایی
                    // =================================================

                    delay(400)

                    // =================================================
                    // جابه‌جایی واقعی
                    // =================================================

                    val newList =
                        list.toMutableList()

                    newList.add(

                        to.index,

                        newList.removeAt(
                            from.index
                        )

                    )

                    list =
                        newList

                    // =================================================
                    // فقط Taskها ذخیره می‌شوند
                    // =================================================

                    val tasks =

                        newList
                            .filterIsInstance<
                                    TodayListItem.Task
                                    >()
                            .map {
                                it.item
                            }

                    onOrderChanged(tasks)

                }

            }

        )

    // =====================================================
    // UI
    // =====================================================

    LazyColumn(

        modifier =
            Modifier.fillMaxSize(),

        state =
            lazyListState,

        verticalArrangement =
            Arrangement.spacedBy(6.dp)

    ) {

        itemsIndexed(

            items =
                list,

            key = { _, item ->

                when (item) {

                    is TodayListItem.Task ->
                        item.item.dailyPlan.id

                    TodayListItem.PriorityDivider ->
                        -9999

                }

            }

        ) { index, item ->

            when (item) {

                // =================================================
                // Task
                // =================================================

                is TodayListItem.Task -> {

                    val dailyPlan =
                        item.item.dailyPlan

                    // =================================================
                    // وضعیت اولویت
                    //
                    // فقط خوانده می‌شود.
                    // تغییرش از اینجا انجام نمی‌شود.
                    // =================================================

                    val isPriority =
                        priorityItems.contains(
                            dailyPlan.id
                        )

                    ReorderableItem(

                        state =
                            reorderState,

                        key =
                            dailyPlan.id

                    ) {

                        DailyPlanCard(

                            item =
                                item.item,

                            number =
                                index + 1,

                            modifier =
                                Modifier,

                            // =================================================
                            // Drag Handle
                            // =================================================

                            dragModifier =

                                if (locked) {

                                    Modifier

                                } else {

                                    Modifier
                                        .longPressDraggableHandle()

                                },

                            locked =
                                locked,

                            // =================================================
                            // وضعیت تیک
                            //
                            // مستقیماً از Room خوانده می‌شود.
                            // =================================================

                            displayChecked =
                                null,

                            // =================================================
                            // اولویت
                            //
                            // فقط نمایش پرچم
                            // =================================================

                            isPriority =
                                isPriority,

                            // =================================================
                            // Checkbox
                            //
                            // همه تسک‌ها، حتی تسک‌های موردی،
                            // به callback اصلی ارسال می‌شوند.
                            // =================================================

                            onCheckedChange = { checked ->

                                Log.d(
                                    "HAMOOM_CLICK",
                                    "title=${item.item.task.title}, " +
                                            "id=${item.item.dailyPlan.id}, " +
                                            "type=${item.item.task.type}, " +
                                            "checked=$checked, " +
                                            "locked=$locked"
                                )

                                if (!locked) {

                                    onCheckedChange(

                                        item.item,

                                        checked

                                    )

                                }

                            },

                            // =================================================
                            // Time
                            // =================================================

                            onTimeClick = {

                                if (!locked) {

                                    onTimeClick(
                                        item.item
                                    )

                                }

                            },

                            // =================================================
                            // Delete
                            // =================================================

                            onDeleteClick = {

                                if (!locked) {

                                    onDeleteClick(
                                        item.item
                                    )

                                }

                            },

                            // =================================================
                            // Progress
                            // =================================================

                            onProgressSave = { minutes ->

                                if (!locked) {

                                    onProgressSave(

                                        item.item,

                                        minutes

                                    )

                                }

                            }

                        )

                    }

                }

                // =================================================
                // PriorityDivider
                //
                // این مورد دیگر استفاده نمی‌شود.
                // =================================================

                TodayListItem.PriorityDivider -> Unit

            }

        }

    }

}