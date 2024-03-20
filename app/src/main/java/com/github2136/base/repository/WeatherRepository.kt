package com.github2136.base.repository

import android.content.Context
import com.github2136.base.model.HttpModel
import com.github2136.basemvvm.BaseRepository
import com.github2136.basemvvm.ResultRepo

/**
 * Created by YB on 2020/7/8
 */
class WeatherRepository(context: Context) : BaseRepository(context) {
    private val httpModel by lazy { HttpModel.getInstance(context) }

    suspend fun getWeather() = launch {
        val weather = httpModel.api.getWeather("101010100")
        ResultRepo.Success(weather)
    }
    suspend fun getWeather2() = launch {
        val weather = httpModel.api.getWeather2()
        ResultRepo.Success(weather)
    }
    fun resetBaseUrl() {
        httpModel.resetBaseUrl("http://www.baidu.com/")
    }
}