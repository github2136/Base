package com.github2136.basemvvm

import com.orhanobut.logger.Logger
import okhttp3.Interceptor
import okhttp3.Response
import okio.*
import retrofit2.Invocation
import java.io.IOException
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

/**
 * Created by YB on 2019/5/5
 * OKHTTP拦截器
 */
class OkHttpInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val startTime = System.currentTimeMillis()
        val request = chain.request()
        val m = request.tag(Invocation::class.java)?.method()
        val dynamicTimeout = m?.getAnnotation(DynamicTimeout::class.java)
        val requestUrl = request.url
        val method = request.method
        val requestHeads = request.headers

        var requestBody: ByteString = ByteString.EMPTY
        request.body?.apply {
            val contentType = contentType()
            if (contentType?.subtype == "json" || contentType?.type == "text") {
                val requestBuffer = Buffer()
                request.body?.writeTo(requestBuffer)
                requestBody = requestBuffer.readByteString()
            }
        }
        val response: Response
        var responseLog = ""
        try {
            dynamicTimeout?.apply {
                if (this.timeout > 0) {
                    chain.withConnectTimeout(dynamicTimeout.timeout, TimeUnit.SECONDS)
                        .withReadTimeout(dynamicTimeout.timeout, TimeUnit.SECONDS)
                        .withWriteTimeout(dynamicTimeout.timeout, TimeUnit.SECONDS)
                }
            }
            response = chain.proceed(request)
            val code = response.code
            val responseHeads = response.headers
            val responseBody = response.body
            var body = ""
            responseBody?.apply {
                val contentType = contentType()
                if (contentType == null || contentType.type == "text" || contentType.subtype.contains("json")) {
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
            // val responseHeaderSb = StringBuilder()
            // responseHeads.forEach {
            //     responseHeaderSb.append(it.first + ":" + it.second + "\n")
            // }
            // |${if (responseHeaderSb.isNotEmpty()) "Header\n$responseHeaderSb" else ""}
            responseLog = """Code $code
                |Response Body time:%time
                |$body
                """.trimIndent()
            return response
        } catch (e: Exception) {
            responseLog = "$e"
            throw e
        } finally {
            val requestHeaderSb = StringBuilder()
            requestHeads.forEach {
                requestHeaderSb.append(it.first + ":" + it.second + "\n")
            }
            val endTime = System.currentTimeMillis()
            Logger.t("HTTP")
                .d(
                    """
                    |$method $requestUrl
                    |Header
                    |${requestHeaderSb}Request Body:${requestBody.utf8()}
                    |┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄
                    |${responseLog.replace("%time", (endTime - startTime).toString())}""".trimMargin()
                )
        }
    }
}