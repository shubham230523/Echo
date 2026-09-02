package com.shubhamthorat.echo.core.common

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AndroidFileSystem(private val context: Context) : FileSystem {
    override suspend fun readBytes(path: String): ByteArray = withContext(Dispatchers.IO) {
        val uri = Uri.parse(path)
        context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: throw IllegalArgumentException("Could not open stream for $path")
    }
}

// In a real app, you'd inject this properly, but for simplicity:
actual fun getPlatformFileSystem(): FileSystem {
    // This is a bit hacky for KMP expect/actual, usually we'd use DI to provide the implementation
    // But since we need an actual fun, we'll need to pass the context somehow or use a singleton
    return object : FileSystem, KoinComponent {
        private val context: Context by inject()
        override suspend fun readBytes(path: String): ByteArray = withContext(Dispatchers.IO) {
            val uri = Uri.parse(path)
            context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                ?: throw IllegalArgumentException("Could not open stream for $path")
        }
    }
}
