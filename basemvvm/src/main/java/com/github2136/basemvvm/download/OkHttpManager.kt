package com.github2136.basemvvm.download

import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Created by YB on 2019/6/11
 * 在application子类onCreate中调用
 * val sSlObj = SSLUtil.notVerified()
 * OkHttpManager.client = OkHttpClient().newBuilder()
 * .sslSocketFactory(sSlObj.socketFactory, sSlObj.trustManager)
 * .hostnameVerifier(HostnameVerifier { hostname, session -> true })
 * .build()
 */
class OkHttpManager(private val client: OkHttpClient) {

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
        var client: OkHttpClient? = null
        val instance by lazy {
            if (client == null) {
                client = OkHttpClient().newBuilder()
                    .build()
            }
            OkHttpManager(client!!)
        }
    }
}