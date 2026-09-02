package com.inkwell.core.data.di

import android.content.Context
import androidx.room.Room
import com.inkwell.core.data.local.database.InkwellDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): InkwellDatabase {
        return Room.databaseBuilder(
            context,
            InkwellDatabase::class.java,
            InkwellDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}
