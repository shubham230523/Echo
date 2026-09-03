package com.shubhamthorat.echo.core.di

import com.shubhamthorat.echo.data.db.EchoDatabase
import com.shubhamthorat.echo.data.db.getRoomDatabase
import com.shubhamthorat.echo.data.repository.*
import com.shubhamthorat.echo.domain.repository.*
import org.koin.core.module.Module
import org.koin.dsl.module

actual val dataModule: Module = module {
    single { getRoomDatabase(get()) }
    single { get<EchoDatabase>().documentDao() }
    single { get<EchoDatabase>().chapterDao() }
    single { get<EchoDatabase>().audiobookDao() }
    
    single<DocumentRepository> { RoomDocumentRepository(get()) }
    single<ChapterRepository> { RoomChapterRepository(get()) }
    single<AudiobookRepository> { RoomAudiobookRepository(get()) }
    single<RemoteGenerationRepository> { ApiRemoteGenerationRepository(get()) }
    single<SystemRepository> { ApiSystemRepository(get()) }
}
