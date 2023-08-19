package cn.wthee.pcrtool.utils

import android.annotation.SuppressLint
import cn.wthee.pcrtool.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


/**
 * Retrofit2 创建服务
 */
object ApiUtil {

    private const val TIMEOUT_NORMAL_SECOND = 10L
    private const val TIMEOUT_DOWNLOAD_SECOND = 60L
    private const val MAX_RETRY = 3

    @SuppressLint("CustomX509TrustManager", "TrustAllX509TrustManager")
    private fun OkHttpClient.Builder.setSSL(): OkHttpClient.Builder {
        val client = this
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
        client.sslSocketFactory(sslContext.socketFactory, trustManager)
            .hostnameVerifier { _, _ -> true }
        return client
    }

    /**
     * 带下载进度 client
     */
    fun buildDownloadClient(listener: DownloadListener): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor(Interceptor {
                val originalResponse: Response = it.proceed(it.request())
                return@Interceptor originalResponse.newBuilder()
                    .body(DownloadResponseBody(originalResponse.body!!, listener))
                    .build()
            })
            .retryOnConnectionFailure(true)
            .connectTimeout(TIMEOUT_NORMAL_SECOND, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_DOWNLOAD_SECOND, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_DOWNLOAD_SECOND, TimeUnit.SECONDS)
        return builder.setSSL().build()
    }


    /**
     * 创建 [OkHttpClient]
     */
    fun buildClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .connectTimeout(TIMEOUT_NORMAL_SECOND, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_NORMAL_SECOND, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_NORMAL_SECOND, TimeUnit.SECONDS)
            .addInterceptor(RetryInterceptor(MAX_RETRY))

        return builder.setSSL().build()
    }


    /**
     * 创建服务
     */
    fun <T> create(serviceClass: Class<T>, url: String): T {
        val builder = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(buildClient())

        return builder.build().create(serviceClass)
    }

    /**
     * 创建自定义 [client] 服务
     */
    fun <T> createWithClient(serviceClass: Class<T>, url: String, client: OkHttpClient): T {
        val builder = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)

        return builder.build().create(serviceClass)
    }

}

/**
 * 重试拦截器
 */
class RetryInterceptor(  //最大重试次数
    private var maxRetry: Int
) : Interceptor {
    private var retryNum = 0 //假如设置为3次重试的话，则最大可能请求4次（默认1次+3次重试）

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("app-version", BuildConfig.VERSION_NAME)
            .build()
        var response = chain.proceed(request)
        while (!response.isSuccessful && retryNum < maxRetry) {
            response.close()
            retryNum++
            response = chain.proceed(request)
        }
        return response
    }
}

