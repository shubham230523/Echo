package com.shubhamthorat.echo.domain.repository

import com.shubhamthorat.echo.domain.model.Chapter
import kotlinx.coroutines.flow.Flow

interface ChapterRepository {
    suspend fun insertChapters(chapters: List<Chapter>)
    fun getChaptersByDocumentId(documentId: String): Flow<List<Chapter>>
    suspend fun updateChapter(chapter: Chapter)
    suspend fun deleteChaptersByDocumentId(documentId: String)
}
