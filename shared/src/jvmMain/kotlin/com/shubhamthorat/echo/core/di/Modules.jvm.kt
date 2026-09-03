package com.shubhamthorat.echo.core.di

import com.shubhamthorat.echo.core.audio.AudioPlayer
import com.shubhamthorat.echo.core.audio.MockAudioPlayer
import com.shubhamthorat.echo.core.network.createJvmHttpClientEngine
import com.shubhamthorat.echo.data.db.EchoDatabase
import com.shubhamthorat.echo.data.db.getDatabaseBuilder
import com.shubhamthorat.echo.shared.ai.*
import com.shubhamthorat.echo.data.repository.JvmPdfProcessor
import com.shubhamthorat.echo.domain.repository.PdfProcessor
import io.ktor.client.engine.HttpClientEngine
import okio.FileSystem
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File

actual val platformModule: Module = module {
    single { getDatabaseBuilder() }
    single { get<EchoDatabase>().vectorDao() }
    single<VectorStore> { RoomVectorStore(get()) }

    single { KtorModelDownloader(get(), FileSystem.SYSTEM, File(System.getProperty("user.home"), ".echo/models").absolutePath) }
    single<ModelManager> { JvmModelManager(get()) }

    single<EmbeddingEngine> { 
        val manager = get<ModelManager>()
        SherpaEmbeddingEngine(manager.getModelPath(ModelType.EMBEDDING) ?: "bge-small.onnx", "tokens.txt")
    }
    single<LlmEngine> { 
        val manager = get<ModelManager>()
        SherpaLlmEngine(manager.getModelPath(ModelType.LLM) ?: "llama.onnx", "tokenizer.model")
    }
    single<AudioGenerator> { 
        val manager = get<ModelManager>()
        SherpaTtsEngine(manager.getModelPath(ModelType.TTS) ?: "vits.onnx", "lexicon.txt", "tokens.txt", "espeak-ng-data")
    }
    
    single<HttpClientEngine> { createJvmHttpClientEngine() }
    single<AudioPlayer> { MockAudioPlayer() }
    single<PdfProcessor> { JvmPdfProcessor() }
}
