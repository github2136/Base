package com.github2136.basemvvm

import java.io.InputStream
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

/**
 * Created by YB on 2020/3/20
 * https工具类
 */
object SSLUtil {
    /**
     * 不验证证书
     * val sSlObj = SSLUtil.notVerified()
     * OkHttpClient().newBuilder()
     * .sslSocketFactory(sSlObj.socketFactory, sSlObj.trustManager)
     * .hostnameVerifier(HostnameVerifier { hostname, session -> true })
     */
    @JvmStatic
    fun notVerified(): SSlObj {
        val sslContext = SSLContext.getInstance("TLS")
        val x509 = arrayOf(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        })
        sslContext.init(null, x509, SecureRandom())

        return SSlObj(sslContext.socketFactory, x509[0] as X509TrustManager)
    }

    /**
     * 验证证书
     * val assets = app.assets
     * val sSlObj = SSLUtil.verified(assets.open("ca.cer"), assets.open("b.cer"))
     * OkHttpClient().newBuilder()
     * .sslSocketFactory(sSlObj.socketFactory, sSlObj.trustManager)
     * //不建议忽略主机验证
     * .hostnameVerifier(HostnameVerifier { hostname, session -> true })
     */
    fun verified(vararg inputStream: InputStream): SSlObj {
        val cf: CertificateFactory = CertificateFactory.getInstance("X.509")
        val keyStoreType = KeyStore.getDefaultType()
        val keyStore = KeyStore.getInstance(keyStoreType).apply {
            load(null, null)
        }
        for ((index, caInput) in inputStream.withIndex()) {
            val ca: X509Certificate = caInput.use {
                cf.generateCertificate(it) as X509Certificate
            }
            keyStore.setCertificateEntry("ca$index", ca)
        }

        val tmfAlgorithm: String = TrustManagerFactory.getDefaultAlgorithm()
        val tmf: TrustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm).apply {
            init(keyStore)
        }
        val sslContext: SSLContext = SSLContext.getInstance("TLS").apply {
            init(null, tmf.trustManagers, null)
        }
        return SSlObj(sslContext.socketFactory, tmf.trustManagers[0] as X509TrustManager)
    }
}

data class SSlObj(val socketFactory: SSLSocketFactory, val trustManager: X509TrustManager)