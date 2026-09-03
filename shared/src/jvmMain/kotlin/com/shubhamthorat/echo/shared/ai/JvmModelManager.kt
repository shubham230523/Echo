package com.shubhamthorat.echo.shared.ai

import okio.FileSystem
import java.io.File

class JvmModelManager(
    downloader: KtorModelDownloader
) : BaseModelManager(
    downloader = downloader,
    fileSystem = FileSystem.SYSTEM,
    rootDir = File(System.getProperty("user.home"), ".echo/models").apply { 
        if (!exists()) mkdirs() 
    }.absolutePath
)
