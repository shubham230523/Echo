package com.shubhamthorat.echo.data.local.chapter

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shubhamthorat.echo.domain.model.ChapterStatus

@Entity(tableName = "chapters")
data class ChapterEntity(
    @PrimaryKey val id: String,
    val documentId: String,
    val index: Int,
    val title: String,
    val originalText: String,
    val narrationText: String,
    val estimatedDurationSeconds: Int,
    val status: ChapterStatus
)
