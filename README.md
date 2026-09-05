# Echo 🎧

### Local-First AI-Powered Document Analyzer & Narrator

**Echo** transforms static documents like PDFs into immersive, chapterized audiobooks and interactive knowledge bases using a sophisticated, fully local AI pipeline.

Unlike traditional apps that rely on expensive cloud APIs, Echo runs **Large Language Models (LLMs)** and **Neural Text-to-Speech (TTS)** directly on your device, ensuring maximum privacy and offline availability.

> **Turn any document into a private, professional audiobook experience.**

## 🌐 Live Demo
You can try the web version of Echo here: [**shubham230523.github.io/Echo**](https://shubham230523.github.io/Echo/)

## ✨ Key Capabilities

*   **Local-First AI Processing**: Privacy-centric analysis using on-device models. Your data never leaves your device.
*   **Intelligent Local RAG**: Uses Retrieval-Augmented Generation to analyze large PDFs locally. It indexes your documents into a local vector store for instant, accurate Q&A.
*   **On-Device Neural TTS**: Generates high-quality, natural-sounding audio using neural voices (Piper/VITS) running locally via Sherpa-ONNX.
*   **Cross-Platform Seamlessness**: A unified experience across Android, iOS, Desktop, and Web built with a single Kotlin codebase.
*   **On-Demand Model Management**: Smart model downloader that keeps the app binary small by retrieving optimized AI weights (Llama, Gemma, etc.) only when needed.
*   **Adaptive Audio Player**: A modern, responsive playback interface built with Compose Multiplatform that handles everything from mobile screens to desktop window resizing.

## 🛠️ Technology Stack

### **AI & Machine Learning**
*   **Sherpa-ONNX**: The high-performance engine powering local LLM inference and TTS.
*   **ONNX Runtime**: Cross-platform acceleration for AI models.
*   **Local RAG Pipeline**: Custom Kotlin implementation for document chunking, embedding generation (BGE-small), and semantic search.

### **Cross-Platform Core (KMP)**
*   **Kotlin Multiplatform (KMP)**: 95% shared business logic across all platforms.
*   **Compose Multiplatform (CMP)**: Declarative UI for Android, iOS, Desktop, and Web.
*   **Room Multiplatform**: Unified database for document metadata and vector storage.
*   **Ktor Client**: Asynchronous networking for model downloads and API communication.
*   **Okio**: High-performance, cross-platform file system access.
*   **Koin**: Modern dependency injection for multiplatform projects.

### **Platform Specifics**
*   **Android**: Hardware acceleration via NNAPI.
*   **iOS**: Native PDF extraction via PDFKit and hardware acceleration via CoreML.
*   **Desktop (JVM)**: Industrial-grade PDF processing via Apache PDFBox.
*   **Web (Wasm/JS)**: High-performance Kotlin/Wasm target for browser execution.

## 🏗️ Architecture

```text
                 Echo (KMP/CMP)
                   │
      ┌────────────┼────────────┐
      │            │            │
  Local RAG     Local TTS    Model Mgr
  (Analysis)   (Narration)  (Downloads)
      │            │            │
      └────────────┼────────────┘
                   │
           ONNX Runtime Engine
                   │
      Local Models (Llama / BGE / VITS)
```

## 🌐 Target Platforms
*   **Android** (Mobile & Tablet)
*   **iOS** (iPhone & iPad)
*   **Desktop** (Windows, macOS, Linux)
*   **Web** (Browser via Wasm/JS)

---

**Echo is a cutting-edge exploration of how on-device AI can redefine how we consume written content, making every book accessible as a private, high-quality audio experience.**
