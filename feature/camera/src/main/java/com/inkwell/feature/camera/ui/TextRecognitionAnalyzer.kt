package com.inkwell.feature.camera.ui

import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class TextRecognitionAnalyzer(
    private val onTextRecognized: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val textRecognizer: TextRecognizer = TextRecognition.getClient(
        TextRecognizerOptions.DEFAULT_OPTIONS
    )

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        val image = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.imageInfo.rotationDegrees
        )

        textRecognizer.process(image)
            .addOnSuccessListener { result ->
                val text = result.textBlocks.joinToString("\n\n") { block ->
                    block.lines.joinToString("\n") { it.text }
                }
                if (text.isNotBlank()) {
                    onTextRecognized(text)
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Text recognition failed", exception)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    fun close() {
        textRecognizer.close()
    }

    companion object {
        private const val TAG = "TextRecognitionAnalyzer"
    }
}
