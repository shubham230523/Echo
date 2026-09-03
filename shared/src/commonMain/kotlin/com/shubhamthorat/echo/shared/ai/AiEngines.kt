package com.shubhamthorat.echo.shared.ai

interface EmbeddingEngine {
    suspend fun getEmbedding(text: String): List<Float>
}

interface LlmEngine {
    suspend fun generate(prompt: String): String
}

interface AudioGenerator {
    /**
     * Generates PCM audio samples from text.
     */
    suspend fun generateAudio(text: String): FloatArray
}
