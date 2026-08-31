package com.shubhamthorat.echo.feature.library

import com.shubhamthorat.echo.domain.model.Audiobook

/**
 * UI State for the Library screen.
 *
 * @property audiobooks List of audiobooks in the library.
 * @property isLoading Whether the library is currently loading.
 * @property error Optional error message.
 */
data class LibraryUiState(
    val audiobooks: List<Audiobook> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val isEmpty: Boolean = !isLoading && audiobooks.isEmpty() && error == null
}
