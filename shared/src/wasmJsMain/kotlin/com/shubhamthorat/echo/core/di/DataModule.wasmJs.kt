package com.shubhamthorat.echo.core.di

import com.shubhamthorat.echo.data.repository.*
import com.shubhamthorat.echo.domain.model.Audiobook
import com.shubhamthorat.echo.domain.model.Chapter
import com.shubhamthorat.echo.domain.model.Document
import com.shubhamthorat.echo.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.koin.core.module.Module
import org.koin.dsl.module

actual val dataModule: Module = module {
    single<DocumentRepository> { 
        object : DocumentRepository {
            override suspend fun insertDocument(document: Document) {}
            override suspend fun updateDocument(document: Document) {}
            override suspend fun getDocumentById(id: String): Document? = null
            override fun observeAllDocuments(): Flow<List<Document>> = flowOf(emptyList())
            override suspend fun deleteDocument(document: Document) {}
        }
    }
    single<ChapterRepository> { 
        object : ChapterRepository {
            override suspend fun insertChapters(chapters: List<Chapter>) {}
            override fun getChaptersByDocumentId(documentId: String): Flow<List<Chapter>> = flowOf(emptyList())
            override suspend fun updateChapter(chapter: Chapter) {}
            override suspend fun deleteChaptersByDocumentId(documentId: String) {}
        }
    }
    single<AudiobookRepository> { 
        object : AudiobookRepository {
            override suspend fun insertAudiobook(audiobook: Audiobook) {}
            override suspend fun updateAudiobook(audiobook: Audiobook) {}
            override suspend fun getAudiobookById(id: String): Audiobook? = null
            override fun observeAllAudiobooks(): Flow<List<Audiobook>> = flowOf(emptyList())
            override suspend fun deleteAudiobook(audiobook: Audiobook) {}
        }
    }
    single<RemoteGenerationRepository> { ApiRemoteGenerationRepository(get()) }
    single<SystemRepository> { ApiSystemRepository(get()) }
}
