package com.shubhamthorat.echo.domain.repository

import com.shubhamthorat.echo.core.result.AppResult
import com.shubhamthorat.echo.domain.model.ChapterDetectionRequest
import com.shubhamthorat.echo.domain.model.ChapterDetectionResult

/**
 * Interface for components that can detect logical chapters within a document.
 * 
 * Supports different implementations such as rule-based (regex/keywords) 
 * or AI-based (NLP/LLM) detection.
 */
interface ChapterDetector {

    /**
     * Analyzes the provided text to identify chapter boundaries and titles.
     *
     * @param request The detection request containing text and metadata.
     * @return [AppResult] containing the [ChapterDetectionResult] on success.
     */
    suspend fun detectChapters(request: ChapterDetectionRequest): AppResult<ChapterDetectionResult>
}
