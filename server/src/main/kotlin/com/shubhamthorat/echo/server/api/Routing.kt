package com.shubhamthorat.echo.server.api

import com.shubhamthorat.echo.server.Config
import com.shubhamthorat.echo.server.ai.*
import com.shubhamthorat.echo.server.api.dto.v1.*
import com.shubhamthorat.echo.server.document.DocumentService
import com.shubhamthorat.echo.server.generation.AudiobookGenerationService
import com.shubhamthorat.echo.server.generation.ChapterInput
import com.shubhamthorat.echo.server.generation.GenerationService
import com.shubhamthorat.echo.server.narration.NarrationService
import com.shubhamthorat.echo.server.voice.VoiceService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.request.*
import io.ktor.utils.io.jvm.javaio.*
import java.io.File
import java.time.Instant
import java.util.*

fun Application.configureRouting(
    aiProvider: AIProvider,
    dialogueService: DialogueService,
    voiceService: VoiceService,
    pronunciationService: PronunciationService,
    narrationService: NarrationService,
    generationService: GenerationService,
    audiobookGenerationService: AudiobookGenerationService
) {
    val documentService = DocumentService(aiProvider)
    
    routing {
        get("/") {
            call.respond(mapOf(
                "name" to "Echo Backend API",
                "description" to "AI Audiobook Generation Service",
                "version" to "1.0.0"
            ))
        }
        
        get("/health") {
            call.respond(HttpStatusCode.OK, mapOf(
                "status" to "ok",
                "service" to "echo-backend",
                "timestamp" to Instant.now().toString(),
                "metadata" to mapOf(
                    "version" to "1.0.0",
                    "environment" to Config.environment
                )
            ))
        }

        route("/documents") {
            post("/analyze") {
                val multipart = call.receiveMultipart()
                var file: File? = null
                var fileName: String?

                multipart.forEachPart { part ->
                    if (part is PartData.FileItem) {
                        fileName = part.originalFileName
                        if (fileName?.endsWith(".pdf", ignoreCase = true) != true) {
                            part.release()
                            throw IllegalArgumentException("Only PDF files are supported")
                        }
                        
                        val tempFile = File.createTempFile("echo-", ".pdf")
                        part.provider().toInputStream().use { input ->
                            tempFile.outputStream().use { output ->
                                input.copyTo(output)
                            }
                        }
                        if (tempFile.length() == 0L) {
                            tempFile.delete()
                            throw IllegalArgumentException("Uploaded file is empty")
                        }
                        file = tempFile
                    }
                    part.release()
                }

                val uploadedFile = file ?: throw IllegalArgumentException("No file uploaded")
                
                try {
                    val result = documentService.analyzePdf(uploadedFile)
                    call.respond(HttpStatusCode.Accepted, result)
                } finally {
                    // Cleanup handled by system or moved to permanent storage
                }
            }
        }

        route("/narration") {
            post("/prepare") {
                val request = call.receive<PrepareNarrationRequest>()
                val result = narrationService.prepareNarration(request.text, request.style)
                call.respond(result)
            }

            post("/pronunciation") {
                val request = call.receive<AssistPronunciationRequest>()
                val result = pronunciationService.assistPronunciation(request.text)
                call.respond(result)
            }
        }

        route("/dialogue") {
            post("/detect") {
                val request = call.receive<DetectDialogueRequest>()
                val result = dialogueService.detectDialogue(request.text)
                call.respond(result)
            }
        }

        get("/voices") {
            val voices = voiceService.getVoices()
            call.respond(GetVoicesResponse(
                voices = voices.map { voice ->
                    GetVoicesResponse.VoiceDto(
                        id = voice.id,
                        name = voice.name,
                        provider = voice.provider,
                        language = voice.language,
                        gender = voice.gender,
                        previewUrl = voice.previewUrl
                    )
                }
            ))
        }

        route("/generation") {
            post("/audiobook") {
                val request = call.receive<GenerateAudiobookRequest>()
                val jobId = audiobookGenerationService.startGenerationJob(
                    documentId = request.documentId,
                    chapters = request.chapters.map { 
                        ChapterInput(it.id, it.title, it.text)
                    },
                    voiceId = request.voiceId,
                    speed = request.speed
                )
                call.respond(HttpStatusCode.Accepted, GenerateAudiobookResponse(
                    generationId = jobId,
                    status = "PENDING",
                    queuePosition = 1
                ))
            }
            
            get("/{id}") {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing generation ID")
                val status = audiobookGenerationService.getJobStatus(id) ?: return@get call.respond(HttpStatusCode.NotFound)
                
                call.respond(GenerationStatusResponse(
                    generationId = status.jobId,
                    status = status.status,
                    progress = status.progress,
                    currentStep = status.currentStep,
                    currentChapter = status.currentChapterTitle,
                    completedChapters = status.completedChapters,
                    totalChapters = status.totalChapters,
                    error = status.errorMessage,
                    audiobookId = if (status.status == "COMPLETED") UUID.randomUUID().toString() else null
                ))
            }
        }
    }
}
