package com.example.yekdarsad.ui.auth

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yekdarsad.data.sync.CategorySyncRunner
import com.example.yekdarsad.data.sync.DailyPlanSyncRunner
import com.example.yekdarsad.data.sync.TaskSyncRunner

@Composable
fun AuthGate(
    content: @Composable () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {

    val state by
    viewModel.uiState
        .collectAsStateWithLifecycle()

    val context =
        LocalContext.current

    if (state.isLoggedIn) {

        LaunchedEffect(Unit) {

            Log.d(
                "DataSync",
                "AUTHGATE: user is logged in"
            )

            // =====================================================
            // CATEGORY SYNC
            // =====================================================

            try {

                Log.d(
                    "DataSync",
                    "AUTHGATE: category sync START"
                )

                CategorySyncRunner.run(
                    context
                )

                Log.d(
                    "DataSync",
                    "AUTHGATE: category sync DONE"
                )

            } catch (e: Exception) {

                Log.e(
                    "DataSync",
                    "AUTHGATE: category sync FAILED: ${e.message}",
                    e
                )
            }

            // =====================================================
            // TASK SYNC
            // =====================================================

            try {

                Log.d(
                    "DataSync",
                    "AUTHGATE: task sync START"
                )

                TaskSyncRunner.run(
                    context
                )

                Log.d(
                    "DataSync",
                    "AUTHGATE: task sync DONE"
                )

            } catch (e: Exception) {

                Log.e(
                    "DataSync",
                    "AUTHGATE: task sync FAILED: ${e.message}",
                    e
                )
            }

            // =====================================================
            // DAILY PLAN SYNC
            // =====================================================

            try {

                Log.d(
                    "DataSync",
                    "AUTHGATE: daily plan sync START"
                )

                DailyPlanSyncRunner.run(
                    context
                )

                Log.d(
                    "DataSync",
                    "AUTHGATE: daily plan sync DONE"
                )

            } catch (e: Exception) {

                Log.e(
                    "DataSync",
                    "AUTHGATE: daily plan sync FAILED: ${e.message}",
                    e
                )
            }

            Log.d(
                "DataSync",
                "AUTHGATE: ALL SYNC FINISHED"
            )
        }

        content()

    } else {

        LoginScreen(
            onAuthenticated = {},
            viewModel = viewModel
        )
    }
}