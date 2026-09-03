package com.shubhamthorat.echo.domain.service

import com.shubhamthorat.echo.domain.model.Chapter
import com.shubhamthorat.echo.shared.ai.AudioGenerator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LocalAudiobookGenerator(
    private val audioGenerator: AudioGenerator
) {
    fun generate(chapters: List<Chapter>): Flow<GenerationProgress> = flow {
        val total = chapters.size
        chapters.forEachIndexed { index, chapter ->
            emit(GenerationProgress(
                status = "PROCESSING",
                progress = index.toFloat() / total,
                currentStep = "Generating local audio",
                currentChapter = chapter.title
            ))
            
            val audio = audioGenerator.generateAudio(chapter.content)
            // TODO: Save PCM samples to a file (WAV/MP3)
        }
        
        emit(GenerationProgress(
            status = "COMPLETED",
            progress = 1.0f,
            currentStep = "Finished local generation"
        ))
    }
}

data class GenerationProgress(
    val status: String,
    val progress: Float,
    val currentStep: String,
    val currentChapter: String? = null,
    val error: String? = null
)
