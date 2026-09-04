package com.k2fsa.sherpa.onnx

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

class OfflineLlm(
    val config: OfflineLlmConfig
) {
    private var ptr: Long = 0

    init {
        ptr = createOfflineLlm(config)
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

    fun generate(text: String): String {
        return generateNative(ptr, text)
    }

    private external fun createOfflineLlm(config: OfflineLlmConfig): Long
    private external fun deleteOfflineLlm(ptr: Long)
    private external fun generateNative(ptr: Long, text: String): String

    companion object {
        init {
            // Use standard library loading or LibraryLoader from the JAR if available
            try {
                System.loadLibrary("sherpa-onnx-jni")
            } catch (e: UnsatisfiedLinkError) {
                // Fallback or ignore if handled elsewhere
            }
        }
    }
}
