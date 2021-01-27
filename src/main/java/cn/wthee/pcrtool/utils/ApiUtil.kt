package cn.wthee.pcrtool.utils

import cn.wthee.pcrtool.MyApplication.Companion.context
import cn.wthee.pcrtool.R
import coil.util.CoilUtils
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager


/**
 * Retrofit2 创建服务
 */
object ApiUtil {

    /**
     * 获取SSL 证书
     */
    private fun initSSL(): List<Any> {
        val cf = CertificateFactory.getInstance("X.509")
        val inputStream = context.resources.openRawResource(R.raw.certificate)
        val ca = cf.generateCertificate(inputStream)
        val kStore = KeyStore.getInstance(KeyStore.getDefaultType())
        kStore.load(null, null)
        kStore.setCertificateEntry("ca", ca)
        val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        tmf.init(kStore)
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, tmf.trustManagers, null)
        return listOf(sslContext, tmf.trustManagers[0])
    }

    /**
     * 下载进度
     */
    fun downloadClientBuild(listener: DownloadListener): OkHttpClient {
        val params = initSSL()
        return OkHttpClient.Builder()
            .addInterceptor(Interceptor {
                val originalResponse: Response = it.proceed(it.request())
                return@Interceptor originalResponse.newBuilder()
                    .body(DownloadResponseBody(originalResponse.body()!!, listener))
                    .build()
            })
            .retryOnConnectionFailure(true)
            .connectTimeout(360, TimeUnit.SECONDS)
            .writeTimeout(360, TimeUnit.SECONDS)
            .readTimeout(360, TimeUnit.SECONDS)
            .sslSocketFactory(
                (params[0] as SSLContext).socketFactory,
                params[1] as X509TrustManager
            )
            .build()
    }

    /**
     * 创建 [OkHttpClient]
     */
    fun getClient(): OkHttpClient {
        val params = initSSL()
        return OkHttpClient.Builder()
            .cache(CoilUtils.createDefaultCache(context))
            .connectTimeout(300, TimeUnit.SECONDS)
            .writeTimeout(300, TimeUnit.SECONDS)
            .readTimeout(300, TimeUnit.SECONDS)
            .addInterceptor(RetryIntercepter(3))
            .sslSocketFactory(
                (params[0] as SSLContext).socketFactory,
                params[1] as X509TrustManager
            )
            .build()
    }

    /**
     * 创建服务
     */
    fun <T> create(serviceClass: Class<T>, url: String): T {
        val builder = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getClient())

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
class RetryIntercepter(  //最大重试次数
    var maxRetry: Int
) : Interceptor {
    private var retryNum = 0 //假如设置为3次重试的话，则最大可能请求4次（默认1次+3次重试）

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response = chain.proceed(request)
        while (!response.isSuccessful && retryNum < maxRetry) {
            response.close()
            retryNum++
            response = chain.proceed(request)
        }
        return response
    }
}
