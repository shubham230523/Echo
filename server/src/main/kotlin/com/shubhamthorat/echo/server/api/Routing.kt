package com.shubhamthorat.echo.server.api

import com.shubhamthorat.echo.server.Config
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import java.time.Instant

fun Application.configureRouting() {
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
    }
}
