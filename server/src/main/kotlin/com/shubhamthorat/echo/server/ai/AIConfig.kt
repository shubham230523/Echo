package com.shubhamthorat.echo.server.ai

/**
 * Types of supported AI providers.
 */
enum class AIProviderType {
    GEMINI,
    OLLAMA,
    OPENAI_COMPATIBLE,
    OPENROUTER,
    MOCK // For development/testing
}

/**
 * Configuration for the AI system, sourced from environment variables.
 */
data class AIConfig(
    val providerType: AIProviderType,
    val apiKey: String?,
    val baseUrl: String?,
    val modelName: String
) {
    companion object {
        fun fromEnvironment(): AIConfig {
            val typeStr = System.getenv("AI_PROVIDER") ?: "OPENROUTER"
            val type = try {
                AIProviderType.valueOf(typeStr.uppercase())
            } catch (e: Exception) {
                AIProviderType.OPENROUTER
            }

            val apiKey = System.getenv("AI_API_KEY")

            return AIConfig(
                providerType = type,
                apiKey = apiKey,
                baseUrl = System.getenv("AI_BASE_URL"), // Mainly for Ollama or custom OpenAI endpoints
                modelName = System.getenv("AI_MODEL_NAME") ?: when (type) {
                    AIProviderType.GEMINI -> "gemini-1.5-pro"
                    AIProviderType.OLLAMA -> "llama3"
                    AIProviderType.OPENAI_COMPATIBLE -> "gpt-4o"
                    AIProviderType.OPENROUTER -> "minimax/minimax-m3:free"
                    AIProviderType.MOCK -> "mock-model"
                }
            )
        }
    }
}
