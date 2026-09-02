package com.shubhamthorat.echo.core.common

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class JvmFileSystem : FileSystem {
    override suspend fun readBytes(path: String): ByteArray = withContext(Dispatchers.IO) {
        File(path).readBytes()
    }
}

actual fun getPlatformFileSystem(): FileSystem = JvmFileSystem()
