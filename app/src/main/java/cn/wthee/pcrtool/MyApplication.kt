package cn.wthee.pcrtool

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.media3.datasource.cache.SimpleCache
import cn.wthee.pcrtool.data.preferences.SettingPreferencesKeys
import cn.wthee.pcrtool.ui.dataStoreSetting
import cn.wthee.pcrtool.utils.BuglyInitializer
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.Constants.SERVER_DOMAIN
import cn.wthee.pcrtool.utils.Constants.SERVER_IP
import cn.wthee.pcrtool.utils.SslUtil
import cn.wthee.pcrtool.utils.VideoCache
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.request.CachePolicy
import coil3.request.allowHardware
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okio.Path.Companion.toOkioPath
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import javax.net.ssl.HttpsURLConnection


/**
 * 应用初始
 */
@HiltAndroidApp
class MyApplication : Application(), SingletonImageLoader.Factory {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        var URL_DOMAIN = SERVER_DOMAIN
        var useIpOnFlag = false
        //视频缓存
        @SuppressLint("UnsafeOptInUsageError")
        lateinit var videoCache: SimpleCache
    }


    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        //忽略证书校验
        disableSSLCertificateChecking()
        //使用ip或域名访问
        runBlocking {
            val preferences = dataStoreSetting.data.first()
            useIpOnFlag = preferences[SettingPreferencesKeys.SP_USE_IP] ?: false
            //使用ip访问
            if (useIpOnFlag) {
                URL_DOMAIN = SERVER_IP
            }
        }
        //Bugly 初始化
        BuglyInitializer().create(this)
        //初始化视频缓存
        videoCache = VideoCache().init(this)
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .allowHardware(false)
            //禁用内存缓存
            .memoryCachePolicy(CachePolicy.DISABLED)
            .diskCache {
                //调整缓存位置，避免缓存被系统自动清除
                val path = context.filesDir.resolve(Constants.COIL_DIR).toOkioPath()
                DiskCache.Builder()
                    .maxSizePercent(0.04)
                    .directory(path)
                    .build()
            }
            .build()
    }

    private fun disableSSLCertificateChecking() {
        try {
            val ssl = SslUtil.getSsl()
            HttpsURLConnection.setDefaultSSLSocketFactory(ssl.first.socketFactory)
            HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
    }
}
