package com.shubhamthorat.echo.shared.ai

import kotlinx.coroutines.flow.Flow

enum class ModelType(val fileName: String, val url: String) {
    EMBEDDING("bge-small.onnx", "https://huggingface.co/csukuangfj/sherpa-onnx-bge-small-en-v1.5/resolve/main/model.onnx"),
    LLM("llama.onnx", "https://huggingface.co/csukuangfj/sherpa-onnx-llm-models/resolve/main/llama-3-8b-instruct-quantized.onnx"),
    TTS("vits.onnx", "https://huggingface.co/csukuangfj/sherpa-onnx-tts-models/resolve/main/vits-en-vctk.onnx")
}

data class DownloadProgress(
    val modelType: ModelType,
    val progress: Float, // 0.0 to 1.0
    val isComplete: Boolean = false,
    val error: String? = null
)

interface ModelManager {
    /**
     * Checks if the specified model is already downloaded and available.
     */
    fun isModelDownloaded(modelType: ModelType): Boolean

    /**
     * Gets the absolute path to the model file.
     * Returns null if the model is not downloaded.
     */
    fun getModelPath(modelType: ModelType): String?

    /**
     * Downloads the model.
     * @return A Flow of download progress.
     */
    fun downloadModel(modelType: ModelType): Flow<DownloadProgress>
}
