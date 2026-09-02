package com.inkwell.feature.editor.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkwell.core.data.repository.ChapterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditorUiState(
    val chapterId: Long = 0L,
    val chapterTitle: String = "Chapter 1",
    val content: String = "",
    val wordCount: Int = 0,
    val isSaving: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val chapterRepository: ChapterRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    init {
        loadChapterContent()
    }

    private fun loadChapterContent() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                val chapters = chapterRepository.getChaptersByNovelId(1L).first()
                val chapter = chapters.firstOrNull()
                if (chapter != null) {
                    _uiState.update { state ->
                        state.copy(
                            chapterId = chapter.id,
                            content = chapter.content,
                            chapterTitle = chapter.title,
                            wordCount = chapter.content.split("\\s+".toRegex()).size,
                            isSaving = false
                        )
                    }
                } else {
                    _uiState.update { it.copy(isSaving = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isSaving = false) }
            }
        }
    }

    fun updateChapterTitle(title: String) {
        _uiState.update { it.copy(chapterTitle = title) }
    }

    fun updateContent(content: String) {
        _uiState.update {
            it.copy(
                content = content,
                wordCount = content.split("\\s+".toRegex()).size
            )
        }
    }

    fun saveContent() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                val state = _uiState.value
                val chapters = chapterRepository.getChaptersByNovelId(1L).first()
                val chapter = chapters.firstOrNull()
                if (chapter != null) {
                    chapterRepository.updateChapter(
                        chapter.copy(
                            title = state.chapterTitle,
                            content = state.content
                        )
                    )
                }
                _uiState.update { it.copy(isSaving = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isSaving = false) }
            }
        }
    }
}
