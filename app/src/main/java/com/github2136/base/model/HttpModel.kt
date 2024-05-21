package com.github2136.base.model

import android.content.Context
import android.util.Log
import com.github2136.basemvvm.BaseWebModel
import okhttp3.Request
import okhttp3.Response

/**
 * Created by yb on 2019/10/7
 */
class HttpModel private constructor(context: Context) : BaseWebModel(context) {
    var api: HttpService

    init {
        baseUrl = "http://www.weather.com.cn/"
        api = getRetrofit().create(HttpService::class.java)
    }

    override fun resetBaseUrl(url: String) {
        if (url.startsWith("http") || url.startsWith("https")) {
            super.resetBaseUrl(url)
            api = getRetrofit().create(HttpService::class.java)
        }
    }

    override fun addHead(): MutableMap<String, String>? = null
    override fun preProcessing(code: Int, request: Request, response: Response, body: String?) {
    }

    companion object {
        private lateinit var context: Context
        private val instances: HttpModel by lazy { HttpModel(context) }
        fun getInstance(context: Context): HttpModel {
            if (!this::context.isInitialized) {
                this.context = context
            }
            return instances
        }
    }
}