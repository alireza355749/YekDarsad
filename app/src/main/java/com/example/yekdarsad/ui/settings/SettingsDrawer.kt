package com.example.yekdarsad.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.yekdarsad.ui.theme.CardBackground
import com.example.yekdarsad.ui.theme.PrimaryGreen
import com.example.yekdarsad.ui.theme.PrimaryGreenLight
import com.example.yekdarsad.ui.theme.TextPrimary
import com.example.yekdarsad.ui.theme.TextSecondary


@Composable
fun SettingsDrawer(
    onClose: () -> Unit,
    onSettingsClick: () -> Unit
) {

    ModalDrawerSheet(

        modifier = Modifier
            .fillMaxHeight()
            .width(310.dp),

        drawerContainerColor = CardBackground,

        drawerShape = RoundedCornerShape(
            topStart = 28.dp,
            bottomStart = 28.dp
        )

    ) {

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)

        ) {

            Row(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 12.dp,
                        bottom = 24.dp
                    ),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically

            ) {

                Column {

                    Text(

                        text = "یک درصد",

                        style =
                            MaterialTheme.typography.headlineSmall,

                        color =
                            TextPrimary

                    )

                    Spacer(
                        modifier =
                            Modifier.height(3.dp)
                    )

                    Text(

                        text = "تنظیمات برنامه",

                        style =
                            MaterialTheme.typography.bodySmall,

                        color =
                            TextSecondary

                    )

                }


                Box(

                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            onClose()
                        },

                    contentAlignment =
                        Alignment.Center

                ) {

                    Icon(

                        imageVector =
                            Icons.Default.Close,

                        contentDescription =
                            "بستن",

                        tint =
                            PrimaryGreen,

                        modifier =
                            Modifier.size(21.dp)

                    )

                }

            }


            DrawerSectionTitle(
                title = "تنظیمات"
            )


            DrawerItem(

                icon =
                    Icons.Default.Settings,

                title =
                    "تنظیمات عمومی"

            ) {

                onSettingsClick()

            }


            DrawerItem(

                icon =
                    Icons.Default.Palette,

                title =
                    "ظاهر و رنگ"

            ) {

                onSettingsClick()

            }


            DrawerItem(

                icon =
                    Icons.Default.Notifications,

                title =
                    "اعلان‌ها"

            ) {

                onSettingsClick()

            }


            DrawerItem(

                icon =
                    Icons.Default.Restaurant,

                title =
                    "تغذیه"

            ) {

                onSettingsClick()

            }


            DrawerItem(

                icon =
                    Icons.Default.Security,

                title =
                    "دسترسی‌ها"

            ) {

                onSettingsClick()

            }


            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )


            DrawerSectionTitle(
                title = "برنامه"
            )


            DrawerItem(

                icon =
                    Icons.Default.Info,

                title =
                    "درباره یک درصد"

            ) {

                onSettingsClick()

            }


            Spacer(
                modifier =
                    Modifier.weight(1f)
            )


            Text(

                text = "YekDarsad",

                style =
                    MaterialTheme.typography.labelSmall,

                color =
                    TextSecondary,

                modifier =
                    Modifier.align(
                        Alignment.CenterHorizontally
                    )

            )

        }

    }

}


@Composable
private fun DrawerSectionTitle(
    title: String
) {

    Text(

        text = title,

        style =
            MaterialTheme.typography.labelMedium,

        color =
            TextSecondary,

        modifier =
            Modifier.padding(
                horizontal = 8.dp,
                vertical = 8.dp
            )

    )

}


@Composable
private fun DrawerItem(

    icon: ImageVector,

    title: String,

    onClick: () -> Unit

) {

    Row(

        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 10.dp,
                vertical = 12.dp
            ),

        verticalAlignment =
            Alignment.CenterVertically

    ) {

        Box(

            modifier = Modifier
                .size(38.dp),

            contentAlignment =
                Alignment.Center

        ) {

            Icon(

                imageVector =
                    icon,

                contentDescription =
                    null,

                tint =
                    PrimaryGreen,

                modifier =
                    Modifier.size(20.dp)

            )

        }


        Spacer(
            modifier =
                Modifier.width(14.dp)
        )


        Text(

            text = title,

            style =
                MaterialTheme.typography.bodyMedium,

            color =
                TextPrimary

        )

    }

}