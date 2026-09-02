package com.inkwell.feature.camera.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.inkwell.core.data.repository.ChapterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class CameraUiState(
    val capturedImageUri: Uri? = null,
    val recognizedText: String = "",
    val isProcessing: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CameraViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val chapterRepository: ChapterRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    private val textRecognizer: TextRecognizer = TextRecognition.getClient(
        TextRecognizerOptions.DEFAULT_OPTIONS
    )

    private var currentChapterId: String = ""

    fun setCurrentChapterId(chapterId: String) {
        currentChapterId = chapterId
    }

    fun captureImage(uri: Uri) {
        _uiState.update { it.copy(capturedImageUri = uri) }
        processImage(uri)
    }

    private fun processImage(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, error = null) }
            try {
                val image = InputImage.fromFilePath(context, uri)
                val result = textRecognizer.process(image).await()
                val text = result.textBlocks.joinToString("\n\n") { block ->
                    block.lines.joinToString("\n") { it.text }
                }
                _uiState.update { state ->
                    state.copy(
                        recognizedText = text,
                        isProcessing = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isProcessing = false,
                        error = e.message ?: "Failed to recognize text"
                    )
                }
            }
        }
    }

    fun saveTextToChapter(text: String) {
        viewModelScope.launch {
            try {
                val chapter = chapterRepository.getChapterById(currentChapterId)
                chapter?.let {
                    val updatedContent = if (it.content.isBlank()) {
                        text
                    } else {
                        "${it.content}\n\n$text"
                    }
                    val wordCount = updatedContent.split("\\s+".toRegex()).filter { word ->
                        word.isNotBlank()
                    }.size
                    val updatedChapter = it.copy(
                        content = updatedContent,
                        wordCount = wordCount
                    )
                    chapterRepository.updateChapter(updatedChapter)
                    _uiState.update { state ->
                        state.copy(isSaved = true)
                    }
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(error = e.message ?: "Failed to save text")
                }
            }
        }
    }

    fun resetState() {
        _uiState.update {
            CameraUiState()
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    override fun onCleared() {
        super.onCleared()
        textRecognizer.close()
    }
}
