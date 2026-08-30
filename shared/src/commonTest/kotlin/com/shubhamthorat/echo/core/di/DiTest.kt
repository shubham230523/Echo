package com.shubhamthorat.echo.core.di

import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DiTest : KoinTest {

    private val testDependency: TestDependency by inject()

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun verifyInjection() {
        initKoin()
        
        val message = testDependency.getMessage()
        assertEquals("Koin DI is working!", message)
    }
}
