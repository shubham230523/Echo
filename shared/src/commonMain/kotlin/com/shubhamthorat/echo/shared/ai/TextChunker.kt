package com.shubhamthorat.echo.shared.ai

object TextChunker {
    fun chunk(text: String, chunkSize: Int = 500, overlap: Int = 50): List<String> {
        if (text.isEmpty()) return emptyList()
        val chunks = mutableListOf<String>()
        var start = 0
        while (start < text.length) {
            val end = minOf(start + chunkSize, text.length)
            chunks.add(text.substring(start, end))
            start += (chunkSize - overlap)
        }
        return chunks
    }
}
