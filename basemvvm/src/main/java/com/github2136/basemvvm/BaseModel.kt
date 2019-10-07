package com.github2136.basemvvm

import android.app.Application
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import androidx.collection.ArrayMap
import com.github2136.util.JsonUtil
import com.github2136.util.MessageDigestUtil
import com.github2136.util.SPUtil
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

/**
 * Created by yb on 2018/11/2.
 */
open class BaseModel(app: Application, tag: String) {
    protected val retrofit by lazy {
        Retrofit
            .Builder()
            .baseUrl("http://www.weather.com.cn/")
            .build()
    }
    protected val client: OkHttpClient by lazy {
        OkHttpClient().newBuilder()
            .addNetworkInterceptor(OkHttpInterceptor())
            .build()
    }

    protected val handler by lazy { Handler(Looper.getMainLooper()) }
    protected var mApp: Application = app

    protected var mTag: String = tag
    protected var mSpUtil: SharedPreferences = SPUtil.getSharedPreferences(mApp)
    protected var mJsonUtil: JsonUtil = JsonUtil.instance

    protected fun httpGet(url: String,
                          method: String,
                          params: ArrayMap<String, Any>?,
                          callback: Callback) {
        val urlSb = StringBuilder(url + method)

        params?.let {
            urlSb.append("?")
            for ((key, value) in it) {
                urlSb.append(key)
                urlSb.append("=")
                urlSb.append(value)
                urlSb.append("&")
            }
            urlSb.deleteCharAt(urlSb.length - 1)
        }
        val requestBuild = Request.Builder()
            .url(urlSb.toString())
            .addHeader("head1", "headvalue1")
            .tag(mTag)
            .get()

        requestBuild.addHeader("head2", "headvaleu2")

        client.newCall(requestBuild.build()).enqueue(callback)
    }

    protected fun httpPost(url: String,
                           method: String,
                           params: ArrayMap<String, Any>?,
                           callback: Callback) {

        val JSON = "application/json".toMediaTypeOrNull()
        var json = ""
        params?.let {
            json = mJsonUtil.getGson().toJson(params)
        }
        val body = json.toRequestBody(JSON)

        val requestBuild = Request.Builder()
            .url(url + method)
            .addHeader("head1", "headvalue1")
            .tag(mTag)
            .post(body)

        requestBuild.addHeader("head2", "headvalue2")
        client.newCall(requestBuild.build()).enqueue(callback)
    }

    fun cancelRequest() {
        for (call in client.dispatcher.queuedCalls()) {
            if (call.request().tag() == mTag)
                call.cancel()
        }
        for (call in client.dispatcher.runningCalls()) {
            if (call.request().tag() == mTag)
                call.cancel()
        }
    }
}