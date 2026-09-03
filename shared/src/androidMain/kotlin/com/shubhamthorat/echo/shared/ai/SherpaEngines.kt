package com.shubhamthorat.echo.shared.ai

import android.content.res.AssetManager
import com.k2fsa.sherpa.onnx.*

class SherpaEmbeddingEngine(
    private val assetManager: AssetManager,
    private val modelPath: String,
    private val tokensPath: String
) : EmbeddingEngine {

    private val embedder: OfflineTextEmbedding by lazy {
        val config = OfflineTextEmbeddingConfig(
            model = modelPath,
            tokens = tokensPath,
            numThreads = 4,
            debug = true,
            provider = "cpu"
        )
        OfflineTextEmbedding(assetManager, config)
    }

    override suspend fun getEmbedding(text: String): List<Float> {
        return embedder.compute(text).toList()
    }
}

class SherpaLlmEngine(
    private val assetManager: AssetManager,
    private val modelPath: String,
    private val tokensPath: String
) : LlmEngine {

    private val llm: OfflineLlm by lazy {
        val config = OfflineLlmConfig(
            model = modelPath,
            tokens = tokensPath,
            numThreads = 4,
            maxContextSize = 1024,
            provider = "cpu"
        )
        OfflineLlm(assetManager, config)
    }

    override suspend fun generate(prompt: String): String {
        return llm.generate(prompt).text
    }
}

class SherpaTtsEngine(
    private val assetManager: AssetManager,
    private val modelPath: String,
    private val lexiconPath: String,
    private val tokensPath: String,
    private val dataDir: String
) : AudioGenerator {

    private val tts: OfflineTts by lazy {
        val config = OfflineTtsConfig(
            model = OfflineTtsVitsModelConfig(
                model = modelPath,
                lexicon = lexiconPath,
                tokens = tokensPath,
                dataDir = dataDir
            ),
            numThreads = 4,
            debug = true
        )
        OfflineTts(assetManager, config)
    }

    override suspend fun generateAudio(text: String): FloatArray {
        val audio = tts.generate(text, sid = 0, speed = 1.0f)
        return audio.samples
    }
}
