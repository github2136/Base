package com.github2136.base.model

import com.github2136.base.model.entity.Weather
import com.github2136.basemvvm.DynamicTimeout
import com.github2136.basemvvm.HttpCache
import okhttp3.ResponseBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Created by yb on 2019/10/7
 */
interface HttpService {
    @HttpCache
    @DynamicTimeout(3)
    @Headers("Cache-Control: public, max-age=" + 24 * 3600)
    @GET("adat/sk/{cityId}.html")
    suspend fun getWeather(@Path("cityId") cityId: String): Weather

    @GET("https://www.baidu.com/sdfs")
    suspend fun getWeather2(): Weather

    @FormUrlEncoded
    @POST("http://www.iotniat.com.cn:8089/LOGIN2")
    suspend fun login(@Field("user") user: String, @Field("pass") pass: String): ResponseBody
}