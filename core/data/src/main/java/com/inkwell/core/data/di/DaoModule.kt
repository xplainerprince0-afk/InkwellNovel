package com.inkwell.core.data.di

import com.inkwell.core.data.local.dao.ChapterDao
import com.inkwell.core.data.local.dao.CharacterDao
import com.inkwell.core.data.local.dao.NovelDao
import com.inkwell.core.data.local.dao.WritingSessionDao
import com.inkwell.core.data.local.dao.WorldNoteDao
import com.inkwell.core.data.local.database.InkwellDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {

    @Provides
    @Singleton
    fun provideNovelDao(database: InkwellDatabase): NovelDao {
        return database.novelDao()
    }

    @Provides
    @Singleton
    fun provideChapterDao(database: InkwellDatabase): ChapterDao {
        return database.chapterDao()
    }

    @Provides
    @Singleton
    fun provideCharacterDao(database: InkwellDatabase): CharacterDao {
        return database.characterDao()
    }

    @Provides
    @Singleton
    fun provideWorldNoteDao(database: InkwellDatabase): WorldNoteDao {
        return database.worldNoteDao()
    }

    @Provides
    @Singleton
    fun provideWritingSessionDao(database: InkwellDatabase): WritingSessionDao {
        return database.writingSessionDao()
    }
}
