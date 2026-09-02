package com.inkwell.feature.world.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.inkwell.core.data.repository.model.WorldNote
import com.inkwell.core.data.repository.model.WorldNoteCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorldScreen(
    novelId: String,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String?) -> Unit,
    viewModel: WorldViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(novelId) {
        viewModel.loadNotes(novelId)
    }

    val categories = WorldNoteCategory.entries
    val worldDescription = stringResource(
        com.inkwell.core.ui.R.string.world_screen_description,
        uiState.filteredNotes.size,
        uiState.selectedCategory.displayName
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(com.inkwell.core.ui.R.string.world_building))
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToDetail(null) },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.semantics {
                    contentDescription = stringResource(com.inkwell.core.ui.R.string.add_world_note)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .semantics {
                    contentDescription = worldDescription
                }
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.semantics {
                    contentDescription = stringResource(com.inkwell.core.ui.R.string.category_tabs)
                }
            ) {
                categories.forEachIndexed { index, category ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            selectedTabIndex = index
                            viewModel.filterByCategory(category)
                        },
                        text = {
                            Text(
                                text = category.displayName,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        modifier = Modifier.semantics {
                            contentDescription = stringResource(
                                com.inkwell.core.ui.R.string.category_tab_description,
                                category.displayName
                            )
                        }
                    )
                }
            }

            if (uiState.filteredNotes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(com.inkwell.core.ui.R.string.no_notes_in_category),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.filteredNotes,
                        key = { it.id }
                    ) { note ->
                        WorldNoteCard(
                            note = note,
                            onClick = { onNavigateToDetail(note.id) },
                            onDelete = { showDeleteDialog = note.id },
                            onTagLocation = { /* Handle location tagging */ }
                        )
                    }
                }
            }
        }
    }

    showDeleteDialog?.let { noteId ->
        val note = uiState.notes.find { it.id == noteId }
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = {
                Text(text = stringResource(com.inkwell.core.ui.R.string.delete_note))
            },
            text = {
                Text(
                    text = stringResource(
                        com.inkwell.core.ui.R.string.delete_note_confirmation,
                        note?.title ?: ""
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteNote(noteId)
                        showDeleteDialog = null
                    }
                ) {
                    Text(text = stringResource(com.inkwell.core.ui.R.string.delete))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = null }
                ) {
                    Text(text = stringResource(com.inkwell.core.ui.R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun WorldNoteCard(
    note: WorldNote,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onTagLocation: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .semantics {
                contentDescription = stringResource(
                    com.inkwell.core.ui.R.string.note_card_description,
                    note.title,
                    note.category.displayName,
                    note.content.take(50)
                )
            },
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Row {
                    if (note.locationTag != null) {
                        IconButton(
                            onClick = onTagLocation,
                            modifier = Modifier
                                .size(24.dp)
                                .semantics {
                                    contentDescription = stringResource(
                                        com.inkwell.core.ui.R.string.location_tagged,
                                        note.locationTag
                                    )
                                }
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(24.dp)
                            .semantics {
                                contentDescription = stringResource(
                                    com.inkwell.core.ui.R.string.delete_note,
                                    note.title
                                )
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                    .androidx.compose.foundation.background(MaterialTheme.colorScheme.secondaryContainer)
                    .androidx.compose.foundation.layout.padding(
                        horizontal = 8.dp,
                        vertical = 4.dp
                    )
                    .semantics {
                        contentDescription = stringResource(
                            com.inkwell.core.ui.R.string.category_badge_description,
                            note.category.displayName
                        )
                    }
            ) {
                Text(
                    text = note.category.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
