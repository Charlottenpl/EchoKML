package com.echo.demo.net

import com.echo.demo.net.bean.NetCallback
import com.echo.demo.net.bean.NetParam
import com.echo.demo.net.bean.NetResult
import com.echo.demo.net.config.C_NET_BASE_URL
import com.echo.demo.net.config.C_NET_CONNECT_TIMEOUT
import com.echo.demo.net.config.C_NET_REQUIRE_TIMEOUT
import com.echo.demo.net.config.C_NET_SOCKET_TIMEOUT
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class NetUtil {
    companion object{
        val client = HttpClient(createEngine()){
            expectSuccess = true

            defaultRequest {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                url {
                    protocol = URLProtocol.HTTPS
                    host = C_NET_BASE_URL
                }
            }

            // 网络请求超时设置
            install(HttpTimeout){
                requestTimeoutMillis = C_NET_REQUIRE_TIMEOUT
                connectTimeoutMillis = C_NET_CONNECT_TIMEOUT
                socketTimeoutMillis = C_NET_SOCKET_TIMEOUT
            }

            // JSON 支持
            install(ContentNegotiation){
                json(Json {
                    encodeDefaults = true
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }

            // 日志打印插件
            install(Logging){
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }

            install(HttpRequestRetry){
                retryOnExceptionOrServerErrors(maxRetries = 3)
                exponentialDelay()
            }
        }

        /**
         * 处理逻辑：
         * - 如果post抛出异常
         *  - fail，网络异常
         * - 如果post正常返回
         *  - 如果error_no == 0
         *      - success
         *  - fail，后端错误
         */
        inline fun <reified R> post(
            param: NetParam,
            callback: NetCallback<R>
        ){
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val result: NetResult<R> = client.post(param.url) {
                        param.headers.forEach { (key, value) -> header(key, value) }
                        contentType(ContentType.Application.Json)
                        setBody(param.data)
                    }.body()

                    CoroutineScope(Dispatchers.Default).launch {
                        callback.onSuccess(result)
                    }
                }catch (serialization: SerializationException){
                    // TODO 处理失败返回
                    CoroutineScope(Dispatchers.Default).launch { callback.onFail() }
                }catch (network: IOException){
                    CoroutineScope(Dispatchers.Default).launch { callback.onFail() }
                }catch (unknown: Exception){
                    CoroutineScope(Dispatchers.Default).launch { callback.onFail() }
                }
            }

        }
    }
}

expect fun createEngine(): HttpClientEngine // 平台引擎创建