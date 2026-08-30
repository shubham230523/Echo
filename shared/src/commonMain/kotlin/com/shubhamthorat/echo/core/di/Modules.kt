package com.shubhamthorat.echo.core.di

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

val featureModule = module {
    viewModel { DocumentAnalysisViewModel(get()) }
}

expect val platformModule: Module

val appModule = module {
    includes(coreModule, platformModule, featureModule)
}
