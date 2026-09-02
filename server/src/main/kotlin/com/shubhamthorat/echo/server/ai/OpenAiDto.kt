package com.shubhamthorat.echo.server.ai

import kotlinx.serialization.Serializable

@Serializable
data class OpenAIRequest(
    val model: String,
    val messages: List<OpenAiMessage>,
    val reasoning: ReasoningConfig? = null
)

@Serializable
data class ReasoningConfig(
    val enabled: Boolean
)

@Serializable
data class OpenAiMessage(
    val role: String,
    val content: String,
    val reasoning_details: String? = null
)

@Serializable
data class OpenAIResponse(
    val choices: List<OpenAiChoice>
)

@Serializable
data class OpenAiChoice(
    val message: OpenAiMessage
)
