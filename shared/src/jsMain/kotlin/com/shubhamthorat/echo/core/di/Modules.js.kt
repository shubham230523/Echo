package com.shubhamthorat.echo.core.di

import com.shubhamthorat.echo.shared.ai.*
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    // Add JS specific implementations here
    single<VectorStore> { InMemoryVectorStore() }
    single<EmbeddingEngine> { SherpaEmbeddingEngine("bge-small.onnx", "tokens.txt") }
    single<LlmEngine> { SherpaLlmEngine("llama.onnx", "tokenizer.model") }
    single<AudioGenerator> { SherpaTtsEngine("vits.onnx", "lexicon.txt", "tokens.txt", "espeak-ng-data") }
}
