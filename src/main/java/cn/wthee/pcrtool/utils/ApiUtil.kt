package cn.wthee.pcrtool.utils

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit2 创建服务
 */
object ApiUtil {

    fun downloadClientBuild(listener: DownloadListener): OkHttpClient {
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
            .build()
    }

    //创建服务
    fun <T> create(serviceClass: Class<T>, url: String): T {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val builder = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)

        return builder.build().create(serviceClass)
    }

    fun <T> createWithClient(serviceClass: Class<T>, url: String, client: OkHttpClient): T {
        val builder = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)

        return builder.build().create(serviceClass)
    }
}
