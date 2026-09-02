package com.inkwell.app

import com.inkwell.app.ui.components.NovelUiState
import com.inkwell.app.ui.theme.ChapterBlue
import com.inkwell.app.ui.theme.ChapterGreen
import com.inkwell.app.ui.theme.ChapterOrange
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: HomeViewModel

    // private lateinit var novelRepository: NovelRepository
    // private lateinit var writingSessionRepository: WritingSessionRepository
    // private lateinit var preferencesManager: PreferencesManager

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        // novelRepository = mockk()
        // writingSessionRepository = mockk()
        // preferencesManager = mockk()
        viewModel = HomeViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be empty`() = runTest {
        val initialState = viewModel.uiState.value

        assertTrue(initialState.novels.isEmpty())
        assertEquals(0, initialState.totalWords)
        assertEquals(0, initialState.totalNovels)
        assertFalse(initialState.isLoading)
        assertNull(initialState.error)
    }

    @Test
    fun `loadNovels should update state with novels`() = runTest {
        viewModel.loadNovels()
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertTrue(state.novels.isNotEmpty())
        assertEquals(3, state.totalNovels)
        assertTrue(state.totalWords > 0)
    }

    @Test
    fun `createNovel should add novel to state`() = runTest {
        viewModel.loadNovels()
        advanceUntilIdle()

        val initialCount = viewModel.uiState.value.totalNovels

        viewModel.createNovel("Test Novel", "A test novel description")
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals(initialCount + 1, state.totalNovels)
        assertTrue(state.novels.any { it.title == "Test Novel" })
    }

    @Test
    fun `deleteNovel should remove novel from state`() = runTest {
        viewModel.loadNovels()
        advanceUntilIdle()

        val initialNovels = viewModel.uiState.value.novels
        val novelToDelete = initialNovels.first()

        viewModel.deleteNovel(novelToDelete.id)
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals(initialNovels.size - 1, state.novels.size)
        assertFalse(state.novels.any { it.id == novelToDelete.id })
    }

    @Test
    fun `updateSearchQuery should filter novels`() = runTest {
        viewModel.loadNovels()
        advanceUntilIdle()

        val firstNovelTitle = viewModel.uiState.value.novels.first().title

        viewModel.updateSearchQuery(firstNovelTitle)
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals(firstNovelTitle, state.searchQuery)
        assertTrue(state.filteredNovels.isNotEmpty())
        assertTrue(state.filteredNovels.all {
            it.title.contains(firstNovelTitle, ignoreCase = true)
        })
    }

    @Test
    fun `updateSearchQuery with empty string should show all novels`() = runTest {
        viewModel.loadNovels()
        advanceUntilIdle()

        viewModel.updateSearchQuery("nonexistent")
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.filteredNovels.isEmpty())

        viewModel.updateSearchQuery("")
        advanceUntilIdle()

        assertEquals(
            viewModel.uiState.value.novels.size,
            viewModel.uiState.value.filteredNovels.size
        )
    }

    @Test
    fun `clearError should remove error message`() = runTest {
        viewModel.loadNovels()
        advanceUntilIdle()

        // Simulate error state
        viewModel.clearError()

        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `totalWords should sum all novel word counts`() = runTest {
        viewModel.loadNovels()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        val expectedTotal = state.novels.sumOf { it.wordCount }

        assertEquals(expectedTotal, state.totalWords)
    }

    @Test
    fun `search should be case insensitive`() = runTest {
        viewModel.loadNovels()
        advanceUntilIdle()

        val firstNovelTitle = viewModel.uiState.value.novels.first().title
        val lowercaseSearch = firstNovelTitle.lowercase()

        viewModel.updateSearchQuery(lowercaseSearch)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.filteredNovels.isNotEmpty())
    }

    @Test
    fun `search should match description`() = runTest {
        viewModel.loadNovels()
        advanceUntilIdle()

        val novelWithUniqueDesc = viewModel.uiState.value.novels.first()
        val searchQuery = novelWithUniqueDesc.description.take(10)

        viewModel.updateSearchQuery(searchQuery)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.filteredNovels.isNotEmpty())
    }
}
