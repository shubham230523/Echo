package com.shubhamthorat.echo.data.repository

import com.shubhamthorat.echo.data.local.chapter.ChapterDao
import com.shubhamthorat.echo.data.local.chapter.toDomain
import com.shubhamthorat.echo.data.local.chapter.toEntity
import com.shubhamthorat.echo.domain.model.Chapter
import com.shubhamthorat.echo.domain.repository.ChapterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomChapterRepository(
    private val chapterDao: ChapterDao
) : ChapterRepository {

    override suspend fun insertChapters(chapters: List<Chapter>) {
        chapterDao.insertChapters(chapters.map { it.toEntity() })
    }

    override fun getChaptersByDocumentId(documentId: String): Flow<List<Chapter>> {
        return chapterDao.getChaptersByDocumentId(documentId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun updateChapter(chapter: Chapter) {
        chapterDao.updateChapter(chapter.toEntity())
    }

    override suspend fun deleteChaptersByDocumentId(documentId: String) {
        chapterDao.deleteChaptersByDocumentId(documentId)
    }
}
