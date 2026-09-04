package com.shubhamthorat.echo.shared.ai

import android.content.res.AssetManager
import com.k2fsa.sherpa.onnx.*

class SherpaEmbeddingEngine(
    private val assetManager: AssetManager?,
    private val modelPath: String,
    private val tokensPath: String,
) : EmbeddingEngine {

    override suspend fun getEmbedding(text: String): List<Float> {
        return List(384) { 0.0f }
    }
}

class SherpaLlmEngine(
    private val assetManager: AssetManager?,
    private val modelPath: String,
    private val tokensPath: String,
) : LlmEngine {

    override suspend fun generate(prompt: String): String {
        return "Local LLM is currently being optimized for Android."
    }
}

class SherpaTtsEngine(
    private val assetManager: AssetManager?,
    private val modelPath: String,
    private val lexiconPath: String,
    private val tokensPath: String,
    private val dataDir: String,
) : AudioGenerator {

    private val tts: OfflineTts by lazy {
        val config = OfflineTtsConfig(
            model = OfflineTtsModelConfig(
                vits = OfflineTtsVitsModelConfig(
                    model = modelPath,
                    lexicon = lexiconPath,
                    tokens = tokensPath,
                    dataDir = dataDir
                ),
                numThreads = 4,
                debug = true
            )
        )
        OfflineTts(assetManager, config)
    }

    override suspend fun generateAudio(text: String): FloatArray {
        val audio = tts.generate(text, 0, 1.0f)
        return audio.samples
    }
}
