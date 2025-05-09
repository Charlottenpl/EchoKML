package com.echo.demo.net

import com.echo.demo.net.bean.InitBean
import com.echo.demo.net.bean.NetCallback
import com.echo.demo.net.bean.NetResult

class ApiServer {
    companion object{
        public fun test(){
            val callback = object : NetCallback<InitBean> {
                override fun onSuccess(data: NetResult<InitBean>) {
                    TODO("Not yet implemented")
                }

                override fun onFail() {
                    TODO("Not yet implemented")
                }
            }
            Net.get(null, callback)
        }
    }
}