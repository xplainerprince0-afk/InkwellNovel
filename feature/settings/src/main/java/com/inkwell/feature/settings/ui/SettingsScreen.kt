package com.inkwell.feature.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(com.inkwell.feature.characters.ui.R.string.settings))
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .semantics {
                    contentDescription = stringResource(com.inkwell.feature.characters.ui.R.string.settings_screen_description)
                }
        ) {
            SettingsSection(
                title = stringResource(com.inkwell.feature.characters.ui.R.string.appearance_section)
            ) {
                DarkModeSetting(
                    isDarkMode = uiState.isDarkMode,
                    onToggle = { viewModel.updateDarkMode(it) }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                FontSizeSetting(
                    fontSize = uiState.fontSize,
                    onFontSizeChange = { viewModel.updateFontSize(it) }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                FontFamilySetting(
                    fontFamily = uiState.fontFamily,
                    onFontFamilyChange = { viewModel.updateFontFamily(it) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            SettingsSection(
                title = stringResource(com.inkwell.feature.characters.ui.R.string.writing_section)
            ) {
                DailyWordGoalSetting(
                    goal = uiState.dailyWordGoal,
                    onGoalChange = { viewModel.updateDailyWordGoal(it) }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                AutoSaveSetting(
                    isEnabled = uiState.isAutoSaveEnabled,
                    onToggle = { viewModel.toggleAutoSave(it) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            SettingsSection(
                title = stringResource(com.inkwell.feature.characters.ui.R.string.security_section)
            ) {
                BiometricSetting(
                    isEnabled = uiState.isBiometricEnabled,
                    onToggle = { viewModel.toggleBiometric(it) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            SettingsSection(
                title = stringResource(com.inkwell.feature.characters.ui.R.string.account_section)
            ) {
                AccountSetting(
                    userName = uiState.userName,
                    userEmail = uiState.userEmail
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            SettingsSection(
                title = stringResource(com.inkwell.feature.characters.ui.R.string.about_section)
            ) {
                AboutSetting(appVersion = uiState.appVersion)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
            content()
        }
    }
}

@Composable
private fun DarkModeSetting(
    isDarkMode: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val darkModeDescription = stringResource(
        com.inkwell.feature.characters.ui.R.string.dark_mode_description,
        if (isDarkMode) stringResource(com.inkwell.feature.characters.ui.R.string.enabled) else stringResource(com.inkwell.feature.characters.ui.R.string.disabled)
    )

    SettingItem(
        title = stringResource(com.inkwell.feature.characters.ui.R.string.dark_mode),
        description = stringResource(com.inkwell.feature.characters.ui.R.string.dark_mode_subtitle),
        modifier = modifier.semantics {
            contentDescription = darkModeDescription
        }
    ) {
        Switch(
            checked = isDarkMode,
            onCheckedChange = onToggle
        )
    }
}

@Composable
private fun FontSizeSetting(
    fontSize: Float,
    onFontSizeChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val fontSizeDescription = stringResource(
        com.inkwell.feature.characters.ui.R.string.font_size_description,
        fontSize.toInt()
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .semantics {
                contentDescription = fontSizeDescription
            }
    ) {
        Text(
            text = stringResource(com.inkwell.feature.characters.ui.R.string.font_size),
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = stringResource(com.inkwell.feature.characters.ui.R.string.current_size, fontSize.toInt()),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Slider(
            value = fontSize,
            onValueChange = onFontSizeChange,
            valueRange = 12f..24f,
            steps = 11,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun FontFamilySetting(
    fontFamily: String,
    onFontFamilyChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val fontFamilies = listOf("Default", "Serif", "Sans-Serif", "Monospace")
    var expanded by remember { mutableStateOf(false) }

    SettingItem(
        title = stringResource(com.inkwell.feature.characters.ui.R.string.font_family),
        description = fontFamily,
        modifier = modifier.semantics {
            contentDescription = stringResource(
                com.inkwell.feature.characters.ui.R.string.font_family_description,
                fontFamily
            )
        }
    ) {
        androidx.compose.material3.DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            fontFamilies.forEach { family ->
                androidx.compose.material3.DropdownMenuItem(
                    text = { Text(text = family) },
                    onClick = {
                        onFontFamilyChange(family)
                        expanded = false
                    }
                )
            }
        }

        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun DailyWordGoalSetting(
    goal: Int,
    onGoalChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val goalDescription = stringResource(
        com.inkwell.feature.characters.ui.R.string.daily_word_goal_description,
        goal
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .semantics {
                contentDescription = goalDescription
            }
    ) {
        Text(
            text = stringResource(com.inkwell.feature.characters.ui.R.string.daily_word_goal),
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = stringResource(com.inkwell.feature.characters.ui.R.string.words_per_day, goal),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Slider(
            value = goal.toFloat(),
            onValueChange = { onGoalChange(it.toInt()) },
            valueRange = 100f..5000f,
            steps = 48,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun AutoSaveSetting(
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val autoSaveDescription = stringResource(
        com.inkwell.feature.characters.ui.R.string.auto_save_description,
        if (isEnabled) stringResource(com.inkwell.feature.characters.ui.R.string.enabled) else stringResource(com.inkwell.feature.characters.ui.R.string.disabled)
    )

    SettingItem(
        title = stringResource(com.inkwell.feature.characters.ui.R.string.auto_save),
        description = stringResource(com.inkwell.feature.characters.ui.R.string.auto_save_subtitle),
        modifier = modifier.semantics {
            contentDescription = autoSaveDescription
        }
    ) {
        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle
        )
    }
}

@Composable
private fun BiometricSetting(
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val biometricDescription = stringResource(
        com.inkwell.feature.characters.ui.R.string.biometric_description,
        if (isEnabled) stringResource(com.inkwell.feature.characters.ui.R.string.enabled) else stringResource(com.inkwell.feature.characters.ui.R.string.disabled)
    )

    SettingItem(
        title = stringResource(com.inkwell.feature.characters.ui.R.string.biometric_lock),
        description = stringResource(com.inkwell.feature.characters.ui.R.string.biometric_subtitle),
        modifier = modifier.semantics {
            contentDescription = biometricDescription
        }
    ) {
        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle
        )
    }
}

@Composable
private fun AccountSetting(
    userName: String,
    userEmail: String,
    modifier: Modifier = Modifier
) {
    SettingItem(
        title = userName.ifBlank { stringResource(com.inkwell.feature.characters.ui.R.string.user_name) },
        description = userEmail.ifBlank { stringResource(com.inkwell.feature.characters.ui.R.string.user_email) },
        modifier = modifier.semantics {
            contentDescription = stringResource(
                com.inkwell.feature.characters.ui.R.string.account_description,
                userName,
                userEmail
            )
        }
    ) {
        IconButton(onClick = { /* Navigate to account edit */ }) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun AboutSetting(
    appVersion: String,
    modifier: Modifier = Modifier
) {
    SettingItem(
        title = stringResource(com.inkwell.feature.characters.ui.R.string.version),
        description = appVersion,
        modifier = modifier.semantics {
            contentDescription = stringResource(
                com.inkwell.feature.characters.ui.R.string.version_description,
                appVersion
            )
        }
    ) {
        IconButton(onClick = { /* Show licenses */ }) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun SettingItem(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        trailing()
    }
}
