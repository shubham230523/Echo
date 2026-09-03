package com.shubhamthorat.echo.core.network

import io.ktor.client.engine.*
import io.ktor.client.engine.js.*

fun createWasmJsHttpClientEngine(): HttpClientEngine = Js.create()
