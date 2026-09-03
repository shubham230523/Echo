package com.shubhamthorat.echo.server.ai

import kotlinx.serialization.json.Json

object JsonExtractor {
    val json = Json { 
        ignoreUnknownKeys = true 
        isLenient = true
    }

    inline fun <reified T> extract(content: String): T {
        val trimmed = content.trim()
        
        // Find the first '{' and last '}' to isolate the JSON block
        val start = trimmed.indexOf('{')
        val end = trimmed.lastIndexOf('}')
        
        if (start == -1 || end == -1 || end <= start) {
            println("❌ FAILED TO FIND JSON BLOCK IN CONTENT. Snippet: ${trimmed.take(500)}...")
            throw AIProviderException.ServiceUnavailable("Malformed AI response: Could not find JSON block.")
        }
        
        val jsonBlock = trimmed.substring(start, end + 1)
        return try {
            json.decodeFromString<T>(jsonBlock)
        } catch (e: Exception) {
            // If it fails, try a more aggressive approach: find the first { that starts a valid JSON
            // But for now, we'll just log and throw.
            println("❌ JSON PARSING ERROR: ${e.message}")
            println("RAW JSON BLOCK: $jsonBlock")
            throw AIProviderException.ServiceUnavailable("Failed to parse AI JSON: ${e.message}")
        }
    }
}
