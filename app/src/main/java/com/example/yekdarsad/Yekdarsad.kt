package com.example.yekdarsad

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yekdarsad.data.DatabaseProvider
import com.example.yekdarsad.data.nutrition.FoodSeeder
import com.example.yekdarsad.data.nutrition.MealType
import com.example.yekdarsad.data.phoneusage.PhoneUsageRepository
import com.example.yekdarsad.data.statistics.StatisticsRepository
import com.example.yekdarsad.notifications.NotificationScheduler
import com.example.yekdarsad.ui.auth.AuthGate
import com.example.yekdarsad.ui.auth.AuthViewModel
import com.example.yekdarsad.ui.components.AppHeader
import com.example.yekdarsad.ui.components.SyncPopupManager
import com.example.yekdarsad.ui.expense.ExpensesScreen
import com.example.yekdarsad.ui.nutrition.NutritionScreen
import com.example.yekdarsad.ui.overview.OverviewMode
import com.example.yekdarsad.ui.overview.OverviewScreen
import com.example.yekdarsad.ui.phoneusage.PhoneUsageScreen
import com.example.yekdarsad.ui.settings.AppearanceSettingsScreen
import com.example.yekdarsad.ui.settings.SettingsDrawer
import com.example.yekdarsad.ui.settings.SettingsPage
import com.example.yekdarsad.ui.settings.SettingsRepository
import com.example.yekdarsad.ui.settings.SettingsScreen
import com.example.yekdarsad.ui.sleep.SleepScreen
import com.example.yekdarsad.ui.theme.CardBackground
import com.example.yekdarsad.ui.theme.PrimaryGreen
import com.example.yekdarsad.ui.theme.PrimaryGreenLight
import com.example.yekdarsad.ui.theme.TextSecondary
import com.example.yekdarsad.ui.theme.ThemeManager
import com.example.yekdarsad.ui.theme.ThemeMode
import com.example.yekdarsad.ui.theme.YekDarsadTheme
import com.example.yekdarsad.viewmodel.PhoneUsageViewModel
import com.example.yekdarsad.viewmodel.PhoneUsageViewModelFactory
import com.example.yekdarsad.viewmodel.PlanningViewModel
import com.example.yekdarsad.viewmodel.PlanningViewModelFactory
import com.example.yekdarsad.viewmodel.SettingsViewModel
import com.example.yekdarsad.viewmodel.SettingsViewModelFactory
import com.example.yekdarsad.viewmodel.StatisticsViewModel
import com.example.yekdarsad.viewmodel.StatisticsViewModelFactory
import java.time.LocalDate
import kotlinx.coroutines.launch


// ============================================================
// APP
// ============================================================

@Composable
fun YekDarsadApp() {

    val context = LocalContext.current

    val themeManager = remember {
        ThemeManager(context)
    }

    val systemDarkTheme = isSystemInDarkTheme()

    val darkTheme = when (themeManager.themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> systemDarkTheme
    }

    YekDarsadTheme(
        darkTheme = darkTheme
    ) {
        AuthGate(
            content = {
                MainScreen(
                    themeManager = themeManager
                )
            }
        )
    }
}


// ============================================================
// MAIN SCREEN
// ============================================================

@Composable
private fun MainScreen(
    themeManager: ThemeManager
) {

    val context = LocalContext.current

    // --------------------------------------------------------
    // Auth ViewModel
    // --------------------------------------------------------

    val authViewModel: AuthViewModel = viewModel()

    // --------------------------------------------------------
    // Notifications
    // --------------------------------------------------------

    LaunchedEffect(Unit) {

        NotificationScheduler.scheduleAll(
            context
        )
    }

    // --------------------------------------------------------
    // Database
    // --------------------------------------------------------

    val database = remember {

        DatabaseProvider.getDatabase(
            context
        )
    }

    // --------------------------------------------------------
    // Nutrition database seeding
    // --------------------------------------------------------

    LaunchedEffect(Unit) {

        FoodSeeder(
            context = context,
            foodDao = database.foodDao()
        ).seed()
    }

    // --------------------------------------------------------
    // ViewModels
    // --------------------------------------------------------

    val phoneUsageViewModel: PhoneUsageViewModel =
        viewModel(
            factory =
                PhoneUsageViewModelFactory(
                    PhoneUsageRepository(
                        context,
                        database.phoneUsageDao()
                    )
                )
        )

    val planningViewModel: PlanningViewModel =
        viewModel(
            factory =
                PlanningViewModelFactory(
                    database.dailyPlanDao()
                )
        )

    val statisticsViewModel: StatisticsViewModel =
        viewModel(
            factory =
                StatisticsViewModelFactory(
                    StatisticsRepository(
                        dailyPlanDao =
                            database.dailyPlanDao(),

                        phoneUsageDao =
                            database.phoneUsageDao(),

                        sleepDao =
                            database.sleepDao(),

                        nutritionDao =
                            database.nutritionDao()
                    )
                )
        )

    // --------------------------------------------------------
    // Settings ViewModel
    // --------------------------------------------------------

    val settingsViewModel: SettingsViewModel =
        viewModel(
            factory =
                SettingsViewModelFactory(
                    SettingsRepository(
                        context.applicationContext
                    )
                )
        )

    /*
     * ========================================================
     * اجازه برنامه‌ریزی روزهای گذشته
     * ========================================================
     */

    val allowPastPlanning by
    settingsViewModel
        .allowPastPlanning
        .collectAsState(
            initial = false
        )

    // --------------------------------------------------------
    // Navigation state
    // --------------------------------------------------------

    var currentScreen by remember {

        mutableStateOf<Screen>(
            Screen.Home
        )
    }

    var selectedCategory by remember {

        mutableStateOf("")
    }

    var selectedCategoryId by remember {

        mutableIntStateOf(0)
    }

    var selectedDate by remember {

        mutableStateOf(
            LocalDate.now().toString()
        )
    }

    var settingsScreen by remember {

        mutableStateOf(
            SettingsPage.MAIN
        )
    }

    // ========================================================
    // OVERVIEW STATE
    // ========================================================

    var overviewModeName by rememberSaveable {

        mutableStateOf(
            OverviewMode.WEEKLY.name
        )
    }

    var overviewWeekOffset by rememberSaveable {

        mutableIntStateOf(0)
    }

    val overviewMode = remember(
        overviewModeName
    ) {

        OverviewMode.valueOf(
            overviewModeName
        )
    }

    // --------------------------------------------------------
    // Nutrition notification state
    // --------------------------------------------------------

    var openNutritionFromNotification by remember {

        mutableStateOf(false)
    }

    var notificationMealType by remember {

        mutableStateOf<MealType?>(null)
    }

    // --------------------------------------------------------
    // Exit confirmation
    // --------------------------------------------------------

    var showExitDialog by remember {

        mutableStateOf(false)
    }

    // --------------------------------------------------------
    // Drawer
    // --------------------------------------------------------

    val drawerState =
        rememberDrawerState(
            initialValue =
                DrawerValue.Closed
        )

    val scope =
        rememberCoroutineScope()

    // --------------------------------------------------------
    // Back Handler
    // --------------------------------------------------------

    BackHandler {

        when (currentScreen) {

            Screen.Home,
            Screen.Overview,
            Screen.Today,
            Screen.Statistics,
            Screen.Activities -> {

                showExitDialog = true
            }

            else -> {
                // صفحات داخلی BackHandler خودشان را دارند
            }
        }
    }

    // --------------------------------------------------------
    // Header
    // --------------------------------------------------------

    val showMainHeader =
        when (currentScreen) {

            Screen.Home,
            Screen.Overview,
            Screen.Today,
            Screen.Statistics,
            Screen.Activities -> true

            else -> false
        }

    val headerTitle =
        when (currentScreen) {

            Screen.Home ->
                "خانه"

            Screen.Overview ->
                "در یک نگاه"

            Screen.Today ->
                "امروز"

            Screen.Statistics ->
                "آمار"

            Screen.Activities ->
                "برنامه ریزی"

            else ->
                ""
        }

    // --------------------------------------------------------
    // Navigation Drawer
    // --------------------------------------------------------

    ModalNavigationDrawer(

        drawerState =
            drawerState,

        drawerContent = {

            SettingsDrawer(

                onClose = {

                    scope.launch {
                        drawerState.close()
                    }
                },

                onSettingsClick = {

                    scope.launch {
                        drawerState.close()
                    }

                    settingsScreen =
                        SettingsPage.MAIN

                    currentScreen =
                        Screen.Settings
                }
            )
        }
    ) {

        Scaffold(

            containerColor =
                CardBackground,

            bottomBar = {

                if (
                    shouldShowBottomBar(
                        currentScreen
                    )
                ) {

                    MainBottomBar(

                        currentScreen =
                            currentScreen,

                        onScreenSelected = {

                            currentScreen = it
                        }
                    )
                }
            }

        ) { paddingValues ->

            Surface(

                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(
                            paddingValues
                        ),

                color =
                    CardBackground

            ) {

                Box(
                    modifier =
                        Modifier.fillMaxSize()
                ) {

                    Column(
                        modifier =
                            Modifier.fillMaxSize()
                    ) {

                        if (showMainHeader) {

                            AppHeader(

                                title =
                                    headerTitle,

                                onMenuClick = {

                                    scope.launch {

                                        if (
                                            drawerState.isClosed
                                        ) {
                                            drawerState.open()
                                        } else {
                                            drawerState.close()
                                        }
                                    }
                                },

                                onSyncClick = {

                                    scope.launch {
                                        SyncPopupManager.run(
                                            context
                                        )
                                    }
                                }
                            )
                        }

                        Box(

                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .weight(1f)

                        ) {

                            AppScreenContent(

                                currentScreen =
                                    currentScreen,

                                selectedDate =
                                    selectedDate,

                                dailyPlanDao =
                                    database.dailyPlanDao(),

                                selectedCategory =
                                    selectedCategory,

                                selectedCategoryId =
                                    selectedCategoryId,

                                settingsScreen =
                                    settingsScreen,

                                themeManager =
                                    themeManager,

                                settingsViewModel =
                                    settingsViewModel,

                                allowPastPlanning =
                                    allowPastPlanning,

                                phoneUsageViewModel =
                                    phoneUsageViewModel,

                                planningViewModel =
                                    planningViewModel,

                                statisticsViewModel =
                                    statisticsViewModel,

                                overviewMode =
                                    overviewMode,

                                overviewWeekOffset =
                                    overviewWeekOffset,

                                onOverviewModeChange = { mode ->

                                    overviewModeName =
                                        mode.name
                                },

                                onOverviewWeekOffsetChange = {
                                        offset ->

                                    overviewWeekOffset =
                                        offset
                                },

                                openNutritionFromNotification =
                                    openNutritionFromNotification,

                                notificationMealType =
                                    notificationMealType,

                                onNutritionSheetHandled = {

                                    openNutritionFromNotification =
                                        false

                                    notificationMealType =
                                        null
                                },

                                onScreenChange = {

                                    currentScreen = it
                                },

                                onDateChange = {

                                    selectedDate = it
                                },

                                onCategoryChange = { id, name ->

                                    selectedCategoryId =
                                        id

                                    selectedCategory =
                                        name
                                },

                                onSettingsPageChange = {

                                    settingsScreen = it
                                },

                                onLogout = {

                                    authViewModel.signOut()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // --------------------------------------------------------
    // EXIT DIALOG
    // --------------------------------------------------------

    if (showExitDialog) {

        AlertDialog(

            onDismissRequest = {
                showExitDialog = false
            },

            title = {
                Text("خروج از برنامه")
            },

            text = {
                Text(
                    "آیا می‌خواهید از برنامه خارج شوید؟"
                )
            },

            confirmButton = {

                TextButton(

                    onClick = {

                        showExitDialog = false

                        (
                                context
                                        as? android.app.Activity
                                )?.finish()
                    }
                ) {
                    Text("خروج")
                }
            },

            dismissButton = {

                TextButton(

                    onClick = {
                        showExitDialog = false
                    }
                ) {
                    Text("انصراف")
                }
            }
        )
    }
}


// ============================================================
// SCREEN CONTENT
// ============================================================

@Composable
private fun AppScreenContent(

    currentScreen: Screen,

    selectedDate: String,

    dailyPlanDao:
    com.example.yekdarsad.data.DailyPlanDao,

    selectedCategory: String,

    selectedCategoryId: Int,

    settingsScreen: SettingsPage,

    themeManager: ThemeManager,

    settingsViewModel:
    SettingsViewModel,

    allowPastPlanning:
    Boolean,

    phoneUsageViewModel:
    PhoneUsageViewModel,

    planningViewModel:
    PlanningViewModel,

    statisticsViewModel:
    StatisticsViewModel,

    overviewMode:
    OverviewMode,

    overviewWeekOffset:
    Int,

    onOverviewModeChange:
        (OverviewMode) -> Unit,

    onOverviewWeekOffsetChange:
        (Int) -> Unit,

    openNutritionFromNotification:
    Boolean,

    notificationMealType:
    MealType?,

    onNutritionSheetHandled:
        () -> Unit,

    onScreenChange:
        (Screen) -> Unit,

    onDateChange:
        (String) -> Unit,

    onCategoryChange:
        (Int, String) -> Unit,

    onSettingsPageChange:
        (SettingsPage) -> Unit,

    onLogout:
        () -> Unit

) {

    when (currentScreen) {

        // ====================================================
        // HOME
        // ====================================================

        Screen.Home -> {

            HomeScreen(
                onTodayClick = { onScreenChange(Screen.Today) },
                onStatisticsClick = { onScreenChange(Screen.Statistics) },
                onOverviewClick = { onScreenChange(Screen.Overview) },
                onActivitiesClick = { onScreenChange(Screen.Activities) }
            )
        }

        // ====================================================
        // OVERVIEW
        // ====================================================

        Screen.Overview -> {

            OverviewScreen(

                overviewMode =
                    overviewMode,

                weekOffset =
                    overviewWeekOffset,

                onModeChange = { mode ->

                    onOverviewModeChange(
                        mode
                    )
                },

                onWeekOffsetChange = { offset ->

                    onOverviewWeekOffsetChange(
                        offset
                    )
                },

                onDateSelected = { date ->

                    onDateChange(
                        date.toString()
                    )

                    onScreenChange(
                        Screen.Today
                    )
                }
            )
        }

        // ====================================================
        // TODAY
        // ====================================================

        Screen.Today -> {

            key(selectedDate) {

                TodayScreen(

                    initialDate =
                        LocalDate.parse(
                            selectedDate
                        ),

                    onPhoneClick = { date ->

                        onDateChange(date)

                        onScreenChange(
                            Screen.PhoneUsage
                        )
                    },

                    onNutritionClick = { date ->

                        onDateChange(date)

                        onScreenChange(
                            Screen.Nutrition
                        )
                    },

                    onSleepClick = { date ->

                        onDateChange(date)

                        onScreenChange(
                            Screen.Sleep
                        )
                    },

                    onExpenseClick = { date ->

                        onDateChange(date)

                        onScreenChange(
                            Screen.Expense
                        )
                    }
                )
            }
        }

        // ====================================================
        // NUTRITION
        // ====================================================

        Screen.Nutrition -> {

            BackHandler {

                onScreenChange(
                    Screen.Today
                )
            }

            NutritionScreen(

                date =
                    selectedDate,

                openAddFoodSheet =
                    openNutritionFromNotification,

                initialMealType =
                    notificationMealType
                        ?: MealType.LUNCH,

                onAddFoodSheetHandled = {

                    onNutritionSheetHandled()
                }
            )
        }

        // ====================================================
        // SLEEP
        // ====================================================

        Screen.Sleep -> {

            BackHandler {

                onScreenChange(
                    Screen.Today
                )
            }

            SleepScreen(
                date =
                    selectedDate
            )
        }

        // ====================================================
        // EXPENSE
        // ====================================================

        Screen.Expense -> {

            BackHandler {

                onScreenChange(
                    Screen.Today
                )
            }

            ExpensesScreen(

                date =
                    selectedDate,

                onBack = {

                    onScreenChange(
                        Screen.Today
                    )
                }
            )
        }

        // ====================================================
        // STATISTICS
        // ====================================================

        Screen.Statistics -> {

            com.example.yekdarsad.ui.statistics.StatisticsScreen(

                statisticsViewModel =
                    statisticsViewModel
            )
        }

        // ====================================================
        // ACTIVITIES
        // ====================================================

        Screen.Activities -> {

            ActivitiesScreen(

                planningViewModel =
                    planningViewModel,

                allowPastPlanning =
                    allowPastPlanning,

                onCategoryClick = {
                        id,
                        name,
                        date ->

                    onCategoryChange(
                        id,
                        name
                    )

                    onDateChange(
                        date
                    )

                    onScreenChange(
                        Screen.CategoryDetail
                    )
                },

                onRoutinePlanningClick = {

                    onScreenChange(
                        Screen.RoutinePlanning
                    )
                }
            )
        }

        // ====================================================
        // ROUTINE PLANNING
        // ====================================================

        Screen.RoutinePlanning -> {

            BackHandler {

                onScreenChange(
                    Screen.Activities
                )
            }

            RoutinePlanningScreen(
                onBack = {

                    onScreenChange(
                        Screen.Activities
                    )
                }
            )
        }

        // ====================================================
        // CATEGORY DETAIL
        // ====================================================

        Screen.CategoryDetail -> {

            BackHandler {

                onScreenChange(
                    Screen.Activities
                )
            }

            CategoryDetailScreen(

                categoryId =
                    selectedCategoryId,

                categoryName =
                    selectedCategory,

                selectedDate =
                    selectedDate,

                planningViewModel =
                    planningViewModel
            )
        }

        // ====================================================
        // PHONE USAGE
        // ====================================================

        Screen.PhoneUsage -> {

            BackHandler {

                onScreenChange(
                    Screen.Today
                )
            }

            PhoneUsageScreen(

                phoneUsageViewModel =
                    phoneUsageViewModel,

                date =
                    selectedDate
            )
        }

        // ====================================================
        // SETTINGS
        // ====================================================

        Screen.Settings -> {

            SettingsContent(

                settingsScreen =
                    settingsScreen,

                themeManager =
                    themeManager,

                settingsViewModel =
                    settingsViewModel,

                onSettingsPageChange =
                    onSettingsPageChange,

                onBackToHome = {

                    onScreenChange(
                        Screen.Home
                    )
                },

                onLogout =
                    onLogout
            )
        }
    }
}


// ============================================================
// SETTINGS
// ============================================================

@Composable
private fun SettingsContent(

    settingsScreen: SettingsPage,

    themeManager: ThemeManager,

    settingsViewModel:
    SettingsViewModel,

    onSettingsPageChange:
        (SettingsPage) -> Unit,

    onBackToHome:
        () -> Unit,

    onLogout:
        () -> Unit

) {

    when (settingsScreen) {

        SettingsPage.MAIN -> {

            BackHandler {
                onBackToHome()
            }

            SettingsScreen(

                themeManager =
                    themeManager,

                settingsViewModel =
                    settingsViewModel,

                onAppearanceClick = {

                    onSettingsPageChange(
                        SettingsPage.APPEARANCE
                    )
                },

                onBack =
                    onBackToHome,

                onLogout =
                    onLogout
            )
        }

        SettingsPage.APPEARANCE -> {

            BackHandler {

                onSettingsPageChange(
                    SettingsPage.MAIN
                )
            }

            AppearanceSettingsScreen(

                themeManager =
                    themeManager,

                onBack = {

                    onSettingsPageChange(
                        SettingsPage.MAIN
                    )
                }
            )
        }
    }
}


// ============================================================
// BOTTOM BAR VISIBILITY
// ============================================================

private fun shouldShowBottomBar(
    screen: Screen
): Boolean {

    return when (screen) {

        Screen.Home,
        Screen.Overview,
        Screen.Today,
        Screen.Statistics,
        Screen.Activities -> true

        Screen.PhoneUsage,
        Screen.Nutrition,
        Screen.Sleep,
        Screen.CategoryDetail,
        Screen.Expense,
        Screen.Settings,
        Screen.RoutinePlanning -> false
    }
}


// ============================================================
// BOTTOM BAR
// ============================================================

@Composable
private fun MainBottomBar(

    currentScreen: Screen,

    onScreenSelected:
        (Screen) -> Unit

) {

    Surface(

        modifier =
            Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(
                    horizontal = 14.dp,
                    vertical = 8.dp
                )
                .height(64.dp),

        shape =
            RoundedCornerShape(24.dp),

        color =
            CardBackground,

        shadowElevation =
            4.dp
    ) {

        Row(

            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = 6.dp
                    ),

            horizontalArrangement =
                Arrangement.SpaceAround,

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            BottomItem(
                selected =
                    currentScreen ==
                            Screen.Home,

                icon =
                    Icons.Default.Home,

                title =
                    "خانه",

                onClick = {

                    onScreenSelected(
                        Screen.Home
                    )
                }
            )

            BottomItem(
                selected =
                    currentScreen ==
                            Screen.Statistics,

                icon =
                    Icons.Default.BarChart,

                title =
                    "آمار",

                onClick = {

                    onScreenSelected(
                        Screen.Statistics
                    )
                }
            )

            BottomItem(
                selected =
                    currentScreen ==
                            Screen.Today,

                icon =
                    Icons.Default.Today,

                title =
                    "امروز",

                onClick = {

                    onScreenSelected(
                        Screen.Today
                    )
                }
            )

            BottomItem(
                selected =
                    currentScreen ==
                            Screen.Overview,

                icon =
                    Icons.Default.Dashboard,

                title =
                    "در یک نگاه",

                onClick = {

                    onScreenSelected(
                        Screen.Overview
                    )
                }
            )

            BottomItem(
                selected =
                    currentScreen ==
                            Screen.Activities,

                icon =
                    Icons.Default.List,

                title =
                    "برنامه ریزی",

                onClick = {

                    onScreenSelected(
                        Screen.Activities
                    )
                }
            )
        }
    }
}


// ============================================================
// BOTTOM ITEM
// ============================================================

@Composable
private fun BottomItem(

    selected: Boolean,

    icon: ImageVector,

    title: String,

    onClick: () -> Unit

) {

    Column(

        modifier =
            Modifier
                .width(62.dp)
                .fillMaxHeight()
                .clickable {
                    onClick()
                },

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Center
    ) {

        Box(

            modifier =
                Modifier
                    .size(
                        if (selected) {
                            34.dp
                        } else {
                            30.dp
                        }
                    )
                    .background(

                        color =
                            if (selected) {
                                PrimaryGreenLight
                            } else {
                                Color.Transparent
                            },

                        shape =
                            RoundedCornerShape(50)
                    ),

            contentAlignment =
                Alignment.Center
        ) {

            Icon(

                imageVector =
                    icon,

                contentDescription =
                    null,

                modifier =
                    Modifier.size(20.dp),

                tint =
                    if (selected) {
                        PrimaryGreen
                    } else {
                        TextSecondary
                    }
            )
        }

        Spacer(
            modifier =
                Modifier.height(3.dp)
        )

        Text(

            text =
                title,

            style =
                MaterialTheme.typography.labelSmall,

            color =
                if (selected) {
                    PrimaryGreen
                } else {
                    TextSecondary
                }
        )
    }
}