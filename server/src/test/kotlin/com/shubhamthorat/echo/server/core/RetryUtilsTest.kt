package com.shubhamthorat.echo.server.core

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RetryUtilsTest {

    @Test
    fun `retryWithBackoff returns result on success`() = runBlocking {
        val result = retryWithBackoff(maxRetries = 3) {
            "success"
        }
        assertEquals("success", result)
    }

    @Test
    fun `retryWithBackoff retries on failure and eventually succeeds`() = runBlocking {
        var attempts = 0
        val result = retryWithBackoff(maxRetries = 3, initialDelay = 10) {
            attempts++
            if (attempts < 3) throw Exception("Fail")
            "success"
        }
        assertEquals("success", result)
        assertEquals(3, attempts)
    }

    @Test
    fun `retryWithBackoff fails after max retries`() = runBlocking {
        var attempts = 0
        assertFailsWith<Exception> {
            retryWithBackoff(maxRetries = 2, initialDelay = 10) {
                attempts++
                throw Exception("Fail")
            }
        }
        assertEquals(3, attempts) // 1 initial + 2 retries
    }
}
