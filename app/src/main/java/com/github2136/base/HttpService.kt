package com.github2136.base

import com.github2136.base.entity.Weather
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by yb on 2019/10/7
 */
interface HttpService {
    @GET("adat/sk/{cityId}.html")
    suspend fun getWeatherFlow(@Path("cityId") cityId: String): Weather
}