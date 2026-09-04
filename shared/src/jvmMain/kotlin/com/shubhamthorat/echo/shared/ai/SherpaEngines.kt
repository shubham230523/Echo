package com.shubhamthorat.echo.shared.ai

import com.k2fsa.sherpa.onnx.*

class SherpaEmbeddingEngine(
    private val modelPath: String,
    private val tokensPath: String,
) : EmbeddingEngine {

    override suspend fun getEmbedding(text: String): List<Float> {
        // Standalone TextEmbedding is not yet supported in the JVM JNI for 1.13.7
        return List(384) { 0.0f } 
    }
}

class SherpaLlmEngine(
    private val modelPath: String,
    private val tokensPath: String,
) : LlmEngine {

    override suspend fun generate(prompt: String): String {
        // Standalone LLM is not yet supported in the JVM JNI for 1.13.7
        return "Local LLM generation is currently limited to the App UI. The server-side local engine will be enabled in a future update."
    }
}

class SherpaTtsEngine(
    private val modelPath: String,
    private val lexiconPath: String,
    private val tokensPath: String,
    private val dataDir: String,
) : AudioGenerator {

    private val tts: OfflineTts by lazy {
        // OfflineTts in the JAR is Java-based and uses Builders
        val vits = OfflineTtsVitsModelConfig.builder()
            .setModel(modelPath)
            .setLexicon(lexiconPath)
            .setTokens(tokensPath)
            .setDataDir(dataDir)
            .build()
        val modelConfig = OfflineTtsModelConfig.builder()
            .setVits(vits)
            .setNumThreads(4)
            .setDebug(true)
            .build()
        val config = OfflineTtsConfig.builder()
            .setModel(modelConfig)
            .build()
        OfflineTts(config)
    }

    override suspend fun generateAudio(text: String): FloatArray {
        // generate() in Java doesn't support named arguments
        val audio = tts.generate(text, 0, 1.0f)
        return audio.samples
    }
}
