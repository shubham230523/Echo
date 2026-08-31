package com.shubhamthorat.echo.core.network

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class HttpClientFactory(
    private val engine: HttpClientEngine,
    private val config: NetworkConfig
) {
    fun create(): HttpClient {
        return HttpClient(engine) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }

            install(HttpTimeout) {
                connectTimeoutMillis = config.connectTimeoutMillis
                requestTimeoutMillis = config.requestTimeoutMillis
            }

            if (config.isDebug) {
                install(Logging) {
                    level = LogLevel.ALL
                    logger = Logger.DEFAULT
                }
            }

            defaultRequest {
                url(config.baseUrl)
            }
        }
    }
}
