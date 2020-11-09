package com.github2136.base

import android.content.Context
import com.github2136.basemvvm.BaseWebModel

/**
 * Created by yb on 2019/10/7
 */
class HttpModel private constructor(context: Context) : BaseWebModel(context) {
    override val baseUrl: String = "http://www.weather.com.cn/"

    val api by lazy { retrofit.create(HttpService::class.java) }

    override fun addHead(): MutableMap<String, String>? = null

    override fun preProcessing(body: String) {
    }

    companion object {
        @Volatile
        private var instances: HttpModel? = null

        fun getInstance(context: Context): HttpModel {
            if (instances == null) {
                synchronized(HttpModel::class.java) {
                    if (instances == null) {
                        instances = HttpModel(context)
                    }
                }
            }
            return instances!!
        }
    }
}