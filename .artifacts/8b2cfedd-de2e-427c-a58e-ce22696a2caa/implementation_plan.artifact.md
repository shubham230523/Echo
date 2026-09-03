# LangGraph-inspired Document Processing & Audio Generation

This plan implements a high-performance, parallelized workflow for document analysis and audio generation, featuring agent-based partitioning, stateful orchestration, and robust error recovery.

## User Review Required

> [!IMPORTANT]
> - Parallelization will significantly increase API usage. Ensure your AI and TTS provider quotas are sufficient.
> - The solution uses Kotlin Coroutines to simulate the graph-based agent workflow.
> - Exponential backoff will be applied to all external service calls.

## Proposed Changes

### 1. Document Analysis Optimization (`DocumentService.kt`)
- **Agent Partitioning**: Divide the PDF into page ranges (e.g., 20 pages per range). Each range is processed by a dedicated "Worker Agent".
- **Parallel Extraction**: Use `async` coroutines to extract text from multiple page ranges concurrently.
- **Stateful Merging**: A "Synthesizer Agent" will merge the extracted text, ensuring chapter boundaries that cross page boundaries are correctly identified and re-aligned.
- **Structural Alignment**: After text extraction, a final pass will align the fragments into a coherent chapter hierarchy.

### 2. Audio Generation Optimization (`AudiobookGenerationService.kt`)
- **Parallel Generation**: Instead of processing chapters sequentially, generate multiple chapters in parallel.
- **Retry Mechanism**: Implement a robust `withRetry` utility with:
    - Max retry count.
    - Exponential backoff (e.g., 2s, 4s, 8s...).
    - Jitter to prevent thundering herd issues.
- **Audio Stitching**: (Optional but recommended) Logic to verify audio integrity before marking a chapter as complete.

### 3. Core Infrastructure
#### [NEW] [RetryUtils.kt](file:///C:/Users/shubham/AndroidStudioProjects/Echo/server/src/main/kotlin/com/shubhamthorat/echo/server/core/RetryUtils.kt)
- Generic utility for retrying suspend functions with exponential backoff.

#### [NEW] [ProcessingGraph.kt](file:///C:/Users/shubham/AndroidStudioProjects/Echo/server/src/main/kotlin/com/shubhamthorat/echo/server/core/ProcessingGraph.kt)
- Base classes for defining "Nodes" and "Edges" to simulate the LangGraph style workflow in Kotlin.

## Verification Plan

### Automated Tests
- Unit tests for `RetryUtils` to verify backoff timings.
- Mock PDF tests with `DocumentService` to verify page range partitioning and re-assembly.

### Manual Verification
- Monitor server logs for parallel execution threads and retry attempts.
- Verify that large PDFs (100+ pages) are processed faster than the sequential baseline.
