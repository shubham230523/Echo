package com.shubhamthorat.echo.data.local.document

import com.shubhamthorat.echo.domain.model.Document

fun DocumentEntity.toDomain(): Document {
    return Document(
        id = id,
        fileName = fileName,
        filePath = filePath,
        fileSizeBytes = fileSizeBytes,
        pageCount = pageCount,
        importedAt = importedAt,
        status = status
    )
}

fun Document.toEntity(): DocumentEntity {
    return DocumentEntity(
        id = id,
        fileName = fileName,
        filePath = filePath,
        fileSizeBytes = fileSizeBytes,
        pageCount = pageCount,
        importedAt = importedAt,
        status = status
    )
}
