package com.inkwell.feature.world.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkwell.core.data.repository.model.WorldNote
import com.inkwell.core.data.repository.model.WorldNoteCategory
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
    val filteredNotes: List<WorldNote> = emptyList(),
    val selectedCategory: WorldNoteCategory = WorldNoteCategory.LOCATIONS,
    val currentNote: WorldNote? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class WorldViewModel @Inject constructor(
    private val worldNoteRepository: WorldNoteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorldUiState())
    val uiState: StateFlow<WorldUiState> = _uiState.asStateFlow()

    private var currentNovelId: String = ""

    fun loadNotes(novelId: String) {
        currentNovelId = novelId
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val notes = worldNoteRepository.getNotesByNovelId(novelId)
                _uiState.update { state ->
                    state.copy(
                        notes = notes,
                        isLoading = false
                    )
                }
                filterByCategory(state.value.selectedCategory)
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(isLoading = false, error = e.message)
                }
            }
        }
    }

    private val state: WorldUiState get() = _uiState.value

    fun addNote(
        title: String,
        category: WorldNoteCategory,
        content: String,
        locationTag: String? = null
    ) {
        viewModelScope.launch {
            try {
                val note = WorldNote(
                    novelId = currentNovelId,
                    title = title,
                    category = category,
                    content = content,
                    locationTag = locationTag
                )
                worldNoteRepository.insertNote(note)
                loadNotes(currentNovelId)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun updateNote(
        noteId: String,
        title: String,
        category: WorldNoteCategory,
        content: String,
        locationTag: String? = null
    ) {
        viewModelScope.launch {
            try {
                val existingNote = worldNoteRepository.getNoteById(noteId)
                existingNote?.let { note ->
                    val updatedNote = note.copy(
                        title = title,
                        category = category,
                        content = content,
                        locationTag = locationTag
                    )
                    worldNoteRepository.updateNote(updatedNote)
                    loadNotes(currentNovelId)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            try {
                worldNoteRepository.deleteNote(noteId)
                loadNotes(currentNovelId)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun filterByCategory(category: WorldNoteCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
        val filtered = state.notes.filter { it.category == category }
        _uiState.update { it.copy(filteredNotes = filtered) }
    }

    fun selectNote(note: WorldNote?) {
        _uiState.update { it.copy(currentNote = note) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
