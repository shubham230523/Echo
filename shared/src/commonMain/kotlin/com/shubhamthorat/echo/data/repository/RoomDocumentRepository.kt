package com.shubhamthorat.echo.data.repository

import com.shubhamthorat.echo.data.local.document.DocumentDao
import com.shubhamthorat.echo.data.local.document.toDomain
import com.shubhamthorat.echo.data.local.document.toEntity
import com.shubhamthorat.echo.domain.model.Document
import com.shubhamthorat.echo.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomDocumentRepository(
    private val documentDao: DocumentDao
) : DocumentRepository {

    override suspend fun insertDocument(document: Document) {
        documentDao.insertDocument(document.toEntity())
    }

    override suspend fun updateDocument(document: Document) {
        documentDao.updateDocument(document.toEntity())
    }

    override suspend fun getDocumentById(id: String): Document? {
        return documentDao.getDocumentById(id)?.toDomain()
    }

    override fun observeAllDocuments(): Flow<List<Document>> {
        return documentDao.observeAllDocuments().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun deleteDocument(document: Document) {
        documentDao.deleteDocument(document.toEntity())
    }
}
