package com.shubhamthorat.echo.server.ai

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class OpenAIRequest(
    val model: String,
    val messages: List<OpenAiMessage>,
    val stream: Boolean = false,
    val reasoning: ReasoningConfig? = null
)

@Serializable
data class ReasoningConfig(
    val enabled: Boolean
)

@Serializable
data class OpenAiMessage(
    val role: String,
    val content: String? = null,
    val reasoning_details: JsonElement? = null
)

@Serializable
data class OpenAIResponse(
    val choices: List<OpenAiChoice>
)

@Serializable
data class OpenAiChoice(
    val message: OpenAiMessage? = null,
    val delta: OpenAiDelta? = null
)

@Serializable
data class OpenAiDelta(
    val content: String? = null,
    val reasoning_details: JsonElement? = null
)
