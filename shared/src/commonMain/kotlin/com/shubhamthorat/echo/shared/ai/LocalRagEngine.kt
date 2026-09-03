package com.shubhamthorat.echo.shared.ai

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LocalRagEngine(
    private val pdfExtractor: PdfExtractor,
    private val vectorStore: VectorStore,
    private val embeddingEngine: EmbeddingEngine,
    private val llmEngine: LlmEngine
) : DocumentAnalyzer {

    override suspend fun ingestDocument(documentPath: String): Flow<Float> = flow {
        emit(0.1f)
        val text = pdfExtractor.extractText(documentPath)
        emit(0.3f)
        
        val chunks = TextChunker.chunk(text)
        val totalChunks = chunks.size
        val floatVectors = mutableListOf<FloatVector>()
        
        chunks.forEachIndexed { index, chunk ->
            val embedding = embeddingEngine.getEmbedding(chunk)
            floatVectors.add(FloatVector(documentId = documentPath, text = chunk, embedding = embedding))
            emit(0.3f + (0.7f * (index + 1) / totalChunks))
        }
        
        vectorStore.addVectors(documentPath, floatVectors)
        emit(1.0f)
    }

    override suspend fun ask(query: String): String {
        val queryEmbedding = embeddingEngine.getEmbedding(query)
        val relevantChunks = vectorStore.search(queryEmbedding, topK = 3)
        
        val context = relevantChunks.joinToString("\n---\n") { it.text }
        val prompt = """
            Use the following pieces of context to answer the question at the end. 
            If you don't know the answer, just say that you don't know, don't try to make up an answer.
            
            Context:
            $context
            
            Question: $query
            Answer:
        """.trimIndent()
        
        return llmEngine.generate(prompt)
    }

    override suspend fun clearIndex() {
        vectorStore.clear()
    }
}
