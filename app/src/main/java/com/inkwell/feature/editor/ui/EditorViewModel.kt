package com.inkwell.feature.editor.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkwell.core.data.repository.model.Chapter
import com.inkwell.core.data.repository.model.Novel
import com.inkwell.core.data.repository.ChapterRepository
import com.inkwell.core.data.repository.NovelRepository
import com.inkwell.core.data.local.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditorUiState(
    val novel: Novel? = null,
    val chapters: List<Chapter> = emptyList(),
    val currentChapter: Chapter? = null,
    val content: String = "",
    val chapterTitle: String = "",
    val wordCount: Int = 0,
    val totalWordCount: Int = 0,
    val chapterCount: Int = 0,
    val isSaving: Boolean = false,
    val isAutoSaving: Boolean = false,
    val lastSavedTime: Long = 0L,
    val error: String? = null
)

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val chapterRepository: ChapterRepository,
    private val novelRepository: NovelRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    private var autoSaveJob: Job? = null
    private var currentNovelId: String = ""

    fun loadNovel(novelId: String) {
        currentNovelId = novelId
        viewModelScope.launch {
            try {
                val novel = novelRepository.getNovelById(novelId)
                val chapters = chapterRepository.getChaptersByNovelId(novelId)
                val currentChapter = chapters.firstOrNull()
                val totalWordCount = chapters.sumOf { it.wordCount }

                _uiState.update { state ->
                    state.copy(
                        novel = novel,
                        chapters = chapters,
                        currentChapter = currentChapter,
                        content = currentChapter?.content ?: "",
                        chapterTitle = currentChapter?.title ?: "",
                        wordCount = currentChapter?.wordCount ?: 0,
                        totalWordCount = totalWordCount,
                        chapterCount = chapters.size
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun updateContent(content: String) {
        val wordCount = content.split("\\s+".toRegex()).filter { it.isNotBlank() }.size
        _uiState.update { state ->
            state.copy(
                content = content,
                wordCount = wordCount
            )
        }
        scheduleAutoSave()
    }

    fun updateChapterTitle(title: String) {
        _uiState.update { it.copy(chapterTitle = title) }
        scheduleAutoSave()
    }

    fun selectChapter(chapterId: String) {
        viewModelScope.launch {
            try {
                saveCurrentChapter()
                val chapter = chapterRepository.getChapterById(chapterId)
                _uiState.update { state ->
                    state.copy(
                        currentChapter = chapter,
                        content = chapter?.content ?: "",
                        chapterTitle = chapter?.title ?: "",
                        wordCount = chapter?.wordCount ?: 0
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun addChapter() {
        viewModelScope.launch {
            try {
                val newChapter = Chapter(
                    novelId = currentNovelId,
                    title = "Chapter ${_uiState.value.chapters.size + 1}",
                    content = "",
                    wordCount = 0,
                    order = _uiState.value.chapters.size
                )
                val chapterId = chapterRepository.insertChapter(newChapter)
                val chapters = chapterRepository.getChaptersByNovelId(currentNovelId)
                val createdChapter = chapters.find { it.id == chapterId }

                _uiState.update { state ->
                    state.copy(
                        chapters = chapters,
                        currentChapter = createdChapter,
                        content = "",
                        chapterTitle = createdChapter?.title ?: "",
                        wordCount = 0,
                        chapterCount = chapters.size
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun deleteChapter(chapterId: String) {
        viewModelScope.launch {
            try {
                chapterRepository.deleteChapter(chapterId)
                val chapters = chapterRepository.getChaptersByNovelId(currentNovelId)
                val currentChapter = chapters.firstOrNull()
                val totalWordCount = chapters.sumOf { it.wordCount }

                _uiState.update { state ->
                    state.copy(
                        chapters = chapters,
                        currentChapter = currentChapter,
                        content = currentChapter?.content ?: "",
                        chapterTitle = currentChapter?.title ?: "",
                        wordCount = currentChapter?.wordCount ?: 0,
                        totalWordCount = totalWordCount,
                        chapterCount = chapters.size
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun reorderChapters(fromIndex: Int, toIndex: Int) {
        viewModelScope.launch {
            try {
                val chapters = _uiState.value.chapters.toMutableList()
                val chapter = chapters.removeAt(fromIndex)
                chapters.add(toIndex, chapter)
                chapters.forEachIndexed { index, ch ->
                    chapterRepository.updateChapterOrder(ch.id, index)
                }
                val updatedChapters = chapterRepository.getChaptersByNovelId(currentNovelId)
                _uiState.update { it.copy(chapters = updatedChapters) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun saveChapter() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                saveCurrentChapter()
                _uiState.update { state ->
                    state.copy(
                        isSaving = false,
                        lastSavedTime = System.currentTimeMillis()
                    )
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(isSaving = false, error = e.message)
                }
            }
        }
    }

    private fun scheduleAutoSave() {
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            delay(500L)
            _uiState.update { it.copy(isAutoSaving = true) }
            try {
                saveCurrentChapter()
                _uiState.update { state ->
                    state.copy(
                        isAutoSaving = false,
                        lastSavedTime = System.currentTimeMillis()
                    )
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(isAutoSaving = false, error = e.message)
                }
            }
        }
    }

    private suspend fun saveCurrentChapter() {
        val state = _uiState.value
        state.currentChapter?.let { chapter ->
            val updatedChapter = chapter.copy(
                title = state.chapterTitle,
                content = state.content,
                wordCount = state.wordCount
            )
            chapterRepository.updateChapter(updatedChapter)

            val chapters = chapterRepository.getChaptersByNovelId(currentNovelId)
            val totalWordCount = chapters.sumOf { it.wordCount }
            _uiState.update { it.copy(chapters = chapters, totalWordCount = totalWordCount) }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
