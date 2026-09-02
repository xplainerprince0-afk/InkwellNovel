package com.inkwell.feature.characters.ui

import androidx.lifecycle.ViewModel
import com.inkwell.core.data.repository.model.Character
import com.inkwell.core.data.repository.model.CharacterRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class CharactersUiState(
    val characters: List<Character> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CharactersViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(CharactersUiState())
    val uiState: StateFlow<CharactersUiState> = _uiState.asStateFlow()
}
