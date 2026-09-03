package com.shubhamthorat.echo.data.local.document

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shubhamthorat.echo.domain.model.DocumentStatus
import kotlinx.datetime.Instant

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey val id: String,
    val fileName: String,
    val filePath: String,
    val fileSizeBytes: Long,
    val pageCount: Int,
    val importedAt: Instant,
    val status: DocumentStatus
)
