package com.shubhamthorat.echo.domain.repository

import com.shubhamthorat.echo.domain.model.Document
import kotlinx.coroutines.flow.Flow

interface DocumentRepository {
    suspend fun insertDocument(document: Document)
    suspend fun updateDocument(document: Document)
    suspend fun getDocumentById(id: String): Document?
    fun observeAllDocuments(): Flow<List<Document>>
    suspend fun deleteDocument(document: Document)
}
