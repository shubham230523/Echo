package com.shubhamthorat.echo.core.common

class IosFileSystem : FileSystem {
    override suspend fun readBytes(path: String): ByteArray {
        // TODO: Implement using NSData
        return ByteArray(0)
    }
}

actual fun getPlatformFileSystem(): FileSystem = IosFileSystem()
