package cn.wthee.pcrtool

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.CachePolicy
import coil.util.CoilUtils
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import com.umeng.umcrash.UMCrash
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


class MyApplication : Application(), ImageLoaderFactory {


    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        UMConfigure.init(
            this,
            "5fe591d7adb42d58268e8603",
            "Umeng",
            UMConfigure.DEVICE_TYPE_PHONE,
            ""
        )
        UMCrash.setDebug(false)
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
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
                OkHttpClient.Builder()
                    .cache(CoilUtils.createDefaultCache(context))
                    .readTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build()
            }
            .build()
    }

    companion object {
        lateinit var context: Context
    }


}