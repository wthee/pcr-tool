package cn.wthee.pcrtool.utils

import android.content.Context
import android.os.StatFs
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import java.io.File

/**
 * 视频缓存
 */
class VideoCache {

    //视频缓存大小
    private val minimumMaxSizeBytes = 50L * 1024 * 1024
    private val maximumMaxSizeBytes = 500L * 1024 * 1024

    //视频缓存占用剩余存储空间的比例
    private val maxSizePercent = 0.06


    fun init(context: Context): SimpleCache {
        //视频缓存文件夹
        val videoDir = File(context.filesDir, Constants.VIDEO_DIR)
        //初始视频缓存大小
        val videoCacheSize = try {
            val stats = StatFs(videoDir.apply { mkdir() }.absolutePath)
            val size = maxSizePercent * stats.blockCountLong * stats.blockSizeLong
            size.toLong().coerceIn(minimumMaxSizeBytes, maximumMaxSizeBytes)
        } catch (_: Exception) {
            maximumMaxSizeBytes
        }

        return SimpleCache(
            videoDir,
            LeastRecentlyUsedCacheEvictor(videoCacheSize),
            StandaloneDatabaseProvider(context)
        )
    }

}