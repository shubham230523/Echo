package com.shubhamthorat.echo.server.api

import com.shubhamthorat.echo.server.Config
import com.shubhamthorat.echo.server.document.DocumentService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.request.*
import io.ktor.utils.io.jvm.javaio.*
import java.io.File
import java.time.Instant

fun Application.configureRouting() {
    val documentService = DocumentService()
    
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
                var fileName: String? = null

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
                    // In a real app, we might move this to a more permanent storage or keep for later stages
                    // For now, we'll keep it in temp or delete after metadata extraction as per "Store temporarily"
                    // Requirement says "Store temporarily", so we won't delete immediately here if it's needed for next steps.
                }
            }
        }
    }
}
