package com.shubhamthorat.echo.data.local.document

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: DocumentEntity)

    @Update
    suspend fun updateDocument(document: DocumentEntity)

    @Query("SELECT * FROM documents WHERE id = :id")
    suspend fun getDocumentById(id: String): DocumentEntity?

    @Query("SELECT * FROM documents ORDER BY importedAt DESC")
    fun observeAllDocuments(): Flow<List<DocumentEntity>>

    @Delete
    suspend fun deleteDocument(document: DocumentEntity)
}
