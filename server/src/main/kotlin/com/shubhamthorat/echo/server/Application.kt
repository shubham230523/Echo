package com.shubhamthorat.echo.server

import com.shubhamthorat.echo.server.api.configureRouting
import com.shubhamthorat.echo.server.api.configureSerialization
import com.shubhamthorat.echo.server.api.configureStatusPages
import com.shubhamthorat.echo.server.api.configureLogging
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = Config.port, host = Config.host, module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureLogging()
    configureSerialization()
    configureStatusPages()
    configureRouting()
}
