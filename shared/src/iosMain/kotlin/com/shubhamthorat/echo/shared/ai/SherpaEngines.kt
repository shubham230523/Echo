package com.shubhamthorat.echo.shared.ai

class SherpaEmbeddingEngine(
    private val modelPath: String,
    private val tokensPath: String
) : EmbeddingEngine {
    override suspend fun getEmbedding(text: String): List<Float> {
        return emptyList()
    }
}

class SherpaLlmEngine(
    private val modelPath: String,
    private val tokensPath: String
) : LlmEngine {
    override suspend fun generate(prompt: String): String {
        return "LLM not implemented for iOS yet."
    }
}

class SherpaTtsEngine(
    private val modelPath: String,
    private val lexiconPath: String,
    private val tokensPath: String,
    private val dataDir: String
) : AudioGenerator {
    override suspend fun generateAudio(text: String): FloatArray {
        return FloatArray(0)
    }
}
