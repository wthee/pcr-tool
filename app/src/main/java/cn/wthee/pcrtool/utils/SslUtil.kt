package cn.wthee.pcrtool.utils

import android.annotation.SuppressLint
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


object SslUtil {

    @SuppressLint("CustomX509TrustManager", "TrustAllX509TrustManager")
    fun getSsl(): Pair<SSLContext, X509TrustManager> {
        //初始 SSL
        val trustManager = object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(
                chain: Array<X509Certificate>,
                authType: String
            ) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(
                chain: Array<X509Certificate>,
                authType: String
            ) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        }

        val sslContext = SSLContext.getInstance("TLSv1.2")
        sslContext.init(null, arrayOf<TrustManager>(trustManager), SecureRandom())

        return Pair(sslContext, trustManager)
    }

}

