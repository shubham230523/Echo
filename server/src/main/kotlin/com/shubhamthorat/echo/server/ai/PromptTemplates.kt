package com.shubhamthorat.echo.server.ai

object PromptTemplates {

    fun documentStructurePrompt(text: String): String {
        return """
            Analyze the following document text and extract its hierarchical structure.
            Respond ONLY with a valid JSON object matching the schema below.
            Do not include any preamble, markdown formatting (like ```json), or postamble.

            IMPORTANT: 
            1. Identify the actual START and END of the narrative book content, ignoring legal boilerplate, license terms, and metadata at the very beginning and very end.
            2. Extract the Table of Contents if present.

            SCHEMA:
            {
              "title": "Main title of the document",
              "author": "Name of the author if found, otherwise null",
              "type": "BOOK, ARTICLE, RESEARCH_PAPER, etc.",
              "language": "en, fr, etc.",
              "contentStartOffset": 5000,
              "contentEndOffset": 150000,
              "tableOfContents": [
                {
                  "title": "Chapter/Section Title",
                  "level": 1
                }
              ],
              "hierarchy": [
                {
                  "type": "PART, CHAPTER, SECTION",
                  "title": "Title of this section",
                  "startIndex": 0,
                  "endIndex": 1000,
                  "children": []
                }
              ]
            }

            DOCUMENT TEXT (First 30k chars):
            ${text.take(30000)}

            DOCUMENT TEXT (Last 10k chars):
            ${text.takeLast(10000)}
        """.trimIndent()
    }

    fun chapterSplittingPrompt(text: String, titles: List<String>): String {
        return """
            I have a list of chapter titles:
            ${titles.joinToString(", ")}

            Your task is to find the character offset where each chapter begins in the provided book text.
            
            Respond ONLY with a valid JSON object matching this schema:
            {
              "anchors": [
                {
                  "title": "Chapter Title",
                  "startIndex": 1234
                }
              ]
            }

            DOCUMENT TEXT (Sample):
            ${text.take(50000)}
        """.trimIndent()
    }

    fun chapterDetectionPrompt(text: String, structure: DocumentStructureResponse?): String {
        val structureInfo = structure?.let { 
            "Document: ${it.title} by ${it.author}. Content range identified: ${it.contentStartOffset} to ${it.contentEndOffset}."
        } ?: "No existing structure info."

        return """
            Detect all CHAPTERS in the provided book text. 
            $structureInfo
            
            CRITICAL RULES:
            1. ONLY extract narrative chapters. IGNORE the Table of Contents, Foreword, Introduction, and legal boilerplate at the end (e.g. Project Gutenberg license).
            2. If you see "Chapter X", "I.", "II.", or similar headers, these are usually chapter markers.
            3. Ensure the chapters are in correct chronological order.
            4. There are NO gaps between chapters. The endIndex of one MUST be the startIndex of the next.
            5. Provide exact character offsets (startIndex and endIndex) for each chapter.
            
            Respond ONLY with a valid JSON object matching the schema below.

            SCHEMA:
            {
              "chapters": [
                {
                  "title": "Chapter Title",
                  "index": 1,
                  "startIndex": 1234,
                  "endIndex": 5678,
                  "confidence": 0.95
                }
              ]
            }

            DOCUMENT TEXT (Focus on transitions):
            ${text}
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

    fun contentComparisonPrompt(sourceText: String, transcription: String): String {
        return """
            Compare the following source text with its transcription.
            Identify missing sections, significant content differences, and potential truncations.
            Ignore minor punctuation or formatting differences.
            The matching does NOT need to be word-for-word, but the meaning and key information must be preserved.

            Respond ONLY with a valid JSON object matching the schema below.

            SCHEMA:
            {
              "matchScore": 0.95,
              "issues": ["List of identified issues"],
              "differences": [
                {
                  "type": "MISSING",
                  "description": "Description of the difference",
                  "severity": "HIGH"
                }
              ]
            }

            SOURCE TEXT:
            $sourceText

            TRANSCRIPTION:
            $transcription
        """.trimIndent()
    }
}
