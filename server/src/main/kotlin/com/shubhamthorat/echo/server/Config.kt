package com.shubhamthorat.echo.server

object Config {
    val port = java.lang.System.getenv("PORT")?.toInt() ?: 8080
    val host = java.lang.System.getenv("HOST") ?: "0.0.0.0"
    val environment = java.lang.System.getenv("KTOR_ENV") ?: "development"
}
