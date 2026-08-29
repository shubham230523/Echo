package com.shubhamthorat.echo

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform