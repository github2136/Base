package com.github2136.base

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by yb on 2019/10/7
 */
interface HttpService {
    @GET("adat/sk/{cityId}.html")
    fun getWeather(@Path("cityId") cityId: String): Call<ResponseBody>
}