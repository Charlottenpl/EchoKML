package com.echo.demo.net.bean


/**
 * 后端请求返回结构
 */
data class NetResult<T>(
    var errorNo: Int, // 状态码
    var message: String, // 状态描述
    var bean: T
)

/**
 * 初始化后端返回结构
 */
data class InitBean(
    var appId: String
)
