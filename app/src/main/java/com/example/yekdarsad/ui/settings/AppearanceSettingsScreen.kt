package com.example.yekdarsad.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.yekdarsad.ui.theme.ThemeManager
import com.example.yekdarsad.ui.theme.ThemeMode


@Composable
fun AppearanceSettingsScreen(

    themeManager: ThemeManager,

    onBack: () -> Unit

) {

    BackHandler {

        onBack()

    }


    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            )

    ) {


        Row(

            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    bottom = 24.dp
                ),

            verticalAlignment =
                Alignment.CenterVertically

        ) {


            Icon(

                imageVector =
                    Icons.Default.Brightness4,

                contentDescription =
                    null,

                tint =
                    PrimaryGreen,

                modifier =
                    Modifier.size(28.dp)

            )


            Spacer(
                modifier =
                    Modifier.width(12.dp)
            )


            Column {

                Text(

                    text =
                        "ظاهر و رنگ",

                    style =
                        MaterialTheme.typography.headlineSmall,

                    color =
                        TextPrimary

                )


                Spacer(
                    modifier =
                        Modifier.height(2.dp)
                )


                Text(

                    text =
                        "ظاهر برنامه را شخصی‌سازی کنید",

                    style =
                        MaterialTheme.typography.bodySmall,

                    color =
                        TextSecondary

                )

            }

        }


        Text(

            text =
                "حالت نمایش",

            style =
                MaterialTheme.typography.titleMedium,

            color =
                TextPrimary,

            modifier =
                Modifier.padding(
                    bottom = 10.dp
                )

        )


        ThemeOption(

            icon =
                Icons.Default.LightMode,

            title =
                "روشن",

            subtitle =
                "استفاده از حالت روشن",

            selected =
                themeManager.themeMode ==
                        ThemeMode.LIGHT,

            onClick = {

                themeManager.changeThemeMode(
                    ThemeMode.LIGHT
                )

            }

        )


        ThemeOption(

            icon =
                Icons.Default.DarkMode,

            title =
                "تاریک",

            subtitle =
                "استفاده از حالت تاریک",

            selected =
                themeManager.themeMode ==
                        ThemeMode.DARK,

            onClick = {

                themeManager.changeThemeMode(
                    ThemeMode.DARK
                )

            }

        )


        ThemeOption(

            icon =
                Icons.Default.SettingsBrightness,

            title =
                "مطابق سیستم",

            subtitle =
                "هماهنگ با تنظیمات گوشی",

            selected =
                themeManager.themeMode ==
                        ThemeMode.SYSTEM,

            onClick = {

                themeManager.changeThemeMode(
                    ThemeMode.SYSTEM
                )

            }

        )

    }

}


@Composable
private fun ThemeOption(

    icon: ImageVector,

    title: String,

    subtitle: String,

    selected: Boolean,

    onClick: () -> Unit

) {

    Row(

        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 5.dp
            )
            .background(

                color =
                    if (selected)
                        PrimaryGreenLight
                    else
                        CardBackground,

                shape =
                    RoundedCornerShape(20.dp)

            )
            .clickable {

                onClick()

            }
            .padding(16.dp),

        verticalAlignment =
            Alignment.CenterVertically

    ) {


        Box(

            modifier = Modifier
                .size(44.dp)
                .background(

                    color =
                        if (selected)
                            PrimaryGreen
                        else
                            PrimaryGreenLight,

                    shape =
                        RoundedCornerShape(14.dp)

                ),

            contentAlignment =
                Alignment.Center

        ) {

            Icon(

                imageVector =
                    icon,

                contentDescription =
                    null,

                tint =
                    if (selected)
                        CardBackground
                    else
                        PrimaryGreen,

                modifier =
                    Modifier.size(21.dp)

            )

        }


        Spacer(
            modifier =
                Modifier.width(14.dp)
        )


        Column(

            modifier =
                Modifier.weight(1f)

        ) {

            Text(

                text =
                    title,

                style =
                    MaterialTheme.typography.titleSmall,

                color =
                    TextPrimary

            )


            Spacer(
                modifier =
                    Modifier.height(3.dp)

            )


            Text(

                text =
                    subtitle,

                style =
                    MaterialTheme.typography.bodySmall,

                color =
                    TextSecondary

            )

        }


        if (selected) {

            Icon(

                imageVector =
                    Icons.Default.Check,

                contentDescription =
                    null,

                tint =
                    PrimaryGreen,

                modifier =
                    Modifier.size(22.dp)

            )

        }

    }

}