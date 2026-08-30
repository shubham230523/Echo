package com.shubhamthorat.echo.domain.usecase

/**
 * Use case for performing basic cleanup on raw text extracted from documents.
 * This prepares the text for further analysis or narration without altering the actual content.
 */
class CleanDocumentTextUseCase {

    /**
     * Cleans the provided raw text.
     *
     * @param pages The list of strings, each representing a page's text.
     * @return The cleaned and combined text.
     */
    operator fun invoke(pages: List<String>): String {
        if (pages.isEmpty()) return ""

        val cleanedPages = if (pages.size >= 3) {
            removeHeadersAndFooters(pages)
        } else {
            pages
        }

        val combinedText = cleanedPages.joinToString("\n\n")
        return cleanRawText(combinedText)
    }

    /**
     * Legacy support for single string input.
     */
    operator fun invoke(rawText: String): String {
        return invoke(listOf(rawText))
    }

    private fun removeHeadersAndFooters(pages: List<String>): List<String> {
        val pageLines = pages.map { it.split('\n').map { line -> line.trim() } }
        val totalPages = pages.size
        
        // Threshold: must appear in at least 3 pages AND more than 50% of pages
        val threshold = maxOf(3, (totalPages * 0.5).toInt())

        val headerCounts = mutableMapOf<Int, MutableMap<String, Int>>()
        val footerCounts = mutableMapOf<Int, MutableMap<String, Int>>()

        // Analyze each page
        pageLines.forEach { lines ->
            val nonEmptyLines = lines.filter { it.isNotEmpty() }
            
            // Check top 3 lines
            nonEmptyLines.take(3).forEachIndexed { index, line ->
                headerCounts.getOrPut(index) { mutableMapOf() }.let {
                    it[line] = (it[line] ?: 0) + 1
                }
            }
            
            // Check bottom 3 lines
            nonEmptyLines.takeLast(3).reversed().forEachIndexed { index, line ->
                footerCounts.getOrPut(index) { mutableMapOf() }.let {
                    it[line] = (it[line] ?: 0) + 1
                }
            }
        }

        // Identify lines to remove
        val headersToRemove = headerCounts.mapValues { (_, counts) ->
            counts.filter { it.value >= threshold }.keys
        }
        val footersToRemove = footerCounts.mapValues { (_, counts) ->
            counts.filter { it.value >= threshold }.keys
        }

        // Apply removal
        return pageLines.map { lines ->
            val nonEmptyLines = lines.filter { it.isNotEmpty() }
            if (nonEmptyLines.isEmpty()) return@map ""

            val toRemove = mutableSetOf<Int>()
            
            // Identify which non-empty lines to remove
            nonEmptyLines.take(3).forEachIndexed { index, line ->
                if (headersToRemove[index]?.contains(line) == true) {
                    toRemove.add(index)
                }
            }
            
            val lastIndexOffset = nonEmptyLines.size - 1
            nonEmptyLines.takeLast(3).reversed().forEachIndexed { index, line ->
                if (footersToRemove[index]?.contains(line) == true) {
                    toRemove.add(lastIndexOffset - index)
                }
            }

            // Reconstruct the page without removed lines
            nonEmptyLines.filterIndexed { index, _ -> index !in toRemove }
                .joinToString("\n")
        }
    }

    private fun cleanRawText(text: String): String {
        if (text.isBlank()) return ""

        return text
            .trim()
            // Replace 3 or more consecutive newlines with exactly 2 newlines
            .replace(Regex("\\n{3,}"), "\n\n")
            // Replace 2 or more consecutive spaces with a single space
            .replace(Regex(" {2,}"), " ")
    }
}
