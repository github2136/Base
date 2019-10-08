package com.github2136.base

import android.app.Application
import com.github2136.basemvvm.BaseModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

/**
 * Created by yb on 2019/10/7
 */
class HttpModel private constructor(app: Application) : BaseModel(app) {
    override val baseUrl: String = "http://www.weather.com.cn/"

    val api by lazy { retrofit.create(HttpService::class.java) }

    companion object {
        @Volatile
        private var instances: HttpModel? = null

        fun getInstance(app: Application): HttpModel {
            if (instances == null) {
                synchronized(HttpModel::class.java) {
                    if (instances == null) {
                        instances = HttpModel(app)
                    }
                }
            }
            return instances!!
        }
    }
}