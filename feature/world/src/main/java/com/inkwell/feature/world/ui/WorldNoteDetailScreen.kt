package com.inkwell.feature.world.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.inkwell.core.data.repository.model.WorldNoteCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorldNoteDetailScreen(
    noteId: String?,
    novelId: String,
    onNavigateBack: () -> Unit,
    onNavigateToMap: () -> Unit,
    viewModel: WorldViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isEditing = noteId != null

    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(WorldNoteCategory.LOCATIONS) }
    var content by remember { mutableStateOf("") }
    var locationTag by remember { mutableStateOf("") }
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(noteId) {
        if (noteId != null) {
            val note = uiState.notes.find { it.id == noteId }
            note?.let {
                title = it.title
                category = it.category
                content = it.content
                locationTag = it.locationTag ?: ""
            }
        }
    }

    val screenTitle = stringResource(
        if (isEditing) com.inkwell.feature.characters.ui.R.string.edit_note
        else com.inkwell.feature.characters.ui.R.string.add_note
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = screenTitle)
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.semantics {
                            contentDescription = stringResource(com.inkwell.feature.characters.ui.R.string.navigate_back)
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
                onClick = {
                    if (isEditing && noteId != null) {
                        viewModel.updateNote(
                            noteId,
                            title,
                            category,
                            content,
                            locationTag.ifBlank { null }
                        )
                    } else {
                        viewModel.addNote(
                            title,
                            category,
                            content,
                            locationTag.ifBlank { null }
                        )
                    }
                    onNavigateBack()
                },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.semantics {
                    contentDescription = stringResource(com.inkwell.feature.characters.ui.R.string.save_note)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = {
                    Text(text = stringResource(com.inkwell.feature.characters.ui.R.string.note_title))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = stringResource(com.inkwell.feature.characters.ui.R.string.note_title_field)
                    },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = isCategoryDropdownExpanded,
                onExpandedChange = { isCategoryDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = category.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text(text = stringResource(com.inkwell.feature.characters.ui.R.string.note_category))
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryDropdownExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .semantics {
                            contentDescription = stringResource(
                                com.inkwell.feature.characters.ui.R.string.note_category_dropdown,
                                category.displayName
                            )
                        }
                )

                ExposedDropdownMenu(
                    expanded = isCategoryDropdownExpanded,
                    onDismissRequest = { isCategoryDropdownExpanded = false }
                ) {
                    WorldNoteCategory.entries.forEach { selectedCategory ->
                        DropdownMenuItem(
                            text = { Text(text = selectedCategory.displayName) },
                            onClick = {
                                category = selectedCategory
                                isCategoryDropdownExpanded = false
                            },
                            modifier = Modifier.semantics {
                                contentDescription = stringResource(
                                    com.inkwell.feature.characters.ui.R.string.select_category,
                                    selectedCategory.displayName
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = {
                    Text(text = stringResource(com.inkwell.feature.characters.ui.R.string.note_content))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .semantics {
                        contentDescription = stringResource(com.inkwell.feature.characters.ui.R.string.note_content_field)
                    },
                maxLines = 15
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = locationTag,
                onValueChange = { locationTag = it },
                label = {
                    Text(text = stringResource(com.inkwell.feature.characters.ui.R.string.location_tag))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = stringResource(com.inkwell.feature.characters.ui.R.string.location_tag_field)
                    },
                singleLine = true
            )
        }
    }
}
