package com.github2136.base.model

import com.github2136.base.model.entity.Weather
import com.github2136.basemvvm.DynamicTimeout
import com.github2136.basemvvm.HttpCache
import retrofit2.http.GET
import retrofit2.http.Headers
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
}