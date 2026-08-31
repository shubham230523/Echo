package com.shubhamthorat.echo.core.di

import io.ktor.client.HttpClient
import com.shubhamthorat.echo.core.network.HttpClientFactory
import com.shubhamthorat.echo.core.network.NetworkConfig
import com.shubhamthorat.echo.data.remote.EchoApi
import com.shubhamthorat.echo.data.remote.KtorEchoApi
import com.shubhamthorat.echo.data.db.EchoDatabase
import com.shubhamthorat.echo.data.db.getRoomDatabase
import com.shubhamthorat.echo.data.repository.*
import com.shubhamthorat.echo.domain.repository.*
import com.shubhamthorat.echo.domain.usecase.CleanDocumentTextUseCase
import com.shubhamthorat.echo.feature.chapters.ChaptersViewModel
import com.shubhamthorat.echo.feature.import_document.ImportDocumentViewModel
import com.shubhamthorat.echo.feature.library.LibraryViewModel
import com.shubhamthorat.echo.feature.document_analysis.DocumentAnalysisViewModel
import com.shubhamthorat.echo.feature.narration.NarrationViewModel
import com.shubhamthorat.echo.feature.voice.VoiceSelectionViewModel
import com.shubhamthorat.echo.feature.generation.GenerationViewModel
import com.shubhamthorat.echo.feature.player.PlayerViewModel
import com.shubhamthorat.echo.feature.settings.SettingsViewModel
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
    single { NetworkConfig(baseUrl = "http://localhost:8080", isDebug = true) }
    single<HttpClient> { HttpClientFactory(get(), get()).create() }
    single<EchoApi> { KtorEchoApi(get()) }
}

val dataModule = module {
    single { getRoomDatabase(get()) }
    single { get<EchoDatabase>().documentDao() }
    single { get<EchoDatabase>().chapterDao() }
    single { get<EchoDatabase>().audiobookDao() }
    
    single<DocumentRepository> { RoomDocumentRepository(get()) }
    single<ChapterRepository> { RoomChapterRepository(get()) }
    single<AudiobookRepository> { RoomAudiobookRepository(get()) }
    single<SystemRepository> { ApiSystemRepository(get()) }
}

val domainModule = module {
    factory { CleanDocumentTextUseCase() }
    factory<ChapterDetector> { RuleBasedChapterDetector() }
    factory<NarrationProcessor> { DefaultNarrationProcessor() }
    single { CurrentAnalysisRepository() }
}

val featureModule = module {
    viewModel { LibraryViewModel(get()) }
    viewModel { ImportDocumentViewModel(get()) }
    viewModel { DocumentAnalysisViewModel(get(), get(), get(), get(), get()) }
    viewModel { ChaptersViewModel(get()) }
    viewModel { NarrationViewModel(get()) }
    viewModel { VoiceSelectionViewModel() }
    viewModel { GenerationViewModel() }
    viewModel { PlayerViewModel() }
    viewModel { SettingsViewModel() }
}

expect val platformModule: Module

val appModule = module {
    includes(coreModule, platformModule, networkModule, dataModule, domainModule, featureModule)
}
