
package com.example.yekdarsad.ui.components

import android.content.Context
import android.os.SystemClock
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import com.example.yekdarsad.data.sync.ExpenseSyncRunner
import com.example.yekdarsad.data.sync.SleepSyncRunner
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import com.example.yekdarsad.data.sync.NutritionSyncRunner
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.yekdarsad.data.sync.CategorySyncRunner
import com.example.yekdarsad.data.sync.DailyPlanSyncRunner
import com.example.yekdarsad.data.sync.TaskSyncRunner
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val SYNC_TAG = "SYNC_TIMING"

enum class SyncStatus {
    IDLE,
    SYNCING,
    SUCCESS,
    ERROR
}

data class SyncProgress(
    val status: SyncStatus = SyncStatus.IDLE,
    val message: String = ""
)

object SyncPopupManager {

    private val _progress = MutableStateFlow(SyncProgress())
    val progress = _progress.asStateFlow()

    private var running = false

    @Synchronized
    fun isRunning(): Boolean = running

    suspend fun run(context: Context) {
        synchronized(this) {
            if (running) {
                Log.d(SYNC_TAG, "Sync already running; ignored")
                return
            }

            running = true

            _progress.value = SyncProgress(
                SyncStatus.SYNCING,
                "در حال همگام‌سازی..."
            )
        }

        val totalStart = SystemClock.elapsedRealtime()

        try {
            runTimed("Categories") {
                CategorySyncRunner.run(context)
            }

            runTimed("Tasks") {
                TaskSyncRunner.run(context)
            }

            runTimed("DailyPlans") {
                DailyPlanSyncRunner.run(context)
            }

            runTimed("Nutrition") {
                NutritionSyncRunner.run(context)
            }

            runTimed("Sleep") {
                SleepSyncRunner.run(context)
            }

            runTimed("Expenses") {
                ExpenseSyncRunner.run(context)
            }

            val totalElapsed =
                SystemClock.elapsedRealtime() - totalStart

            Log.d(SYNC_TAG, "TOTAL took ${totalElapsed}ms")

            _progress.value = SyncProgress(
                SyncStatus.SUCCESS,
                "همگام‌سازی تمام شد"
            )
        } catch (e: Exception) {
            val totalElapsed =
                SystemClock.elapsedRealtime() - totalStart

            Log.e(
                SYNC_TAG,
                "Sync failed after ${totalElapsed}ms",
                e
            )

            _progress.value = SyncProgress(
                SyncStatus.ERROR,
                "همگام‌سازی ناموفق بود"
            )
        } finally {
            synchronized(this) {
                running = false
            }
        }
    }

    private suspend fun runTimed(
        name: String,
        action: suspend () -> Unit
    ) {
        val start = SystemClock.elapsedRealtime()

        Log.d(SYNC_TAG, "$name started")

        try {
            action()

            val elapsed =
                SystemClock.elapsedRealtime() - start

            Log.d(SYNC_TAG, "$name completed in ${elapsed}ms")
        } catch (e: Exception) {
            val elapsed =
                SystemClock.elapsedRealtime() - start

            Log.e(
                SYNC_TAG,
                "$name failed after ${elapsed}ms",
                e
            )

            throw e
        }
    }

    fun reset() {
        _progress.value = SyncProgress()
    }
}

@Composable
fun SyncStatusPopup(
    modifier: Modifier = Modifier
) {
    val progress by SyncPopupManager.progress.collectAsState()

    LaunchedEffect(progress.status) {
        if (
            progress.status == SyncStatus.SUCCESS ||
            progress.status == SyncStatus.ERROR
        ) {
            delay(2500)
            SyncPopupManager.reset()
        }
    }

    AnimatedVisibility(
        visible = progress.status != SyncStatus.IDLE,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            tonalElevation = 5.dp,
            shadowElevation = 6.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = 14.dp,
                    vertical = 11.dp
                ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                when (progress.status) {
                    SyncStatus.SYNCING -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(19.dp),
                            strokeWidth = 2.dp
                        )
                    }

                    SyncStatus.SUCCESS -> {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    SyncStatus.ERROR -> {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    SyncStatus.IDLE -> {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = progress.message,
                        style = MaterialTheme.typography.labelMedium
                    )

                    if (progress.status == SyncStatus.SYNCING) {
                        Text(
                            text = "لطفاً صبر کنید",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}