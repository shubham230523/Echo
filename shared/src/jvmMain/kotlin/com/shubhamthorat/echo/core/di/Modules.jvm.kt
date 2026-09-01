package com.shubhamthorat.echo.core.di

import com.shubhamthorat.echo.data.db.getDatabaseBuilder
import com.shubhamthorat.echo.core.network.createJvmHttpClientEngine
import com.shubhamthorat.echo.core.audio.AudioPlayer
import com.shubhamthorat.echo.core.audio.MockAudioPlayer
import com.shubhamthorat.echo.data.repository.JvmPdfProcessor
import com.shubhamthorat.echo.domain.repository.PdfProcessor
import io.ktor.client.engine.HttpClientEngine
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { getDatabaseBuilder() }
    single<HttpClientEngine> { createJvmHttpClientEngine() }
    single<AudioPlayer> { MockAudioPlayer() }
    single<PdfProcessor> { JvmPdfProcessor() }
}
