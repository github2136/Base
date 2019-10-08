package com.github2136.basemvvm

import android.app.Application
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import com.github2136.util.JsonUtil
import com.github2136.util.SPUtil
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by yb on 2018/11/2.
 */
open class BaseModel(app: Application) {
    protected val retrofit by lazy {
        Retrofit
            .Builder()
            .client(client)
            .baseUrl("http://www.weather.com.cn/")
            .addConverterFactory(GsonConverterFactory.create(mJsonUtil.getGson()))
            .build()
    }
    protected val client: OkHttpClient by lazy {
        OkHttpClient().newBuilder()
            .addNetworkInterceptor(OkHttpInterceptor())
            .build()
    }


    protected val handler by lazy { Handler(Looper.getMainLooper()) }
    protected var mApp: Application = app

    protected var mSpUtil: SharedPreferences = SPUtil.getSharedPreferences(mApp)
    protected var mJsonUtil: JsonUtil = JsonUtil.instance
}