package com.inkwell.core.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "inkwell_preferences")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private object Keys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val FONT_SIZE = intPreferencesKey("font_size")
        val FONT_FAMILY = stringPreferencesKey("font_family")
        val AUTO_SAVE = booleanPreferencesKey("auto_save")
        val WRITING_GOAL_DAILY = intPreferencesKey("writing_goal_daily")
        val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
    }

    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[Keys.DARK_MODE] ?: false
    }

    val fontSize: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[Keys.FONT_SIZE] ?: 16
    }

    val fontFamily: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[Keys.FONT_FAMILY] ?: "serif"
    }

    val isAutoSaveEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[Keys.AUTO_SAVE] ?: true
    }

    val dailyWritingGoal: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[Keys.WRITING_GOAL_DAILY] ?: 1000
    }

    val isBiometricEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[Keys.BIOMETRIC_ENABLED] ?: false
    }

    val userName: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[Keys.USER_NAME] ?: ""
    }

    val userEmail: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[Keys.USER_EMAIL] ?: ""
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[Keys.DARK_MODE] = enabled
        }
    }

    suspend fun setFontSize(size: Int) {
        context.dataStore.edit { preferences ->
            preferences[Keys.FONT_SIZE] = size
        }
    }

    suspend fun setFontFamily(family: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.FONT_FAMILY] = family
        }
    }

    suspend fun setAutoSave(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[Keys.AUTO_SAVE] = enabled
        }
    }

    suspend fun setDailyWritingGoal(goal: Int) {
        context.dataStore.edit { preferences ->
            preferences[Keys.WRITING_GOAL_DAILY] = goal
        }
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[Keys.BIOMETRIC_ENABLED] = enabled
        }
    }

    suspend fun setUserName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.USER_NAME] = name
        }
    }

    suspend fun setUserEmail(email: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.USER_EMAIL] = email
        }
    }
}
