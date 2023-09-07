package com.github2136.basemvvm

import android.content.Context
import com.github2136.util.JsonUtil
import com.github2136.util.NetworkUtil
import com.github2136.util.SPUtil
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okio.BufferedSource
import okio.GzipSource
import okio.buffer
import retrofit2.Invocation
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession


/**
 * Created by yb on 2018/11/2.
 * webModel
 */
abstract class BaseWebModel(context: Context) {
    open var baseUrl = ""
    private var _retrofit: Retrofit? = null
    open val connectTimeout = 10L
    open val readTimeout = 10L
    open val writeTimeout = 10L
    protected val jsonUtil by lazy { JsonUtil.instance }
    protected val spUtil by lazy { SPUtil.getSharedPreferences(context) }

    //请求缓存地址
    private val cache by lazy { Cache(File(context.cacheDir, "http"), 10 * 1024 * 1024) }

    protected val client by lazy {
        val client = OkHttpClient().newBuilder()
            .cache(cache)
            .connectTimeout(connectTimeout, TimeUnit.SECONDS)
            .readTimeout(readTimeout, TimeUnit.SECONDS)
            .writeTimeout(writeTimeout, TimeUnit.SECONDS)
            //前置处理
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuild = original.newBuilder()
                addHead()?.run {
                    val temp = requestBuild.build()
                    val headNames = temp.headers.names()
                    for (entry in this) {
                        if (!headNames.contains(entry.key)) {
                            requestBuild.addHeader(entry.key, entry.value)
                        }
                    }
                }

                val request = requestBuild.build()
                val response = chain.proceed(request)
                val responseCode = response.code
                val responseBody = response.body
                val responseHeads = response.headers
                var body: String? = null
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
                            body = buffer.clone().readString(charset!!)
                        }
                    }
                }
                preProcessing(responseCode, body)
                return@addInterceptor response
            }
            .addInterceptor(cacheInterceptor)
            .addInterceptor(OkHttpInterceptor())
        getSSlObj()?.apply {
            client
                .sslSocketFactory(this.socketFactory, this.trustManager)
                .hostnameVerifier(HostnameVerifier { hostname, session -> hostnameVerifier(hostname, session) })
        }
        client.build()
    }

    open fun resetBaseUrl(url: String) {
        baseUrl = url
        _retrofit = null
    }

    fun getRetrofit(): Retrofit {
        if (_retrofit == null) {
            _retrofit = Retrofit
                .Builder()
                .client(client)
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(jsonUtil.gson))
                .build()
        }
        return _retrofit!!
    }

    private val cacheInterceptor by lazy {
        object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                var request = chain.request()
                val m = request.tag(Invocation::class.java)?.method()
                val httpCache = m?.getAnnotation(HttpCache::class.java)

                val cacheBuilder = CacheControl.Builder()
                cacheBuilder.maxAge(0, TimeUnit.SECONDS) //缓存
                cacheBuilder.maxStale(365, TimeUnit.DAYS)
                val cacheControl: CacheControl = cacheBuilder.build()

                if (httpCache == null) {
                    return chain.proceed(request)
                } else {
                    val networkUtil = NetworkUtil.getInstance(context)
                    if (!networkUtil.isNetworkAvailable()) {
                        request = request.newBuilder()
                            .cacheControl(cacheControl)
                            .build()
                    }
                    val originalResponse = chain.proceed(request)
                    return if (networkUtil.isNetworkAvailable()) {
                        val maxAge = 0 // read from cache
                        originalResponse.newBuilder()
                            .removeHeader("Pragma")
                            .header("Cache-Control", "public ,max-age=$maxAge")
                            .build()
                    } else {
                        val maxStale = 60 * 60 * 24 * 28 // tolerate 4-weeks stale
                        originalResponse.newBuilder()
                            .removeHeader("Pragma")
                            .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                            .build()
                    }
                }
            }
        }
    }

    /**
     * 添加Head
     */
    open fun addHead(): MutableMap<String, String>? = null

    /**
     * ssl验证对象
     */
    open fun getSSlObj(): SSlObj? = null

    /**
     * 域名验证
     * @return Boolean
     */
    open fun hostnameVerifier(hostname: String, session: SSLSession): Boolean = true

    /**
     * 前置通用处理
     */
    abstract fun preProcessing(code: Int, body: String?)
}