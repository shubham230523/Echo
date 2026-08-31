package com.shubhamthorat.echo.domain.model

import kotlinx.serialization.Serializable

/**
 * Status of the audio generation for an individual chapter.
 */
enum class AudioGenerationStatus {
    NOT_STARTED,
    GENERATING,
    COMPLETED,
    FAILED
}

/**
 * Domain model representing the audio metadata for a specific chapter.
 *
 * @property chapterId Unique identifier of the chapter.
 * @property audioPath Local path or URL to the generated audio file.
 * @property durationSeconds Duration of the audio in seconds.
 * @property fileSizeBytes Size of the audio file in bytes.
 * @property generationStatus Current generation status of this specific chapter.
 */
@Serializable
data class AudioChapter(
    val chapterId: String,
    val audioPath: String? = null,
    val durationSeconds: Double = 0.0,
    val fileSizeBytes: Long = 0L,
    val generationStatus: AudioGenerationStatus = AudioGenerationStatus.NOT_STARTED
)
