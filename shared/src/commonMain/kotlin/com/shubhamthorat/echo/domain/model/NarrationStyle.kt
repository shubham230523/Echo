package com.shubhamthorat.echo.domain.model

import kotlinx.serialization.Serializable

/**
 * Defines the tone and style of the narration text optimization.
 */
@Serializable
enum class NarrationStyle {
    /** Standard text-to-speech cleanup, preserving original phrasing. */
    NATURAL,
    /** Dynamic pacing and emphasis, suitable for fiction. */
    STORYTELLING,
    /** Clear, formal, and authoritative, suitable for textbooks or reports. */
    PROFESSIONAL,
    /** Simplified sentences and engaging tone, suitable for blogs or informal content. */
    CONVERSATIONAL
}
