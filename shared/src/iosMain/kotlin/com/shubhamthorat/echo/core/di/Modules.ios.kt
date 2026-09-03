package com.shubhamthorat.echo.core.di

import com.shubhamthorat.echo.core.audio.AudioPlayer
import com.shubhamthorat.echo.core.audio.MockAudioPlayer
import com.shubhamthorat.echo.core.network.createIosHttpClientEngine
import com.shubhamthorat.echo.data.db.EchoDatabase
import com.shubhamthorat.echo.data.db.getDatabaseBuilder
import com.shubhamthorat.echo.shared.ai.*
import io.ktor.client.engine.HttpClientEngine
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { getDatabaseBuilder() }
    single { get<EchoDatabase>().vectorDao() }
    single<VectorStore> { RoomVectorStore(get()) }
    single<EmbeddingEngine> { SherpaEmbeddingEngine("bge-small.onnx", "tokens.txt") }
    single<LlmEngine> { SherpaLlmEngine("llama.onnx", "tokenizer.model") }
    single<AudioGenerator> { SherpaTtsEngine("vits.onnx", "lexicon.txt", "tokens.txt", "espeak-ng-data") }
    
    single<HttpClientEngine> { createIosHttpClientEngine() }
    single<AudioPlayer> { MockAudioPlayer() }
}
