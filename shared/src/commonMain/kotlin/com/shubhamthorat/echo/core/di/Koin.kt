package com.shubhamthorat.echo.core.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

/**
 * Initializes Koin for the application.
 * This can be called from platform-specific code (e.g., Android Application class).
 *
 * @param appDeclaration Optional platform-specific configuration.
 */
fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(appModule)
    }

/**
 * Convenience function for starting Koin without any platform-specific declaration.
 * Useful for tests or simple desktop applications.
 */
fun initKoin() = initKoin {}
