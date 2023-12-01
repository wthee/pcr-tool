package cn.wthee.pcrtool

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import cn.wthee.pcrtool.data.preferences.SettingPreferencesKeys
import cn.wthee.pcrtool.database.tryOpenDatabase
import cn.wthee.pcrtool.ui.dataStoreSetting
import cn.wthee.pcrtool.utils.ApiUtil
import cn.wthee.pcrtool.utils.BuglyInitializer
import cn.wthee.pcrtool.utils.Constants
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.request.CachePolicy
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.io.File


/**
 * 应用初始
 */
@HiltAndroidApp
class MyApplication : Application(), ImageLoaderFactory {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        var backupMode = false
        var URL_DOMAIN = "wthee.xyz"
        var useIpOnFlag = false

        //视频缓存
        lateinit var simpleCache: SimpleCache
        lateinit var leastRecentlyUsedCacheEvictor: LeastRecentlyUsedCacheEvictor
        lateinit var standaloneDatabaseProvider: StandaloneDatabaseProvider
        private const val exoCacheSize: Long =
            100 * 1024 * 1024 // Setting cache size to be ~ 100 MB

    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext

        runBlocking {
            val preferences = dataStoreSetting.data.first()
            useIpOnFlag = preferences[SettingPreferencesKeys.SP_USE_IP] ?: false
            //使用ip访问
            if (useIpOnFlag) {
                URL_DOMAIN = "96.45.190.76"
            }
        }

        //Bugly
        BuglyInitializer().create(this)
        //数据库
        if (!BuildConfig.DEBUG) {
            backupMode = tryOpenDatabase() == 0
        }

        //初始视频缓存
        leastRecentlyUsedCacheEvictor = LeastRecentlyUsedCacheEvictor(
            exoCacheSize
        )
        standaloneDatabaseProvider = StandaloneDatabaseProvider(this)
        simpleCache = SimpleCache(
            File(this.cacheDir, "media"),
            leastRecentlyUsedCacheEvictor,
            standaloneDatabaseProvider
        )
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(context)
            .allowHardware(false)
            //禁用内存缓存
            .memoryCachePolicy(CachePolicy.DISABLED)
            .diskCache {
                //调整缓存位置，避免缓存被系统自动清除
                DiskCache.Builder()
                    .directory(context.filesDir.resolve(Constants.COIL_DIR))
                    .build()
            }
            //禁用后，优先从本地缓存STANDARD_MEMORY_MULTIPLIER
            .respectCacheHeaders(false)
            .okHttpClient {
                ApiUtil.buildClient()
            }
            .build()
    }
}
