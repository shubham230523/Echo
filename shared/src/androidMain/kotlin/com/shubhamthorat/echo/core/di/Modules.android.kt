package com.shubhamthorat.echo.core.di

import io.ktor.client.engine.*
import com.shubhamthorat.echo.core.network.createAndroidHttpClientEngine
import com.shubhamthorat.echo.core.audio.AndroidAudioPlayer
import com.shubhamthorat.echo.core.audio.AudioPlayer
import com.shubhamthorat.echo.data.db.getDatabaseBuilder
import com.shubhamthorat.echo.data.db.EchoDatabase
import com.shubhamthorat.echo.data.repository.AndroidPdfProcessor
import com.shubhamthorat.echo.domain.repository.PdfProcessor
import com.shubhamthorat.echo.shared.ai.*
import okio.FileSystem
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<PdfProcessor> { AndroidPdfProcessor(androidContext()) }
    single { getDatabaseBuilder(androidContext()) }
    single { get<EchoDatabase>().vectorDao() }
    single<VectorStore> { RoomVectorStore(get()) }
    
    single { KtorModelDownloader(get(), FileSystem.SYSTEM, androidContext().filesDir.absolutePath) }
    single<ModelManager> { AndroidModelManager(androidContext(), get()) }

    single<EmbeddingEngine> { 
        val manager = get<ModelManager>()
        if (manager.isModelDownloaded(ModelType.EMBEDDING)) {
            SherpaEmbeddingEngine(null, manager.getModelPath(ModelType.EMBEDDING)!!, "tokens.txt")
        } else {
            SherpaEmbeddingEngine(androidContext().assets, "bge-small.onnx", "tokens.txt")
        }
    }
    single<LlmEngine> { 
        val manager = get<ModelManager>()
        if (manager.isModelDownloaded(ModelType.LLM)) {
            SherpaLlmEngine(null, manager.getModelPath(ModelType.LLM)!!, "tokenizer.model")
        } else {
            SherpaLlmEngine(androidContext().assets, "llama.onnx", "tokenizer.model")
        }
    }
    single<AudioGenerator> { 
        val manager = get<ModelManager>()
        if (manager.isModelDownloaded(ModelType.TTS)) {
            SherpaTtsEngine(null, manager.getModelPath(ModelType.TTS)!!, "lexicon.txt", "tokens.txt", "espeak-ng-data")
        } else {
            SherpaTtsEngine(androidContext().assets, "vits.onnx", "lexicon.txt", "tokens.txt", "espeak-ng-data")
        }
    }
    
    single<HttpClientEngine> { createAndroidHttpClientEngine() }
    single<AudioPlayer> { AndroidAudioPlayer(androidContext()) }
}
