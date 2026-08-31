package com.shubhamthorat.echo.server.ai

/**
 * Types of supported AI providers.
 */
enum class AIProviderType {
    GEMINI,
    OLLAMA,
    OPENAI_COMPATIBLE,
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
            val typeStr = System.getenv("AI_PROVIDER") ?: "MOCK"
            val type = try {
                AIProviderType.valueOf(typeStr.uppercase())
            } catch (e: Exception) {
                AIProviderType.MOCK
            }

            return AIConfig(
                providerType = type,
                apiKey = System.getenv("AI_API_KEY"),
                baseUrl = System.getenv("AI_BASE_URL"), // Mainly for Ollama or custom OpenAI endpoints
                modelName = System.getenv("AI_MODEL_NAME") ?: when (type) {
                    AIProviderType.GEMINI -> "gemini-1.5-pro"
                    AIProviderType.OLLAMA -> "llama3"
                    AIProviderType.OPENAI_COMPATIBLE -> "gpt-4o"
                    AIProviderType.MOCK -> "mock-model"
                }
            )
        }
    }
}
