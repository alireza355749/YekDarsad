package com.example.yekdarsad.ui.overview

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yekdarsad.ui.theme.PrimaryGreen
import com.example.yekdarsad.ui.theme.TextSecondary
import com.example.yekdarsad.ui.theme.categoryColors

@Composable
fun OverviewSummaryPopup(
    title: String,
    totals: List<OverviewCategoryTime>,
    onClose: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth(0.90f)
            .clip(
                RoundedCornerShape(15.dp)
            )
            .background(
                MaterialTheme.colorScheme.surface
            )
            .border(
                width = 0.8.dp,
                color = PrimaryGreen.copy(alpha = 0.25f),
                shape = RoundedCornerShape(15.dp)
            )
            .padding(11.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "مجموع زمان هر دسته‌بندی",
                    fontSize = 8.sp,
                    color = TextSecondary
                )
            }

            IconButton(
                onClick = onClose,
                modifier = Modifier.size(28.dp)
            ) {

                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "بستن",
                    modifier = Modifier.size(16.dp),
                    tint = TextSecondary
                )
            }
        }

        Spacer(
            modifier = Modifier.size(7.dp)
        )

        if (totals.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "برای این بازه زمانی فعالیت زمانی ثبت نشده",
                    fontSize = 9.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }

        } else {

            totals.forEachIndexed { index, item ->

                OverviewCategoryTimeRow(
                    item = item
                )

                if (index != totals.lastIndex) {

                    Spacer(
                        modifier = Modifier.size(7.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun OverviewCategoryTimeRow(
    item: OverviewCategoryTime
) {

    val colors = categoryColors(
        item.category.name
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 3.dp,
                vertical = 2.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        /*
         * دایره توپر رنگی
         */
        Box(
            modifier = Modifier
                .size(9.dp)
                .clip(
                    RoundedCornerShape(50.dp)
                )
                .background(
                    colors.accent
                )
        )

        Spacer(
            modifier = Modifier.width(8.dp)
        )

        /*
         * نام دسته‌بندی
         */
        Text(
            text = item.category.name,
            modifier = Modifier.weight(1f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        /*
         * زمان
         */
        Text(
            text = formatOverviewMinutes(
                item.minutes
            ),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = colors.accent
        )
    }
}