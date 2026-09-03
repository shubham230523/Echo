package com.shubhamthorat.echo.core.di

import com.shubhamthorat.echo.core.audio.AudioPlayer
import com.shubhamthorat.echo.core.audio.MockAudioPlayer
import com.shubhamthorat.echo.core.network.createWasmJsHttpClientEngine
import com.shubhamthorat.echo.data.repository.WasmJsPdfProcessor
import com.shubhamthorat.echo.domain.repository.PdfProcessor
import com.shubhamthorat.echo.shared.ai.*
import io.ktor.client.engine.HttpClientEngine
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<HttpClientEngine> { createWasmJsHttpClientEngine() }
    single<AudioPlayer> { MockAudioPlayer() }
    single<PdfProcessor> { WasmJsPdfProcessor() }
    
    single<VectorStore> { InMemoryVectorStore() }
    single<EmbeddingEngine> { SherpaEmbeddingEngine("bge-small.onnx", "tokens.txt") }
    single<LlmEngine> { SherpaLlmEngine("llama.onnx", "tokenizer.model") }
    single<AudioGenerator> { SherpaTtsEngine("vits.onnx", "lexicon.txt", "tokens.txt", "espeak-ng-data") }
}
