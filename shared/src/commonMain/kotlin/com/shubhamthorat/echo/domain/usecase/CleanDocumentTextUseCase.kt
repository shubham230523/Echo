package com.shubhamthorat.echo.domain.usecase

/**
 * Use case for performing basic cleanup on raw text extracted from documents.
 * This prepares the text for further analysis or narration without altering the actual content.
 */
class CleanDocumentTextUseCase {

    /**
     * Cleans the provided raw text.
     * 
     * - Trims leading and trailing whitespaces.
     * - Normalizes repeated internal whitespaces (multiple spaces -> single space).
     * - Reduces excessive blank lines (more than two consecutive newlines -> two newlines).
     *
     * @param rawText The raw string extracted from a document.
     * @return The cleaned text.
     */
    operator fun invoke(rawText: String): String {
        if (rawText.isBlank()) return ""

        return rawText
            .trim()
            // Replace 3 or more consecutive newlines with exactly 2 newlines (one empty line between text)
            .replace(Regex("\\n{3,}"), "\n\n")
            // Replace 2 or more consecutive spaces with a single space
            .replace(Regex(" {2,}"), " ")
            // Ensure single spaces around single newlines don't accumulate if desired, 
            // but the prompt specifically says "normalize repeated whitespace".
            // Let's also handle tabs and other whitespaces if needed, 
            // but sticking to standard spaces and newlines for now.
    }
}
