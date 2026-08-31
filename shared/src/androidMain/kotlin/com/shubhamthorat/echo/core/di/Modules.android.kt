package com.shubhamthorat.echo.core.di

import io.ktor.client.engine.*
import com.shubhamthorat.echo.core.network.createAndroidHttpClientEngine
import com.shubhamthorat.echo.core.audio.AndroidAudioPlayer
import com.shubhamthorat.echo.core.audio.AudioPlayer
import com.shubhamthorat.echo.data.db.getDatabaseBuilder
import com.shubhamthorat.echo.data.repository.AndroidPdfProcessor
import com.shubhamthorat.echo.domain.repository.PdfProcessor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<PdfProcessor> { AndroidPdfProcessor(androidContext()) }
    single { getDatabaseBuilder(androidContext()) }
    single<HttpClientEngine> { createAndroidHttpClientEngine() }
    single<AudioPlayer> { AndroidAudioPlayer(androidContext()) }
}
