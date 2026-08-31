package com.shubhamthorat.echo.server.ai

/**
 * Base exception for all AI provider-related errors.
 */
sealed class AIProviderException(message: String, cause: Throwable? = null) : Exception(message, cause) {

    /**
     * Thrown when the provider's rate limit is reached.
     */
    class RateLimitExceeded(message: String = "Rate limit exceeded") : AIProviderException(message)

    /**
     * Thrown when the input text exceeds the provider's token or character limit.
     */
    class TokenLimitExceeded(message: String = "Input exceeds token limit") : AIProviderException(message)

    /**
     * Thrown when the provider's content safety filters flag the input or output.
     */
    class ContentPolicyViolation(message: String) : AIProviderException(message)

    /**
     * Thrown for authentication or configuration errors with the AI provider.
     */
    class ConfigurationError(message: String) : AIProviderException(message)

    /**
     * Generic error for unexpected provider failures.
     */
    class ServiceUnavailable(message: String, cause: Throwable? = null) : AIProviderException(message, cause)
}
