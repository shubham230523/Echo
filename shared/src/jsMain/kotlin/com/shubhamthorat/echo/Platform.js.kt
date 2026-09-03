package com.shubhamthorat.echo

import kotlinx.browser.window

class JsPlatform: Platform {
    override val name: String = "Web"
}

actual fun getPlatform(): Platform = JsPlatform()
