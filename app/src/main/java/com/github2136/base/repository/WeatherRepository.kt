package com.github2136.base.repository

import android.app.Application
import com.github2136.base.HttpModel
import com.github2136.base.entity.Result
import com.github2136.base.entity.Weather
import com.github2136.basemvvm.BaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * Created by YB on 2020/7/8
 */
class WeatherRepository(app: Application) : BaseRepository(app) {
    val httpModel: HttpModel by lazy { HttpModel.getInstance(app) }

    suspend fun getWeatherFlow(): Flow<Result<Weather>> = flow {
        if (networkUtil.isNetworkAvailable()) {

            try {
                val weather = httpModel.api.getWeatherFlow("101010100")
                emit(Result.Success(weather))
            } catch (e: Exception) {
                emit(Result.Error(0, "error"))
            }
        }else{
            emit(Result.Error(0, "No network"))
        }
    }.flowOn(Dispatchers.IO)
}