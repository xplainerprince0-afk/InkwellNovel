package com.inkwell.feature.settings.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SettingsUiState(
    val darkMode: Boolean = true,
    val autoSave: Boolean = true,
    val biometricLock: Boolean = false,
    val fontSize: Int = 14
)

class SettingsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun setDarkMode(enabled: Boolean) {
        _uiState.update { it.copy(darkMode = enabled) }
    }

    fun setAutoSave(enabled: Boolean) {
        _uiState.update { it.copy(autoSave = enabled) }
    }

    fun setBiometricLock(enabled: Boolean) {
        _uiState.update { it.copy(biometricLock = enabled) }
    }

    fun setFontSize(size: Int) {
        _uiState.update { it.copy(fontSize = size) }
    }
}
