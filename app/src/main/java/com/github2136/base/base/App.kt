package com.github2136.base.base

import com.github2136.basemvvm.BaseApplication
import com.github2136.basemvvm.SSLUtil
import com.github2136.basemvvm.download.DownloadUtil
import com.github2136.basemvvm.download.OkHttpManager
import okhttp3.OkHttpClient
import javax.net.ssl.HostnameVerifier

/**
 * Created by YB on 2023/1/11
 */
class App : BaseApplication() {
    override val saveFileEnable = true
    override fun onCreate() {
        super.onCreate()
        val sSlObj = SSLUtil.notVerified()
        OkHttpManager.client = OkHttpClient().newBuilder()
            .sslSocketFactory(sSlObj.socketFactory, sSlObj.trustManager)
            .hostnameVerifier(HostnameVerifier { hostname, session -> true })
            .build()
    }
}