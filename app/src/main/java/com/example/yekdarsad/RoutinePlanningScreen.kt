package com.example.yekdarsad

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yekdarsad.data.AppDatabase
import com.example.yekdarsad.data.Category
import com.example.yekdarsad.data.DailyPlan
import com.example.yekdarsad.data.DatabaseProvider
import com.example.yekdarsad.data.Routine
import com.example.yekdarsad.data.RoutineJalali
import com.example.yekdarsad.data.RoutineType
import com.example.yekdarsad.data.Task
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private val RoutineTextPrimary = Color(0xFF252525)
private val RoutineTextSecondary = Color(0xFF777777)

private data class RoutineCategoryStyle(
    val background: Color,
    val iconBackground: Color,
    val accent: Color,
    val taskBackground: Color
)

private fun routineCategoryStyle(
    title: String
): RoutineCategoryStyle {

    return when {

        title == "ورزش" ->
            RoutineCategoryStyle(
                background = Color(0xFFD7F4F0),
                iconBackground = Color(0xFF9DDED7),
                accent = Color(0xFF008F83),
                taskBackground = Color(0xFFF1FBF9)
            )

        title == "معنویت" ->
            RoutineCategoryStyle(
                background = Color(0xFFDFF1DF),
                iconBackground = Color(0xFFAED5B0),
                accent = Color(0xFF2E823B),
                taskBackground = Color(0xFFF3FAF3)
            )

        title == "زبان انگلیسی" ->
            RoutineCategoryStyle(
                background = Color(0xFFF9D7D7),
                iconBackground = Color(0xFFEEA4A4),
                accent = Color(0xFFC92F32),
                taskBackground = Color(0xFFFFF5F5)
            )

        title == "کتاب و پادکست" ->
            RoutineCategoryStyle(
                background = Color(0xFFFFE2C2),
                iconBackground = Color(0xFFFFB968),
                accent = Color(0xFFE06B00),
                taskBackground = Color(0xFFFFF8EF)
            )

        title == "مهارت‌های شغلی" ->
            RoutineCategoryStyle(
                background = Color(0xFFF6D8E5),
                iconBackground = Color(0xFFE9A8C3),
                accent = Color(0xFFC34F7D),
                taskBackground = Color(0xFFFFF5F9)
            )

        title == "شغل" ->
            RoutineCategoryStyle(
                background = Color(0xFFD9DDE2),
                iconBackground = Color(0xFFADB4BC),
                accent = Color(0xFF414A54),
                taskBackground = Color(0xFFF5F6F7)
            )

        title == "تفریح" ->
            RoutineCategoryStyle(
                background = Color(0xFFFFF0B5),
                iconBackground = Color(0xFFFFD85C),
                accent = Color(0xFFC38A00),
                taskBackground = Color(0xFFFFFBEB)
            )

        title == "متفرقه" ->
            RoutineCategoryStyle(
                background = Color(0xFFD7E8FA),
                iconBackground = Color(0xFFA3C9F0),
                accent = Color(0xFF2E70B7),
                taskBackground = Color(0xFFF3F8FD)
            )

        title.contains("دانشگاه") ->
            RoutineCategoryStyle(
                background = Color(0xFFE6D9F7),
                iconBackground = Color(0xFFC7AAEA),
                accent = Color(0xFF7040AD),
                taskBackground = Color(0xFFF8F4FC)
            )

        title.contains("هنر") ->
            RoutineCategoryStyle(
                background = Color(0xFFF6D6E7),
                iconBackground = Color(0xFFE8A6C6),
                accent = Color(0xFFB83270),
                taskBackground = Color(0xFFFFF5F9)
            )

        title.contains("سفر") ->
            RoutineCategoryStyle(
                background = Color(0xFFD5EDF1),
                iconBackground = Color(0xFFA5D5DE),
                accent = Color(0xFF277C8A),
                taskBackground = Color(0xFFF2FAFB)
            )

        title.contains("مالی") ->
            RoutineCategoryStyle(
                background = Color(0xFFF1E4BD),
                iconBackground = Color(0xFFD9C47D),
                accent = Color(0xFF80651C),
                taskBackground = Color(0xFFFBF8EE)
            )

        else ->
            RoutineCategoryStyle(
                background = Color(0xFFE4E8ED),
                iconBackground = Color(0xFFC6CDD5),
                accent = Color(0xFF596572),
                taskBackground = Color(0xFFF6F7F8)
            )
    }
}

class RoutineViewModel(
    private val database: AppDatabase
) : ViewModel() {

    private val categoryDao: com.example.yekdarsad.data.CategoryDao =
        database.categoryDao()

    private val taskDao = database.taskDao()
    private val routineDao = database.routineDao()
    private val dailyPlanDao = database.dailyPlanDao()

    private val _categories =
        MutableStateFlow<List<Category>>(emptyList())

    val categories: StateFlow<List<Category>> =
        _categories.asStateFlow()

    private val _tasks =
        MutableStateFlow<List<Task>>(emptyList())

    val tasks: StateFlow<List<Task>> =
        _tasks.asStateFlow()

    private val _routines =
        MutableStateFlow<List<Routine>>(emptyList())

    val routines: StateFlow<List<Routine>> =
        _routines.asStateFlow()

    private val _message =
        MutableStateFlow<String?>(null)

    val message: StateFlow<String?> =
        _message.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _categories.value = categoryDao.getAll()
            _tasks.value = taskDao.getAllTasksOnce()
            _routines.value = routineDao.getAllOnce()
        }
    }

    fun clearMessage() {
        _message.value = null
    }

    fun addRoutine(
        task: Task,
        type: RoutineType
    ) {
        viewModelScope.launch {

            val period =
                RoutineJalali.currentPeriod(type)

            val existing =
                routineDao.getExistingRoutine(
                    taskId = task.id,
                    type = type.value,
                    startDate = period.first,
                    endDate = period.second
                )

            if (existing != null) {

                if (!existing.enabled) {

                    routineDao.updateEnabled(
                        routineId = existing.id,
                        enabled = true
                    )

                    generateDailyPlans(
                        task = task,
                        routine = existing.copy(
                            enabled = true
                        )
                    )

                    _message.value =
                        "روتین دوباره فعال شد."

                } else {

                    _message.value =
                        "این روتین از قبل فعال است."
                }

                load()
                return@launch
            }

            val routine = Routine(
                taskId = task.id,
                type = type.value,
                startDate = period.first,
                endDate = period.second,
                enabled = true,
                showInOverview = true,
                deleted = false
            )

            val routineId =
                routineDao
                    .insert(routine)
                    .toInt()

            val savedRoutine =
                routine.copy(
                    id = routineId
                )

            generateDailyPlans(
                task = task,
                routine = savedRoutine
            )

            _message.value =
                "روتین اضافه شد."

            load()
        }
    }

    fun setRoutineEnabled(
        routine: Routine,
        enabled: Boolean
    ) {
        viewModelScope.launch {

            if (enabled) {

                routineDao.updateEnabled(
                    routineId = routine.id,
                    enabled = true
                )

                val task =
                    taskDao.getById(
                        routine.taskId
                    )

                if (task != null) {

                    generateDailyPlans(
                        task = task,
                        routine = routine.copy(
                            enabled = true
                        )
                    )
                }

                _message.value =
                    "روتین فعال شد."

            } else {

                dailyPlanDao.markDeletedByRoutineId(
                    routineId = routine.id
                )

                routineDao.updateEnabled(
                    routineId = routine.id,
                    enabled = false
                )

                _message.value =
                    "روتین غیرفعال شد."
            }

            load()
        }
    }

    fun setShowInOverview(
        routine: Routine,
        show: Boolean
    ) {
        viewModelScope.launch {

            routineDao.updateShowInOverview(
                routineId = routine.id,
                showInOverview = show
            )

            load()
        }
    }

    fun deleteRoutine(
        routine: Routine
    ) {
        viewModelScope.launch {

            dailyPlanDao.markDeletedByRoutineId(
                routineId = routine.id
            )

            routineDao.markDeleted(
                routineId = routine.id
            )

            _message.value =
                "روتین حذف شد."

            load()
        }
    }

    private suspend fun generateDailyPlans(
        task: Task,
        routine: Routine
    ) {
        val type =
            RoutineType.fromValue(
                routine.type
            )

        val dates =
            when (type) {

                RoutineType.WEEKLY -> {
                    RoutineJalali.currentWeekDates()
                }

                RoutineType.MONTHLY -> {
                    RoutineJalali.currentMonthDates()
                }
            }

        for (jalaliDate in dates) {

            val gregorianDate =
                RoutineJalali.keyToGregorian(
                    jalaliDate
                )

            val dateString =
                gregorianDate.toString()

            val existing =
                dailyPlanDao.getRoutinePlanByDate(
                    routineId = routine.id,
                    date = dateString
                )

            if (existing != null) {
                continue
            }

            val plannedMinutes =
                if (task.type == "TIME") {
                    30
                } else {
                    0
                }

            dailyPlanDao.insert(
                DailyPlan(
                    taskId = task.id,
                    routineId = routine.id,
                    date = dateString,
                    plannedMinutes = plannedMinutes,
                    actualMinutes = 0,
                    earnedScore = 0.0,
                    completed = false,
                    exerciseCalories = 0.0,
                    dayRegistered = false,
                    orderIndex = 0,
                    note = "",
                    cloudId = null,
                    deleted = false,
                    syncBase = null,
                    syncVersion = 0
                )
            )
        }
    }
}

class RoutineViewModelFactory(
    private val database: AppDatabase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        return RoutineViewModel(
            database = database
        ) as T
    }
}

@Composable
fun RoutinePlanningScreen(
    onBack: () -> Unit
) {
    val context =
        androidx.compose.ui.platform.LocalContext.current

    val database =
        remember {
            DatabaseProvider.getDatabase(
                context
            )
        }

    val viewModel: RoutineViewModel =
        viewModel(
            factory = RoutineViewModelFactory(
                database
            )
        )

    val categories by
    viewModel.categories.collectAsState()

    val tasks by
    viewModel.tasks.collectAsState()

    val routines by
    viewModel.routines.collectAsState()

    val message by
    viewModel.message.collectAsState()

    var expandedCategoryId by remember {
        mutableIntStateOf(-1)
    }

    var selectedRoutineForDelete by remember {
        mutableStateOf<Routine?>(null)
    }

    LaunchedEffect(message) {
        if (message != null) {
            delay(1800)
            viewModel.clearMessage()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            RoutineHeader(
                onBack = onBack
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                item {

                    Text(
                        text = "برنامه‌ریزی روتین",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 20.dp,
                                end = 20.dp,
                                top = 4.dp
                            ),
                        style =
                            MaterialTheme.typography.titleLarge,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            RoutineTextPrimary
                    )

                    Text(
                        text =
                            "فعالیت‌هایی را انتخاب کن که می‌خواهی به‌صورت هفتگی یا ماهانه تکرار شوند.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 20.dp,
                                end = 20.dp,
                                top = 5.dp
                            ),
                        style =
                            MaterialTheme.typography.bodyMedium,
                        color =
                            RoutineTextSecondary
                    )
                }

                item {

                    Text(
                        text = "انتخاب فعالیت",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 20.dp,
                                top = 8.dp
                            ),
                        style =
                            MaterialTheme.typography.titleMedium,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            RoutineTextPrimary
                    )
                }

                items(
                    items = categories,
                    key = {
                        "category_${it.id}"
                    }
                ) { category ->

                    val categoryTasks =
                        tasks.filter {
                            it.categoryId == category.id &&
                                    !it.deleted
                        }

                    RoutineCategoryCard(
                        category = category,
                        tasks = categoryTasks,
                        expanded =
                            expandedCategoryId ==
                                    category.id,
                        onClick = {

                            expandedCategoryId =
                                if (
                                    expandedCategoryId ==
                                    category.id
                                ) {
                                    -1
                                } else {
                                    category.id
                                }
                        },
                        onAddRoutine = {
                            viewModel.addRoutine(
                                task = it.first,
                                type = it.second
                            )
                        }
                    )
                }

                item {

                    Text(
                        text = "روتین‌های من",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 20.dp,
                                end = 20.dp,
                                top = 8.dp
                            ),
                        style =
                            MaterialTheme.typography.titleMedium,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            RoutineTextPrimary
                    )
                }

                if (routines.isEmpty()) {

                    item {
                        EmptyRoutineCard()
                    }

                } else {

                    items(
                        items = routines,
                        key = {
                            "routine_${it.id}"
                        }
                    ) { routine ->

                        val task =
                            tasks.firstOrNull {
                                it.id == routine.taskId
                            }

                        val category =
                            if (task != null) {
                                categories.firstOrNull {
                                    it.id == task.categoryId
                                }
                            } else {
                                null
                            }

                        if (task != null) {

                            RoutineListItem(
                                routine = routine,
                                task = task,
                                category = category,
                                onEnabledChanged = {
                                    viewModel.setRoutineEnabled(
                                        routine = routine,
                                        enabled = it
                                    )
                                },
                                onShowInOverviewChanged = {
                                    viewModel.setShowInOverview(
                                        routine = routine,
                                        show = it
                                    )
                                },
                                onDelete = {
                                    selectedRoutineForDelete =
                                        routine
                                }
                            )
                        }
                    }
                }

                item {
                    Spacer(
                        modifier = Modifier.height(
                            20.dp
                        )
                    )
                }
            }
        }

        if (message != null) {

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(
                        bottom = 24.dp,
                        start = 20.dp,
                        end = 20.dp
                    )
                    .clip(
                        RoundedCornerShape(12.dp)
                    )
                    .background(
                        RoutineTextPrimary
                    )
                    .padding(
                        horizontal = 18.dp,
                        vertical = 10.dp
                    )
            ) {

                Text(
                    text = message ?: "",
                    color = Color.White,
                    style =
                        MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    if (selectedRoutineForDelete != null) {

        val routine =
            selectedRoutineForDelete!!

        val task =
            tasks.firstOrNull {
                it.id == routine.taskId
            }

        AlertDialog(
            onDismissRequest = {
                selectedRoutineForDelete = null
            },
            title = {
                Text(
                    text = "حذف روتین"
                )
            },
            text = {
                Text(
                    text =
                        if (task != null) {
                            "روتین «${task.title}» حذف شود؟ فعالیت‌های ساخته‌شده توسط این روتین هم از برنامه‌های روزانه حذف می‌شوند."
                        } else {
                            "این روتین حذف شود؟"
                        }
                )
            },
            confirmButton = {

                TextButton(
                    onClick = {

                        viewModel.deleteRoutine(
                            routine
                        )

                        selectedRoutineForDelete = null
                    }
                ) {

                    Text(
                        text = "حذف",
                        color = Color(0xFFD64545)
                    )
                }
            },
            dismissButton = {

                TextButton(
                    onClick = {
                        selectedRoutineForDelete = null
                    }
                ) {

                    Text(
                        text = "انصراف"
                    )
                }
            }
        )
    }
}

@Composable
private fun RoutineHeader(
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .padding(
                horizontal = 10.dp
            ),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        IconButton(
            onClick = onBack,
            modifier = Modifier.size(42.dp)
        ) {

            Icon(
                imageVector =
                    Icons.Default.ArrowBack,
                contentDescription = "بازگشت",
                tint = RoutineTextPrimary
            )
        }

        Text(
            text = "روتین",
            modifier = Modifier.padding(
                start = 4.dp
            ),
            style =
                MaterialTheme.typography.titleLarge,
            fontWeight =
                FontWeight.Bold,
            color =
                RoutineTextPrimary
        )
    }
}

@Composable
private fun RoutineCategoryCard(
    category: Category,
    tasks: List<Task>,
    expanded: Boolean,
    onClick: () -> Unit,
    onAddRoutine: (Pair<Task, RoutineType>) -> Unit
) {
    val style =
        routineCategoryStyle(category.name)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp
            ),
        shape =
            RoundedCornerShape(18.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    style.background
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 0.dp
            )
    ) {

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clickable(
                        onClick = onClick
                    )
                    .padding(
                        horizontal = 14.dp
                    ),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(
                            RoundedCornerShape(10.dp)
                        )
                        .background(
                            style.iconBackground
                        ),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text = category.icon,
                        style =
                            MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(
                    modifier = Modifier.width(11.dp)
                )

                Text(
                    text = category.name,
                    modifier = Modifier.weight(1f),
                    style =
                        MaterialTheme.typography.bodyLarge,
                    fontWeight =
                        FontWeight.SemiBold,
                    color =
                        RoutineTextPrimary
                )

                Icon(
                    imageVector =
                        if (expanded) {
                            Icons.Default.ExpandLess
                        } else {
                            Icons.Default.ExpandMore
                        },
                    contentDescription = null,
                    modifier = Modifier.size(21.dp),
                    tint =
                        style.accent
                )
            }

            if (expanded) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 12.dp,
                            end = 12.dp,
                            bottom = 10.dp
                        ),
                    verticalArrangement =
                        Arrangement.spacedBy(6.dp)
                ) {

                    if (tasks.isEmpty()) {

                        Text(
                            text =
                                "فعالیتی در این دسته وجود ندارد.",
                            modifier =
                                Modifier.padding(
                                    horizontal = 4.dp,
                                    vertical = 8.dp
                                ),
                            color =
                                style.accent,
                            style =
                                MaterialTheme.typography.bodySmall
                        )

                    } else {

                        tasks.forEach { task ->

                            RoutineTaskRow(
                                task = task,
                                style = style,
                                onAddWeekly = {
                                    onAddRoutine(
                                        task to
                                                RoutineType.WEEKLY
                                    )
                                },
                                onAddMonthly = {
                                    onAddRoutine(
                                        task to
                                                RoutineType.MONTHLY
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RoutineTaskRow(
    task: Task,
    style: RoutineCategoryStyle,
    onAddWeekly: () -> Unit,
    onAddMonthly: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(10.dp)
            )
            .background(
                style.taskBackground
            )
            .padding(
                horizontal = 11.dp,
                vertical = 8.dp
            )
    ) {

        Text(
            text = task.title,
            style =
                MaterialTheme.typography.bodyMedium,
            fontWeight =
                FontWeight.Medium,
            color =
                RoutineTextPrimary
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Row(
            horizontalArrangement =
                Arrangement.spacedBy(7.dp)
        ) {

            RoutineTypeButton(
                text = "هفتگی",
                color = style.accent,
                lightColor = style.background,
                onClick = onAddWeekly,
                modifier = Modifier.weight(1f)
            )

            RoutineTypeButton(
                text = "ماهانه",
                color = style.accent,
                lightColor = style.background,
                onClick = onAddMonthly,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun RoutineTypeButton(
    text: String,
    color: Color,
    lightColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(32.dp)
            .clip(
                RoundedCornerShape(8.dp)
            )
            .background(
                lightColor
            )
            .clickable(
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = text,
            color = color,
            style =
                MaterialTheme.typography.labelMedium,
            fontWeight =
                FontWeight.SemiBold
        )
    }
}

@Composable
private fun RoutineListItem(
    routine: Routine,
    task: Task,
    category: Category?,
    onEnabledChanged: (Boolean) -> Unit,
    onShowInOverviewChanged: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember(
        routine.id
    ) {
        mutableStateOf(false)
    }

    val style =
        routineCategoryStyle(
            category?.name ?: ""
        )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp
            ),
        shape =
            RoundedCornerShape(14.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    style.taskBackground
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 0.dp
            )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 14.dp,
                    end = 7.dp,
                    top = 10.dp,
                    bottom = 10.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(
                        RoundedCornerShape(9.dp)
                    )
                    .background(
                        style.iconBackground
                    ),
                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text = category?.icon ?: "•",
                    style =
                        MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = task.title,
                    style =
                        MaterialTheme.typography.bodyLarge,
                    fontWeight =
                        FontWeight.SemiBold,
                    color =
                        RoutineTextPrimary
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(6.dp),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    RoutineBadge(
                        text =
                            RoutineType.fromValue(
                                routine.type
                            ).title,
                        color = style.accent,
                        lightColor = style.background
                    )

                    if (!routine.enabled) {

                        RoutineBadge(
                            text = "خاموش",
                            muted = true,
                            color = style.accent,
                            lightColor = style.background
                        )
                    }
                }
            }

            SmallRoutineSwitch(
                checked = routine.enabled,
                color = style.accent,
                onCheckedChange =
                    onEnabledChanged
            )

            Box {

                IconButton(
                    onClick = {
                        menuExpanded = true
                    },
                    modifier = Modifier.size(36.dp)
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.MoreVert,
                        contentDescription =
                            "گزینه‌های روتین",
                        modifier =
                            Modifier.size(20.dp),
                        tint =
                            style.accent
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = {
                        menuExpanded = false
                    }
                ) {

                    DropdownMenuItem(
                        text = {

                            Text(
                                text =
                                    if (
                                        routine.showInOverview
                                    ) {
                                        "عدم نمایش در تب یک نگاه"
                                    } else {
                                        "نمایش در یک نگاه"
                                    }
                            )
                        },
                        leadingIcon = {

                            Icon(
                                imageVector =
                                    if (
                                        routine.showInOverview
                                    ) {
                                        Icons.Default.VisibilityOff
                                    } else {
                                        Icons.Default.Visibility
                                    },
                                contentDescription = null,
                                modifier =
                                    Modifier.size(19.dp)
                            )
                        },
                        onClick = {

                            menuExpanded = false

                            onShowInOverviewChanged(
                                !routine.showInOverview
                            )
                        }
                    )

                    DropdownMenuItem(
                        text = {

                            Text(
                                text = "حذف روتین",
                                color =
                                    Color(0xFFD64545)
                            )
                        },
                        leadingIcon = {

                            Icon(
                                imageVector =
                                    Icons.Default.Delete,
                                contentDescription = null,
                                modifier =
                                    Modifier.size(19.dp),
                                tint =
                                    Color(0xFFD64545)
                            )
                        },
                        onClick = {

                            menuExpanded = false

                            onDelete()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SmallRoutineSwitch(
    checked: Boolean,
    color: Color,
    onCheckedChange: (Boolean) -> Unit
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = Modifier.size(
            width = 38.dp,
            height = 24.dp
        ),
        thumbContent = {
            Box(
                modifier = Modifier.size(14.dp)
            )
        },
        colors =
            SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = color,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor =
                    Color(0xFFD9DEE7),
                uncheckedBorderColor =
                    Color(0xFFC7CDD7)
            )
    )
}

@Composable
private fun RoutineBadge(
    text: String,
    muted: Boolean = false,
    color: Color,
    lightColor: Color
) {
    Box(
        modifier = Modifier
            .clip(
                RoundedCornerShape(6.dp)
            )
            .background(
                if (muted) {
                    Color(0xFFF0F1F3)
                } else {
                    lightColor
                }
            )
            .padding(
                horizontal = 7.dp,
                vertical = 3.dp
            )
    ) {

        Text(
            text = text,
            style =
                MaterialTheme.typography.labelSmall,
            color =
                if (muted) {
                    RoutineTextSecondary
                } else {
                    color
                },
            fontWeight =
                FontWeight.Medium
        )
    }
}

@Composable
private fun EmptyRoutineCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp
            ),
        shape =
            RoundedCornerShape(14.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color(0xFFF8FAFC)
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 0.dp
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 22.dp
                ),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text = "هنوز روتینی نساختی",
                style =
                    MaterialTheme.typography.bodyLarge,
                fontWeight =
                    FontWeight.SemiBold,
                color =
                    RoutineTextPrimary
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text =
                    "از فعالیت‌های بالا یک روتین هفتگی یا ماهانه انتخاب کن.",
                style =
                    MaterialTheme.typography.bodySmall,
                color =
                    RoutineTextSecondary
            )
        }
    }
}