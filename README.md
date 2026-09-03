# Echo 🎧

### AI-Powered Audiobook Creator

**Echo** transforms static documents like PDFs into immersive, chapterized audiobooks using a sophisticated multi-agent AI pipeline.

Instead of basic Text-to-Speech (TTS), Echo uses "LangGraph-style" intelligent workflows to understand document structure, prepare natural narrative text, detect dialogue, and validate audio quality.

> **Turn any document into a professional, structured audiobook experience.**

## ✨ Key Capabilities

*   **Intelligent Document Analysis**: Uses a parallelized multi-agent workflow to scan large PDFs, detect narrative chapters, and extract metadata like title and author.
*   **Narrative Preparation**: AI agents transform raw PDF text (often filled with page numbers and headers) into clean, narration-ready prose.
*   **Parallel Audio Generation**: Optimized "fan-out" generation that produces multiple chapter audio files simultaneously with automatic retry logic and exponential backoff.
*   **High-Fidelity Simulation**: Includes a "Total Mock" mode for development that simulates the entire AI pipeline locally, allowing for end-to-end testing without API costs.
*   **Persistent Caching**: Intelligent "Record & Replay" caching for both AI responses and generated audio, ensuring you never pay for the same request twice.
*   **Adaptive Audio Player**: A modern, responsive playback interface built with Compose Multiplatform that handles everything from mobile screens to desktop window resizing.

## 🤖 AI Multi-Agent Pipeline

Echo employs a node-based state machine architecture to process books at scale:

1.  **Extract Node**: Rapid PDF text extraction.
2.  **Batch Analysis Agents**: Parallel workers that analyze 50-page chunks of the book simultaneously.
3.  **Reconciliation Node**: Merges overlapping batch results into a seamless, ordered chapter list.
4.  **Narration Agent**: Refines text for high-quality storytelling.
5.  **Generation Engine**: Resilient TTS orchestration with independent chapter retries.

## 🛠️ Technology Stack

### **Client (Cross-Platform)**
*   **Kotlin Multiplatform (KMP)**: Shared business logic across all platforms.
*   **Compose Multiplatform (CMP)**: Declarative, responsive UI for Android, iOS, and Desktop.
*   **Voyager**: Multiplatform navigation.
*   **Koin**: Dependency injection.
*   **Ktor Client**: Resilient networking.
*   **Kotlin Coroutines & Flow**: High-concurrency reactive programming.

### **Server (Backend)**
*   **Ktor Server**: High-performance asynchronous Kotlin framework.
*   **Apache PDFBox**: Industrial-grade PDF processing.
*   **jaudiotagger**: Audio metadata and validation.

### **AI & Cloud**
*   **OpenRouter & OpenAI**: Integration with state-of-the-art LLMs (like Nemotron-3.5) for structural analysis.
*   **Advanced TTS Engines**: Support for flux-tts and other high-quality neural voice models.
*   **LangGraph-Inspired Workflows**: Custom state-machine orchestration for complex multi-step AI tasks.

## 🏗️ Architecture

```text
                 Echo (KMP/CMP)
                   │
      ┌────────────┼────────────┐
      │            │            │
  Document       AI Agent      Audio
  Workflow       Workflow     Workflow
  (Parallel)    (Caching)    (Resilient)
      │            │            │
      └────────────┼────────────┘
                   │
             Echo Backend
                   │
      LLMs (OpenRouter) / TTS Engines
```

## 🌐 Target Platforms
*   **Android** (Mobile & Tablet)
*   **iOS** (iPhone & iPad)
*   **Desktop** (Windows, macOS, Linux)

---

**Echo is a cutting-edge exploration of how multi-agent AI orchestration can redefine how we consume written content, making every book accessible as a high-quality audio experience.**
