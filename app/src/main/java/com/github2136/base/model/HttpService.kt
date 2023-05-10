package com.github2136.base.model

import com.github2136.base.model.entity.Weather
import com.github2136.basemvvm.DynamicTimeout
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by yb on 2019/10/7
 */
interface HttpService {
    @DynamicTimeout(3)
    @GET("adat/sk/{cityId}.html")
    suspend fun getWeatherFlow(@Path("cityId") cityId: String): Weather
}