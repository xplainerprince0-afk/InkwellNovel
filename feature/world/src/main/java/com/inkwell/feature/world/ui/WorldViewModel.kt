package com.inkwell.feature.world.ui

import androidx.lifecycle.ViewModel
import com.inkwell.core.data.repository.model.WorldNote
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class WorldUiState(
    val notes: List<WorldNote> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class WorldViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(WorldUiState())
    val uiState: StateFlow<WorldUiState> = _uiState.asStateFlow()
}
