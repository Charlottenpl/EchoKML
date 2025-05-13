package com.echo.demo.net.bean

import com.echo.demo.net.NetUtil
import io.ktor.http.HttpMethod

class Net<T, R> private constructor (
    var method: HttpMethod,
    var url: String,
    var data: T? = null,
    val queryParams: Map<String, Any> = emptyMap(),
    val headers: Map<String, String> = emptyMap<String, String>(),
    val callback: NetCallback<R>
){

    class Post<T, R>() {
        private val method: HttpMethod = HttpMethod.Post
        private lateinit var  url: String
        private val headers: MutableMap<String, String> = mutableMapOf()
        private var data: T? = null
        private val queryParams: MutableMap<String, String> = mutableMapOf()
        private lateinit var callback: NetCallback<R>


        /** 设置请求的 URL（必填） */
        fun url(url: String) = apply { this.url = url }

        /** 添加单个 Header */
        fun header(key: String, value: String) = apply { headers[key] = value }

        /** 批量添加 Headers */
        fun headers(headers: Map<String, String>) = apply {
            this.headers.putAll(headers)
        }

        /** 设置请求体 **/
        fun data(data: T) = apply { this.data = data }

        /** 设置url参数 **/
        fun queryParam(key: String, value: String) = apply { queryParams[key] = value }

        /** 设置请求体 **/
        fun callback(callback: NetCallback<R>) = apply { this.callback = callback }





        /**
         * 构建一个 BaseNetParam 的匿名子类实例
         * @throws IllegalStateException 如果 URL 未设置
         */
        fun send() {
            require(url.isNotBlank()) { "URL must be provided" }
            // 这里直接创建一个匿名子类；如果你有具体子类，也可以返回其实例
            val param =  Net(method, url, data, queryParams, headers, callback)

            NetUtil.post(param)
        }
    }
}