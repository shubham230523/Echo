package com.shubhamthorat.echo.core.di

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

val appModule = module {
    includes(coreModule)
}
