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
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import com.shubhamthorat.echo.server.ai.AIProviderFactory
import com.shubhamthorat.echo.server.ai.DialogueService
import com.shubhamthorat.echo.server.ai.PronunciationService
import com.shubhamthorat.echo.server.narration.NarrationService
import com.shubhamthorat.echo.server.generation.GenerationService
import com.shubhamthorat.echo.server.generation.AudiobookGenerationService
import com.shubhamthorat.echo.server.voice.MockVoiceProvider
import com.shubhamthorat.echo.server.voice.TTSProviderFactory
import com.shubhamthorat.echo.server.voice.VoiceService
import com.shubhamthorat.echo.shared.ai.JvmModelManager
import com.shubhamthorat.echo.shared.ai.KtorModelDownloader
import okio.FileSystem
import java.io.File

fun main() {
    println("🚀 Starting Echo Backend Server on ${Config.host}:${Config.port}...")
    embeddedServer(Netty, port = Config.port, host = Config.host, module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(kotlinx.serialization.json.Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 1_800_000 // 30 minutes
            connectTimeoutMillis = 60_000 // 60 seconds
            socketTimeoutMillis = 1_800_000 // 30 minutes
        }
    }

    val modelManager = JvmModelManager(
        KtorModelDownloader(httpClient, FileSystem.SYSTEM, File(System.getProperty("user.home"), ".echo/models").absolutePath)
    )
    
    val aiProvider = AIProviderFactory(httpClient, Config.ai).create(modelManager)
    val ttsProvider = TTSProviderFactory(httpClient, Config.tts).create(modelManager)
    
    if (Config.ai.providerType == com.shubhamthorat.echo.server.ai.AIProviderType.MOCK && 
        Config.tts.providerType == com.shubhamthorat.echo.server.voice.TTSProviderType.MOCK) {
        println("🛠️  RUNNING IN TOTAL MOCK MODE - No API credits will be used.")
    }
    
    val dialogueService = DialogueService(aiProvider)
    val pronunciationService = PronunciationService(aiProvider)
    val narrationService = NarrationService(aiProvider)
    val generationService = GenerationService(ttsProvider)
    val audiobookGenerationService = AudiobookGenerationService(narrationService, generationService)
    val voiceService = VoiceService(MockVoiceProvider())
    
    configureLogging()
    configureSerialization()
    configureStatusPages()
    configureRouting(
        aiProvider = aiProvider,
        dialogueService = dialogueService,
        voiceService = voiceService,
        pronunciationService = pronunciationService,
        narrationService = narrationService,
        generationService = generationService,
        audiobookGenerationService = audiobookGenerationService
    )
}
