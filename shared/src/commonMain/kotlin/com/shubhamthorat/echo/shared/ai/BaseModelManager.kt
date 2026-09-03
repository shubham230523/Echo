package com.shubhamthorat.echo.shared.ai

import kotlinx.coroutines.flow.Flow
import okio.FileSystem
import okio.Path.Companion.toPath

abstract class BaseModelManager(
    private val downloader: KtorModelDownloader,
    protected val fileSystem: FileSystem,
    protected val rootDir: String
) : ModelManager {

    override fun isModelDownloaded(modelType: ModelType): Boolean {
        val path = rootDir.toPath().resolve(modelType.fileName)
        return fileSystem.exists(path)
    }

    override fun getModelPath(modelType: ModelType): String? {
        val path = rootDir.toPath().resolve(modelType.fileName)
        return if (fileSystem.exists(path)) path.toString() else null
    }

    override fun downloadModel(modelType: ModelType): Flow<DownloadProgress> {
        return downloader.download(modelType)
    }
}
