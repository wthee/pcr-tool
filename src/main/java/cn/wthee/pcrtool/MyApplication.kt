package cn.wthee.pcrtool

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.createDataStore
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.utils.ApiUtil
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.CachePolicy
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * 应用初始
 */
class MyApplication : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        dataStore = context.createDataStore(name = "main")
        backupMode = DatabaseUpdater.tryOpenDatabase() == 0
        MainScope().launch {
            //数据库版本检查
            DatabaseUpdater.checkDBVersion()
        }
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(context)
            .componentRegistry {
                if (SDK_INT >= Build.VERSION_CODES.P) {
                    add(ImageDecoderDecoder())
                } else {
                    add(GifDecoder())
                }
            }
            .crossfade(true)
            .allowHardware(false)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            .availableMemoryPercentage(0.75)
            .okHttpClient {
                ApiUtil.getClient()
            }
            .build()
    }

    companion object {
        lateinit var context: Context
        lateinit var dataStore: DataStore<Preferences>
        var backupMode = false
    }


}