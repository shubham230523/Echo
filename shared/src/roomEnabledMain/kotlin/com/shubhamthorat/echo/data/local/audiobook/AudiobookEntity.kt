package com.shubhamthorat.echo.data.local.audiobook

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shubhamthorat.echo.domain.model.AudiobookStatus
import kotlinx.datetime.Instant

@Entity(tableName = "audiobooks")
data class AudiobookEntity(
    @PrimaryKey val id: String,
    val documentId: String,
    val title: String,
    val author: String,
    val coverImagePath: String?,
    val totalDurationSeconds: Int,
    val chapterCount: Int,
    val createdAt: Instant,
    val updatedAt: Instant,
    val status: AudiobookStatus
)
