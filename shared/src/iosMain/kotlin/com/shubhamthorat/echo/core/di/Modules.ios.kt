package com.shubhamthorat.echo.core.di

import com.shubhamthorat.echo.data.db.getDatabaseBuilder
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { getDatabaseBuilder() }
}
