# Echo 🎧

### Local-First AI-Powered Document Analyzer & Narrator

**Echo** transforms static documents like PDFs into immersive, chapterized audiobooks and interactive knowledge bases using a sophisticated, fully local AI pipeline with high-performance cloud enhancements.

Unlike traditional apps that rely solely on expensive cloud APIs, Echo prioritizes **Large Language Models (LLMs)** and **Neural Text-to-Speech (TTS)** directly on your device, ensuring maximum privacy and offline availability, while leveraging **OpenRouter's Deepgram Flux TTS** for premium audio generation.

> **Turn any document into a private, professional audiobook experience.**

## 🌐 Live Demo
You can try the web version of Echo here: [**shubham230523.github.io/Echo**](https://shubham230523.github.io/Echo/)

## ✨ Key Capabilities

*   **Local-First AI Processing**: Privacy-centric analysis using on-device models. Your data never leaves your device unless you opt for cloud-enhanced narration.
*   **Intelligent Local RAG**: Uses Retrieval-Augmented Generation to analyze large PDFs locally. It indexes your documents into a local vector store for instant, accurate Q&A.
*   **Advanced Neural TTS**:
    *   **Local**: Natural-sounding audio using neural voices (Piper/VITS) running via Sherpa-ONNX.
    *   **Cloud**: Premium high-fidelity narration powered by **OpenRouter's Deepgram Flux TTS** model.
*   **Smart Chapter Detection**: Robust multi-agent analysis with Regex-based fallback and **Table of Contents (TOC) awareness** to accurately extract narrative content.
*   **Large Chapter Support**: Automatic text chunking and sequential synthesis ensures even the longest chapters are processed smoothly.
*   **Cross-Platform Adaptive UI**: A unified experience across Android, iOS, Desktop, and Web built with a single Kotlin codebase and **Compose Multiplatform**.
*   **On-Demand Model Management**: Smart model downloader that keeps the app binary small by retrieving optimized AI weights (Llama, Gemma, etc.) only when needed.

## 🛠️ Technology Stack

### **AI & Machine Learning**
*   **Sherpa-ONNX**: High-performance engine for local LLM inference and TTS.
*   **OpenRouter**: Gateway for **Deepgram Flux TTS**, providing human-like cloud voices.
*   **ONNX Runtime**: Cross-platform acceleration for local AI models.
*   **Local RAG Pipeline**: Custom Kotlin implementation for document chunking, embedding generation (BGE-small), and semantic search.

### **Server & Infrastructure (Ktor)**
*   **Static Audio Hosting**: Built-in streaming server for instant Desktop playback.
*   **Parallel Analysis**: Multi-agent chunked document processing for rapid analysis.
*   **Unified Storage**: Reliable storage structure at `~/.echo/output/audiobooks/`.

### **Cross-Platform Core (KMP)**
*   **Kotlin Multiplatform (KMP)**: 95% shared business logic across all targets.
*   **Compose Multiplatform (CMP)**: Declarative UI for Android, iOS, Desktop, and Web.
*   **Room Multiplatform**: Unified database for document metadata and vector storage.

## 🏗️ Architecture

```text
                 Echo (KMP/CMP)
                   │
      ┌────────────┼────────────┐
      │            │            │
  Local RAG     TTS Engine   Model Mgr
  (Analysis)   (Local/Cloud) (Downloads)
      │            │            │
      └────────────┼────────────┘
                   │
    ┌──────────────┴──────────────┐
    │                             │
Local Models (ONNX)         Cloud APIs (OpenRouter)
```

## 🌐 Target Platforms
*   **Android** (Mobile & Tablet)
*   **iOS** (iPhone & iPad)
*   **Desktop** (Windows, macOS, Linux)
*   **Web** (Browser via Wasm/JS)

---

**Echo is a cutting-edge exploration of how hybrid AI can redefine how we consume written content, making every book accessible as a private, high-quality audio experience.**
