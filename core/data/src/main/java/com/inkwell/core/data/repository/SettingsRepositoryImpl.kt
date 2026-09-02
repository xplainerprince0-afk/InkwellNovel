package com.inkwell.core.data.repository

import com.inkwell.core.data.local.PreferencesManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val preferencesManager: PreferencesManager
) : SettingsRepository {

    override fun getDarkMode(): Flow<Boolean> = preferencesManager.isDarkMode
    override suspend fun setDarkMode(enabled: Boolean) = preferencesManager.setDarkMode(enabled)
    override fun getAutoSave(): Flow<Boolean> = preferencesManager.isAutoSaveEnabled
    override suspend fun setAutoSave(enabled: Boolean) = preferencesManager.setAutoSave(enabled)
    override fun getBiometricLock(): Flow<Boolean> = preferencesManager.isBiometricEnabled
    override suspend fun setBiometricLock(enabled: Boolean) = preferencesManager.setBiometricEnabled(enabled)
    override fun getFontSize(): Flow<Int> = preferencesManager.fontSize
    override suspend fun setFontSize(size: Int) = preferencesManager.setFontSize(size)
}
