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
    val filteredCharacters: List<Character> = emptyList(),
    val searchQuery: String = "",
    val filterRole: CharacterRole? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CharactersViewModel @Inject constructor(
    private val characterRepository: CharacterRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CharactersUiState())
    val uiState: StateFlow<CharactersUiState> = _uiState.asStateFlow()

    private var currentNovelId: String = ""

    fun loadCharacters(novelId: String) {
        currentNovelId = novelId
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val characters = characterRepository.getCharactersByNovelId(novelId)
                _uiState.update { state ->
                    state.copy(
                        characters = characters,
                        filteredCharacters = characters,
                        isLoading = false
                    )
                }
                applyFilters()
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(isLoading = false, error = e.message)
                }
            }
        }
    }

    fun addCharacter(
        name: String,
        role: CharacterRole,
        description: String,
        notes: String
    ) {
        viewModelScope.launch {
            try {
                val character = Character(
                    novelId = currentNovelId,
                    name = name,
                    role = role,
                    description = description,
                    notes = notes
                )
                characterRepository.insertCharacter(character)
                loadCharacters(currentNovelId)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun updateCharacter(
        characterId: String,
        name: String,
        role: CharacterRole,
        description: String,
        notes: String
    ) {
        viewModelScope.launch {
            try {
                val existingCharacter = characterRepository.getCharacterById(characterId)
                existingCharacter?.let { character ->
                    val updatedCharacter = character.copy(
                        name = name,
                        role = role,
                        description = description,
                        notes = notes
                    )
                    characterRepository.updateCharacter(updatedCharacter)
                    loadCharacters(currentNovelId)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun deleteCharacter(characterId: String) {
        viewModelScope.launch {
            try {
                characterRepository.deleteCharacter(characterId)
                loadCharacters(currentNovelId)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    fun filterByRole(role: CharacterRole?) {
        _uiState.update { it.copy(filterRole = role) }
        applyFilters()
    }

    private fun applyFilters() {
        val state = _uiState.value
        var filtered = state.characters

        if (state.searchQuery.isNotBlank()) {
            filtered = filtered.filter { character ->
                character.name.contains(state.searchQuery, ignoreCase = true) ||
                    character.description.contains(state.searchQuery, ignoreCase = true)
            }
        }

        state.filterRole?.let { role ->
            filtered = filtered.filter { it.role == role }
        }

        _uiState.update { it.copy(filteredCharacters = filtered) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
