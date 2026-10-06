package com.example.yekdarsad.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.yekdarsad.ui.theme.*


@Composable
fun AppHeader(
    title: String,
    subtitle: String? = null,
    progress: Int? = null,
    onMenuClick: () -> Unit,
    onNotificationClick: () -> Unit = {},
    onSyncClick: () -> Unit = {}
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 14.dp,
                vertical = 5.dp
            )
            .clip(
                RoundedCornerShape(20.dp)
            )
            .background(
                color = CardBackground.copy(
                    alpha = 0.92f
                )
            )
            .padding(
                horizontal = 14.dp,
                vertical = 7.dp
            )
    ) {

        // =========================
        // عنوان دقیقاً وسط هدر
        // =========================

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),

            horizontalAlignment =
                Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.Center

        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )

            if (subtitle != null) {

                Spacer(
                    modifier = Modifier.height(1.dp)
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // =========================
        // سمت چپ - منو
        // =========================

        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(36.dp)
                .clip(
                    RoundedCornerShape(12.dp)
                )
                .background(
                    PrimaryGreenLight.copy(
                        alpha = 0.75f
                    )
                )
                .clickable {
                    onMenuClick()
                },

            contentAlignment = Alignment.Center

        ) {

            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "منو",
                tint = PrimaryGreen,
                modifier = Modifier.size(20.dp)
            )
        }

        // =========================
        // سمت راست
        // اعلان + همگام‌سازی
        // =========================

        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd),

            verticalAlignment =
                Alignment.CenterVertically

        ) {

            if (progress != null) {

                Column(
                    horizontalAlignment =
                        Alignment.End
                ) {

                    Text(
                        text = "$progress%",
                        style =
                            MaterialTheme.typography.labelMedium,
                        color = PrimaryGreen
                    )

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Box(
                        modifier = Modifier
                            .width(42.dp)
                            .height(4.dp)
                            .clip(
                                RoundedCornerShape(50)
                            )
                            .background(
                                PrimaryGreenLight
                            )
                    ) {

                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(
                                    progress
                                        .coerceIn(0, 100)
                                        .div(100f)
                                )
                                .clip(
                                    RoundedCornerShape(50)
                                )
                                .background(
                                    ProgressColor
                                )
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.width(9.dp)
                )
            }

            // =========================
            // همگام‌سازی + Popup
            // =========================

            Box(
                modifier = Modifier
                    .wrapContentSize()
            ) {

                // دکمه Sync
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(
                            RoundedCornerShape(12.dp)
                        )
                        .background(
                            Color.Transparent
                        )
                        .clickable {
                            onSyncClick()
                        },

                    contentAlignment = Alignment.Center

                ) {

                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = "همگام‌سازی",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Popup دقیقاً کنار دکمه Sync
                SyncStatusPopup(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(
                            x = (-42).dp
                        )
                )
            }

            // =========================
            // اعلان‌ها
            // =========================

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(
                        RoundedCornerShape(12.dp)
                    )
                    .background(
                        Color.Transparent
                    )
                    .clickable {
                        onNotificationClick()
                    },

                contentAlignment = Alignment.Center

            ) {

                Icon(
                    imageVector =
                        Icons.Default.NotificationsNone,

                    contentDescription =
                        "اعلان‌ها",

                    tint = TextSecondary,

                    modifier =
                        Modifier.size(20.dp)
                )
            }
        }
    }
}