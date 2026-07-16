package com.example.yekdarsad

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yekdarsad.data.Category
import com.example.yekdarsad.data.DatabaseProvider
import com.example.yekdarsad.viewmodel.ActivitiesViewModel
import com.example.yekdarsad.viewmodel.ActivitiesViewModelFactory
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ActivitiesScreen(
    onCategoryClick: (Int, String, String) -> Unit
) {

    val context = LocalContext.current

    val database = remember {
        DatabaseProvider.getDatabase(context)
    }

    val activitiesViewModel: ActivitiesViewModel = viewModel(
        factory = ActivitiesViewModelFactory(
            database.categoryDao()
        )
    )

    val categories by activitiesViewModel.categories.collectAsState()

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val scope = rememberCoroutineScope()


    var selectedDate by remember {
        mutableStateOf(LocalDate.now())
    }


    var categoryToDelete by remember {
        mutableStateOf<Category?>(null)
    }


    var showDateMenu by remember {
        mutableStateOf(false)
    }


    var showCalendar by remember {
        mutableStateOf(false)
    }


    var showCategorySheet by remember {
        mutableStateOf(false)
    }


    var showCustomCategoryDialog by remember {
        mutableStateOf(false)
    }


    var customCategoryName by remember {
        mutableStateOf("")
    }



    ActivitiesContent(

        categories = categories,

        selectedDate = selectedDate,

        showDateMenu = showDateMenu,


        onShowDateMenuChange = {

            showDateMenu = it

        },


        onTodayClick = {

            selectedDate = LocalDate.now()
            showDateMenu = false

        },


        onTomorrowClick = {

            selectedDate = LocalDate.now().plusDays(1)
            showDateMenu = false

        },


        onCalendarClick = {

            showDateMenu = false
            showCalendar = true

        },


        onAddClick = {

            showCategorySheet = true

        },


        onCategoryClick = {

                category ->

            onCategoryClick(
                category.id,
                category.name,
                selectedDate.toString()
            )

        },


        onCategoryLongClick = {

            categoryToDelete = it

        }

    )



    ActivitiesBottomSheets(

        categories = categories,

        snackbarHostState = snackbarHostState,

        scope = scope,

        viewModel = activitiesViewModel,


        showCalendar = showCalendar,

        selectedDate = selectedDate,


        onDateSelected = {

            selectedDate = it
            showCalendar = false

        },


        onCalendarDismiss = {

            showCalendar = false

        },


        showCategorySheet = showCategorySheet,


        onCategorySheetDismiss = {

            showCategorySheet = false

        },


        onAddCategory = {

                name,
                icon ->


            if (categories.any { it.name == name }) {


                scope.launch {

                    snackbarHostState.showSnackbar(
                        "این دسته قبلاً ایجاد شده است"
                    )

                }


            } else {


                activitiesViewModel.addCategory(
                    name,
                    icon
                )

                showCategorySheet = false

            }


        },


        onOtherCategoryClick = {

            showCategorySheet = false
            showCustomCategoryDialog = true

        },


        categoryToDelete = categoryToDelete,


        onDeleteDismiss = {

            categoryToDelete = null

        },


        onDeleteConfirm = {


            categoryToDelete?.let {

                activitiesViewModel.deleteCategory(it)

            }


            categoryToDelete = null


        },


        showCustomCategoryDialog = showCustomCategoryDialog,


        customCategoryName = customCategoryName,


        onCustomCategoryNameChange = {

            customCategoryName = it

        },


        onCustomDismiss = {

            showCustomCategoryDialog = false
            customCategoryName = ""

        },


        onCreateCustomCategory = {


            if (customCategoryName.isNotBlank()) {


                activitiesViewModel.addCategory(

                    customCategoryName,

                    "📌"

                )


                customCategoryName = ""

                showCustomCategoryDialog = false


            }


        }

    )

}