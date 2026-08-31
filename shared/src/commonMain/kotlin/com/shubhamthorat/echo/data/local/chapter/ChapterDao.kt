package com.shubhamthorat.echo.data.local.chapter

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ChapterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<ChapterEntity>)

    @Query("SELECT * FROM chapters WHERE documentId = :documentId ORDER BY `index` ASC")
    fun getChaptersByDocumentId(documentId: String): Flow<List<ChapterEntity>>

    @Update
    suspend fun updateChapter(chapter: ChapterEntity)

    @Query("DELETE FROM chapters WHERE documentId = :documentId")
    suspend fun deleteChaptersByDocumentId(documentId: String)
}
