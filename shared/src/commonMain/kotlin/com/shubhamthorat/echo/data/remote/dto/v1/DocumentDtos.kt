package com.shubhamthorat.echo.data.remote.dto.v1

import kotlinx.serialization.Serializable

@Serializable
data class AnalyzeDocumentResponse(
    val analysisId: String,
    val fileName: String,
    val pageCount: Int,
    val totalCharacters: Int,
    val totalWords: Int,
    val title: String,
    val author: String?,
    val documentType: String,
    val language: String,
    val hierarchy: List<ChapterDto>,
    val status: String
)

@Serializable
data class ChapterDto(
    val id: String,
    val title: String,
    val index: Int,
    val content: String? = null,
    val byteOffset: Long? = null
)
