package com.shubhamthorat.echo.data.repository

import com.shubhamthorat.echo.core.result.AppResult
import com.shubhamthorat.echo.domain.model.*
import com.shubhamthorat.echo.domain.repository.ChapterDetector

/**
 * A deterministic, regex-based implementation of [ChapterDetector].
 * Identifies common chapter headings and splits text accordingly.
 */
class RuleBasedChapterDetector : ChapterDetector {

    private val chapterPattern = Regex(
        pattern = "^(?i)(Chapter|Part|Section)\\s+([0-9]+|One|Two|Three|Four|Five|Six|Seven|Eight|Nine|Ten)\\b.*$",
        options = setOf(RegexOption.MULTILINE)
    )

    override suspend fun detectChapters(request: ChapterDetectionRequest): AppResult<ChapterDetectionResult> {
        val text = request.cleanedText
        if (text.isBlank()) {
            return AppResult.Success(
                ChapterDetectionResult(
                    chapters = emptyList(),
                    detectionMethod = "RuleBased"
                )
            )
        }

        val matches = chapterPattern.findAll(text).toList()

        if (matches.isEmpty()) {
            val singleChapter = createChapter(
                documentId = request.documentId,
                index = 0,
                title = "Document",
                content = text
            )
            return AppResult.Success(
                ChapterDetectionResult(
                    chapters = listOf(singleChapter),
                    detectionMethod = "RuleBased"
                )
            )
        }

        val chapters = mutableListOf<Chapter>()
        
        // Handle potential pre-chapter text (e.g. Intro/Foreword) if significant
        val firstMatchIndex = matches.first().range.first
        if (firstMatchIndex > 50) { // Threshold for "significant" pre-chapter text
            val preChapterText = text.substring(0, firstMatchIndex).trim()
            if (preChapterText.isNotEmpty()) {
                chapters.add(
                    createChapter(
                        documentId = request.documentId,
                        index = 0,
                        title = "Introduction",
                        content = preChapterText
                    )
                )
            }
        }

        // Process detected chapters
        for (i in matches.indices) {
            val match = matches[i]
            val nextMatchStart = if (i + 1 < matches.size) matches[i + 1].range.first else text.length
            
            val chapterTitle = match.value.trim()
            val chapterContent = text.substring(match.range.first, nextMatchStart).trim()
            
            chapters.add(
                createChapter(
                    documentId = request.documentId,
                    index = chapters.size,
                    title = chapterTitle,
                    content = chapterContent
                )
            )
        }

        return AppResult.Success(
            ChapterDetectionResult(
                chapters = chapters,
                detectionMethod = "RuleBased",
                metadata = mapOf("matchCount" to matches.size.toString())
            )
        )
    }

    private fun createChapter(
        documentId: String,
        index: Int,
        title: String,
        content: String
    ): Chapter {
        return Chapter(
            id = "${documentId}_ch_${index}",
            documentId = documentId,
            index = index,
            title = title,
            originalText = content,
            narrationText = content, // Default to same as original
            estimatedDurationSeconds = estimateDuration(content),
            status = ChapterStatus.PENDING
        )
    }

    private fun estimateDuration(text: String): Int {
        val wordCount = text.split(Regex("\\s+")).filter { it.isNotBlank() }.size
        // Average speaking rate: 130 words per minute (approx 2.1 words per second)
        return (wordCount / 2.1).toInt()
    }
}
