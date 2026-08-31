package com.shubhamthorat.echo.server.ai

/**
 * Provider-independent interface for AI-powered features.
 */
interface AIProvider {

    /**
     * Analyzes the overall hierarchical structure of a document.
     */
    suspend fun analyzeDocumentStructure(request: DocumentStructureRequest): DocumentStructureResponse

    /**
     * Detects individual chapters within a large block of text.
     */
    suspend fun detectChapters(request: ChapterDetectionRequest): ChapterDetectionResponse

    /**
     * Optimizes text for high-quality narration, optionally generating SSML.
     */
    suspend fun prepareNarration(request: NarrationPreparationRequest): NarrationPreparationResponse

    /**
     * Identifies dialogue segments and attributes them to characters.
     */
    suspend fun detectDialogue(request: DialogueDetectionRequest): DialogueDetectionResponse

    /**
     * Provides pronunciation guides for difficult words or names.
     */
    suspend fun assistPronunciation(request: PronunciationRequest): PronunciationResponse
}
