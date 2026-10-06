package com.example.yekdarsad.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.yekdarsad.data.sync.AuthRepository
import com.example.yekdarsad.ui.theme.CardBackground
import com.example.yekdarsad.ui.theme.PrimaryGreen
import com.example.yekdarsad.ui.theme.PrimaryGreenLight
import com.example.yekdarsad.ui.theme.TextPrimary
import com.example.yekdarsad.ui.theme.TextSecondary
import com.example.yekdarsad.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    themeManager: com.example.yekdarsad.ui.theme.ThemeManager,
    onAppearanceClick: () -> Unit,
    onBack: () -> Unit,
    settingsViewModel: SettingsViewModel,
    onLogout: () -> Unit = {}
) {
    BackHandler {
        onBack()
    }

    val allowPastPlanning by
    settingsViewModel.allowPastPlanning.collectAsState()

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
                .padding(bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        color = PrimaryGreenLight,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .clickable {
                        onBack()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "بازگشت",
                    tint = PrimaryGreen,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Column {
                Text(
                    text = "تنظیمات",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = "شخصی‌سازی یک درصد",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        SettingsAccountCard(
            onLogout = onLogout
        )

        SettingsGeneralCard(
            allowPastPlanning = allowPastPlanning,
            onAllowPastPlanningChange = {
                settingsViewModel.setAllowPastPlanning(it)
            }
        )

        SettingsCategoryCard(
            icon = Icons.Default.Palette,
            title = "ظاهر و رنگ",
            subtitle = "تم، رنگ و ظاهر برنامه",
            onClick = onAppearanceClick
        )

        SettingsCategoryCard(
            icon = Icons.Default.Notifications,
            title = "اعلان‌ها",
            subtitle = "مدیریت یادآوری‌ها و اعلان‌ها",
            onClick = {}
        )

        SettingsCategoryCard(
            icon = Icons.Default.Restaurant,
            title = "تغذیه",
            subtitle = "هدف کالری و تنظیمات تغذیه",
            onClick = {}
        )

        SettingsCategoryCard(
            icon = Icons.Default.Security,
            title = "دسترسی‌ها",
            subtitle = "مدیریت دسترسی‌های برنامه",
            onClick = {}
        )

        SettingsCategoryCard(
            icon = Icons.Default.Info,
            title = "درباره یک درصد",
            subtitle = "اطلاعات برنامه و نسخه",
            onClick = {}
        )
    }
}

@Composable
private fun SettingsAccountCard(
    onLogout: () -> Unit
) {
    val authRepository = AuthRepository()
    val email = authRepository.currentUserEmail()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(
                color = CardBackground,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(15.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        color = PrimaryGreenLight,
                        shape = RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(21.dp)
                )
            }

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "حساب کاربری",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = email ?: "حسابی وارد نشده است",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryGreen
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Text(
                text = "خروج از حساب"
            )
        }
    }
}

@Composable
private fun SettingsGeneralCard(
    allowPastPlanning: Boolean,
    onAllowPastPlanningChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(
                color = CardBackground,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(15.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        color = PrimaryGreenLight,
                        shape = RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(21.dp)
                )
            }

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "تنظیمات عمومی",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = "تنظیمات پایه برنامه",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "برنامه‌ریزی برای روزهای گذشته",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = "امکان برنامه‌ریزی برای تاریخ‌های قبل از امروز",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Switch(
                checked = allowPastPlanning,
                onCheckedChange = onAllowPastPlanningChange
            )
        }
    }
}

@Composable
private fun SettingsCategoryCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(
                color = CardBackground,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable {
                onClick()
            }
            .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(46.dp)
                .background(
                    color = PrimaryGreenLight,
                    shape = RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryGreen,
                modifier = Modifier.size(21.dp)
            )
        }

        Spacer(
            modifier = Modifier.width(14.dp)
        )

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}