package com.shubhamthorat.echo.server.ai

/**
 * Types of supported AI providers.
 */
enum class AIProviderType {
    GEMINI,
    OLLAMA,
    OPENAI_COMPATIBLE,
    OPENROUTER,
    LOCAL,
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
            
            // Simulation check: if keys are exactly the dummy ones and not using LOCAL, force MOCK
            val isDummyKey = apiKey == "ai-api-key" || apiKey == "dummy"
            val finalType = if (isDummyKey && type != AIProviderType.LOCAL) AIProviderType.MOCK else type
            
            // Bypass caching (restoring b2fb240 behavior) when using real keys
            // Default useCache to true ONLY for dummy/mock mode or if explicitly requested
            val useCache = if (!isDummyKey) {
                getProperty("AI_USE_CACHE")?.toBoolean() ?: false 
            } else {
                getProperty("AI_USE_CACHE")?.toBoolean() ?: true
            }

            return AIConfig(
                providerType = finalType,
                apiKey = apiKey,
                baseUrl = getProperty("AI_BASE_URL"),
                modelName = getProperty("AI_MODEL_NAME") ?: when (finalType) {
                    AIProviderType.GEMINI -> "gemini-1.5-pro"
                    AIProviderType.OLLAMA -> "llama3"
                    AIProviderType.OPENAI_COMPATIBLE -> "gpt-4o"
                    AIProviderType.OPENROUTER -> "nvidia/nemotron-3.5-lightning:free"
                    AIProviderType.LOCAL -> "local-qwen"
                    AIProviderType.MOCK -> "mock-model"
                },
                useCache = useCache
            )
        }
    }
}
