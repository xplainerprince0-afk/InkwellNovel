package com.inkwell.feature.characters.ui

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
import com.inkwell.core.data.repository.model.CharacterRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailScreen(
    characterId: String?,
    novelId: String,
    onNavigateBack: () -> Unit,
    viewModel: CharactersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isEditing = characterId != null

    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf(CharacterRole.SUPPORTING) }
    var description by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var isRoleDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(characterId) {
        if (characterId != null) {
            val character = uiState.characters.find { it.id == characterId }
            character?.let {
                name = it.name
                role = it.role
                description = it.description
                notes = it.notes
            }
        }
    }

    val titleDescription = stringResource(
        if (isEditing) com.inkwell.core.ui.R.string.edit_character
        else com.inkwell.core.ui.R.string.add_character
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = titleDescription)
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
                onClick = {
                    if (isEditing && characterId != null) {
                        viewModel.updateCharacter(characterId, name, role, description, notes)
                    } else {
                        viewModel.addCharacter(name, role, description, notes)
                    }
                    onNavigateBack()
                },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.semantics {
                    contentDescription = stringResource(com.inkwell.core.ui.R.string.save_character)
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
                value = name,
                onValueChange = { name = it },
                label = {
                    Text(text = stringResource(com.inkwell.core.ui.R.string.character_name))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = stringResource(com.inkwell.core.ui.R.string.character_name_field)
                    },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = isRoleDropdownExpanded,
                onExpandedChange = { isRoleDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = role.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text(text = stringResource(com.inkwell.core.ui.R.string.character_role))
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isRoleDropdownExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .semantics {
                            contentDescription = stringResource(
                                com.inkwell.core.ui.R.string.character_role_dropdown,
                                role.displayName
                            )
                        }
                )

                ExposedDropdownMenu(
                    expanded = isRoleDropdownExpanded,
                    onDismissRequest = { isRoleDropdownExpanded = false }
                ) {
                    CharacterRole.entries.forEach { selectedRole ->
                        DropdownMenuItem(
                            text = { Text(text = selectedRole.displayName) },
                            onClick = {
                                role = selectedRole
                                isRoleDropdownExpanded = false
                            },
                            modifier = Modifier.semantics {
                                contentDescription = stringResource(
                                    com.inkwell.core.ui.R.string.select_role,
                                    selectedRole.displayName
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = {
                    Text(text = stringResource(com.inkwell.core.ui.R.string.character_description))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .semantics {
                        contentDescription = stringResource(com.inkwell.core.ui.R.string.character_description_field)
                    },
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = {
                    Text(text = stringResource(com.inkwell.core.ui.R.string.character_notes))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .semantics {
                        contentDescription = stringResource(com.inkwell.core.ui.R.string.character_notes_field)
                    },
                maxLines = 10
            )
        }
    }
}
