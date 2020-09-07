package com.github2136.base.repository

import android.app.Application
import com.github2136.base.HttpModel
import com.github2136.base.entity.Weather
import com.github2136.basemvvm.BaseRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by YB on 2020/7/8
 */
class WeatherRepository(app: Application) : BaseRepository(app) {
    val httpModel: HttpModel by lazy { HttpModel.getInstance(app) }

    fun getWeather(callback: RepositoryCallback<String>.() -> Unit) {
        val mCallback = RepositoryCallback<String>().apply(callback)
        if (networkUtil.isNetworkAvailable()) {
            val call = httpModel.api.getWeather("101010100")
            addCall(call)
            call.enqueue(object : Callback<Weather> {
                override fun onFailure(call: Call<Weather>, t: Throwable) {
                    mCallback.mComplete?.invoke()
                    mCallback.mFail?.invoke(-1, "fail")
                }

                override fun onResponse(call: Call<Weather>, response: Response<Weather>) {
                    mCallback.mComplete?.invoke()
                    if (response.isSuccessful) {
                        mCallback.mSuccess?.invoke(response.body().toString())
                    } else {
                        mCallback.mFail?.invoke(-1, "fail")
                    }
                }
            })
        } else {
            mCallback.mComplete?.invoke()
            mCallback.mSuccess?.invoke("local data")
        }
    }
}