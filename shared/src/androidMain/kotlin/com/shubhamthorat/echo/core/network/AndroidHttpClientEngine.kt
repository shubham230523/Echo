package com.shubhamthorat.echo.core.network

import io.ktor.client.engine.cio.*

fun createAndroidHttpClientEngine() = CIO.create()
