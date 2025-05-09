package com.echo.demo.net

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

actual fun createEngine(): HttpClientEngine = OkHttp.create {
    // OkHttp 引擎基础配置
    config {
        followRedirects(true)
        followSslRedirects(true)
    }
}