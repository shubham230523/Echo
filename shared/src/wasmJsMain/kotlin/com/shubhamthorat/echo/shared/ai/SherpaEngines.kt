package com.shubhamthorat.echo.shared.ai

class SherpaEmbeddingEngine(
    val modelPath: String,
    val tokensPath: String
) : EmbeddingEngine {
    override suspend fun getEmbedding(text: String): List<Float> = emptyList()
}

class SherpaLlmEngine(
    val modelPath: String,
    val tokensPath: String
) : LlmEngine {
    override suspend fun generate(prompt: String): String = "LLM not implemented for Wasm yet."
}

class SherpaTtsEngine(
    val modelPath: String,
    val lexiconPath: String,
    val tokensPath: String,
    val dataDir: String
) : AudioGenerator {
    override suspend fun generateAudio(text: String): FloatArray = FloatArray(0)
}
