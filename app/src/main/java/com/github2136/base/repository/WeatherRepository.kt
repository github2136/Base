package com.github2136.base.repository

import android.app.Application
import com.github2136.base.HttpModel
import com.github2136.base.entity.Weather
import com.github2136.basemvvm.BaseRepository
import com.github2136.basemvvm.RepositoryCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by YB on 2020/7/8
 */
class WeatherRepository(app: Application) : BaseRepository(app) {
    val httpModel: HttpModel by lazy { HttpModel.getInstance(app) }

    fun getWeather(callback: RepositoryCallback<String>) {
        if (networkUtil.isNetworkAvailable()) {
            val call = httpModel.api.getWeather("101010100")
            addCall(call)
            call.enqueue(object : Callback<Weather> {
                override fun onFailure(call: Call<Weather>, t: Throwable) {
                    callback.onFail(-1, "fail")
                }

                override fun onResponse(call: Call<Weather>, response: Response<Weather>) {
                    if (response.isSuccessful) {
                        callback.onSuccess(response.body().toString())
                    } else {
                        callback.onFail(-1, "fail")
                    }
                }
            })
        } else {
            callback.onSuccess("local data")
        }
    }
}