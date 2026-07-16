package com.example.yekdarsad

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.yekdarsad.data.Category
import com.example.yekdarsad.ui.components.PlanningPreviewCard
import java.time.LocalDate


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ActivitiesContent(

    categories: List<Category>,

    selectedDate: LocalDate,

    showDateMenu: Boolean,

    onShowDateMenuChange: (Boolean) -> Unit,

    onTodayClick: () -> Unit,

    onTomorrowClick: () -> Unit,

    onCalendarClick: () -> Unit,

    onAddClick: () -> Unit,

    onCategoryClick: (Category) -> Unit,

    onCategoryLongClick: (Category) -> Unit

) {


    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)

    ) {


        Text(

            text = "برنامه‌ریزی",

            style = MaterialTheme.typography.headlineMedium

        )


        Spacer(
            modifier = Modifier.height(18.dp)
        )


        PlanningPreviewCard(
            totalMinutes = 0
        )


        Spacer(
            modifier = Modifier.height(20.dp)
        )


        OutlinedCard(

            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = {
                        onShowDateMenuChange(true)
                    }
                )

        ) {


            Column(

                modifier = Modifier.padding(16.dp)

            ) {


                Text(
                    text = "📅 برنامه‌ریزی برای"
                )


                Spacer(
                    modifier = Modifier.height(6.dp)
                )


                Text(
                    text = getPersianDate(selectedDate),
                    style = MaterialTheme.typography.titleMedium
                )

            }

        }



        DropdownMenu(

            expanded = showDateMenu,

            onDismissRequest = {

                onShowDateMenuChange(false)

            }

        ) {


            DropdownMenuItem(

                text = {
                    Text("امروز")
                },

                onClick = {

                    onTodayClick()

                }

            )


            DropdownMenuItem(

                text = {
                    Text("فردا")
                },

                onClick = {

                    onTomorrowClick()

                }

            )


            DropdownMenuItem(

                text = {
                    Text("📅 انتخاب تاریخ دیگر")
                },

                onClick = {

                    onCalendarClick()

                }

            )


        }



        Spacer(
            modifier = Modifier.height(16.dp)
        )



        Row(

            modifier = Modifier.fillMaxWidth(),

            horizontalArrangement = Arrangement.End

        ) {


            FloatingActionButton(

                onClick = onAddClick,

                containerColor = Color(0xFF7A9A6D),

                contentColor = Color.White

            ) {


                Icon(

                    imageVector = Icons.Default.Add,

                    contentDescription = null

                )

            }

        }



        Spacer(
            modifier = Modifier.height(16.dp)
        )



        LazyColumn(

            verticalArrangement = Arrangement.spacedBy(10.dp)

        ) {


            items(categories) { category ->


                ElevatedCard(

                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(

                            onClick = {

                                onCategoryClick(category)

                            },

                            onLongClick = {

                                onCategoryLongClick(category)

                            }

                        )

                ) {


                    Row(

                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),

                        verticalAlignment = Alignment.CenterVertically

                    ) {


                        Text(

                            text = "${category.icon}  ${category.name}",

                            style = MaterialTheme.typography.titleMedium

                        )


                    }


                }


            }


        }


    }

}