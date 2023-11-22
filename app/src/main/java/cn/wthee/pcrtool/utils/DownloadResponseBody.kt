package cn.wthee.pcrtool.utils

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*
import java.io.IOException

/**
 * 带进度下载
 */
class DownloadResponseBody(
    private val responseBody: ResponseBody,
    private val downloadListener: DownloadListener?
) : ResponseBody() {
    private var bufferedSource: BufferedSource? = null
    override fun contentType(): MediaType? {
        return responseBody.contentType()
    }

    override fun contentLength(): Long {
        return responseBody.contentLength()
    }

    override fun source(): BufferedSource {
        if (bufferedSource == null) {
            bufferedSource = source(responseBody.source()).buffer()
        }
        return bufferedSource!!
    }

    private fun source(source: Source): Source {
        return object : ForwardingSource(source) {
            var totalBytesRead = 0L

            @Throws(IOException::class)
            override fun read(sink: Buffer, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                if (null != downloadListener && bytesRead != -1L && totalBytesRead < responseBody.contentLength()) {
                    totalBytesRead += bytesRead
                    if (totalBytesRead < 100) {
                        downloadListener.onErrorSize()
                        return -1
                    }
                    val progress = (totalBytesRead * 100.0 / responseBody.contentLength()).toInt()
                    downloadListener.onProgress(
                        progress,
                        totalBytesRead,
                        responseBody.contentLength()
                    )
                    if (progress == 100) {
                        downloadListener.onFinish()
                    }
                }
                return bytesRead
            }
        }
    }

}

/**
 * 下载回调
 */
interface DownloadListener {
    fun onProgress(progress: Int, currSize: Long, totalSize: Long)
    fun onFinish()
    fun onErrorSize()
}