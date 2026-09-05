package com.shubhamthorat.echo.core.di

import io.ktor.client.HttpClient
import com.shubhamthorat.echo.core.network.HttpClientFactory
import com.shubhamthorat.echo.core.network.NetworkConfig
import com.shubhamthorat.echo.data.remote.EchoApi
import com.shubhamthorat.echo.data.remote.KtorEchoApi
import com.shubhamthorat.echo.data.repository.*
import com.shubhamthorat.echo.domain.repository.*
import com.shubhamthorat.echo.domain.service.LocalAudiobookGenerator
import com.shubhamthorat.echo.domain.usecase.CleanDocumentTextUseCase
import com.shubhamthorat.echo.feature.chapters.ChaptersViewModel
import com.shubhamthorat.echo.feature.import_document.ImportDocumentViewModel
import com.shubhamthorat.echo.feature.library.LibraryViewModel
import com.shubhamthorat.echo.feature.document_analysis.DocumentAnalysisViewModel
import com.shubhamthorat.echo.feature.narration.NarrationViewModel
import com.shubhamthorat.echo.feature.voice.VoiceSelectionViewModel
import com.shubhamthorat.echo.feature.generation.GenerationViewModel
import com.shubhamthorat.echo.feature.generation.LocalGenerationViewModel
import com.shubhamthorat.echo.feature.player.PlayerViewModel
import com.shubhamthorat.echo.feature.settings.SettingsViewModel
import com.shubhamthorat.echo.shared.ai.*
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * A simple dependency for DI verification.
 */
class TestDependency {
    fun getMessage() = "Koin DI is working!"
}

val coreModule = module {
    single { TestDependency() }
}

val networkModule = module {
    single { NetworkConfig(baseUrl = "http://localhost:8080", isDebug = true, requestTimeoutMillis = 1_800_000L) }
    single<HttpClient> { HttpClientFactory(get(), get()).create() }
    single<EchoApi> { KtorEchoApi(get()) }
}

expect val dataModule: Module

val domainModule = module {
    factory { CleanDocumentTextUseCase() }
    factory<ChapterDetector> { RuleBasedChapterDetector() }
    factory<NarrationProcessor> { DefaultNarrationProcessor() }
    factory { LocalAudiobookGenerator(get()) }
    single { CurrentAnalysisRepository() }
}

val featureModule = module {
    viewModel { LibraryViewModel(get()) }
    viewModel { ImportDocumentViewModel(get()) }
    viewModel { DocumentAnalysisViewModel(get(), get(), get(), get(), get()) }
    viewModel { ChaptersViewModel(get()) }
    viewModel { NarrationViewModel(get()) }
    viewModel { VoiceSelectionViewModel(get(), get(), get()) }
    viewModel { GenerationViewModel(get(), get(), get(), get()) }
    viewModel { LocalGenerationViewModel(get(), get(), get(), get()) }
    viewModel { PlayerViewModel(get(), get(), get()) }
    viewModel { SettingsViewModel() }
}

expect val platformModule: Module

val aiModule = module {
    single<DocumentAnalyzer> { LocalRagEngine(PdfExtractor(), get(), get(), get()) }
}

val appModule = module {
    includes(coreModule, platformModule, networkModule, dataModule, domainModule, aiModule, featureModule)
}
