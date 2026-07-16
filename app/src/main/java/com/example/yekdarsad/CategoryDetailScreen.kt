package com.example.yekdarsad

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yekdarsad.data.DatabaseProvider
import com.example.yekdarsad.data.TaskWithDurations
import com.example.yekdarsad.viewmodel.CategoryDetailViewModel
import com.example.yekdarsad.viewmodel.CategoryDetailViewModelFactory
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow


fun getCategoryColor(categoryName: String): Color {

    return when(categoryName) {

        "زبان" ->
            Color(0xFF4A90E2)

        "مهارت‌های شغلی" ->
            Color(0xFF7E57C2)

        "مطالعه" ->
            Color(0xFFFF9800)

        "ورزش" ->
            Color(0xFF43A047)

        "نماز" ->
            Color(0xFF009688)

        "شغل" ->
            Color(0xFF546E7A)

        "متفرقه" ->
            Color(0xFF78909C)

        else ->
            Color(0xFF78909C)

    }

}



@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
fun CategoryDetailScreen(
    categoryId: Int,
    categoryName: String,
    selectedDate: String
) {


    val context = LocalContext.current

    val database = DatabaseProvider.getDatabase(context)


    val viewModel: CategoryDetailViewModel = viewModel(

        key = "category_$categoryId",

        factory = CategoryDetailViewModelFactory(

            database.taskDao(),
            database.activityDurationDao(),
            database.dailyPlanDao(),
            categoryId

        )

    )


    val tasks by viewModel.tasks.collectAsState()


    val categoryColor = getCategoryColor(categoryName)



    var showDialog by remember {

        mutableStateOf(false)

    }


    var title by remember {

        mutableStateOf("")

    }


    var coefficient by remember {

        mutableStateOf("")

    }


    var selectedTask by remember {

        mutableStateOf<TaskWithDurations?>(null)

    }


    var selectedMinutes by remember {

        mutableStateOf<Int?>(null)

    }


    var showBottomSheet by remember {

        mutableStateOf(false)

    }



    Column(

        modifier = Modifier

            .fillMaxSize()

            .padding(20.dp)

    ) {


        Text(

            text = categoryName,

            style = MaterialTheme.typography.headlineMedium

        )


        Spacer(

            modifier = Modifier.height(20.dp)

        )



        Button(

            onClick = {

                showDialog = true

            },


            colors = ButtonDefaults.buttonColors(

                containerColor = categoryColor,

                contentColor = Color.White

            ),


            shape = RoundedCornerShape(16.dp)

        ) {


            Text(
                "افزودن فعالیت"
            )


        }



        Spacer(

            modifier = Modifier.height(20.dp)

        )



        LazyColumn {


            items(tasks) { item ->



                Card(

                    modifier = Modifier

                        .fillMaxWidth()

                        .padding(vertical = 8.dp)

                        .clickable {


                            if (item.task.type == "TIME") {


                                selectedTask = item

                                selectedMinutes = null

                                showBottomSheet = true



                            } else {



                                viewModel.addPlan(

                                    taskId = item.task.id,

                                    date = selectedDate,

                                    minutes = 0

                                )



                                Toast.makeText(

                                    context,

                                    "${item.task.title} به برنامه امروز اضافه شد",

                                    Toast.LENGTH_SHORT

                                ).show()



                            }



                        },


                    shape = RoundedCornerShape(22.dp)

                ) {



                    Row(

                        modifier = Modifier

                            .fillMaxWidth()

                            .background(

                                Brush.horizontalGradient(

                                    listOf(

                                        categoryColor.copy(
                                            alpha = 0.35f
                                        ),

                                        Color.White

                                    )

                                )

                            )

                            .padding(18.dp),


                        horizontalArrangement = Arrangement.SpaceBetween,

                        verticalAlignment = Alignment.CenterVertically

                    ) {


                        Column {


                            Text(

                                text = item.task.title,

                                style = MaterialTheme.typography.titleMedium

                            )


                            Spacer(

                                modifier = Modifier.height(4.dp)

                            )


                            Text(

                                text = "ضریب ${item.task.coefficient}",

                                color = Color.Gray

                            )


                        }



                        Icon(

                            imageVector = Icons.Default.KeyboardArrowRight,

                            contentDescription = null

                        )



                    }


                }


            }


        }


    }

    if (showBottomSheet && selectedTask != null) {


        ModalBottomSheet(

            onDismissRequest = {

                showBottomSheet = false

            }

        ) {



            Column(

                modifier = Modifier.padding(20.dp)

            ) {



                Text(

                    text = selectedTask!!.task.title,

                    style = MaterialTheme.typography.headlineSmall

                )



                Spacer(

                    modifier = Modifier.height(20.dp)

                )



                Text("مدت زمان")



                Spacer(

                    modifier = Modifier.height(12.dp)

                )



                FlowRow(

                    horizontalArrangement = Arrangement.spacedBy(10.dp),

                    verticalArrangement = Arrangement.spacedBy(10.dp)

                ) {



                    selectedTask!!.durations.forEach { duration ->



                        FilterChip(



                            selected = selectedMinutes == duration.minutes,



                            onClick = {

                                selectedMinutes = duration.minutes

                            },



                            label = {

                                Text("${duration.minutes} دقیقه")

                            }



                        )



                    }



                }



                Spacer(

                    modifier = Modifier.height(25.dp)

                )



                Button(



                    modifier = Modifier.fillMaxWidth(),



                    enabled = selectedMinutes != null,



                    onClick = {



                        viewModel.addPlan(



                            taskId = selectedTask!!.task.id,



                            date = selectedDate,



                            minutes = selectedMinutes!!



                        )



                        showBottomSheet = false



                    }



                ) {



                    Text("➕ افزودن به برنامه")



                }



            }



        }



    }





    if (showDialog) {

        AlertDialog(

            onDismissRequest = {
                showDialog = false
            },

            title = {
                Text("فعالیت جدید")
            },

            text = {

                Column {

                    TextField(
                        value = title,
                        onValueChange = {
                            title = it
                        },
                        label = {
                            Text("نام فعالیت")
                        }
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    TextField(
                        value = coefficient,
                        onValueChange = {
                            coefficient = it
                        },
                        label = {
                            Text("ضریب")
                        }
                    )

                }

            },

            confirmButton = {

                Button(

                    onClick = {

                        val coef = coefficient.toDoubleOrNull()

                        if (coef != null && title.isNotBlank()) {

                            viewModel.addTask(
                                title,
                                coef,
                                "TIME"
                            )

                            title = ""
                            coefficient = ""
                            showDialog = false

                        }

                    }

                ) {

                    Text("ذخیره")

                }

            },

            dismissButton = {

                TextButton(

                    onClick = {
                        showDialog = false
                    }

                ) {

                    Text("لغو")

                }

            }

        )

    }

}