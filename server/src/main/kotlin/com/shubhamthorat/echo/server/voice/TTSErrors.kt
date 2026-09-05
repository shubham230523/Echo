package com.shubhamthorat.echo.server.voice

/**
 * Base exception for all TTS provider-related errors.
 */
sealed class TTSProviderException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class RateLimitExceeded(message: String = "TTS rate limit exceeded") : TTSProviderException(message)
    class VoiceNotFound(voiceId: String) : TTSProviderException("Voice with ID $voiceId not found")
    class InvalidParameters(message: String) : TTSProviderException(message)
    class ServiceUnavailable(message: String, cause: Throwable? = null) : TTSProviderException(message, cause)
}
