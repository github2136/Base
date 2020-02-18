package com.github2136.basemvvm.download

import okhttp3.OkHttpClient
import okhttp3.Call
import okhttp3.Request


/**
 * Created by YB on 2019/6/11
 */
class OkHttpManager private constructor() {
    private val client: OkHttpClient by lazy {
        OkHttpClient().newBuilder()
            .build()
    }

    fun call(url: String): Call {
        val request = Request.Builder()
            .url(url)
            .addHeader("Accept-Encoding", "identity")
            .build()
        return client.newCall(request)
    }


    fun call(url: String, start: Long, end: Long): Call {
        val request = Request.Builder()
            .url(url)
            .header("RANGE", "bytes=$start-$end")
            .build()
        return client.newCall(request)
    }

    companion object {
        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { OkHttpManager() }
    }
}