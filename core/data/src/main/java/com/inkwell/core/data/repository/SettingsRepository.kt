package com.inkwell.core.data.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getDarkMode(): Flow<Boolean>
    suspend fun setDarkMode(enabled: Boolean)
    fun getAutoSave(): Flow<Boolean>
    suspend fun setAutoSave(enabled: Boolean)
    fun getBiometricLock(): Flow<Boolean>
    suspend fun setBiometricLock(enabled: Boolean)
    fun getFontSize(): Flow<Int>
    suspend fun setFontSize(size: Int)
}
