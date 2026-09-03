package com.shubhamthorat.echo.shared.ai

import android.content.Context
import okio.FileSystem

class AndroidModelManager(
    context: Context,
    downloader: KtorModelDownloader
) : BaseModelManager(
    downloader = downloader,
    fileSystem = FileSystem.SYSTEM,
    rootDir = context.filesDir.absolutePath
)
