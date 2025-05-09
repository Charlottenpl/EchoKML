package com.echo.demo.net

import com.echo.demo.net.bean.GetParam
import com.echo.demo.net.bean.NetCallback
import com.echo.demo.net.bean.NetResult
import com.echo.demo.net.bean.PostParam
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
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.CancellationException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlin.math.max

class Net {
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

        suspend inline fun <reified T> get(
            param: GetParam,
            callback: NetCallback<T>
        ){
            return client.get(param.url){
                param.headers.forEach { (key, value) -> header(key, value) }
                url { param.queryParams.forEach { (key, value) -> parameters.append(key, value.toString()) } }
            }.body()
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
        suspend inline fun <reified T, reified R> post(
            param: PostParam<R>,
            callback: NetCallback<T>
        ){
            try {
                val result: NetResult<T> = client.post(param.url) {
                    param.headers.forEach { (key, value) -> header(key, value) }
                    contentType(ContentType.Application.Json)
                    setBody(param.data)
                }.body()

                if (result.errorNo != 0){
                    // TODO 后端返回错误
                    callback.onFail()
                    return
                }

                callback.onSuccess(result)
            }catch (serialization: SerializationException){
                // TODO 处理失败返回
                callback.onFail()
            }catch (network: IOException){
                callback.onFail()
            }catch (unknown: Exception){
                callback.onFail()
            }
        }
    }
}

expect fun createEngine(): HttpClientEngine // 平台引擎创建