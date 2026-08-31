package com.shubhamthorat.echo.server.ai

object PromptTemplates {

    fun documentStructurePrompt(text: String): String {
        return """
            Analyze the following document text and extract its hierarchical structure.
            Respond ONLY with a valid JSON object matching the schema below.
            Do not include any preamble, markdown formatting (like ```json), or postamble.

            SCHEMA:
            {
              "title": "Main title of the document",
              "author": "Name of the author if found, otherwise null",
              "type": "The type of document (e.g., BOOK, ARTICLE, RESEARCH_PAPER)",
              "language": "ISO 639-1 language code (e.g., en, fr)",
              "hierarchy": [
                {
                  "type": "Level type (e.g., PART, CHAPTER, SECTION)",
                  "title": "Title of this section",
                  "startIndex": 0,
                  "endIndex": 1000,
                  "children": []
                }
              ]
            }

            DOCUMENT TEXT:
            ${text.take(20000)}
        """.trimIndent()
    }

    fun chapterDetectionPrompt(text: String, structure: DocumentStructureResponse?): String {
        val structureInfo = structure?.let { 
            "Existing structure found: ${it.title} (${it.type}) with ${it.hierarchy.size} top-level nodes."
        } ?: "No existing structure info."

        return """
            Detect all chapters in the following document text. 
            $structureInfo
            
            Respond ONLY with a valid JSON object matching the schema below.
            Ensure:
            1. Chapters are in correct chronological order.
            2. There are NO gaps between chapters (the endIndex of one should be the startIndex of next, or very close).
            3. The full content is covered from start to finish.
            4. Provide a confidence score (0.0 to 1.0) for each chapter.

            SCHEMA:
            {
              "chapters": [
                {
                  "title": "Chapter Title",
                  "index": 1,
                  "startIndex": 0,
                  "endIndex": 5000,
                  "confidence": 0.95
                }
              ]
            }

            DOCUMENT TEXT:
            ${text.take(30000)}
        """.trimIndent()
    }

    fun narrationPreparationPrompt(text: String, style: String): String {
        return """
            Transform the following written text into a version optimized for high-quality audio narration.
            The desired style is: $style.

            GOALS:
            1. Improve punctuation for natural speech patterns (add commas for breath, appropriate ellipses for pauses).
            2. Preserve the exact paragraph structure.
            3. Expand common abbreviations into their spoken forms (e.g., "Dr." to "Doctor", "10:30 AM" to "ten thirty A M").
            4. Improve flow and readability for a voice actor or TTS engine.

            CRITICAL CONSTRAINTS:
            - DO NOT SUMMARIZE.
            - DO NOT REMOVE ANY MEANINGFUL CONTENT.
            - DO NOT INVENT NEW INFORMATION.
            - DO NOT CHANGE THE FACTUAL MEANING OR THE AUTHOR'S VOICE.
            - THE OUTPUT LENGTH SHOULD BE ALMOST IDENTICAL TO THE INPUT.

            Respond ONLY with a valid JSON object matching the schema below.

            SCHEMA:
            {
              "preparedText": "The fully transformed text here",
              "estimatedDurationSeconds": 125.5,
              "notes": "Brief explanation of key changes (optional)"
            }

            INPUT TEXT:
            $text
        """.trimIndent()
    }

    fun dialogueDetectionPrompt(text: String): String {
        return """
            Analyze the following text and segment it into narration and dialogue blocks.
            Identify the potential speaker for each dialogue block.
            If a speaker is uncertain, use "Unknown" or your best guess based on context.
            For narration, set "speaker" to "Narrator" and "isDialogue" to false.

            Respond ONLY with a valid JSON object matching the schema below.
            Ensure every part of the input text is accounted for in the segments.

            SCHEMA:
            {
              "segments": [
                {
                  "text": "Segment of text here",
                  "speaker": "Speaker name or Narrator",
                  "isDialogue": true
                }
              ]
            }

            INPUT TEXT:
            ${text.take(15000)}
        """.trimIndent()
    }

    fun pronunciationPrompt(text: String): String {
        return """
            Detect potentially difficult words or phrases in the following text that might need special pronunciation guidance for a narrator.
            Focus on:
            1. Unfamiliar proper names (people, places).
            2. Complex technical or scientific terminology.
            3. Foreign words or loanwords.
            4. Acronyms that are not obvious.

            For each detected item, provide:
            - The original word/phrase.
            - IPA (International Phonetic Alphabet) notation.
            - A simple phonetic respelling (e.g., "Apple" -> "A-puhl").
            - A confidence score (0.0 to 1.0) for your guidance.

            Respond ONLY with a valid JSON object matching the schema below.

            SCHEMA:
            {
              "guides": [
                {
                  "word": "word",
                  "ipa": "/ˈwɜːrd/",
                  "phoneticRespelling": "WURD",
                  "confidence": 0.98
                }
              ]
            }

            INPUT TEXT:
            ${text.take(10000)}
        """.trimIndent()
    }
}
