package com.hm.viscosityauto.http

import com.hm.viscosityauto.http.ResultData
import com.hm.viscosityauto.model.ApkModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface HttpUrl {
    @POST("upload/data/uploadData")
    suspend fun uploadData(@Body map: Map<String, String>): Response<ResultData<String>>


    @GET("produceApkRela/getByApp")
    suspend fun getByApp(@QueryMap map: Map<String, String>): ResultData<ApkModel>
}