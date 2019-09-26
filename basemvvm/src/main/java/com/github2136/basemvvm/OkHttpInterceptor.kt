package com.github2136.basemvvm

import com.orhanobut.logger.Logger
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import okio.ByteString
import java.nio.charset.Charset

/**
 * Created by YB on 2019/5/5
 * OKHTTP拦截器
 */
class OkHttpInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestUrl = request.url
        val method = request.method
        val requestHeads = request.headers
        val response = chain.proceed(request)
        val code = response.code
        val responseHeads = response.headers
        val responseBody = response.body
        var body = ""
        responseBody?.apply {
            val contentType = contentType()
            if (contentType?.subtype.equals("json", true) || contentType?.type.equals("text")) {
                val contentLength = contentLength()
                val source = source()
                source.request(java.lang.Long.MAX_VALUE) // Buffer the entire body.+  Charset charset = UTF8;
                val buffer = source.buffer()

                var charset: Charset? = Charset.forName("UTF-8")

                if (contentType != null) {
                    charset = contentType.charset(Charset.forName("UTF-8"))
                }
                if (contentLength != 0L) {
                    body = buffer.clone().readString(charset!!)
                }
            }
        }

        var requestBody: ByteString = ByteString.EMPTY
        if (!request.body?.contentType().toString().startsWith("multipart/form-data;boundary=")) {
            val requestBuffer = Buffer()
            request.body?.writeTo(requestBuffer)
            requestBody = requestBuffer.readByteString()
        }

        Logger.t("HTTP")
                .d("""
            |$method $requestUrl
            |Header
            |${requestHeads}Request Body:${requestBody.utf8()}
            |
            |Code $code
            |Response Body $body
            """.trimMargin())
//        |${if (responseHeads.size() > 0) {
//            "Header\n$responseHeads"
//        } else {
//            ""
//        }}
        return response
    }
}