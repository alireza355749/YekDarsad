package com.example.yekdarsad

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yekdarsad.data.Category
import com.example.yekdarsad.data.DatabaseProvider
import com.example.yekdarsad.viewmodel.ActivitiesViewModel
import com.example.yekdarsad.viewmodel.ActivitiesViewModelFactory
import com.example.yekdarsad.viewmodel.PlanningViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ActivitiesScreen(
    planningViewModel: PlanningViewModel,

    // اجازه برنامه‌ریزی برای روزهای گذشته
    allowPastPlanning: Boolean,

    onCategoryClick: (Int, String, String) -> Unit,

    // ورود به صفحه برنامه‌ریزی روتین
    onRoutinePlanningClick: () -> Unit
) {

    val context = LocalContext.current

    val database = remember {
        DatabaseProvider.getDatabase(context)
    }

    val activitiesViewModel: ActivitiesViewModel =
        viewModel(
            factory = ActivitiesViewModelFactory(
                database.categoryDao(),
                database.taskDao(),
                database.activityDurationDao(),
                database.dailyTaskDao(),
                database.dailyPlanDao()
            )
        )

    val categories by activitiesViewModel
        .categories
        .collectAsState()

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val scope = rememberCoroutineScope()

    var categoryToDelete by remember {
        mutableStateOf<Category?>(null)
    }

    var showCalendar by remember {
        mutableStateOf(false)
    }

    var showCustomCategoryDialog by remember {
        mutableStateOf(false)
    }

    var customCategoryName by remember {
        mutableStateOf("")
    }

    val selectedDate =
        planningViewModel.selectedDate

    // =====================================================
    // صفحه اصلی برنامه‌ریزی
    // =====================================================

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        ActivitiesContent(

            categories = categories,

            selectedDate = selectedDate,

            planningViewModel = planningViewModel,

            // مهم:
            // مقدار فعال/غیرفعال بودن برنامه‌ریزی روز گذشته
            allowPastPlanning = allowPastPlanning,

            onCategoryClick = { category ->

                onCategoryClick(
                    category.id,
                    category.name,
                    selectedDate.toString()
                )
            },

            onCategoryLongClick = { category ->

                categoryToDelete = category
            },

            onDateClick = {

                showCalendar = true
            },

            onAddCategory = { name, icon ->

                if (
                    categories.any {
                        it.name == name
                    }
                ) {

                    scope.launch {

                        snackbarHostState.showSnackbar(
                            "این دسته قبلاً ایجاد شده است"
                        )
                    }

                } else {

                    activitiesViewModel.addCategory(
                        name = name,
                        icon = icon
                    ) { newCategory ->

                        onCategoryClick(
                            newCategory.id,
                            newCategory.name,
                            selectedDate.toString()
                        )
                    }
                }
            },

            onOtherCategoryClick = {

                showCustomCategoryDialog = true
            },

            onRoutinePlanningClick = {

                onRoutinePlanningClick()
            }
        )

        // =====================================================
        // BottomSheets / Dialogs
        // =====================================================

        ActivitiesBottomSheets(

            categories = categories,

            snackbarHostState = snackbarHostState,

            scope = scope,

            viewModel = activitiesViewModel,

            showCalendar = showCalendar,

            selectedDate = selectedDate,

            onDateSelected = {

                planningViewModel.selectCustomDate(it)

                showCalendar = false
            },

            onCalendarDismiss = {

                showCalendar = false
            },

            showCategorySheet = false,

            onCategorySheetDismiss = {},

            onAddCategory = { _, _ -> },

            onOtherCategoryClick = {

                showCustomCategoryDialog = true
            },

            categoryToDelete = categoryToDelete,

            onDeleteDismiss = {

                categoryToDelete = null
            },

            onDeleteConfirm = {

                categoryToDelete?.let { category ->

                    activitiesViewModel.deleteCategory(
                        category
                    )
                }

                categoryToDelete = null
            },

            showCustomCategoryDialog =
                showCustomCategoryDialog,

            customCategoryName =
                customCategoryName,

            onCustomCategoryNameChange = {

                customCategoryName = it
            },

            onCustomDismiss = {

                showCustomCategoryDialog = false

                customCategoryName = ""
            },

            onCreateCustomCategory = {

                if (
                    customCategoryName.isNotBlank()
                ) {

                    val name =
                        customCategoryName.trim()

                    activitiesViewModel.addCategory(
                        name = name,
                        icon = "📌"
                    ) { newCategory ->

                        customCategoryName = ""

                        showCustomCategoryDialog = false

                        onCategoryClick(
                            newCategory.id,
                            newCategory.name,
                            selectedDate.toString()
                        )
                    }
                }
            }
        )
    }
}