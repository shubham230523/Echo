package com.shubhamthorat.echo.shared.ai

class SherpaEmbeddingEngine(
    private val modelPath: String,
    private val tokensPath: String
) : EmbeddingEngine {
    override suspend fun getEmbedding(text: String): List<Float> {
        // TODO: Implement using sherpa-onnx for JVM
        return emptyList()
    }
}

class SherpaLlmEngine(
    private val modelPath: String,
    private val tokensPath: String
) : LlmEngine {
    override suspend fun generate(prompt: String): String {
        // TODO: Implement using sherpa-onnx for JVM
        return "LLM not implemented for JVM yet."
    }
}

class SherpaTtsEngine(
    private val modelPath: String,
    private val lexiconPath: String,
    private val tokensPath: String,
    private val dataDir: String
) : AudioGenerator {
    override suspend fun generateAudio(text: String): FloatArray {
        // TODO: Implement using sherpa-onnx for JVM
        return FloatArray(0)
    }
}
