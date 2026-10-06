package com.example.yekdarsad.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun PriorityDivider(

    modifier: Modifier = Modifier,

    dragModifier: Modifier = Modifier

) {


    Row(

        modifier = modifier
            .fillMaxWidth()
            .height(32.dp),

        verticalAlignment = Alignment.CenterVertically

    ) {


        Box(

            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(
                    Color(0xFF8D9780),
                    RoundedCornerShape(10.dp)
                )

        )



        Text(

            text = "وظایف با اولویت بیشتر را به بالای این خط ببرید",

            color = Color(0xFF6F7868),

            fontSize = 9.sp,

            textAlign = TextAlign.Center,

            maxLines = 1

        )



        Box(

            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(
                    Color(0xFF8D9780),
                    RoundedCornerShape(10.dp)
                )

        )


    }


}