package com.shubhamthorat.echo.shared.ai

import com.k2fsa.sherpa.onnx.*

class SherpaEmbeddingEngine(
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
        OfflineTextEmbedding(config)
    }

    override suspend fun getEmbedding(text: String): List<Float> {
        return embedder.compute(text).toList()
    }
}

class SherpaLlmEngine(
    private val modelPath: String,
    private val tokensPath: String
) : LlmEngine {

    private val llm: OfflineLlm by lazy {
        val config = OfflineLlmConfig(
            model = OfflineLlmModelConfig(
                model = modelPath,
                tokens = tokensPath,
                numThreads = 4,
                maxContextSize = 1024,
                device = "cpu"
            )
        )
        OfflineLlm(config)
    }

    override suspend fun generate(prompt: String): String {
        val stream = llm.createStream()
        stream.inputPrompt(prompt)
        val result = StringBuilder()
        while (!llm.isFinished(stream)) {
            llm.decode(stream)
            result.append(llm.retrive(stream))
        }
        return result.toString()
    }
}

class SherpaTtsEngine(
    private val modelPath: String,
    private val lexiconPath: String,
    private val tokensPath: String,
    private val dataDir: String
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
        OfflineTts(config)
    }

    override suspend fun generateAudio(text: String): FloatArray {
        val audio = tts.generate(text, sid = 0, speed = 1.0f)
        return audio.samples
    }
}
