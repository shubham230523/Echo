package com.shubhamthorat.echo.feature.chapters

import androidx.lifecycle.ViewModel
import com.shubhamthorat.echo.domain.model.Chapter
import com.shubhamthorat.echo.domain.model.ChapterStatus
import com.shubhamthorat.echo.domain.repository.CurrentAnalysisRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import androidx.lifecycle.viewModelScope

/**
 * ViewModel for the Chapters screen.
 */
class ChaptersViewModel(
    private val currentAnalysisRepository: CurrentAnalysisRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChaptersUiState(isLoading = true))
    val uiState: StateFlow<ChaptersUiState> = _uiState.asStateFlow()

    init {
        observeAnalysisResult()
    }

    fun onEditChapterClick(chapter: Chapter) {
        _uiState.value = _uiState.value.copy(editingChapter = chapter)
    }

    fun onDismissEditDialog() {
        _uiState.value = _uiState.value.copy(editingChapter = null)
    }

    fun onUpdateChapterTitle(newTitle: String) {
        val currentEditing = _uiState.value.editingChapter ?: return
        if (newTitle.isBlank()) return

        val updatedChapters = _uiState.value.chapters.map { chapter ->
            if (chapter.id == currentEditing.id) {
                chapter.copy(title = newTitle)
            } else {
                chapter
            }
        }

        _uiState.value = _uiState.value.copy(
            chapters = updatedChapters,
            editingChapter = null
        )
        currentAnalysisRepository.updateChapters(updatedChapters)
    }

    fun toggleChapterSelection(chapterId: String) {
        val currentSelected = _uiState.value.selectedChapterIds
        val newSelected = if (currentSelected.contains(chapterId)) {
            currentSelected - chapterId
        } else {
            currentSelected + chapterId
        }
        _uiState.value = _uiState.value.copy(selectedChapterIds = newSelected)
    }

    fun mergeSelectedChapters() {
        val selectedIds = _uiState.value.selectedChapterIds
        if (selectedIds.size < 2) return

        val allChapters = _uiState.value.chapters
        val selectedChapters = allChapters.filter { it.id in selectedIds }
            .sortedBy { it.index }

        // Check adjacency
        val indices = selectedChapters.map { it.index }
        val isAdjacent = indices.zipWithNext().all { (a, b) -> b == a + 1 }

        if (!isAdjacent) {
            _uiState.value = _uiState.value.copy(error = "Only adjacent chapters can be merged.")
            return
        }

        val firstChapter = selectedChapters.first()
        val mergedChapter = Chapter(
            id = firstChapter.id,
            documentId = firstChapter.documentId,
            index = firstChapter.index,
            title = firstChapter.title,
            originalText = selectedChapters.joinToString("\n\n") { it.originalText },
            narrationText = selectedChapters.joinToString("\n\n") { it.narrationText },
            estimatedDurationSeconds = selectedChapters.sumOf { it.estimatedDurationSeconds },
            status = firstChapter.status
        )

        val newChaptersList = mutableListOf<Chapter>()
        var insertedMerged = false

        allChapters.forEach { chapter ->
            if (chapter.id in selectedIds) {
                if (!insertedMerged) {
                    newChaptersList.add(mergedChapter)
                    insertedMerged = true
                }
            } else {
                newChaptersList.add(chapter)
            }
        }

        // Re-index
        val reIndexedChapters = newChaptersList.mapIndexed { i, chapter ->
            chapter.copy(index = i)
        }

        _uiState.value = _uiState.value.copy(
            chapters = reIndexedChapters,
            selectedChapterIds = emptySet(),
            error = null
        )
        currentAnalysisRepository.updateChapters(reIndexedChapters)
    }

    private fun observeAnalysisResult() {
        currentAnalysisRepository.currentDocument.onEach { document ->
            _uiState.value = _uiState.value.copy(
                documentTitle = document?.fileName ?: "Unknown Document"
            )
        }.launchIn(viewModelScope)

        currentAnalysisRepository.chapters.onEach { chapters ->
            _uiState.value = _uiState.value.copy(
                chapters = chapters,
                isLoading = false
            )
            
            if (chapters.isEmpty() && _uiState.value.documentTitle != "") {
                _uiState.value = _uiState.value.copy(
                    error = "No chapters could be detected."
                )
            }
        }.launchIn(viewModelScope)
    }
}
