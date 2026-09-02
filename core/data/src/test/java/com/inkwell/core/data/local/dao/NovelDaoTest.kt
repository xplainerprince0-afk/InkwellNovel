package com.inkwell.core.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.inkwell.core.data.local.database.InkwellDatabase
import com.inkwell.core.data.local.entity.NovelEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Date

@RunWith(AndroidJUnit4::class)
class NovelDaoTest {

    private lateinit var database: InkwellDatabase
    private lateinit var novelDao: NovelDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            InkwellDatabase::class.java
        ).allowMainThreadQueries().build()

        novelDao = database.novelDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertNovel_returnsSuccess() = runTest {
        val novel = createTestNovel(id = 1, title = "Test Novel")

        novelDao.insert(novel)

        val retrieved = novelDao.getById(1)
        assertNotNull(retrieved)
        assertEquals("Test Novel", retrieved?.title)
    }

    @Test
    fun insertMultipleNovels_returnsAll() = runTest {
        val novels = listOf(
            createTestNovel(id = 1, title = "Novel 1"),
            createTestNovel(id = 2, title = "Novel 2"),
            createTestNovel(id = 3, title = "Novel 3")
        )

        novels.forEach { novelDao.insert(it) }

        val allNovels = novelDao.getAll().first()
        assertEquals(3, allNovels.size)
    }

    @Test
    fun getById_returnsCorrectNovel() = runTest {
        val novel = createTestNovel(id = 1, title = "Specific Novel")

        novelDao.insert(novel)

        val retrieved = novelDao.getById(1)
        assertNotNull(retrieved)
        assertEquals("Specific Novel", retrieved?.title)
        assertEquals(1L, retrieved?.id)
    }

    @Test
    fun getById_returnsNullForNonexistentId() = runTest {
        val retrieved = novelDao.getById(999)
        assertNull(retrieved)
    }

    @Test
    fun updateNovel_modifiesExistingRecord() = runTest {
        val novel = createTestNovel(id = 1, title = "Original Title")
        novelDao.insert(novel)

        val updatedNovel = novel.copy(title = "Updated Title")
        novelDao.update(updatedNovel)

        val retrieved = novelDao.getById(1)
        assertEquals("Updated Title", retrieved?.title)
    }

    @Test
    fun deleteNovel_removesRecord() = runTest {
        val novel = createTestNovel(id = 1, title = "To Delete")
        novelDao.insert(novel)

        novelDao.delete(novel)

        val retrieved = novelDao.getById(1)
        assertNull(retrieved)
    }

    @Test
    fun deleteAll_clearsAllRecords() = runTest {
        val novels = listOf(
            createTestNovel(id = 1, title = "Novel 1"),
            createTestNovel(id = 2, title = "Novel 2")
        )
        novels.forEach { novelDao.insert(it) }

        novelDao.deleteAll()

        val allNovels = novelDao.getAll().first()
        assertTrue(allNovels.isEmpty())
    }

    @Test
    fun getAll_returnsFlow() = runTest {
        val novel = createTestNovel(id = 1, title = "Flow Test")
        novelDao.insert(novel)

        val flow = novelDao.getAll()
        val novels = flow.first()

        assertEquals(1, novels.size)
        assertEquals("Flow Test", novels[0].title)
    }

    @Test
    fun searchByTitle_returnsMatchingNovels() = runTest {
        val novels = listOf(
            createTestNovel(id = 1, title = "The Great Adventure"),
            createTestNovel(id = 2, title = "Mystery of the Night"),
            createTestNovel(id = 3, title = "Adventure Continues")
        )
        novels.forEach { novelDao.insert(it) }

        val results = novelDao.searchByTitle("Adventure").first()
        assertEquals(2, results.size)
        assertTrue(results.all { it.title.contains("Adventure") })
    }

    @Test
    fun insertNovel_withWordCount_storesCorrectly() = runTest {
        val novel = createTestNovel(id = 1, title = "Long Novel", wordCount = 50000)

        novelDao.insert(novel)

        val retrieved = novelDao.getById(1)
        assertEquals(50000, retrieved?.wordCount)
    }

    @Test
    fun updateWordCount_modifiesCorrectly() = runTest {
        val novel = createTestNovel(id = 1, title = "Growing Novel", wordCount = 1000)
        novelDao.insert(novel)

        val updatedNovel = novel.copy(wordCount = 2500)
        novelDao.update(updatedNovel)

        val retrieved = novelDao.getById(1)
        assertEquals(2500, retrieved?.wordCount)
    }

    @Test
    fun insertNovel_withDescription_storesCorrectly() = runTest {
        val novel = createTestNovel(
            id = 1,
            title = "Descriptive Novel",
            description = "A novel with a detailed description"
        )

        novelDao.insert(novel)

        val retrieved = novelDao.getById(1)
        assertEquals("A novel with a detailed description", retrieved?.description)
    }

    private fun createTestNovel(
        id: Long = 1,
        title: String = "Test Novel",
        description: String = "A test novel description",
        wordCount: Int = 0
    ): NovelEntity {
        return NovelEntity(
            id = id,
            title = title,
            description = description,
            wordCount = wordCount,
            coverColor = "#6650A4",
            isBiometricLocked = false,
            createdAt = Date(),
            updatedAt = Date()
        )
    }
}
