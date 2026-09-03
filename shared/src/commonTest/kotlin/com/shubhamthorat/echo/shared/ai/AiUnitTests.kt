package com.shubhamthorat.echo.shared.ai

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AiUnitTests {

    @Test
    fun testTextChunking() {
        val text = "This is a long text that needs to be chunked into smaller pieces for better analysis."
        val chunks = TextChunker.chunk(text, chunkSize = 20, overlap = 5)
        
        assertTrue(chunks.isNotEmpty())
        assertTrue(chunks.size > 1)
        assertEquals("This is a long text ", chunks[0])
    }

    @Test
    fun testVectorSearch() {
        val vectorStore = InMemoryVectorStore()
        val docId = "test.pdf"
        
        val vectors = listOf(
            FloatVector(docId, "Chunk 1", listOf(1.0f, 0.0f, 0.0f)),
            FloatVector(docId, "Chunk 2", listOf(0.0f, 1.0f, 0.0f)),
            FloatVector(docId, "Chunk 3", listOf(0.0f, 0.0f, 1.0f))
        )
        
        suspend fun runTest() {
            vectorStore.addVectors(docId, vectors)
            
            val query = listOf(0.9f, 0.1f, 0.0f)
            val results = vectorStore.search(query, topK = 1)
            
            assertEquals(1, results.size)
            assertEquals("Chunk 1", results[0].text)
        }
    }
}
