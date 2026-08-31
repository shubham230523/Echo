package com.shubhamthorat.echo.server.validation

/**
 * Orchestrates multiple validators to provide a comprehensive quality check.
 */
class CompositeAudioQualityValidator(
    private val deterministicValidator: AudioQualityValidator,
    private val aiValidator: AudioQualityValidator? = null
) : AudioQualityValidator {

    override suspend fun validate(request: AudioValidationRequest): QualityValidationResult {
        // Always perform basic deterministic validation first
        val basicResult = deterministicValidator.validate(request)
        if (!basicResult.passed) {
            return basicResult
        }

        // Optionally perform AI-assisted validation
        if (aiValidator != null) {
            val aiResult = aiValidator.validate(request)
            
            return QualityValidationResult(
                passed = aiResult.passed,
                score = (basicResult.score + aiResult.score) / 2.0f,
                issues = basicResult.issues + aiResult.issues,
                recommendations = basicResult.recommendations + aiResult.recommendations
            )
        }

        return basicResult
    }
}
