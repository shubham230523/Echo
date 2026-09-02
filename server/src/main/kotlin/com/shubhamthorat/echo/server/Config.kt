package com.shubhamthorat.echo.server

import com.shubhamthorat.echo.server.ai.AIConfig
import com.shubhamthorat.echo.server.voice.TTSConfig
import java.io.File
import java.util.*

object Config {
    private val localProperties = Properties().apply {
        val file = File("local.properties")
        if (file.exists()) {
            file.inputStream().use { load(it) }
        } else {
            // Also try root if running from server module
            val rootFile = File("../local.properties")
            if (rootFile.exists()) {
                rootFile.inputStream().use { load(it) }
            }
        }
    }

    private fun getProperty(key: String): String? {
        return System.getenv(key) ?: localProperties.getProperty(key)
    }

    val port = getProperty("PORT")?.toInt() ?: 8080
    val host = getProperty("HOST") ?: "0.0.0.0"
    val environment = getProperty("KTOR_ENV") ?: "development"
    
    val ai = AIConfig.fromEnvironment(::getProperty)
    val tts = TTSConfig.fromEnvironment(::getProperty)
}
