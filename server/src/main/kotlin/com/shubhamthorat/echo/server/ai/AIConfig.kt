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
    val modelName: String,
    val useCache: Boolean = true
) {
    companion object {
        fun fromEnvironment(getProperty: (String) -> String? = { System.getenv(it) }): AIConfig {
            val typeStr = getProperty("AI_PROVIDER") ?: "OPENROUTER"
            val type = try {
                AIProviderType.valueOf(typeStr.uppercase())
            } catch (e: Exception) {
                AIProviderType.OPENROUTER
            }

            val apiKey = getProperty("AI_API_KEY")
            val useCache = getProperty("AI_USE_CACHE")?.toBoolean() ?: true

            return AIConfig(
                providerType = type,
                apiKey = apiKey,
                baseUrl = getProperty("AI_BASE_URL"), // Mainly for Ollama or custom OpenAI endpoints
                modelName = getProperty("AI_MODEL_NAME") ?: when (type) {
                    AIProviderType.GEMINI -> "gemini-1.5-pro"
                    AIProviderType.OLLAMA -> "llama3"
                    AIProviderType.OPENAI_COMPATIBLE -> "gpt-4o"
                    AIProviderType.OPENROUTER -> "nvidia/nemotron-3.5-lightning:free"
                    AIProviderType.MOCK -> "mock-model"
                },
                useCache = useCache
            )
        }
    }
}
