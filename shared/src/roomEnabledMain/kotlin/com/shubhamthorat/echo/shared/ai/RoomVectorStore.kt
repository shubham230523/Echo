package com.shubhamthorat.echo.shared.ai

import com.shubhamthorat.echo.data.local.vector.VectorDao
import com.shubhamthorat.echo.data.local.vector.VectorEntity

class RoomVectorStore(private val vectorDao: VectorDao) : VectorStore {

    override suspend fun addVectors(documentId: String, vectors: List<FloatVector>) {
        val entities = vectors.map {
            VectorEntity(
                documentId = it.documentId,
                chunkText = it.text,
                embedding = it.embedding
            )
        }
        vectorDao.insertAll(entities)
    }

    override suspend fun search(queryVector: List<Float>, topK: Int): List<FloatVector> {
        val allVectors = vectorDao.getAllVectors()
        return allVectors
            .asSequence()
            .map { it to dotProduct(queryVector, it.embedding) }
            .sortedByDescending { it.second }
            .take(topK)
            .map {
                FloatVector(
                    documentId = it.first.documentId,
                    text = it.first.chunkText,
                    embedding = it.first.embedding
                )
            }
            .toList()
    }

    override suspend fun clear() {
        vectorDao.clearAll()
    }

    private fun dotProduct(a: List<Float>, b: List<Float>): Float {
        var result = 0f
        for (i in a.indices) {
            result += a[i] * b[i]
        }
        return result
    }
}
