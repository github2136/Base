package com.github2136.base

import com.github2136.base.entity.Weather
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by yb on 2019/10/7
 */
interface HttpService {
    @GET("adat/sk/{cityId}.html")
    fun getWeather(@Path("cityId") cityId: String): Call<Weather>

    @GET("http://192.168.1.106:8080/queryUserList")
    fun getUrl(): Call<ResponseBody>

    @GET("http://192.168.1.106:8080/queryUserLists")
    fun getUrl404(): Call<ResponseBody>

    @GET("http://192.168.1.106:8080/queryUserList500")
    fun getUrl500(): Call<ResponseBody>

    @GET("http://192.168.1.106:8080/queryUserListTimeOut")
    fun getUrlTimeOut(): Call<ResponseBody>
}