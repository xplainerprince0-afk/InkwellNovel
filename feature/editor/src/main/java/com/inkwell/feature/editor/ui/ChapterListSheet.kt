package com.inkwell.feature.editor.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.inkwell.core.data.repository.model.Chapter
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterListSheet(
    chapters: List<Chapter>,
    currentChapterId: String?,
    onChapterSelected: (String) -> Unit,
    onAddChapter: () -> Unit,
    onDeleteChapter: (String) -> Unit,
    onReorderChapters: (Int, Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var draggedItemIndex by remember { mutableStateOf<Int?>(null) }

    val chaptersListDescription = stringResource(
        com.inkwell.feature.characters.ui.R.string.chapter_list_description,
        chapters.size
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier.semantics {
            contentDescription = chaptersListDescription
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(com.inkwell.feature.characters.ui.R.string.chapters),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = {
                        onAddChapter()
                        scope.launch {
                            sheetState.hide()
                            onDismiss()
                        }
                    },
                    modifier = Modifier.semantics {
                        contentDescription = stringResource(com.inkwell.feature.characters.ui.R.string.add_chapter)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (chapters.isEmpty()) {
                Text(
                    text = stringResource(com.inkwell.feature.characters.ui.R.string.no_chapters),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    modifier = Modifier.height(400.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(
                        items = chapters,
                        key = { _, chapter -> chapter.id }
                    ) { index, chapter ->
                        ChapterItem(
                            chapter = chapter,
                            isSelected = chapter.id == currentChapterId,
                            onChapterSelected = {
                                onChapterSelected(chapter.id)
                                scope.launch {
                                    sheetState.hide()
                                    onDismiss()
                                }
                            },
                            onDeleteChapter = { onDeleteChapter(chapter.id) },
                            onDragStart = { draggedItemIndex = index },
                            onDragEnd = { targetIndex ->
                                draggedItemIndex?.let { sourceIndex ->
                                    onReorderChapters(sourceIndex, targetIndex)
                                }
                                draggedItemIndex = null
                            },
                            modifier = Modifier.animateContentSize()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = {
                    scope.launch {
                        sheetState.hide()
                        onDismiss()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(com.inkwell.feature.characters.ui.R.string.close))
            }
        }
    }
}

@Composable
private fun ChapterItem(
    chapter: Chapter,
    isSelected: Boolean,
    onChapterSelected: () -> Unit,
    onDeleteChapter: () -> Unit,
    onDragStart: () -> Unit,
    onDragEnd: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }

    val contentDescription = stringResource(
        com.inkwell.feature.characters.ui.R.string.chapter_item_description,
        chapter.title,
        chapter.wordCount,
        if (isSelected) stringResource(com.inkwell.feature.characters.ui.R.string.currently_selected) else ""
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(onClick = onChapterSelected)
            .padding(12.dp)
            .semantics {
                this.contentDescription = contentDescription
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.DragHandle,
            contentDescription = stringResource(com.inkwell.feature.characters.ui.R.string.drag_to_reorder),
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    onDragStart()
                }
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = chapter.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = stringResource(com.inkwell.feature.characters.ui.R.string.word_count, chapter.wordCount),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(
            onClick = onDeleteChapter,
            modifier = Modifier.semantics {
                contentDescription = stringResource(
                    com.inkwell.feature.characters.ui.R.string.delete_chapter,
                    chapter.title
                )
            }
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}
