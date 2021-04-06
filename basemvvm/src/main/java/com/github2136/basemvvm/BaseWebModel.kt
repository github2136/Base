package com.github2136.basemvvm

import android.content.Context
import com.github2136.util.JsonUtil
import com.github2136.util.SPUtil
import okhttp3.OkHttpClient
import okio.BufferedSource
import okio.GzipSource
import okio.buffer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.nio.charset.Charset

/**
 * Created by yb on 2018/11/2.
 * webModel
 */
abstract class BaseWebModel(context: Context) {
    open val baseUrl = ""

    protected val retrofit by lazy {
        Retrofit
            .Builder()
            .client(client)
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(mJsonUtil.getGson()))
            .build()
    }

    protected val mJsonUtil by lazy { JsonUtil.instance }
    protected val mSpUtil by lazy { SPUtil.getSharedPreferences(context) }
    protected val client by lazy {
        OkHttpClient().newBuilder()
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuild = original.newBuilder()
                addHead()?.run {
                    for (entry in this) {
                        requestBuild.addHeader(entry.key, entry.value)
                    }
                }

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
                            val body = buffer.clone().readString(charset!!)
                            preProcessing(body)
                        }
                    }
                }
                return@addInterceptor response
            }
            .addInterceptor(OkHttpInterceptor())
            .build()
    }

    /**
     * 添加Head
     */
    abstract fun addHead(): MutableMap<String, String>?

    /**
     * 前置通用处理
     */
    abstract fun preProcessing(body: String)
}