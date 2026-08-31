package com.shubhamthorat.echo.core.di

import com.shubhamthorat.echo.data.db.getDatabaseBuilder
import com.shubhamthorat.echo.core.network.createJvmHttpClientEngine
import com.shubhamthorat.echo.core.audio.AudioPlayer
import com.shubhamthorat.echo.core.audio.MockAudioPlayer
import io.ktor.client.engine.HttpClientEngine
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { getDatabaseBuilder() }
    single<HttpClientEngine> { createJvmHttpClientEngine() }
    single<AudioPlayer> { MockAudioPlayer() }
}
