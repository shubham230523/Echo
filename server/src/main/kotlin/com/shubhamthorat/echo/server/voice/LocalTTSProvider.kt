package com.shubhamthorat.echo.server.voice

import com.shubhamthorat.echo.shared.ai.ModelManager
import com.shubhamthorat.echo.shared.ai.ModelType
import com.shubhamthorat.echo.shared.ai.SherpaTtsEngine
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*

class LocalTTSProvider(
    private val config: TTSConfig,
    private val modelManager: ModelManager? = null
) : TTSProvider {

    private val ttsEngine: SherpaTtsEngine by lazy {
        // 1. Try manual path from config
        val configPath = if (config.voiceModel != "local-vits" && config.voiceModel.isNotBlank() && File(config.voiceModel).exists()) {
            config.voiceModel
        } else null

        // 2. Try ModelManager (auto-downloaded by app)
        val managerPath = modelManager?.getModelPath(ModelType.TTS)
        
        // 3. Fallback to standard app location
        val defaultPath = File(System.getProperty("user.home"), ".echo/models/vits.onnx").absolutePath
        
        val finalModelPath = configPath ?: managerPath ?: defaultPath
        val modelFile = File(finalModelPath)
        val modelDir = modelFile.parentFile ?: File(System.getProperty("user.home"), ".echo/models")
        
        println("🔊 Initializing Local TTS Engine:")
        println("   Model: ${modelFile.absolutePath} (Exists: ${modelFile.exists()})")
        
        SherpaTtsEngine(
            modelPath = modelFile.absolutePath,
            lexiconPath = File(modelDir, "lexicon.txt").absolutePath,
            tokensPath = File(modelDir, "tokens.txt").absolutePath,
            dataDir = File(modelDir, "espeak-ng-data").absolutePath
        )
    }

    override suspend fun synthesize(request: TTSRequest): TTSResult {
        println("🔊 Synthesizing locally: ${request.text.take(50)}...")
        
        val audioSamples = ttsEngine.generateAudio(request.text)
        
        // Save to temporary WAV file
        val outputDir = File("output/audio").apply { mkdirs() }
        val outputFile = File(outputDir, "local_${UUID.randomUUID()}.wav")
        
        saveWav(outputFile, audioSamples, 22050) // Assuming 22050Hz for Sherpa VITS
        
        return TTSResult(
            audioFileUri = outputFile.absolutePath,
            durationSeconds = audioSamples.size.toDouble() / 22050.0,
            format = "WAV"
        )
    }

    private fun saveWav(file: File, samples: FloatArray, sampleRate: Int) {
        val bytesPerSample = 2
        val headerSize = 44
        val dataSize = samples.size * bytesPerSample
        val totalSize = headerSize + dataSize
        
        val buffer = ByteBuffer.allocate(totalSize).order(ByteOrder.LITTLE_ENDIAN)
        
        // RIFF header
        buffer.put("RIFF".toByteArray())
        buffer.putInt(totalSize - 8)
        buffer.put("WAVE".toByteArray())
        
        // fmt chunk
        buffer.put("fmt ".toByteArray())
        buffer.putInt(16) // Subchunk1Size
        buffer.putShort(1) // AudioFormat (PCM)
        buffer.putShort(1) // NumChannels
        buffer.putInt(sampleRate)
        buffer.putInt(sampleRate * bytesPerSample) // ByteRate
        buffer.putShort(bytesPerSample.toShort()) // BlockAlign
        buffer.putShort((bytesPerSample * 8).toShort()) // BitsPerSample
        
        // data chunk
        buffer.put("data".toByteArray())
        buffer.putInt(dataSize)
        
        for (sample in samples) {
            val s = (sample * 32767).toInt().coerceIn(-32768, 32767).toShort()
            buffer.putShort(s)
        }
        
        file.writeBytes(buffer.array())
    }
}
