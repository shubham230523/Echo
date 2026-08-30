package com.shubhamthorat.echo.domain.repository

import com.shubhamthorat.echo.core.result.AppResult
import com.shubhamthorat.echo.domain.model.NarrationPreparationRequest
import com.shubhamthorat.echo.domain.model.NarrationPreparationResult

/**
 * Interface for preparing and optimizing text for narration.
 * This can be implemented by rule-based engines or AI-powered LLMs.
 */
interface NarrationProcessor {

    /**
     * Prepares the given chapter text for narration based on requested style and language.
     *
     * @param request The preparation parameters including chapter content and style.
     * @return [AppResult] containing the [NarrationPreparationResult] on success.
     */
    suspend fun prepareNarration(
        request: NarrationPreparationRequest
    ): AppResult<NarrationPreparationResult>
}
