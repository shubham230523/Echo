package com.shubhamthorat.echo.core.common

/**
 * Platform-independent abstraction for file system operations.
 */
interface FileSystem {
    /**
     * Reads all bytes from the given path (which could be a URI on Android).
     */
    suspend fun readBytes(path: String): ByteArray
}

expect fun getPlatformFileSystem(): FileSystem
