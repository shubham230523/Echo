package com.shubhamthorat.echo.server.voice

/**
 * Provider-independent interface for Text-to-Speech synthesis.
 */
interface TTSProvider {

    /**
     * Synthesizes the given text into audio using the specified voice and parameters.
     */
    suspend fun synthesize(request: TTSRequest): TTSResult
}
