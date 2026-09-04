package com.shubhamthorat.echo.shared.ai

import com.k2fsa.sherpa.onnx.*

class SherpaEmbeddingEngine(
    private val modelPath: String,
    private val tokensPath: String,
) : EmbeddingEngine {

    private val embedder: OfflineTextEmbedding by lazy {
        val config = OfflineTextEmbeddingConfig(
            model = modelPath,
            tokens = tokensPath,
            numThreads = 4,
            debug = true,
            provider = "cpu"
        )
        OfflineTextEmbedding(config)
    }

    override suspend fun getEmbedding(text: String): List<Float> {
        return embedder.compute(text).toList()
    }
}

class SherpaLlmEngine(
    private val modelPath: String,
    private val tokensPath: String,
) : LlmEngine {

    private val llm: OfflineLlm by lazy {
        val config = OfflineLlmConfig(
            model = OfflineLlmModelConfig(
                qwen2 = modelPath, // Assuming qwen2 based on common models, update if needed
                tokens = tokensPath,
                numThreads = 4,
                device = "cpu",
            ),
            maxNumToken = 1024,
        )
        OfflineLlm(config)
    }

    override suspend fun generate(prompt: String): String {
        return llm.generate(prompt)
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
