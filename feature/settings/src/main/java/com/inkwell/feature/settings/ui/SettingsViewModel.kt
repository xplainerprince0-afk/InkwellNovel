package com.inkwell.feature.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkwell.core.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val darkMode: Boolean = true,
    val autoSave: Boolean = true,
    val biometricLock: Boolean = false,
    val fontSize: Int = 14
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.getDarkMode().collect { enabled ->
                _uiState.update { it.copy(darkMode = enabled) }
            }
        }
        viewModelScope.launch {
            settingsRepository.getAutoSave().collect { enabled ->
                _uiState.update { it.copy(autoSave = enabled) }
            }
        }
        viewModelScope.launch {
            settingsRepository.getBiometricLock().collect { enabled ->
                _uiState.update { it.copy(biometricLock = enabled) }
            }
        }
        viewModelScope.launch {
            settingsRepository.getFontSize().collect { size ->
                _uiState.update { it.copy(fontSize = size) }
            }
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setDarkMode(enabled) }
    }

    fun setAutoSave(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setAutoSave(enabled) }
    }

    fun setBiometricLock(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setBiometricLock(enabled) }
    }

    fun setFontSize(size: Int) {
        viewModelScope.launch { settingsRepository.setFontSize(size) }
    }
}
