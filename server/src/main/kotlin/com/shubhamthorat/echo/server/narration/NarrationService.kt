package com.shubhamthorat.echo.server.narration

import com.shubhamthorat.echo.server.ai.AIProvider
import com.shubhamthorat.echo.server.ai.NarrationPreparationRequest
import com.shubhamthorat.echo.server.ai.NarrationPreparationResponse

class NarrationService(private val aiProvider: AIProvider) {

    suspend fun prepareNarration(text: String, style: String): NarrationPreparationResponse {
        if (text.isBlank()) {
            throw IllegalArgumentException("Text cannot be empty")
        }

        val response = aiProvider.prepareNarration(
            NarrationPreparationRequest(
                text = text,
                style = style
            )
        )

        // Basic validation: ensure AI didn't return something too short or too long compared to input
        validateNarrationQuality(text, response.preparedText)

        return response
    }

    private fun validateNarrationQuality(original: String, prepared: String) {
        val originalLen = original.length
        val preparedLen = prepared.length

        // AI shouldn't shrink or expand the text by more than 30% usually for simple narration optimization
        val threshold = 0.3
        val diff = Math.abs(originalLen - preparedLen).toDouble() / originalLen

        if (diff > threshold) {
            // Log warning in real app
            println("Warning: Significant length difference in narration prep: Original=$originalLen, Prepared=$preparedLen")
        }
    }
}
