package com.echo.demo.net.bean

interface NetCallback<T> {
    fun onSuccess(data: NetResult<T>)
    fun onFail()
}