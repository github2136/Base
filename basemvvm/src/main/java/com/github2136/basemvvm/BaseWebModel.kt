package com.github2136.basemvvm

import android.app.Application
import com.github2136.util.JsonUtil
import okhttp3.OkHttpClient
import okio.BufferedSource
import okio.GzipSource
import okio.buffer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

/**
 * Created by yb on 2018/11/2.
 * webModel
 */
open class BaseWebModel(app: Application) {
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

                val response = chain.proceed(request)
                val responseBody = response.body
                val responseHeads = response.headers
                responseBody?.apply {
                    val contentType = contentType()
                    if (contentType == null || contentType.subtype == "json" || contentType.type == "text") {
                        val contentLength = contentLength()
                        val source: BufferedSource
                        source = if ("gzip" == responseHeads["Content-Encoding"]) {
                            val gzipSource = GzipSource(source().peek())
                            gzipSource.buffer()
                        } else {
                            source().peek()
                        }
                        source.request(java.lang.Long.MAX_VALUE) // Buffer the entire body.+  Charset charset = UTF8;
                        val buffer = source.buffer

                        var charset: Charset? = Charset.forName("UTF-8")

                        if (contentType != null) {
                            charset = contentType.charset(Charset.forName("UTF-8"))
                        }
                        if (contentLength != 0L) {
                            //返回内容通用处理
                            val body = buffer.clone().readString(charset!!)
                        }
                    }
                }
                return@addInterceptor response
            }
            .addInterceptor(OkHttpInterceptor())
            .build()
    }
    protected var mJsonUtil: JsonUtil = JsonUtil.instance
}