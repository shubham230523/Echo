package com.shubhamthorat.echo.core.common

class WasmJsFileSystem : FileSystem {
    override suspend fun readBytes(path: String): ByteArray {
        // Web file system access is different. 
        // This is a placeholder as direct path access isn't supported in browser.
        return ByteArray(0)
    }
}

actual fun getPlatformFileSystem(): FileSystem = WasmJsFileSystem()
