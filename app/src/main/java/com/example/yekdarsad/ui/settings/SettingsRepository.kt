package com.example.yekdarsad.ui.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(
    name = "settings"
)

class SettingsRepository(
    private val context: Context
) {

    companion object {

        private val ALLOW_PAST_PLANNING =
            booleanPreferencesKey(
                "allow_past_planning"
            )
    }


    /*
     * =====================================================
     * آیا برنامه‌ریزی برای روزهای گذشته مجاز است؟
     * =====================================================
     *
     * مقدار پیش‌فرض:
     * false
     */

    val allowPastPlanning: Flow<Boolean> =
        context.settingsDataStore.data.map { preferences ->

            preferences[
                ALLOW_PAST_PLANNING
            ] ?: false
        }


    /*
     * =====================================================
     * تغییر وضعیت برنامه‌ریزی برای روزهای گذشته
     * =====================================================
     */

    suspend fun setAllowPastPlanning(
        enabled: Boolean
    ) {

        context.settingsDataStore.edit { preferences ->

            preferences[
                ALLOW_PAST_PLANNING
            ] = enabled
        }
    }
}