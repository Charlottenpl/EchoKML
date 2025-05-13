package com.echo.demo.net

import com.echo.demo.net.bean.InitBean
import com.echo.demo.net.bean.NetCallback
import com.echo.demo.net.bean.NetResult
import com.echo.demo.net.bean.Post

class ApiServer {
    companion object{

        fun test(){
            Post()
                .url("")
                .header("", "")
                .header("","")
                .data(23)
                .send<InitBean>(
                    object : NetCallback<InitBean> {
                    override fun onSuccess(data: NetResult<InitBean>) {
                        // TODO success
                    }

                    override fun onFail() {
                        // TODO fail
                    }
                })
        }
    }
}