package com.shubhamthorat.echo.core.network

import io.ktor.client.engine.darwin.*

fun createIosHttpClientEngine() = Darwin.create()
