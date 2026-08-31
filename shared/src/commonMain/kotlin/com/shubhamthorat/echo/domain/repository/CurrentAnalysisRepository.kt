package com.shubhamthorat.echo.domain.repository

import com.shubhamthorat.echo.domain.model.Chapter
import com.shubhamthorat.echo.domain.model.Document
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * In-memory repository to hold the current document and detected chapters
 * during the analysis and configuration flow.
 */
class CurrentAnalysisRepository {
    
    private val _currentDocument = MutableStateFlow<Document?>(null)
    val currentDocument: StateFlow<Document?> = _currentDocument.asStateFlow()

    private val _chapters = MutableStateFlow<List<Chapter>>(emptyList())
    val chapters: StateFlow<List<Chapter>> = _chapters.asStateFlow()

    private val _selectedVoiceId = MutableStateFlow<String?>(null)
    val selectedVoiceId: StateFlow<String?> = _selectedVoiceId.asStateFlow()

    fun setAnalysisResult(document: Document, chapters: List<Chapter>) {
        _currentDocument.value = document
        _chapters.value = chapters
    }

    fun updateChapters(chapters: List<Chapter>) {
        _chapters.value = chapters
    }

    fun setVoiceId(voiceId: String?) {
        _selectedVoiceId.value = voiceId
    }

    fun clear() {
        _currentDocument.value = null
        _chapters.value = emptyList()
        _selectedVoiceId.value = null
    }
}
