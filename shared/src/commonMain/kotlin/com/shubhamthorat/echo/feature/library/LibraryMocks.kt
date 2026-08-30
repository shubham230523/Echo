package com.shubhamthorat.echo.feature.library

import com.shubhamthorat.echo.domain.model.Audiobook
import com.shubhamthorat.echo.domain.model.AudiobookStatus
import kotlinx.datetime.Instant

object LibraryMocks {
    val sampleAudiobooks = listOf(
        Audiobook(
            id = "1",
            documentId = "doc_1",
            title = "The Art of War",
            author = "Sun Tzu",
            coverImagePath = null,
            totalDurationSeconds = 3600,
            chapterCount = 13,
            createdAt = Instant.fromEpochMilliseconds(0),
            updatedAt = Instant.fromEpochMilliseconds(0),
            status = AudiobookStatus.READY
        ),
        Audiobook(
            id = "2",
            documentId = "doc_2",
            title = "Meditations",
            author = "Marcus Aurelius",
            coverImagePath = null,
            totalDurationSeconds = 5400,
            chapterCount = 12,
            createdAt = Instant.fromEpochMilliseconds(0),
            updatedAt = Instant.fromEpochMilliseconds(0),
            status = AudiobookStatus.PROCESSING
        ),
        Audiobook(
            id = "3",
            documentId = "doc_3",
            title = "Principles",
            author = "Ray Dalio",
            coverImagePath = null,
            totalDurationSeconds = 0,
            chapterCount = 0,
            createdAt = Instant.fromEpochMilliseconds(0),
            updatedAt = Instant.fromEpochMilliseconds(0),
            status = AudiobookStatus.DRAFT
        )
    )
}
