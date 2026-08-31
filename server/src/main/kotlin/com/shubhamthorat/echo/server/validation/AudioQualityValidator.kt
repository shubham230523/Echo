package com.shubhamthorat.echo.server.validation

/**
 * Interface for validating the quality of generated audio.
 */
interface AudioQualityValidator {

    /**
     * Validates the quality of a generated audio file against the source text.
     */
    suspend fun validate(request: AudioValidationRequest): QualityValidationResult
}
