package com.inkwell.app.worker

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File
import java.io.FileOutputStream

@HiltWorker
class ExportWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val novelId = inputData.getLong(KEY_NOVEL_ID, -1)
            val outputFormat = inputData.getString(KEY_OUTPUT_FORMAT) ?: FORMAT_TXT

            if (novelId == -1L) {
                Log.e(TAG, "Invalid novel ID")
                return Result.failure()
            }

            Log.d(TAG, "Starting export for novel: $novelId, format: $outputFormat")

            // TODO: Implement actual export logic
            // 1. Fetch novel data from database
            // 2. Fetch all chapters
            // 3. Convert to desired format
            // 4. Write to output file

            val outputFile = createOutputFile(novelId, outputFormat)

            // Placeholder for actual content generation
            val content = generateSampleContent(novelId)
            FileOutputStream(outputFile).use { outputStream ->
                outputStream.write(content.toByteArray())
            }

            val outputUri = Uri.fromFile(outputFile)

            Log.d(TAG, "Export completed: $outputUri")
            Result.success(
                Data.Builder()
                    .putString(KEY_OUTPUT_URI, outputUri.toString())
                    .build()
            )
        } catch (e: Exception) {
            Log.e(TAG, "Export failed", e)
            Result.failure()
        }
    }

    private fun createOutputFile(novelId: Long, format: String): File {
        val outputDir = File(applicationContext.filesDir, "exports")
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        val extension = when (format) {
            FORMAT_TXT -> "txt"
            FORMAT_MD -> "md"
            FORMAT_HTML -> "html"
            else -> "txt"
        }

        return File(outputDir, "novel_${novelId}_${System.currentTimeMillis()}.$extension")
    }

    private fun generateSampleContent(novelId: Long): String {
        return buildString {
            appendLine("=== Exported Novel ===")
            appendLine("Novel ID: $novelId")
            appendLine("Export Date: ${java.util.Date()}")
            appendLine()
            appendLine("This is a placeholder for the actual novel content.")
            appendLine("The real implementation will fetch chapters from the database")
            appendLine("and format them according to the selected output format.")
        }
    }

    companion object {
        private const val TAG = "ExportWorker"
        const val KEY_NOVEL_ID = "novel_id"
        const val KEY_OUTPUT_FORMAT = "output_format"
        const val KEY_OUTPUT_URI = "output_uri"

        const val FORMAT_TXT = "txt"
        const val FORMAT_MD = "md"
        const val FORMAT_HTML = "html"
    }
}
