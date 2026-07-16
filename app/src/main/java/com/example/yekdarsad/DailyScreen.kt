package com.example.yekdarsad

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yekdarsad.data.DatabaseProvider
import com.example.yekdarsad.viewmodel.DailyPlanViewModel
import com.example.yekdarsad.viewmodel.DailyPlanViewModelFactory
import java.time.LocalDate


@Composable
fun DailyScreen() {


    val context = LocalContext.current

    val database = DatabaseProvider.getDatabase(context)


    val viewModel: DailyPlanViewModel = viewModel(
        factory = DailyPlanViewModelFactory(
            database.dailyPlanDao(),
            database.taskDao()
        )
    )


    val today = LocalDate.now().toString()


    val plans by viewModel
        .getPlans(today)
        .collectAsState(
            initial = emptyList()
        )



    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)

    ) {


        Text(

            text = "برنامه امروز",

            style = MaterialTheme.typography.headlineMedium

        )


        Spacer(

            modifier = Modifier.height(20.dp)

        )



        if (plans.isEmpty()) {


            Text(
                text = "برنامه‌ای برای امروز ثبت نشده"
            )


        } else {


            LazyColumn {


                items(plans) { item ->


                    Card(

                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)

                    ) {


                        Row(

                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp),

                            horizontalArrangement = Arrangement.SpaceBetween

                        ) {


                            Column {


                                Text(

                                    text = item.task.title,

                                    style = MaterialTheme.typography.titleMedium

                                )


                                Spacer(

                                    modifier = Modifier.height(5.dp)

                                )


                                Text(

                                    text = "${item.dailyPlan.plannedMinutes} دقیقه"

                                )


                            }



                            Row {


                                Checkbox(

                                    checked = item.dailyPlan.completed,

                                    onCheckedChange = {

                                        viewModel.toggleCompleted(

                                            item.dailyPlan.id,

                                            it

                                        )

                                    }

                                )



                                IconButton(

                                    onClick = {

                                        viewModel.deletePlan(

                                            item.dailyPlan.id

                                        )

                                    }

                                ) {


                                    Icon(

                                        imageVector = Icons.Default.Delete,

                                        contentDescription = "Delete"

                                    )


                                }


                            }


                        }


                    }


                }


            }


        }


    }


}