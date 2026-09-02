package com.inkwell.feature.settings.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Settings") }) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(16.dp)
        ) {
            item { Text("Appearance", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(vertical = 8.dp)) }
            item { SettingsSwitch("Dark Mode", uiState.darkMode) { viewModel.setDarkMode(it) } }
            item { SettingsSwitch("Auto-Save", uiState.autoSave) { viewModel.setAutoSave(it) } }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item { Text("Writing", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(vertical = 8.dp)) }
            item { SettingsSwitch("Biometric Lock", uiState.biometricLock) { viewModel.setBiometricLock(it) } }
        }
    }
}

@Composable
private fun SettingsSwitch(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
