package com.inkwell.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkwell.app.ui.components.NovelUiState
import com.inkwell.app.ui.theme.ChapterBlue
import com.inkwell.app.ui.theme.ChapterGreen
import com.inkwell.app.ui.theme.ChapterOrange
import com.inkwell.app.ui.theme.ChapterRed
import com.inkwell.app.ui.theme.ChapterTeal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val novels: List<NovelUiState> = emptyList(),
    val searchQuery: String = "",
    val totalWords: Int = 0,
    val totalNovels: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val filteredNovels: List<NovelUiState>
        get() = if (searchQuery.isBlank()) {
            novels
        } else {
            novels.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true)
            }
        }
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    // private val novelRepository: NovelRepository,
    // private val writingSessionRepository: WritingSessionRepository,
    // private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val coverColors = listOf(
        ChapterBlue,
        ChapterGreen,
        ChapterOrange,
        ChapterRed,
        ChapterTeal
    )

    fun loadNovels() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // TODO: Replace with actual repository calls
                // val novels = novelRepository.getAllNovels()
                // val statistics = writingSessionRepository.getStatistics()

                // Sample data for demonstration
                val sampleNovels = listOf(
                    NovelUiState(
                        id = 1,
                        title = "The Midnight Garden",
                        description = "A magical realism tale about a hidden garden that only appears at midnight.",
                        wordCount = 45230,
                        lastUpdated = System.currentTimeMillis() - 86400000,
                        coverColor = coverColors[0],
                        isBiometricLocked = false
                    ),
                    NovelUiState(
                        id = 2,
                        title = "Chronicles of the Lost City",
                        description = "An epic fantasy adventure following a group of explorers.",
                        wordCount = 128450,
                        lastUpdated = System.currentTimeMillis() - 172800000,
                        coverColor = coverColors[1],
                        isBiometricLocked = true
                    ),
                    NovelUiState(
                        id = 3,
                        title = "Whispers in the Wind",
                        description = "A contemporary romance set in a small coastal town.",
                        wordCount = 67890,
                        lastUpdated = System.currentTimeMillis() - 259200000,
                        coverColor = coverColors[2],
                        isBiometricLocked = false
                    )
                )

                val totalWords = sampleNovels.sumOf { it.wordCount }

                _uiState.update {
                    it.copy(
                        novels = sampleNovels,
                        totalWords = totalWords,
                        totalNovels = sampleNovels.size,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load novels"
                    )
                }
            }
        }
    }

    fun createNovel(title: String, description: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                // TODO: Replace with actual repository call
                // val newNovel = novelRepository.createNovel(title, description)

                val newNovel = NovelUiState(
                    id = System.currentTimeMillis(),
                    title = title,
                    description = description,
                    wordCount = 0,
                    lastUpdated = System.currentTimeMillis(),
                    coverColor = coverColors.random(),
                    isBiometricLocked = false
                )

                _uiState.update {
                    it.copy(
                        novels = it.novels + newNovel,
                        totalNovels = it.totalNovels + 1,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to create novel"
                    )
                }
            }
        }
    }

    fun deleteNovel(novelId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                // TODO: Replace with actual repository call
                // novelRepository.deleteNovel(novelId)

                val novelToDelete = _uiState.value.novels.find { it.id == novelId }

                _uiState.update {
                    it.copy(
                        novels = it.novels.filter { novel -> novel.id != novelId },
                        totalNovels = it.totalNovels - 1,
                        totalWords = it.totalWords - (novelToDelete?.wordCount ?: 0),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to delete novel"
                    )
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
