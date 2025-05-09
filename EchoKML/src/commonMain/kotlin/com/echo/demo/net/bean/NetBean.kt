package com.echo.demo.net.bean

import io.ktor.http.HttpMethod

abstract class BaseNetParam(var method: HttpMethod){
    lateinit var url: String
    val headers: Map<String, String> = emptyMap()
}

class GetParam(
    val queryParams: Map<String, Any> = emptyMap()
) : BaseNetParam(HttpMethod.Get)

class PostParam<T>(
    var data: T
) : BaseNetParam(HttpMethod.Post)
