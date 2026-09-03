package com.shubhamthorat.echo.shared.ai

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.buffer
import okio.use
import io.ktor.utils.io.core.*

class KtorModelDownloader(
    private val httpClient: HttpClient,
    private val fileSystem: FileSystem,
    private val rootDir: String
) {
    fun download(modelType: ModelType): Flow<DownloadProgress> = flow {
        val targetPath = rootDir.toPath().resolve(modelType.fileName)
        
        try {
            emit(DownloadProgress(modelType, 0f))
            
            val response = httpClient.get(modelType.url)
            
            if (response.status.isSuccess()) {
                val contentLength = response.contentLength() ?: -1L
                var bytesReadTotal = 0L
                val channel = response.bodyAsChannel()
                
                fileSystem.sink(targetPath).buffer().use { sink ->
                    while (!channel.isClosedForRead) {
                        val buffer = ByteArray(8192)
                        val bytesRead = channel.readAvailable(buffer)
                        if (bytesRead == -1) break
                        
                        sink.write(buffer, 0, bytesRead)
                        bytesReadTotal += bytesRead
                        
                        if (contentLength > 0) {
                            emit(DownloadProgress(modelType, bytesReadTotal.toFloat() / contentLength))
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
