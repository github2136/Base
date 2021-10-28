package com.github2136.base.model

import android.content.Context
import com.github2136.basemvvm.BaseWebModel

/**
 * Created by yb on 2019/10/7
 */
class HttpModel private constructor(context: Context) : BaseWebModel(context) {
    override val baseUrl: String = "http://www.weather.com.cn/"

    val api by lazy { retrofit.create(HttpService::class.java) }

    override fun addHead(): MutableMap<String, String>? = null
    override fun preProcessing(code: Int, body: String?) {
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