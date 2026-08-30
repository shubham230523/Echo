package com.shubhamthorat.echo.presentation.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation route definitions for the Echo application.
 * These routes are designed to be used with Jetpack Navigation or any
 * custom navigation logic.
 */
@Serializable
sealed interface Route {

    /**
     * The main library screen displaying the user's audiobook collection.
     */
    @Serializable
    data object Library : Route

    /**
     * Screen for importing new documents (PDFs, text files, etc.).
     */
    @Serializable
    data object ImportDocument : Route

    /**
     * Screen for analyzing the imported document and extracting structure.
     */
    @Serializable
    data object DocumentAnalysis : Route

    /**
     * Screen for managing and selecting chapters from the analyzed document.
     */
    @Serializable
    data object Chapters : Route

    /**
     * Screen for configuring narration settings and previewing voices.
     */
    @Serializable
    data object Narration : Route

    /**
     * Screen for selecting and customizing the AI voice.
     */
    @Serializable
    data object VoiceSelection : Route

    /**
     * Screen displaying the progress of audiobook generation.
     */
    @Serializable
    data object Generation : Route

    /**
     * The core audiobook player screen.
     */
    @Serializable
    data object Player : Route

    /**
     * Application settings screen.
     */
    @Serializable
    data object Settings : Route
}
