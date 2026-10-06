package com.example.yekdarsad.ui.theme

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ThemeManager(
    context: Context
) {

    private val preferences =
        context.getSharedPreferences(
            "yekdarsad_theme",
            Context.MODE_PRIVATE
        )


    var themeMode by mutableStateOf(
        loadThemeMode()
    )
        private set


    fun changeThemeMode(
        mode: ThemeMode
    ) {

        themeMode = mode

        preferences
            .edit()
            .putString(
                "theme_mode",
                mode.name
            )
            .apply()

    }


    private fun loadThemeMode(): ThemeMode {

        val savedMode =
            preferences.getString(
                "theme_mode",
                ThemeMode.SYSTEM.name
            )


        return try {

            ThemeMode.valueOf(
                savedMode
                    ?: ThemeMode.SYSTEM.name
            )

        } catch (
            e: IllegalArgumentException
        ) {

            ThemeMode.SYSTEM

        }

    }

}