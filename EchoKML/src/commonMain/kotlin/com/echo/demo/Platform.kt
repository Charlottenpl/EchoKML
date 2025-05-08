package com.echo.demo

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform