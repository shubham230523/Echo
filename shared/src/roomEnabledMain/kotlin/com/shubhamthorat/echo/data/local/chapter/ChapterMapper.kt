package com.shubhamthorat.echo.data.local.chapter

import com.shubhamthorat.echo.domain.model.Chapter

fun ChapterEntity.toDomain(): Chapter {
    return Chapter(
        id = id,
        documentId = documentId,
        index = index,
        title = title,
        originalText = originalText,
        narrationText = narrationText,
        estimatedDurationSeconds = estimatedDurationSeconds,
        status = status
    )
}

fun Chapter.toEntity(): ChapterEntity {
    return ChapterEntity(
        id = id,
        documentId = documentId,
        index = index,
        title = title,
        originalText = originalText,
        narrationText = narrationText,
        estimatedDurationSeconds = estimatedDurationSeconds,
        status = status
    )
}
