package com.example.yekdarsad.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.yekdarsad.data.DailyPlanWithTask

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayPreviewBottomSheet(

    plans: List<DailyPlanWithTask>,

    onDismiss: () -> Unit,

    onOpenToday: () -> Unit

) {

    ModalBottomSheet(

        onDismissRequest = onDismiss

    ) {

        Column(

            modifier = Modifier.padding(20.dp)

        ) {

            Text(

                "برنامه امروز",

                style = MaterialTheme.typography.headlineSmall

            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Text(

                "${plans.size} فعالیت",

                style = MaterialTheme.typography.bodyMedium

            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            LazyColumn(

                modifier = Modifier.weight(
                    1f,
                    fill = false
                )

            ) {

                items(plans) {

                    TodayPreviewItem(it)

                }

            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Button(

                modifier = Modifier.fillMaxWidth(),

                onClick = onOpenToday

            ) {

                Text("رفتن به صفحه امروز")

            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

        }

    }

}