package com.github2136.base.repository

import android.content.Context
import com.github2136.base.model.HttpModel
import com.github2136.base.model.entity.ResultRepo
import com.github2136.base.model.entity.Weather
import com.github2136.basemvvm.BaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by YB on 2020/7/8
 */
class WeatherRepository(context: Context) : BaseRepository(context) {
    private val httpModel by lazy { HttpModel.getInstance(context) }

    suspend fun getWeather(): ResultRepo<Weather> = withContext(Dispatchers.IO) {
        try {
            val weather = httpModel.api.getWeatherFlow("101010100")
            ResultRepo.Success(weather)
        } catch (e: Exception) {
            ResultRepo.Error(0, "error", e)
        }
    }
}