package com.shubhamthorat.echo.shared.ai

interface VectorStore {
    suspend fun addVectors(documentId: String, vectors: List<FloatVector>)
    suspend fun search(queryVector: List<Float>, topK: Int): List<FloatVector>
    suspend fun clear()
}

data class FloatVector(
    val documentId: String,
    val text: String,
    val embedding: List<Float>
)
