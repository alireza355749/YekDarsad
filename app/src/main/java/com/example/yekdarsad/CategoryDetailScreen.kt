package com.example.yekdarsad

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yekdarsad.data.DatabaseProvider
import com.example.yekdarsad.data.TaskWithDurations
import com.example.yekdarsad.ui.categorydetail.DurationBottomSheet
import com.example.yekdarsad.ui.categorydetail.TaskList
import com.example.yekdarsad.ui.components.FlyingTimeAnimation
import com.example.yekdarsad.ui.components.PlanningBasket
import com.example.yekdarsad.viewmodel.CategoryDetailViewModel
import com.example.yekdarsad.viewmodel.CategoryDetailViewModelFactory
import com.example.yekdarsad.viewmodel.PlanningViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailScreen(
    categoryId: Int,
    categoryName: String,
    selectedDate: String,
    planningViewModel: PlanningViewModel
) {

    val context = LocalContext.current

    val database =
        DatabaseProvider.getDatabase(context)

    val viewModel: CategoryDetailViewModel =
        viewModel(
            key = "category_$categoryId",
            factory = CategoryDetailViewModelFactory(
                database.taskDao(),
                database.activityDurationDao(),
                database.dailyPlanDao(),
                categoryId
            )
        )

    // =====================================================
    // Category
    // =====================================================

    val isExercise =
        categoryName.trim() == "ورزش"

    val categoryColor =
        getCategoryColor(categoryName)

    // =====================================================
    // Tasks
    // =====================================================

    val tasks by viewModel.tasks.collectAsState()

    // =====================================================
    // Add Task Dialog
    // =====================================================

    var showDialog by remember {
        mutableStateOf(false)
    }

    var title by remember {
        mutableStateOf("")
    }

    var coefficient by remember {
        mutableStateOf("")
    }

    var caloriesPerHour by remember {
        mutableStateOf("")
    }

    // =====================================================
    // Selected Task
    // =====================================================

    var selectedTask by remember {
        mutableStateOf<TaskWithDurations?>(null)
    }

    val sheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

    val scope =
        rememberCoroutineScope()

    // =====================================================
    // Basket / Animation
    // =====================================================

    val taskPositions =
        remember {
            mutableStateMapOf<Int, IntOffset>()
        }

    var basketPosition by remember {
        mutableStateOf(IntOffset.Zero)
    }

    var flyingMinutes by remember {
        mutableStateOf<Int?>(null)
    }

    var flyingStart by remember {
        mutableStateOf(IntOffset.Zero)
    }

    // =====================================================
    // Total Minutes
    // =====================================================

    val totalMinutes by planningViewModel
        .getTotalMinutes(selectedDate)
        .collectAsState(initial = 0)

    // =====================================================
    // Category Total Minutes
    // =====================================================

    val categoryTotalMinutes by planningViewModel
        .getTotalMinutesForCategory(
            date = selectedDate,
            categoryId = categoryId
        )
        .collectAsState(initial = 0)

    // =====================================================
    // Edit Task
    // =====================================================

    var taskToEdit by remember {
        mutableStateOf<TaskWithDurations?>(null)
    }

    var editTitle by remember {
        mutableStateOf("")
    }

    var editCoefficient by remember {
        mutableStateOf("")
    }

    var editCaloriesPerHour by remember {
        mutableStateOf("")
    }

    // =====================================================
    // Delete Task
    // =====================================================

    var taskToDelete by remember {
        mutableStateOf<TaskWithDurations?>(null)
    }

    // =====================================================
    // Main
    // =====================================================

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // =================================================
            // Category Visual Header
            // =================================================

            CategoryVisualHeader(
                categoryName = categoryName
            )

            // =================================================
            // Planning Basket
            // زیر هدر، سمت چپ
            // =================================================

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {

                PlanningBasket(
                    totalMinutes = totalMinutes,

                    planningViewModel =
                        planningViewModel,

                    selectedDate =
                        selectedDate,

                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .onGloballyPositioned { coordinates ->

                            val pos =
                                coordinates.localToWindow(
                                    androidx.compose.ui.geometry
                                        .Offset.Zero
                                )

                            basketPosition =
                                IntOffset(
                                    pos.x.toInt(),
                                    pos.y.toInt()
                                )
                        }
                )
            }

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            // =================================================
            // Category Total
            // =================================================

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {

                Text(
                    text = formatCategoryTotalTime(
                        categoryName = categoryName,
                        minutes = categoryTotalMinutes
                    ),
                    style =
                        MaterialTheme.typography.bodyMedium,
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            // =================================================
            // Add Activity
            // =================================================

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {

                Row(
                    modifier = Modifier
                        .clickable {

                            title = ""
                            coefficient = ""
                            caloriesPerHour = ""

                            showDialog = true
                        }
                        .padding(
                            horizontal = 2.dp,
                            vertical = 4.dp
                        ),

                    verticalAlignment =
                        Alignment.CenterVertically,

                    horizontalArrangement =
                        Arrangement.spacedBy(7.dp)
                ) {

                    Text(
                        text = "افزودن فعالیت",
                        color = Color.Black,
                        style =
                            MaterialTheme.typography.labelLarge,
                        fontWeight =
                            FontWeight.Medium
                    )

                    Box(
                        modifier = Modifier
                            .size(25.dp)
                            .background(
                                color = Color.Black,
                                shape = CircleShape
                            ),
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(
                            text = "+",
                            color = Color.White,
                            style =
                                MaterialTheme
                                    .typography
                                    .labelLarge,
                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            // =================================================
            // Task List
            // =================================================

            TaskList(
                tasks = tasks,
                categoryName = categoryName,

                onTaskPositionChanged = { id, position ->

                    taskPositions[id] = position
                },

                onTaskEdit = { item ->

                    taskToEdit = item

                    editTitle =
                        item.task.title

                    editCoefficient =
                        item.task.coefficient.toString()

                    editCaloriesPerHour =
                        item.task.caloriesPerHour
                            .takeIf {
                                it > 0.0
                            }
                            ?.toString()
                            ?: ""
                },

                onTaskDelete = { item ->

                    taskToDelete = item
                },

                onTaskClick = { item ->

                    selectedTask = item

                    scope.launch {
                        sheetState.show()
                    }
                }
            )
        }

        // =====================================================
        // Flying Animation
        // =====================================================

        flyingMinutes?.let { minutes ->

            Box(
                modifier = Modifier.fillMaxSize()
            ) {

                FlyingTimeAnimation(

                    startX =
                        flyingStart.x.toFloat(),

                    startY =
                        flyingStart.y.toFloat(),

                    endX =
                        basketPosition.x.toFloat() + 12,

                    endY =
                        basketPosition.y.toFloat() - 10,

                    minutes =
                        minutes,

                    onFinished = {

                        flyingMinutes = null
                    }
                )
            }
        }
    }

    // =========================================================
    // Duration / Item Bottom Sheet
    // =========================================================

    selectedTask?.let { taskItem ->

        DurationBottomSheet(

            task = taskItem,

            sheetState =
                sheetState,

            categoryColor =
                categoryColor,

            onDismiss = {

                scope.launch {
                    sheetState.hide()
                }.invokeOnCompletion {

                    selectedTask = null
                }
            },

            // =================================================
            // انتخاب مدت
            // =================================================

            onDurationSelected = { minutes ->

                // ---------------------------------------------
                // Animation
                // ---------------------------------------------

                flyingMinutes = minutes

                val taskPosition =
                    taskPositions[
                        taskItem.task.id
                    ] ?: IntOffset.Zero

                flyingStart =
                    IntOffset(
                        taskPosition.x + 150,
                        taskPosition.y + 40
                    )

                // ---------------------------------------------
                // Add to Planning
                // ---------------------------------------------

                planningViewModel.addTask(

                    taskId =
                        taskItem.task.id,

                    title =
                        taskItem.task.title,

                    minutes =
                        minutes,

                    coefficient =
                        taskItem.task.coefficient
                )

                // ---------------------------------------------
                // Save Daily Plan
                // ---------------------------------------------

                viewModel.addPlan(

                    taskId =
                        taskItem.task.id,

                    date =
                        selectedDate,

                    minutes =
                        minutes
                )
            },

            // =================================================
            // انتخاب فعالیت موردی
            // =================================================

            onItemSelected = {

                planningViewModel.addTask(

                    taskId =
                        taskItem.task.id,

                    title =
                        taskItem.task.title,

                    minutes = 0,

                    coefficient =
                        taskItem.task.coefficient
                )

                viewModel.addPlan(

                    taskId =
                        taskItem.task.id,

                    date =
                        selectedDate,

                    minutes = 0
                )
            }
        )
    }

    // =========================================================
    // Add Task Dialog
    // =========================================================

    AddTaskDialog(

        show =
            showDialog,

        title =
            title,

        coefficient =
            coefficient,

        caloriesPerHour =
            caloriesPerHour,

        isExercise =
            isExercise,

        onTitleChange = {

            title = it
        },

        onCoefficientChange = {

            coefficient = it
        },

        onCaloriesPerHourChange = {

            caloriesPerHour = it
        },

        onDismiss = {

            showDialog = false
        },

        onSave = {

            val coef =
                coefficient
                    .replace(",", ".")
                    .toDoubleOrNull()

            val calories =
                caloriesPerHour
                    .replace(",", ".")
                    .toDoubleOrNull()

            val validCalories =
                if (isExercise) {

                    calories != null &&
                            calories > 0.0

                } else {

                    true
                }

            if (
                coef != null &&
                coef >= 0.0 &&
                title.isNotBlank() &&
                validCalories
            ) {

                viewModel.addTask(

                    title =
                        title.trim(),

                    coefficient =
                        coef,

                    caloriesPerHour =
                        if (isExercise) {
                            calories ?: 0.0
                        } else {
                            0.0
                        }
                )

                title = ""
                coefficient = ""
                caloriesPerHour = ""

                showDialog = false
            }
        }
    )

    // =========================================================
    // Edit Task Dialog
    // =========================================================

    taskToEdit?.let { item ->

        AlertDialog(

            onDismissRequest = {

                taskToEdit = null
            },

            title = {

                Text(
                    if (isExercise) {
                        "ویرایش فعالیت ورزشی"
                    } else {
                        "ویرایش فعالیت"
                    }
                )
            },

            text = {

                Column {

                    OutlinedTextField(
                        value = editTitle,

                        onValueChange = {
                            editTitle = it
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        label = {
                            Text("نام فعالیت")
                        },

                        singleLine = true
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    OutlinedTextField(
                        value =
                            editCoefficient,

                        onValueChange = {
                            editCoefficient = it
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        label = {
                            Text("ضریب")
                        },

                        singleLine = true
                    )

                    if (isExercise) {

                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )

                        OutlinedTextField(
                            value =
                                editCaloriesPerHour,

                            onValueChange = {
                                editCaloriesPerHour = it
                            },

                            modifier =
                                Modifier.fillMaxWidth(),

                            label = {
                                Text(
                                    "کالری مصرفی در یک ساعت"
                                )
                            },

                            placeholder = {
                                Text("مثلاً 400")
                            },

                            singleLine = true
                        )
                    }
                }
            },

            confirmButton = {

                TextButton(

                    enabled =
                        editTitle.isNotBlank() &&
                                editCoefficient
                                    .replace(",", ".")
                                    .toDoubleOrNull() != null &&
                                (
                                        !isExercise ||
                                                (
                                                        editCaloriesPerHour
                                                            .replace(",", ".")
                                                            .toDoubleOrNull()
                                                            ?.let {
                                                                it > 0.0
                                                            } == true
                                                        )
                                        ),

                    onClick = {

                        val coefficient =
                            editCoefficient
                                .replace(",", ".")
                                .toDoubleOrNull()
                                ?: return@TextButton

                        val calories =
                            editCaloriesPerHour
                                .replace(",", ".")
                                .toDoubleOrNull()

                        viewModel.updateTask(

                            taskId =
                                item.task.id,

                            title =
                                editTitle.trim(),

                            coefficient =
                                coefficient,

                            caloriesPerHour =
                                if (isExercise) {
                                    calories ?: 0.0
                                } else {
                                    null
                                }
                        )

                        taskToEdit = null
                    }
                ) {

                    Text("ذخیره")
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        taskToEdit = null
                    }
                ) {

                    Text("انصراف")
                }
            }
        )
    }

    // =========================================================
    // Delete Task Dialog
    // =========================================================

    taskToDelete?.let { item ->

        AlertDialog(

            onDismissRequest = {
                taskToDelete = null
            },

            title = {
                Text("حذف فعالیت")
            },

            text = {
                Text(
                    "آیا می‌خواهی «${item.task.title}» حذف شود؟"
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        viewModel.deleteTask(
                            item.task.id
                        )

                        taskToDelete = null
                    }
                ) {

                    Text("حذف")
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        taskToDelete = null
                    }
                ) {

                    Text("انصراف")
                }
            }
        )
    }
}


// =========================================================
// Category Visual Header
// =========================================================

@Composable
private fun CategoryVisualHeader(
    categoryName: String
) {

    val name =
        categoryName.trim()

    /*
     * category_headers.png
     *
     * ساختار تصویر:
     *
     * ┌──────────────┬──────────────┐
     * │    ورزش      │    تفریح     │
     * ├──────────────┼──────────────┤
     * │     زبان     │ کتاب/پادکست │
     * ├──────────────┼──────────────┤
     * │    مهارت      │     شغل     │
     * ├──────────────┼──────────────┤
     * │   معنویت      │   متفرقه     │
     * └──────────────┴──────────────┘
     *
     * متن روی تصویر عمداً حذف شده است،
     * چون متن هر دسته داخل خود تصویر قرار دارد.
     */

    val tileIndex =
        when {

            name == "ورزش" ->
                0

            name == "تفریح" ->
                1

            name.contains("زبان") ->
                2

            name.contains("کتاب") ||
                    name.contains("پادکست") ->
                3

            name.contains("مهارت") ->
                4

            name == "شغل" ->
                5

            name == "معنویت" ->
                6

            else ->
                7
        }

    // =====================================================
    // Load image
    // =====================================================

    val imageBitmap: ImageBitmap =
        ImageBitmap.imageResource(
            id = R.drawable.category_headers
        )

    // =====================================================
    // Source grid
    // =====================================================

    val columns = 2
    val rows = 4

    val sourceWidth =
        imageBitmap.width / columns

    val sourceHeight =
        imageBitmap.height / rows

    val column =
        tileIndex % columns

    val row =
        tileIndex / columns

    val sourceLeft =
        column * sourceWidth

    val sourceTop =
        row * sourceHeight

    // =====================================================
    // Header
    // =====================================================

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(125.dp)
            .clip(
                RoundedCornerShape(20.dp)
            )
    ) {

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {

            drawImage(

                image = imageBitmap,

                srcOffset =
                    IntOffset(
                        x = sourceLeft,
                        y = sourceTop
                    ),

                srcSize =
                    IntSize(
                        width = sourceWidth,
                        height = sourceHeight
                    ),

                dstOffset =
                    IntOffset(
                        x = 0,
                        y = 0
                    ),

                dstSize =
                    IntSize(
                        width = size.width.toInt(),
                        height = size.height.toInt()
                    )
            )
        }
    }
}


// =========================================================
// Category Total Time Formatter
// =========================================================

private fun formatCategoryTotalTime(
    categoryName: String,
    minutes: Int
): String {

    val title =
        "مجموع فعالیت‌های ${categoryName.trim()}"

    if (minutes <= 0) {
        return "$title: ۰ دقیقه"
    }

    val hours =
        minutes / 60

    val remainingMinutes =
        minutes % 60

    return when {

        hours > 0 &&
                remainingMinutes > 0 ->
            "$title: $hours ساعت و $remainingMinutes دقیقه"

        hours > 0 ->
            "$title: $hours ساعت"

        else ->
            "$title: $remainingMinutes دقیقه"
    }
}