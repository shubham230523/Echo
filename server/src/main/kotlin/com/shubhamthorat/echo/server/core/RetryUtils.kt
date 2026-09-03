package com.shubhamthorat.echo.server.core

import kotlinx.coroutines.delay
import kotlin.math.pow

/**
 * Executes a [block] with exponential backoff retry logic.
 *
 * @param maxRetries Maximum number of retry attempts.
 * @param initialDelay Initial delay in milliseconds before the first retry.
 * @param maxDelay Maximum delay in milliseconds between retries.
 * @param factor Exponential backoff factor.
 * @param shouldRetry Optional predicate to determine if a specific exception should trigger a retry.
 */
suspend fun <T> retryWithBackoff(
    maxRetries: Int = 3,
    initialDelay: Long = 1000,
    maxDelay: Long = 10000,
    factor: Double = 2.0,
    shouldRetry: (Throwable) -> Boolean = { true },
    block: suspend () -> T
): T {
    var currentDelay = initialDelay
    var lastException: Throwable? = null

    repeat(maxRetries + 1) { attempt ->
        try {
            return block()
        } catch (e: Throwable) {
            lastException = e
            if (attempt == maxRetries || !shouldRetry(e)) {
                throw e
            }
            
            println("⚠️ Retry attempt ${attempt + 1}/$maxRetries failed: ${e.message}. Retrying in ${currentDelay}ms...")
            delay(currentDelay)
            currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
        }
    }
    
    throw lastException ?: IllegalStateException("Unknown error in retryWithBackoff")
}
