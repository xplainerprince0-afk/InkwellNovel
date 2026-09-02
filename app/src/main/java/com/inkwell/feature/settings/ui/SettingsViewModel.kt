package com.inkwell.feature.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkwell.core.data.repository.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val isDarkMode: Boolean = false,
    val fontSize: Float = 16f,
    val fontFamily: String = "Default",
    val dailyWordGoal: Int = 1000,
    val isAutoSaveEnabled: Boolean = true,
    val isBiometricEnabled: Boolean = false,
    val userName: String = "",
    val userEmail: String = "",
    val appVersion: String = "1.0.0",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val darkMode = preferencesManager.isDarkMode()
                val fontSize = preferencesManager.getFontSize()
                val fontFamily = preferencesManager.getFontFamily()
                val dailyWordGoal = preferencesManager.getDailyWordGoal()
                val isAutoSaveEnabled = preferencesManager.isAutoSaveEnabled()
                val isBiometricEnabled = preferencesManager.isBiometricEnabled()
                val userName = preferencesManager.getUserName()
                val userEmail = preferencesManager.getUserEmail()
                val appVersion = preferencesManager.getAppVersion()

                _uiState.update { state ->
                    state.copy(
                        isDarkMode = darkMode,
                        fontSize = fontSize,
                        fontFamily = fontFamily,
                        dailyWordGoal = dailyWordGoal,
                        isAutoSaveEnabled = isAutoSaveEnabled,
                        isBiometricEnabled = isBiometricEnabled,
                        userName = userName,
                        userEmail = userEmail,
                        appVersion = appVersion,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(isLoading = false, error = e.message)
                }
            }
        }
    }

    fun updateDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.setDarkMode(enabled)
                _uiState.update { it.copy(isDarkMode = enabled) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun updateFontSize(size: Float) {
        viewModelScope.launch {
            try {
                preferencesManager.setFontSize(size)
                _uiState.update { it.copy(fontSize = size) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun updateFontFamily(family: String) {
        viewModelScope.launch {
            try {
                preferencesManager.setFontFamily(family)
                _uiState.update { it.copy(fontFamily = family) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun updateDailyWordGoal(goal: Int) {
        viewModelScope.launch {
            try {
                preferencesManager.setDailyWordGoal(goal)
                _uiState.update { it.copy(dailyWordGoal = goal) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun toggleAutoSave(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.setAutoSaveEnabled(enabled)
                _uiState.update { it.copy(isAutoSaveEnabled = enabled) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun toggleBiometric(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.setBiometricEnabled(enabled)
                _uiState.update { it.copy(isBiometricEnabled = enabled) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun updateUserName(name: String) {
        viewModelScope.launch {
            try {
                preferencesManager.setUserName(name)
                _uiState.update { it.copy(userName = name) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun updateUserEmail(email: String) {
        viewModelScope.launch {
            try {
                preferencesManager.setUserEmail(email)
                _uiState.update { it.copy(userEmail = email) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
