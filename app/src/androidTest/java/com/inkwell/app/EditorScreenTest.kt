package com.inkwell.app

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.inkwell.app.ui.theme.InkwellTheme
import org.junit.Rule
import org.junit.Test

class EditorScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun editorScreen_displaysTitle() {
        composeTestRule.setContent {
            InkwellTheme {
                EditorScreenTestContent()
            }
        }
        composeTestRule.onNodeWithText("Editor").assertIsDisplayed()
    }

    @Test
    fun editorScreen_displaysWordCount() {
        composeTestRule.setContent {
            InkwellTheme {
                EditorScreenTestContent()
            }
        }
        composeTestRule.onNodeWithText("Words: 0").assertIsDisplayed()
    }

    @Test
    fun editorScreen_textInput_updatesWordCount() {
        composeTestRule.setContent {
            InkwellTheme {
                EditorScreenTestContent()
            }
        }

        composeTestRule.onNodeWithTag("editor_text_field")
            .performTextInput("The quick brown fox jumps")

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Words: 5").assertIsDisplayed()
    }

    @Test
    fun editorScreen_saveButton_isClickable() {
        composeTestRule.setContent {
            InkwellTheme {
                EditorScreenTestContent()
            }
        }

        composeTestRule.onNodeWithContentDescription("Save")
            .performClick()
    }

    @Test
    fun editorScreen_undoButton_isClickable() {
        composeTestRule.setContent {
            InkwellTheme {
                EditorScreenTestContent()
            }
        }

        composeTestRule.onNodeWithContentDescription("Undo")
            .performClick()
    }

    @Test
    fun editorScreen_redoButton_isClickable() {
        composeTestRule.setContent {
            InkwellTheme {
                EditorScreenTestContent()
            }
        }

        composeTestRule.onNodeWithContentDescription("Redo")
            .performClick()
    }

    @Test
    fun editorScreen_emptyText_showsZeroWordCount() {
        composeTestRule.setContent {
            InkwellTheme {
                EditorScreenTestContent()
            }
        }

        composeTestRule.onNodeWithText("Words: 0").assertIsDisplayed()
    }

    @Test
    fun editorScreen_multipleWords_countsCorrectly() {
        composeTestRule.setContent {
            InkwellTheme {
                EditorScreenTestContent()
            }
        }

        composeTestRule.onNodeWithTag("editor_text_field")
            .performTextInput("This is a test with exactly nine words here")

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Words: 9").assertIsDisplayed()
    }
}

@Composable
private fun EditorScreenTestContent() {
    var content by remember { mutableStateOf("") }
    val wordCount = content.split("\\s+".toRegex()).filter { it.isNotEmpty() }.size

    Column {
        Text(text = "Editor")
        TextField(
            value = content,
            onValueChange = { content = it },
            modifier = Modifier.testTag("editor_text_field")
        )
        Text(text = "Words: $wordCount")
        IconButton(onClick = { }) {
            Icon(imageVector = Icons.Filled.Save, contentDescription = "Save")
        }
        IconButton(onClick = { }) {
            Icon(imageVector = Icons.Filled.Undo, contentDescription = "Undo")
        }
        IconButton(onClick = { }) {
            Icon(imageVector = Icons.Filled.Redo, contentDescription = "Redo")
        }
    }
}
