package com.inkwell.feature.editor.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class EditorUiState(
    val chapterId: Long = 0L,
    val chapterTitle: String = "Chapter 1",
    val content: String = "",
    val wordCount: Int = 0,
    val isSaving: Boolean = false,
    val error: String? = null
)

class EditorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

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
}
