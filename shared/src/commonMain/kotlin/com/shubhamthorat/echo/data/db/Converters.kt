package com.shubhamthorat.echo.data.db

import androidx.room.TypeConverter
import com.shubhamthorat.echo.domain.model.AudiobookStatus
import com.shubhamthorat.echo.domain.model.ChapterStatus
import com.shubhamthorat.echo.domain.model.DocumentStatus
import kotlinx.datetime.Instant

class Converters {
    @TypeConverter
    fun fromChapterStatus(status: ChapterStatus): String {
        return status.name
    }

    @TypeConverter
    fun toChapterStatus(statusName: String): ChapterStatus {
        return ChapterStatus.valueOf(statusName)
    }

    @TypeConverter
    fun fromAudiobookStatus(status: AudiobookStatus): String {
        return status.name
    }

    @TypeConverter
    fun toAudiobookStatus(statusName: String): AudiobookStatus {
        return AudiobookStatus.valueOf(statusName)
    }

    @TypeConverter
    fun fromDocumentStatus(status: DocumentStatus): String {
        return status.name
    }

    @TypeConverter
    fun toDocumentStatus(statusName: String): DocumentStatus {
        return DocumentStatus.valueOf(statusName)
    }

    @TypeConverter
    fun fromInstant(instant: Instant): Long {
        return instant.toEpochMilliseconds()
    }

    @TypeConverter
    fun toInstant(millis: Long): Instant {
        return Instant.fromEpochMilliseconds(millis)
    }
}
