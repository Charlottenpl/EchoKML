package com.echo.demo.net

import com.echo.demo.net.bean.InitBean
import com.echo.demo.net.bean.NetCallback
import com.echo.demo.net.bean.Net
import com.echo.demo.net.bean.NetResult

class ApiServer {
    companion object{

        fun test(){


            val callback = object : NetCallback<InitBean> {
                override fun onSuccess(data: NetResult<InitBean>) {
                    // TODO success
                }

                override fun onFail() {
                    // TODO fail
                }
            }

            Net.Post<Int>()
                .url("")
                .header("", "")
                .header("","")

                .send()

            NetUtil.post<Int, InitBean>(param, callback)
        }
    }
}