package cn.wthee.pcrtool.workers

import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.utils.Constants
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.header

/**
 * 文件下载
 */
object DownloadFileClient {
    private const val TIMEOUT_DOWNLOAD_SECOND = 30 * 1000L

    // 配置 HttpClient
    var client = HttpClient(Android) {

        // 超时设置
        install(HttpTimeout) {
            requestTimeoutMillis = TIMEOUT_DOWNLOAD_SECOND
            connectTimeoutMillis = TIMEOUT_DOWNLOAD_SECOND
            socketTimeoutMillis = TIMEOUT_DOWNLOAD_SECOND
        }

        install(DefaultRequest) {
            // 应用版本
            header(Constants.APP_VERSION, BuildConfig.VERSION_NAME)
        }
    }
}
