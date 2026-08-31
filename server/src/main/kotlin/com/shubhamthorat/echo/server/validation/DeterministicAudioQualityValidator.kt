package com.shubhamthorat.echo.server.validation

import java.io.File
import java.net.URI

/**
 * A deterministic validator that performs basic integrity checks on generated audio files.
 */
class DeterministicAudioQualityValidator : AudioQualityValidator {

    override suspend fun validate(request: AudioValidationRequest): QualityValidationResult {
        val issues = mutableListOf<String>()
        val recommendations = mutableListOf<String>()

        // 1. Check if file exists
        val file = try {
            File(URI(request.audioUrl))
        } catch (e: Exception) {
            null
        }

        if (file == null || !file.exists()) {
            return QualityValidationResult(
                passed = false,
                score = 0.0f,
                issues = listOf("Audio file not found at: ${request.audioUrl}"),
                recommendations = listOf("Verify storage path and retry generation")
            )
        }

        // 2. Check file size
        if (file.length() <= 0) {
            issues.add("Audio file is empty (0 bytes)")
            recommendations.add("Check TTS provider output and connectivity")
        }

        // 3. Check duration
        val minDurationThreshold = 0.5 // seconds
        if (request.durationSeconds < minDurationThreshold) {
            issues.add("Audio duration is too short (${request.durationSeconds}s). Minimum allowed is ${minDurationThreshold}s.")
            recommendations.add("Ensure input text is sufficient for audio generation")
        }

        // 4. Check file format
        val supportedFormats = listOf("MP3", "WAV")
        if (!supportedFormats.contains(request.format.uppercase())) {
            issues.add("Unsupported audio format: ${request.format}")
            recommendations.add("Configure TTS provider to use one of: ${supportedFormats.joinToString()}")
        }

        val passed = issues.isEmpty()
        val score = if (passed) 1.0f else 0.0f // Deterministic pass/fail

        return QualityValidationResult(
            passed = passed,
            score = score,
            issues = issues,
            recommendations = recommendations
        )
    }
}
