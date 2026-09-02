package com.inkwell.feature.characters.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkwell.core.data.repository.model.Character
import com.inkwell.core.data.repository.model.CharacterRole
import com.inkwell.core.data.repository.CharacterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CharactersUiState(
    val characters: List<Character> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CharactersViewModel @Inject constructor(
    private val characterRepository: CharacterRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CharactersUiState())
    val uiState: StateFlow<CharactersUiState> = _uiState.asStateFlow()

    init {
        loadCharacters()
    }

    private fun loadCharacters() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            characterRepository.getCharactersByNovelId(1L).collect { characters ->
                _uiState.update { state ->
                    state.copy(characters = characters, isLoading = false)
                }
            }
        }
    }

    fun addCharacter(name: String, description: String, role: CharacterRole) {
        viewModelScope.launch {
            characterRepository.createCharacter(
                novelId = 1L,
                name = name,
                description = description,
                role = role.name
            )
        }
    }

    fun deleteCharacter(character: Character) {
        viewModelScope.launch {
            characterRepository.deleteCharacter(character)
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }
}
