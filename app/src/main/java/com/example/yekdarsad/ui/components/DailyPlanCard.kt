package com.example.yekdarsad.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yekdarsad.data.DailyPlanWithTask

@Composable
fun DailyPlanCard(

    item: DailyPlanWithTask,

    number: Int,

    modifier: Modifier = Modifier,

    dragModifier: Modifier = Modifier,

    locked: Boolean = false,

    displayChecked: Boolean? = null,

    // =========================================
    // اولویت فقط نمایشی
    // =========================================

    isPriority: Boolean = false,

    onCheckedChange: (Boolean) -> Unit,

    onTimeClick: () -> Unit,

    onDeleteClick: () -> Unit,

    onProgressSave: (Int) -> Unit

) {

    Card(

        modifier = modifier
            .fillMaxWidth()
            .height(38.dp),

        shape = RoundedCornerShape(10.dp),

        colors = CardDefaults.cardColors(

            containerColor =
                if (locked)
                    Color(0xFFD0D0D0)
                else
                    Color(0xFFDDE8C8)

        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )

    ) {

        Row(

            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),

            verticalAlignment =
                Alignment.CenterVertically

        ) {

            Text(

                text =
                    number.toString(),

                style =
                    MaterialTheme
                        .typography
                        .labelSmall,

                color =
                    if (locked)
                        Color.Gray
                    else
                        Color(0xFF6F7868)

            )

            Spacer(
                modifier = Modifier.width(6.dp)
            )

            // =========================================
            // Checkbox
            // =========================================

            Checkbox(

                checked =
                    displayChecked
                        ?: item.dailyPlan.completed,

                enabled =
                    !locked,

                onCheckedChange = {

                    if (!locked) {
                        onCheckedChange(it)
                    }

                },

                modifier =
                    Modifier.size(30.dp)

            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            // =========================================
            // Task
            // =========================================

            Column(

                modifier =
                    Modifier
                        .weight(1f)
                        .clickable(
                            enabled =
                                !locked &&
                                        item.task.type == "TIME"
                        ) {

                            if (!locked) {
                                onTimeClick()
                            }

                        },

                verticalArrangement =
                    Arrangement.Center

            ) {

                Text(

                    text =
                        item.task.title,

                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,

                    maxLines = 1,

                    overflow =
                        TextOverflow.Clip,

                    color =
                        if (locked)
                            Color.DarkGray
                        else
                            Color(0xFF354D2E)

                )

                if (item.task.type == "TIME") {

                    Text(

                        text =
                            "${item.dailyPlan.actualMinutes}/${item.dailyPlan.plannedMinutes} دقیقه",

                        style =
                            MaterialTheme
                                .typography
                                .labelSmall
                                .copy(
                                    fontSize = 10.sp
                                ),

                        color =
                            if (locked)
                                Color.Gray
                            else
                                Color(0xFF8A8F82)

                    )

                }

            }

            // =========================================
            // پرچم اولویت
            //
            // فقط نمایش داده می‌شود
            // هیچ clickable ندارد
            // =========================================

            if (isPriority) {

                Icon(

                    imageVector =
                        Icons.Default.Flag,

                    contentDescription =
                        "اولویت بالا",

                    modifier =
                        Modifier.size(18.dp),

                    tint =
                        Color(0xFFD67B3C)

                )

                Spacer(
                    modifier =
                        Modifier.width(5.dp)
                )

            }

            // =========================================
            // Drag Icon
            // =========================================

            Icon(

                imageVector =
                    Icons.Default.Menu,

                contentDescription =
                    null,

                modifier =
                    dragModifier
                        .size(20.dp),

                tint =
                    if (locked)
                        Color.Gray
                    else
                        Color(0xFF8D9780)

            )

        }

    }

}