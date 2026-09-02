package com.inkwell.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.inkwell.app.worker.AutoSaveWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class InkwellApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        initializeFirebase()
        initializeCrashlytics()
        initializeWorkManager()
    }

    private fun initializeFirebase() {
        // Firebase is initialized via google-services.json
        // No additional initialization needed here
    }

    private fun initializeCrashlytics() {
        val crashlytics = FirebaseCrashlytics.getInstance()
        crashlytics.setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
    }

    private fun initializeWorkManager() {
        val autoSaveRequest = PeriodicWorkRequestBuilder<AutoSaveWorker>(
            repeatInterval = 15,
            repeatIntervalTimeUnit = TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "auto_save_work",
            ExistingPeriodicWorkPolicy.KEEP,
            autoSaveRequest
        )
    }
}
