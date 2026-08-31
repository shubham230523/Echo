package com.shubhamthorat.echo.core.di

import com.shubhamthorat.echo.data.db.EchoDatabase
import com.shubhamthorat.echo.data.db.getRoomDatabase
import com.shubhamthorat.echo.data.repository.*
import com.shubhamthorat.echo.domain.repository.*
import com.shubhamthorat.echo.domain.usecase.CleanDocumentTextUseCase
import com.shubhamthorat.echo.feature.chapters.ChaptersViewModel
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

val dataModule = module {
    single { getRoomDatabase(get()) }
    single { get<EchoDatabase>().documentDao() }
    single { get<EchoDatabase>().chapterDao() }
    single { get<EchoDatabase>().audiobookDao() }
    
    single<DocumentRepository> { RoomDocumentRepository(get()) }
    single<ChapterRepository> { RoomChapterRepository(get()) }
    single<AudiobookRepository> { RoomAudiobookRepository(get()) }
}

val domainModule = module {
    factory { CleanDocumentTextUseCase() }
    factory<ChapterDetector> { RuleBasedChapterDetector() }
    factory<NarrationProcessor> { DefaultNarrationProcessor() }
    single { CurrentAnalysisRepository() }
}

val featureModule = module {
    viewModel { DocumentAnalysisViewModel(get(), get(), get(), get()) }
    viewModel { ChaptersViewModel(get()) }
    viewModel { NarrationViewModel(get()) }
    viewModel { VoiceSelectionViewModel() }
    viewModel { GenerationViewModel() }
    viewModel { PlayerViewModel() }
    viewModel { SettingsViewModel() }
}

expect val platformModule: Module

val appModule = module {
    includes(coreModule, platformModule, dataModule, domainModule, featureModule)
}
