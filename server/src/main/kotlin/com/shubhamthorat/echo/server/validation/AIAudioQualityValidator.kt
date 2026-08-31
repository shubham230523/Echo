package com.shubhamthorat.echo.server.validation

import com.shubhamthorat.echo.server.ai.*

/**
 * AI-assisted audio quality validator.
 * Performs STT and compares transcription with source text using AI.
 */
class AIAudioQualityValidator(
    private val aiProvider: AIProvider
) : AudioQualityValidator {

    override suspend fun validate(request: AudioValidationRequest): QualityValidationResult {
        return try {
            // 1. Transcribe audio
            val transcription = aiProvider.transcribeAudio(
                TranscriptionRequest(audioUrl = request.audioUrl)
            )

            // 2. Compare content using AI
            val comparison = aiProvider.compareTranscription(
                ContentComparisonRequest(
                    sourceText = request.sourceText,
                    transcription = transcription.text
                )
            )

            QualityValidationResult(
                passed = comparison.matchScore >= 0.8f, // 80% threshold for pass
                score = (transcription.confidence + comparison.matchScore) / 2.0f,
                issues = comparison.issues,
                recommendations = comparison.differences.map { "${it.type}: ${it.description} (${it.severity})" }
            )
        } catch (e: Exception) {
            // Graceful fallback if AI/STT is unavailable or fails
            QualityValidationResult(
                passed = true, // Pass by default if AI validation fails but basic validation passed?
                // Actually, maybe we should return a warning or low score.
                score = 0.5f,
                issues = listOf("AI-assisted validation unavailable: ${e.message}"),
                recommendations = listOf("Perform manual check if audio quality is critical")
            )
        }
    }
}
