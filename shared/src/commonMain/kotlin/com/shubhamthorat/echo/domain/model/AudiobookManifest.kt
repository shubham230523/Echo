package com.shubhamthorat.echo.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Represents the final playable structure of an audiobook, including all audio chapter metadata.
 * This manifest is used by the player to organize playback and navigation.
 *
 * @property audiobookId Unique identifier for the audiobook.
 * @property title The display title of the audiobook.
 * @property totalDurationSeconds Combined duration of all chapters in seconds.
 * @property chapters Ordered list of audio chapters.
 * @property createdAt Timestamp when the manifest was generated.
 */
@Serializable
data class AudiobookManifest(
    val audiobookId: String,
    val title: String,
    val totalDurationSeconds: Double,
    val chapters: List<AudioChapter>,
    val createdAt: Instant
)
