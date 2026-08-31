package com.shubhamthorat.echo.server.ai

import kotlinx.serialization.json.Json

object JsonExtractor {
    val json = Json { 
        ignoreUnknownKeys = true 
        isLenient = true
    }

    inline fun <reified T> extract(content: String): T {
        // Find the first '{' and last '}'
        val start = content.indexOf('{')
        val end = content.lastIndexOf('}')
        
        if (start == -1 || end == -1 || end <= start) {
            throw AIProviderException.ServiceUnavailable("Malformed AI response: Could not find JSON block.")
        }
        
        val jsonBlock = content.substring(start, end + 1)
        return try {
            json.decodeFromString<T>(jsonBlock)
        } catch (e: Exception) {
            throw AIProviderException.ServiceUnavailable("Failed to parse AI JSON: ${e.message}")
        }
    }
}
