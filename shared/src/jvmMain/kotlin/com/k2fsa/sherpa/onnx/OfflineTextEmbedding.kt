package com.k2fsa.sherpa.onnx

data class OfflineTextEmbeddingConfig(
    var model: String = "",
    var tokens: String = "",
    var numThreads: Int = 1,
    var debug: Boolean = false,
    var provider: String = "cpu",
)

class OfflineTextEmbedding(
    val config: OfflineTextEmbeddingConfig
) {
    private var ptr: Long = 0

    init {
        ptr = newFromFile(config)
        require(ptr != 0L) { "Failed to create OfflineTextEmbedding" }
    }

    @Suppress("DEPRECATION")
    protected fun finalize() {
        if (ptr != 0L) {
            delete(ptr)
            ptr = 0
        }
    }

    fun release() = finalize()

    fun compute(text: String): FloatArray {
        return compute(ptr, text)
    }

    fun dim(): Int {
        return getDim(ptr)
    }

    private external fun newFromFile(config: OfflineTextEmbeddingConfig): Long
    private external fun delete(ptr: Long)
    private external fun compute(ptr: Long, text: String): FloatArray
    private external fun getDim(ptr: Long): Int

    companion object {
        init {
            try {
                System.loadLibrary("sherpa-onnx-jni")
            } catch (e: UnsatisfiedLinkError) {
                // Fallback handled by LibraryLoader
            }
        }
    }
}
