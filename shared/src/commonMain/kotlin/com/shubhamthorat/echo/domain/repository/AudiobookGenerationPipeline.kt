package com.shubhamthorat.echo.domain.repository

import com.shubhamthorat.echo.domain.model.*

/**
 * Abstraction for the audiobook generation process.
 * Defines the contract for each stage of the pipeline to ensure clean boundaries
 * between the presentation layer and the actual generation implementation.
 */
interface AudiobookGenerationPipeline {

    /**
     * Stage 1: Prepares the document for narration by cleaning text and analyzing structure.
     */
    suspend fun prepareDocument(request: DocumentPreparationRequest): DocumentPreparationResult

    /**
     * Stage 2: Prepares narration data, such as SSML or engine-specific metadata for each chapter.
     */
    suspend fun prepareNarration(request: NarrationPreparationRequest): NarrationPreparationResult

    /**
     * Stage 3: Generates audio for a specific chapter using the selected voice.
     */
    suspend fun generateChapterAudio(request: ChapterGenerationRequest): ChapterGenerationResult

    /**
     * Stage 4: Validates the generated audio for quality, length, and consistency.
     */
    suspend fun validateAudio(request: AudioValidationRequest): AudioValidationResult

    /**
     * Stage 5: Finalizes the audiobook by merging audio files and creating the final metadata.
     */
    suspend fun finalizeAudiobook(request: AudiobookFinalizationRequest): AudiobookFinalizationResult
}
