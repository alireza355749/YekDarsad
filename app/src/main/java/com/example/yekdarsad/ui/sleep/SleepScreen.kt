package com.example.yekdarsad.ui.sleep

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yekdarsad.data.DatabaseProvider
import com.example.yekdarsad.viewmodel.SleepViewModel
import com.example.yekdarsad.viewmodel.SleepViewModelFactory
import androidx.compose.foundation.layout.width

@Composable
fun SleepScreen(
    date: String
) {

    val context = LocalContext.current

    val database = remember {
        DatabaseProvider.getDatabase(context)
    }

    val sleepViewModel: SleepViewModel = viewModel(
        factory = SleepViewModelFactory(
            database.sleepDao()
        )
    )

    val sleepEntry by sleepViewModel
        .getEntry(date)
        .collectAsState(initial = null)

    var sleepStartHour by remember {
        mutableIntStateOf(23)
    }

    var sleepStartMinute by remember {
        mutableIntStateOf(0)
    }

    var wakeHour by remember {
        mutableIntStateOf(7)
    }

    var wakeMinute by remember {
        mutableIntStateOf(30)
    }

    var napsMinutes by remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(sleepEntry) {

        sleepEntry?.let { entry ->

            sleepStartHour = entry.sleepStartHour
            sleepStartMinute = entry.sleepStartMinute

            wakeHour = entry.wakeHour
            wakeMinute = entry.wakeMinute

            napsMinutes = entry.napsMinutes
        }
    }

    val nightSleepMinutes =
        calculateSleepMinutes(
            sleepStartHour,
            sleepStartMinute,
            wakeHour,
            wakeMinute
        )

    val totalSleepMinutes =
        nightSleepMinutes + napsMinutes

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color(0xFFF8F6FF)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        // =====================================================
        // Header
        // =====================================================

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Default.Bedtime,
                contentDescription = null,
                modifier = Modifier.size(30.dp),
                tint = Color(0xFF6C63FF)
            )

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            Column {

                Text(
                    text = "خواب",
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = date,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        // =====================================================
        // Summary
        // =====================================================

        SleepSummaryCard(
            totalMinutes = totalSleepMinutes
        )

        // =====================================================
        // Night Sleep
        // =====================================================

        SleepTimeCard(
            startHour = sleepStartHour,
            startMinute = sleepStartMinute,
            wakeHour = wakeHour,
            wakeMinute = wakeMinute,
            sleepMinutes = nightSleepMinutes,

            onStartTimeClick = {

                TimePickerDialog(
                    context,
                    { _, hour, minute ->

                        sleepStartHour = hour
                        sleepStartMinute = minute

                        sleepViewModel.saveSleep(
                            date = date,
                            sleepStartHour = hour,
                            sleepStartMinute = minute,
                            wakeHour = wakeHour,
                            wakeMinute = wakeMinute,
                            napsMinutes = napsMinutes
                        )
                    },
                    sleepStartHour,
                    sleepStartMinute,
                    true
                ).show()
            },

            onWakeTimeClick = {

                TimePickerDialog(
                    context,
                    { _, hour, minute ->

                        wakeHour = hour
                        wakeMinute = minute

                        sleepViewModel.saveSleep(
                            date = date,
                            sleepStartHour = sleepStartHour,
                            sleepStartMinute = sleepStartMinute,
                            wakeHour = hour,
                            wakeMinute = minute,
                            napsMinutes = napsMinutes
                        )
                    },
                    wakeHour,
                    wakeMinute,
                    true
                ).show()
            }
        )

        // =====================================================
        // Naps
        // =====================================================

        NapCard(
            minutes = napsMinutes,

            onAdd = {

                napsMinutes += 10

                sleepViewModel.saveSleep(
                    date = date,
                    sleepStartHour = sleepStartHour,
                    sleepStartMinute = sleepStartMinute,
                    wakeHour = wakeHour,
                    wakeMinute = wakeMinute,
                    napsMinutes = napsMinutes
                )
            },

            onRemove = {

                if (napsMinutes >= 10) {

                    napsMinutes -= 10

                    sleepViewModel.saveSleep(
                        date = date,
                        sleepStartHour = sleepStartHour,
                        sleepStartMinute = sleepStartMinute,
                        wakeHour = wakeHour,
                        wakeMinute = wakeMinute,
                        napsMinutes = napsMinutes
                    )
                }
            }
        )
    }
}

// =========================================================
// Summary Card
// =========================================================

@Composable
private fun SleepSummaryCard(
    totalMinutes: Int
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEDE7FF)
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = Icons.Default.NightsStay,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = Color(0xFF6C63FF)
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = formatSleepTime(totalMinutes),
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = "مجموع خواب",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

// =========================================================
// Night Sleep Card
// =========================================================

@Composable
private fun SleepTimeCard(
    startHour: Int,
    startMinute: Int,
    wakeHour: Int,
    wakeMinute: Int,
    sleepMinutes: Int,
    onStartTimeClick: () -> Unit,
    onWakeTimeClick: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = Color(0xFF6C63FF)
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = "خواب شب",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = formatSleepTime(sleepMinutes),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            TimePickerRow(
                title = "زمان خواب",
                icon = Icons.Default.Bedtime,
                hour = startHour,
                minute = startMinute,
                onClick = onStartTimeClick
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            TimePickerRow(
                title = "زمان بیداری",
                icon = Icons.Default.WbSunny,
                hour = wakeHour,
                minute = wakeMinute,
                onClick = onWakeTimeClick
            )
        }
    }
}

// =========================================================
// Time Picker Row
// =========================================================

@Composable
private fun TimePickerRow(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    hour: Int,
    minute: Int,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = Color(0xFF6C63FF)
        )

        Spacer(
            modifier = Modifier.width(8.dp)
        )

        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(
            modifier = Modifier.weight(1f)
        )

        Box(
            modifier = Modifier
                .background(
                    color = Color(0xFFF3F0FF),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                )
        ) {

            Text(
                text = String.format(
                    "%02d:%02d",
                    hour,
                    minute
                ),
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF4E467A)
            )
        }
    }
}

// =========================================================
// Nap Card
// =========================================================

@Composable
private fun NapCard(
    minutes: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.Bedtime,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = Color(0xFF6C63FF)
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = "چرت‌های روزانه",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "مجموع چرت‌ها",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                NapButton(
                    text = "−",
                    enabled = minutes >= 10,
                    onClick = onRemove
                )

                Text(
                    text = "$minutes دقیقه",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(
                        horizontal = 14.dp
                    )
                )

                NapButton(
                    text = "+",
                    enabled = true,
                    onClick = onAdd
                )
            }
        }
    }
}

// =========================================================
// Nap Button
// =========================================================

@Composable
private fun NapButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .size(38.dp)
            .background(
                color = if (enabled) {
                    Color(0xFFEDE7FF)
                } else {
                    Color(0xFFF3F3F3)
                },
                shape = RoundedCornerShape(11.dp)
            )
            .clickable(
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            color = if (enabled) {
                Color(0xFF6C63FF)
            } else {
                Color.Gray
            }
        )
    }
}

// =========================================================
// Calculate
// =========================================================

private fun calculateSleepMinutes(
    startHour: Int,
    startMinute: Int,
    wakeHour: Int,
    wakeMinute: Int
): Int {

    val start =
        startHour * 60 + startMinute

    val wake =
        wakeHour * 60 + wakeMinute

    return if (wake >= start) {
        wake - start
    } else {
        (24 * 60 - start) + wake
    }
}

// =========================================================
// Format
// =========================================================

private fun formatSleepTime(
    minutes: Int
): String {

    if (minutes <= 0) {
        return "ثبت نشده"
    }

    val hours = minutes / 60
    val remaining = minutes % 60

    return when {

        hours > 0 && remaining > 0 ->
            "$hours ساعت و $remaining دقیقه"

        hours > 0 ->
            "$hours ساعت"

        else ->
            "$remaining دقیقه"
    }
}