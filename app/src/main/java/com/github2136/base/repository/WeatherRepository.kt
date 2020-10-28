package com.github2136.base.repository

import android.app.Application
import com.github2136.base.HttpModel
import com.github2136.base.entity.ResultFlow
import com.github2136.base.entity.Weather
import com.github2136.basemvvm.BaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * Created by YB on 2020/7/8
 */
class WeatherRepository(app: Application) : BaseRepository(app) {
    val httpModel by lazy { HttpModel.getInstance(app) }

    suspend fun getWeatherFlow(): Flow<ResultFlow<Weather>> = flow {
        if (networkUtil.isNetworkAvailable()) {
            val weather = httpModel.api.getWeatherFlow("101010100")
            emit(ResultFlow.Success(weather))
        } else {
            emit(ResultFlow.Error(0, "No network"))
        }
    }.catch { e -> emit(ResultFlow.Error(0, "error", e)) }.flowOn(Dispatchers.IO)
}