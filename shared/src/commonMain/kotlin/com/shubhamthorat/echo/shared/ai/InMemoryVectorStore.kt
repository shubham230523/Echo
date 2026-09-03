package com.shubhamthorat.echo.shared.ai

class InMemoryVectorStore : VectorStore {
    private val vectors = mutableListOf<FloatVector>()

    override suspend fun addVectors(documentId: String, vectors: List<FloatVector>) {
        this.vectors.addAll(vectors)
    }

    override suspend fun search(queryVector: List<Float>, topK: Int): List<FloatVector> {
        return vectors
            .asSequence()
            .map { it to dotProduct(queryVector, it.embedding) }
            .sortedByDescending { it.second }
            .take(topK)
            .map { it.first }
            .toList()
    }

    override suspend fun clear() {
        vectors.clear()
    }

    private fun dotProduct(a: List<Float>, b: List<Float>): Float {
        var result = 0f
        val size = minOf(a.size, b.size)
        for (i in 0 until size) {
            result += a[i] * b[i]
        }
        return result
    }
}
