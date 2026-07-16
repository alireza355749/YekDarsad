package com.example.yekdarsad

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.yekdarsad.data.Category
import com.example.yekdarsad.ui.components.PersianCalendarDialog
import kotlinx.coroutines.CoroutineScope
import com.example.yekdarsad.viewmodel.ActivitiesViewModel
import java.time.LocalDate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitiesBottomSheets(

    categories: List<Category>,

    snackbarHostState: SnackbarHostState,

    scope: CoroutineScope,

    viewModel: ActivitiesViewModel,


    showCalendar: Boolean,

    selectedDate: LocalDate,

    onDateSelected: (LocalDate) -> Unit,

    onCalendarDismiss: () -> Unit,


    showCategorySheet: Boolean,

    onCategorySheetDismiss: () -> Unit,

    onAddCategory: (String, String) -> Unit,

    onOtherCategoryClick: () -> Unit,


    categoryToDelete: Category?,

    onDeleteDismiss: () -> Unit,

    onDeleteConfirm: () -> Unit,


    showCustomCategoryDialog: Boolean,

    customCategoryName: String,

    onCustomCategoryNameChange: (String) -> Unit,

    onCustomDismiss: () -> Unit,

    onCreateCustomCategory: () -> Unit

) {



    if (showCategorySheet) {


        ModalBottomSheet(

            onDismissRequest = onCategorySheetDismiss,

            containerColor = Color(0xFFFFFCF5)

        ) {


            Column(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)

            ) {


                Text(

                    text = "افزودن دسته‌بندی",

                    style = MaterialTheme.typography.headlineSmall

                )


                Spacer(
                    modifier = Modifier.height(20.dp)
                )


                CategoryItem(
                    "🌍",
                    "زبان"
                ) {

                    onAddCategory(
                        "زبان",
                        "🌍"
                    )

                }



                CategoryItem(
                    "💼",
                    "مهارت‌های شغلی"
                ) {

                    onAddCategory(
                        "مهارت‌های شغلی",
                        "💼"
                    )

                }



                CategoryItem(
                    "📚",
                    "مطالعه"
                ) {

                    onAddCategory(
                        "مطالعه",
                        "📚"
                    )

                }



                CategoryItem(
                    "🏋️",
                    "ورزش"
                ) {

                    onAddCategory(
                        "ورزش",
                        "🏋️"
                    )

                }



                CategoryItem(
                    "💼",
                    "شغل"
                ) {

                    onAddCategory(
                        "شغل",
                        "💼"
                    )

                }



                CategoryItem(
                    "📌",
                    "متفرقه"
                ) {

                    onOtherCategoryClick()

                }


            }


        }


    }




    if (showCalendar) {


        PersianCalendarDialog(

            selectedDate = selectedDate,

            onDateSelected = onDateSelected,

            onDismiss = onCalendarDismiss

        )


    }





    categoryToDelete?.let { category ->


        AlertDialog(

            onDismissRequest = onDeleteDismiss,


            title = {

                Text("حذف دسته")

            },


            text = {

                Text(
                    "دسته «${category.name}» حذف شود؟"
                )

            },


            confirmButton = {

                TextButton(

                    onClick = onDeleteConfirm

                ) {

                    Text("حذف")

                }

            },


            dismissButton = {

                TextButton(

                    onClick = onDeleteDismiss

                ) {

                    Text("انصراف")

                }

            }


        )


    }




    if (showCustomCategoryDialog) {


        AlertDialog(

            onDismissRequest = onCustomDismiss,


            title = {

                Text("نام دسته جدید")

            },


            text = {


                OutlinedTextField(

                    value = customCategoryName,

                    onValueChange = onCustomCategoryNameChange,

                    label = {

                        Text("نام دسته")

                    },

                    singleLine = true

                )


            },


            confirmButton = {


                TextButton(

                    onClick = onCreateCustomCategory

                ) {

                    Text("ایجاد")

                }


            },


            dismissButton = {


                TextButton(

                    onClick = onCustomDismiss

                ) {

                    Text("لغو")

                }


            }


        )


    }


}



@Composable
private fun CategoryItem(

    icon: String,

    title: String,

    onClick: () -> Unit

) {


    ElevatedCard(

        onClick = onClick,

        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),

        colors = CardDefaults.elevatedCardColors(

            containerColor = Color(0xFFFFFCF5)

        )

    ) {


        Row(

            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)

        ) {


            Text(
                text = icon
            )


            Spacer(
                modifier = Modifier.width(14.dp)
            )


            Column {


                Text(
                    text = title
                )


                Text(
                    text = "ایجاد برنامه جدید",
                    style = MaterialTheme.typography.bodySmall
                )


            }


        }


    }


}