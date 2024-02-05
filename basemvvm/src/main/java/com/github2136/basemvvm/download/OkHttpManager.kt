package com.github2136.basemvvm.download

import okhttp3.Call
import okhttp3.Headers
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

    fun call(url: String, header: Map<String, String>?): Call {
        val builder = Request.Builder()
            .url(url)
            .addHeader("Accept-Encoding", "identity")
        header?.forEach {
            builder.addHeader(it.key, it.value)
        }
        return client.newCall(builder.build())
    }

    fun call(url: String, start: Long, end: Long, header: Map<String, String>?): Call {
        val builder = Request.Builder()
            .url(url)
            .header("RANGE", "bytes=$start-$end")
        header?.forEach {
            builder.addHeader(it.key, it.value)
        }
        return client.newCall(builder.build())
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