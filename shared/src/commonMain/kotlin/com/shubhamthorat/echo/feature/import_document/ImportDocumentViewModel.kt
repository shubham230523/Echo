package com.shubhamthorat.echo.feature.import_document

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shubhamthorat.echo.core.common.PlatformFile
import com.shubhamthorat.echo.domain.model.Document
import com.shubhamthorat.echo.domain.model.DocumentStatus
import com.shubhamthorat.echo.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ImportDocumentViewModel(
    private val documentRepository: DocumentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ImportDocumentUiState())
    val uiState: StateFlow<ImportDocumentUiState> = _uiState.asStateFlow()

    fun onFileSelected(file: PlatformFile) {
        _uiState.update { it.copy(selectedFile = file, error = null) }
    }

    fun onContinue() {
        val file = _uiState.value.selectedFile ?: return
        
        val validation = validateSelectedFile(file)
        if (validation is FileValidationResult.Invalid) {
            _uiState.update { it.copy(error = validation.message) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isImporting = true, error = null) }
            try {
                // Generate a stable ID based on path to avoid duplicates if same file re-imported
                val documentId = generateDocumentId(file)
                
                val existing = documentRepository.getDocumentById(documentId)
                if (existing != null) {
                    // Already exists, just proceed
                    _uiState.update { 
                        it.copy(
                            isImporting = false, 
                            isSuccess = true, 
                            importedDocumentId = documentId 
                        ) 
                    }
                    return@launch
                }

                val document = Document(
                    id = documentId,
                    fileName = file.name,
                    filePath = file.path,
                    fileSizeBytes = file.sizeBytes ?: 0L,
                    pageCount = 0, // Will be updated during analysis
                    importedAt = kotlinx.datetime.Instant.fromEpochMilliseconds(0), // Placeholder for MVP
                    status = DocumentStatus.IMPORTED
                )

                documentRepository.insertDocument(document)
                
                _uiState.update { 
                    it.copy(
                        isImporting = false, 
                        isSuccess = true, 
                        importedDocumentId = documentId 
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isImporting = false, error = e.message ?: "Failed to import document") }
            }
        }
    }

    private fun generateDocumentId(file: PlatformFile): String {
        // Simple hash of path for MVP, ideally use a more robust unique ID
        return file.path.hashCode().toString()
    }
}
