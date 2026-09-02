package com.inkwell.feature.camera.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class CameraUiState(
    val isScanning: Boolean = false,
    val recognizedText: String = "",
    val error: String? = null
)

@HiltViewModel
class CameraViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    fun startScan() {
        _uiState.update { it.copy(isScanning = true) }
    }

    fun onTextRecognized(text: String) {
        _uiState.update { it.copy(isScanning = false, recognizedText = text) }
    }

    fun onError(error: String) {
        _uiState.update { it.copy(isScanning = false, error = error) }
    }
}
