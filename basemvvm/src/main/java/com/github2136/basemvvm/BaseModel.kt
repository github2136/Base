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
import java.util.concurrent.TimeUnit

/**
 * Created by yb on 2018/11/2.
 */
open class BaseModel(app: Application) {
    open val baseUrl = ""
    protected val retrofit by lazy {
        Retrofit
            .Builder()
            .client(client)
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(mJsonUtil.getGson()))
            .build()
    }
    protected val client: OkHttpClient by lazy {
        OkHttpClient().newBuilder()
            .addNetworkInterceptor(OkHttpInterceptor())
            .connectTimeout(2, TimeUnit.SECONDS)
            .callTimeout(2, TimeUnit.SECONDS)
            .readTimeout(2, TimeUnit.SECONDS)
            .writeTimeout(2, TimeUnit.SECONDS)
            //使用拦截器添加通用参数
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuild = original.newBuilder()
                    .addHeader("deviceType", "0")
                val request = requestBuild.build()
                return@addInterceptor chain.proceed(request)
            }
            .build()
    }


    protected val handler by lazy { Handler(Looper.getMainLooper()) }
    protected var mApp: Application = app

    protected var mSpUtil: SharedPreferences = SPUtil.getSharedPreferences(mApp)
    protected var mJsonUtil: JsonUtil = JsonUtil.instance
}