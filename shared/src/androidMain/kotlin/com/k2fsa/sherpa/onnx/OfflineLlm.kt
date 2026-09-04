package com.k2fsa.sherpa.onnx

import android.content.res.AssetManager

data class OfflineLlmModelConfig(
    var llama: String = "",
    var gemma: String = "",
    var phi2: String = "",
    var phi3: String = "",
    var qwen2: String = "",
    var tokens: String = "",
    var numThreads: Int = 4,
    var debug: Boolean = false,
    var device: String = "cpu",
)

data class OfflineLlmConfig(
    var model: OfflineLlmModelConfig = OfflineLlmModelConfig(),
    var template: String = "",
    var maxNumToken: Int = 1024,
)

data class OfflineLlmResult(
    val text: String
)

class OfflineLlm(
    assetManager: AssetManager? = null,
    val config: OfflineLlmConfig
) {
    private var ptr: Long = 0

    init {
        ptr = if (assetManager != null) {
            createOfflineLlmFromAsset(assetManager, config)
        } else {
            createOfflineLlm(config)
        }
        require(ptr != 0L) { "Failed to create OfflineLlm" }
    }

    @Suppress("DEPRECATION")
    protected fun finalize() {
        if (ptr != 0L) {
            deleteOfflineLlm(ptr)
            ptr = 0
        }
    }

    fun release() = finalize()

    fun generate(text: String): OfflineLlmResult {
        return OfflineLlmResult(generateNative(ptr, text))
    }

    private external fun createOfflineLlm(config: OfflineLlmConfig): Long
    private external fun createOfflineLlmFromAsset(
        assetManager: AssetManager,
        config: OfflineLlmConfig
    ): Long
    private external fun deleteOfflineLlm(ptr: Long)
    private external fun generateNative(ptr: Long, text: String): String

    companion object {
        init {
            System.loadLibrary("sherpa-onnx-jni")
        }
    }
}
