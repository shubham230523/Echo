package com.shubhamthorat.echo.server

import com.shubhamthorat.echo.server.api.configureRouting
import com.shubhamthorat.echo.server.api.configureSerialization
import com.shubhamthorat.echo.server.api.configureStatusPages
import com.shubhamthorat.echo.server.api.configureLogging
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import com.shubhamthorat.echo.server.ai.AIProviderFactory
import com.shubhamthorat.echo.server.ai.DialogueService
import com.shubhamthorat.echo.server.ai.PronunciationService
import com.shubhamthorat.echo.server.voice.MockVoiceProvider
import com.shubhamthorat.echo.server.voice.TTSProviderFactory
import com.shubhamthorat.echo.server.voice.VoiceService

fun main() {
    embeddedServer(Netty, port = Config.port, host = Config.host, module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }
    
    val aiProvider = AIProviderFactory(httpClient, Config.ai).create()
    val ttsProvider = TTSProviderFactory(httpClient, Config.tts).create()
    val dialogueService = DialogueService(aiProvider)
    val pronunciationService = PronunciationService(aiProvider)
    val voiceService = VoiceService(MockVoiceProvider())
    
    configureLogging()
    configureSerialization()
    configureStatusPages()
    configureRouting(aiProvider, dialogueService, voiceService, pronunciationService, ttsProvider)
}
