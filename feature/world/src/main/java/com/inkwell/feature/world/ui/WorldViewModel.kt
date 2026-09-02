package com.inkwell.feature.world.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkwell.core.data.repository.model.WorldNote
import com.inkwell.core.data.repository.WorldNoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorldUiState(
    val notes: List<WorldNote> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class WorldViewModel @Inject constructor(
    private val worldNoteRepository: WorldNoteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorldUiState())
    val uiState: StateFlow<WorldUiState> = _uiState.asStateFlow()

    init {
        loadNotes()
    }

    private fun loadNotes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            worldNoteRepository.getWorldNotesByNovelId(1L).collect { notes ->
                _uiState.update { state ->
                    state.copy(notes = notes, isLoading = false)
                }
            }
        }
    }

    fun addNote(title: String, content: String) {
        viewModelScope.launch {
            worldNoteRepository.createWorldNote(
                novelId = 1L,
                title = title,
                content = content
            )
        }
    }

    fun deleteNote(note: WorldNote) {
        viewModelScope.launch {
            worldNoteRepository.deleteWorldNote(note)
        }
    }
}
