package com.inkwell.core.data.di

import com.inkwell.core.data.repository.ChapterRepository
import com.inkwell.core.data.repository.ChapterRepositoryImpl
import com.inkwell.core.data.repository.CharacterRepository
import com.inkwell.core.data.repository.CharacterRepositoryImpl
import com.inkwell.core.data.repository.NovelRepository
import com.inkwell.core.data.repository.NovelRepositoryImpl
import com.inkwell.core.data.repository.SettingsRepository
import com.inkwell.core.data.repository.SettingsRepositoryImpl
import com.inkwell.core.data.repository.WritingSessionRepository
import com.inkwell.core.data.repository.WritingSessionRepositoryImpl
import com.inkwell.core.data.repository.WorldNoteRepository
import com.inkwell.core.data.repository.WorldNoteRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindNovelRepository(impl: NovelRepositoryImpl): NovelRepository

    @Binds
    @Singleton
    abstract fun bindChapterRepository(impl: ChapterRepositoryImpl): ChapterRepository

    @Binds
    @Singleton
    abstract fun bindCharacterRepository(impl: CharacterRepositoryImpl): CharacterRepository

    @Binds
    @Singleton
    abstract fun bindWorldNoteRepository(impl: WorldNoteRepositoryImpl): WorldNoteRepository

    @Binds
    @Singleton
    abstract fun bindWritingSessionRepository(impl: WritingSessionRepositoryImpl): WritingSessionRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}
