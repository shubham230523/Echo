package com.shubhamthorat.echo.data.local.audiobook

import com.shubhamthorat.echo.domain.model.Audiobook

fun AudiobookEntity.toDomain(): Audiobook {
    return Audiobook(
        id = id,
        documentId = documentId,
        title = title,
        author = author,
        coverImagePath = coverImagePath,
        totalDurationSeconds = totalDurationSeconds,
        chapterCount = chapterCount,
        createdAt = createdAt,
        updatedAt = updatedAt,
        status = status
    )
}

fun Audiobook.toEntity(): AudiobookEntity {
    return AudiobookEntity(
        id = id,
        documentId = documentId,
        title = title,
        author = author,
        coverImagePath = coverImagePath,
        totalDurationSeconds = totalDurationSeconds,
        chapterCount = chapterCount,
        createdAt = createdAt,
        updatedAt = updatedAt,
        status = status
    )
}
