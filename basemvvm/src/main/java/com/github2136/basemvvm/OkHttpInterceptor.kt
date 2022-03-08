package com.github2136.basemvvm

import com.orhanobut.logger.Logger
import okhttp3.Interceptor
import okhttp3.Response
import okio.*
import java.io.IOException
import java.nio.charset.Charset

/**
 * Created by YB on 2019/5/5
 * OKHTTP拦截器
 */
class OkHttpInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
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
            responseLog = """Code $code
                |Response Body 
                |$body
                """.trimIndent()
            // |${if (responseHeads.size > 0) "Header\n$responseHeads" else ""}
            return response
        } catch (e: Exception) {
            responseLog = "$e"
            throw e
        } finally {
            Logger.t("HTTP")
                .d(
                    """
                    |$method $requestUrl
                    |Header
                    |${requestHeads}Request Body:${requestBody.utf8()}
                    |┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄
                    |$responseLog""".trimMargin()
                )
        }
    }
}