package com.example.yekdarsad.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    value: String,
    title: String,
    icon: ImageVector,
    color: Color
) {

    Card(
        modifier = modifier
            .height(66.dp),

        shape = RoundedCornerShape(18.dp),

        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.15f)
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )

    ) {

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(
                    vertical = 5.dp,
                    horizontal = 6.dp
                ),

            horizontalAlignment =
                Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.Center

        ) {

            Icon(

                imageVector = icon,

                contentDescription = null,

                tint = color,

                modifier = Modifier.size(17.dp)

            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(

                text = value,

                style = MaterialTheme.typography.titleSmall,

                textAlign = TextAlign.Center,

                maxLines = 1

            )

            Spacer(
                modifier = Modifier.height(1.dp)
            )

            Text(

                text = title,

                style = MaterialTheme.typography.labelSmall,

                color = Color.Gray,

                textAlign = TextAlign.Center,

                maxLines = 1

            )

        }

    }
}