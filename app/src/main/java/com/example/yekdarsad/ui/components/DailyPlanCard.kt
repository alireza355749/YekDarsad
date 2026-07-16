package com.example.yekdarsad.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.yekdarsad.data.DailyPlanWithTask


@Composable
fun DailyPlanCard(
    item: DailyPlanWithTask,
    onCheckedChange: (Boolean) -> Unit,
    onDeleteClick: () -> Unit,
    onProgressSave: (Int) -> Unit,
    onTimeClick: () -> Unit
) {


    Card(

        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),

        shape = RoundedCornerShape(14.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFFDF5)
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )

    ) {


        Row(

            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 10.dp,
                    vertical = 2.dp
                ),

            horizontalArrangement = Arrangement.SpaceBetween,

            verticalAlignment = Alignment.CenterVertically

        ) {



            Column(

                modifier = Modifier
                    .weight(1f)

            ) {


                Text(

                    text = item.task.title,

                    style = MaterialTheme.typography.bodyLarge

                )



                if (item.task.type == "TIME") {


                    Text(

                        text = "${item.dailyPlan.actualMinutes}/${item.dailyPlan.plannedMinutes} دقیقه",

                        style = MaterialTheme.typography.bodySmall,

                        color = Color.Gray

                    )


                }


            }



            Row(

                verticalAlignment = Alignment.CenterVertically

            ) {


                Checkbox(

                    checked = item.dailyPlan.completed,

                    onCheckedChange = {

                        if (item.task.type == "TIME") {

                            onTimeClick()

                        } else {

                            onCheckedChange(it)

                        }

                    }

                )



                IconButton(

                    onClick = onDeleteClick,

                    modifier = Modifier.padding(0.dp)

                ) {


                    Icon(

                        imageVector = Icons.Default.Delete,

                        contentDescription = null,

                        tint = Color.Gray

                    )


                }


            }



        }


    }


}