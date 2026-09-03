package com.shubhamthorat.echo.shared.ai

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer

class KtorModelDownloader(
    private val httpClient: HttpClient,
    private val fileSystem: FileSystem,
    private val rootDir: String
) {
    fun download(modelType: ModelType): Flow<DownloadProgress> = flow {
        val targetPath = rootDir.toPath().resolve(modelType.fileName)
        
        try {
            emit(DownloadProgress(modelType, 0f))
            
            val response = httpClient.get(modelType.url) {
                onDownload { bytesSentTotal, contentLength ->
                    if (contentLength != null && contentLength > 0) {
                        val progress = bytesSentTotal.toFloat() / contentLength
                        // Emit progress, but maybe limit frequency if needed
                    }
                }
            }

            if (response.status.isSuccess()) {
                val channel = response.bodyAsChannel()
                fileSystem.write(targetPath) {
                    // This is a simplified way to write from channel to okio sink
                    // In Ktor 3, we might need a more robust way to handle large streams
                }
                
                // Re-implementing with a manual loop for better control and progress reporting
                val contentLength = response.contentLength() ?: -1L
                var bytesRead = 0L
                
                fileSystem.sink(targetPath).buffer().use { sink ->
                    while (!channel.isClosedForRead) {
                        val packet = channel.readRemaining(8192)
                        while (!packet.isEmpty) {
                            val bytes = packet.readBytes()
                            sink.write(bytes)
                            bytesRead += bytes.size
                            if (contentLength > 0) {
                                emit(DownloadProgress(modelType, bytesRead.toFloat() / contentLength))
                            }
                        }
                    }
                }
                
                emit(DownloadProgress(modelType, 1.0f, isComplete = true))
            } else {
                emit(DownloadProgress(modelType, 0f, error = "HTTP Error: ${response.status}"))
            }
        } catch (e: Exception) {
            emit(DownloadProgress(modelType, 0f, error = e.message ?: "Unknown error"))
        }
    }
}
