package com.hm.viscosityauto.http

class ResultData<T> {
    var code: Int = 0
    var message: String = ""
    var data: T? = null

    override fun toString(): String {
        return "ResultData{code:$code,message:$message,result:${data.toString()}}"
    }
}