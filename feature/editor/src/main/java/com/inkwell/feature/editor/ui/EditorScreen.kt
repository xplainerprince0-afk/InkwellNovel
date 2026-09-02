package com.inkwell.feature.editor.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    novelId: String,
    onNavigateBack: () -> Unit,
    onNavigateToChapterList: () -> Unit,
    viewModel: EditorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    var showChapterSheet by remember { mutableStateOf(false) }

    LaunchedEffect(novelId) {
        viewModel.loadNovel(novelId)
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            scope.launch {
                snackbarHostState.showSnackbar(error)
                viewModel.clearError()
            }
        }
    }

    LaunchedEffect(Unit) {
        delay(300)
        focusRequester.requestFocus()
    }

    val editorDescription = stringResource(com.inkwell.core.ui.R.string.editor_screen_description)
    val wordCountDescription = stringResource(
        com.inkwell.core.ui.R.string.word_count_description,
        uiState.wordCount
    )
    val chapterCountDescription = stringResource(
        com.inkwell.core.ui.R.string.chapter_count_description,
        uiState.chapterCount
    )
    val saveDescription = stringResource(com.inkwell.core.ui.R.string.save_chapter)
    val autoSaveIndicatorDescription = stringResource(com.inkwell.core.ui.R.string.auto_save_indicator)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    var titleText by remember(uiState.chapterTitle) {
                        mutableStateOf(uiState.chapterTitle)
                    }

                    BasicTextField(
                        value = titleText,
                        onValueChange = { newValue ->
                            titleText = newValue
                            viewModel.updateChapterTitle(newValue)
                        },
                        textStyle = MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        singleLine = true,
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics {
                                contentDescription = stringResource(
                                    com.inkwell.core.ui.R.string.chapter_title_field
                                )
                            }
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.semantics {
                            contentDescription = stringResource(com.inkwell.core.ui.R.string.navigate_back)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showChapterSheet = true },
                        modifier = Modifier.semantics {
                            contentDescription = stringResource(com.inkwell.core.ui.R.string.show_chapters)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = stringResource(com.inkwell.core.ui.R.string.editor_status_bar)
                    }
            ) {
                AnimatedVisibility(
                    visible = uiState.isAutoSaving,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = stringResource(com.inkwell.core.ui.R.string.auto_saving),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .semantics {
                                contentDescription = autoSaveIndicatorDescription
                            }
                    )
                }

                Text(
                    text = stringResource(
                        com.inkwell.core.ui.R.string.editor_bottom_bar,
                        uiState.wordCount,
                        uiState.chapterCount
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .semantics {
                            contentDescription = "$wordCountDescription, $chapterCountDescription"
                        }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.saveChapter() },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.semantics {
                    contentDescription = saveDescription
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            var contentText by remember(uiState.content) {
                mutableStateOf(TextFieldValue(uiState.content))
            }

            BasicTextField(
                value = contentText,
                onValueChange = { newValue ->
                    contentText = newValue
                    viewModel.updateContent(newValue.text)
                },
                textStyle = TextStyle(
                    fontSize = 18.sp,
                    lineHeight = 28.sp,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .focusRequester(focusRequester)
                    .semantics {
                        contentDescription = editorDescription
                    }
            )
        }
    }

    if (showChapterSheet) {
        ChapterListSheet(
            chapters = uiState.chapters,
            currentChapterId = uiState.currentChapter?.id,
            onChapterSelected = { chapterId ->
                viewModel.selectChapter(chapterId)
                showChapterSheet = false
            },
            onAddChapter = {
                viewModel.addChapter()
            },
            onDeleteChapter = { chapterId ->
                viewModel.deleteChapter(chapterId)
            },
            onReorderChapters = { fromIndex, toIndex ->
                viewModel.reorderChapters(fromIndex, toIndex)
            },
            onDismiss = { showChapterSheet = false }
        )
    }
}
