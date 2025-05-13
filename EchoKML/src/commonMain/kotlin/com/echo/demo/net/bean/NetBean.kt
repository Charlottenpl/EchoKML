package com.echo.demo.net.bean

import com.echo.demo.net.NetUtil
import io.ktor.http.HttpMethod

class NetParam(
    var method: HttpMethod,
    var url: String,
    var data: Any? = null,
    val queryParams: Map<String, Any> = emptyMap(),
    val headers: Map<String, String> = emptyMap<String, String>()
)

class Post() {
     val method: HttpMethod = HttpMethod.Post
     lateinit var  url: String
     val headers: MutableMap<String, String> = mutableMapOf()
     var data: Any? = null
     val queryParams: MutableMap<String, String> = mutableMapOf()


    /** 设置请求的 URL（必填） */
    fun url(url: String) = apply { this.url = url }

    /** 添加单个 Header */
    fun header(key: String, value: String) = apply { headers[key] = value }

    /** 批量添加 Headers */
    fun headers(headers: Map<String, String>) = apply {
        this.headers.putAll(headers)
    }

    /** 设置请求体 **/
    fun data(data: Any) = apply { this.data = data }

    /** 设置url参数 **/
    fun queryParam(key: String, value: String) = apply { queryParams[key] = value }





    /**
     * 构建一个 BaseNetParam 的匿名子类实例
     * @throws IllegalStateException 如果 URL 未设置
     */
    inline fun <reified R> send(callback: NetCallback<R>){
        require(url.isNotBlank()) { "URL must be provided" }
        // 这里直接创建一个匿名子类；如果你有具体子类，也可以返回其实例
        val param =  NetParam(method, url, data, queryParams, headers)

        NetUtil.post<R>(param, callback)
    }
}