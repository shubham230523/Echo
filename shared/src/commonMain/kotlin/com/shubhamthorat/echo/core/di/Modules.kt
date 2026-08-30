package com.shubhamthorat.echo.core.di

import com.shubhamthorat.echo.data.repository.RuleBasedChapterDetector
import com.shubhamthorat.echo.domain.repository.ChapterDetector
import com.shubhamthorat.echo.domain.usecase.CleanDocumentTextUseCase
import com.shubhamthorat.echo.feature.chapters.ChaptersViewModel
import com.shubhamthorat.echo.feature.document_analysis.DocumentAnalysisViewModel
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

val domainModule = module {
    factory { CleanDocumentTextUseCase() }
    factory<ChapterDetector> { RuleBasedChapterDetector() }
}

val featureModule = module {
    viewModel { DocumentAnalysisViewModel(get(), get()) }
    viewModel { ChaptersViewModel() }
}

expect val platformModule: Module

val appModule = module {
    includes(coreModule, platformModule, domainModule, featureModule)
}
