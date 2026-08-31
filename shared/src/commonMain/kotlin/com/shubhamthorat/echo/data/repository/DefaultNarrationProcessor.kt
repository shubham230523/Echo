package com.shubhamthorat.echo.data.repository

import com.shubhamthorat.echo.core.result.AppResult
import com.shubhamthorat.echo.domain.model.NarrationPreparationRequest
import com.shubhamthorat.echo.domain.model.NarrationPreparationResult
import com.shubhamthorat.echo.domain.repository.NarrationProcessor

/**
 * A deterministic, rule-based implementation of [NarrationProcessor] for MVP.
 * Performs basic text normalization without altering sentence structure or using AI.
 */
class DefaultNarrationProcessor : NarrationProcessor {

    override suspend fun prepareNarration(
        request: NarrationPreparationRequest
    ): AppResult<NarrationPreparationResult> {
        val originalText = request.chapter.originalText
        
        if (originalText.isBlank()) {
            return AppResult.Success(
                NarrationPreparationResult(
                    chapterId = request.chapter.id,
                    narrationText = "",
                    changesSummary = "Empty content"
                )
            )
        }

        // 1. Split into paragraphs to preserve structure
        val paragraphs = originalText.split(Regex("\\n\\s*\\n"))
        
        // 2. Clean each paragraph
        val cleanedText = paragraphs.joinToString("\n\n") { paragraph ->
            paragraph.trim()
                .replace(Regex("\\s+"), " ") // Normalize internal whitespace
        }

        return AppResult.Success(
            NarrationPreparationResult(
                chapterId = request.chapter.id,
                narrationText = cleanedText,
                changesSummary = "Default text normalization applied (MVP fallback)."
            )
        )
    }
}
