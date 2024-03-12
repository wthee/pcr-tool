package cn.wthee.pcrtool.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Process
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.DisposableEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavHostController
import androidx.work.WorkManager
import cn.wthee.pcrtool.MyApplication.Companion.context
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.RegionType
import cn.wthee.pcrtool.database.AppBasicDatabase
import cn.wthee.pcrtool.navigation.NavViewModel
import cn.wthee.pcrtool.ui.tool.pvp.PvpFloatService
import cn.wthee.pcrtool.utils.ActivityHelper
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.LogReportUtil
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.VibrateUtil
import dagger.hilt.android.AndroidEntryPoint

/**
 * 本地存储：收藏信息
 */
private const val MAIN_PREFERENCES_NAME = "main"
val Context.dataStoreMain: DataStore<Preferences> by preferencesDataStore(
    name = MAIN_PREFERENCES_NAME,
    produceMigrations = {
        listOf(SharedPreferencesMigration(context, MAIN_PREFERENCES_NAME))
    })

/**
 * 本地存储：版本、设置信息
 */
private const val SETTING_PREFERENCES_NAME = "setting"
val Context.dataStoreSetting: DataStore<Preferences> by preferencesDataStore(
    name = SETTING_PREFERENCES_NAME,
    produceMigrations = {
        listOf(SharedPreferencesMigration(context, SETTING_PREFERENCES_NAME))
    })


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        lateinit var handler: Handler

        lateinit var navViewModel: NavViewModel

        lateinit var navSheetState: ModalBottomSheetState

        @SuppressLint("StaticFieldLeak")
        lateinit var navController: NavHostController

        var vibrateOnFlag = true
        var animOnFlag = true
        var dynamicColorOnFlag = true
        var r6Ids = listOf<Int>()
        var regionType = RegionType.CN
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //加载开屏页面
        installSplashScreen()
        super.onCreate(savedInstanceState)
        //系统栏适配
        enableEdgeToEdge()
        ActivityHelper.instance.currentActivity = this
        //设置 handler
        setHandler()

        setContent {
            //状态栏、导航栏适配
            val darkTheme = isSystemInDarkTheme()
            DisposableEffect(isSystemInDarkTheme()) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        Color.TRANSPARENT,
                        Color.TRANSPARENT,
                    ) { darkTheme },
                    navigationBarStyle = SystemBarStyle.auto(
                        Color.TRANSPARENT,
                        Color.TRANSPARENT,
                    ) { darkTheme },
                )
                onDispose {}
            }

            PCRToolApp()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(context, PvpFloatService::class.java))
        WorkManager.getInstance(context).cancelAllWork()
        val notificationManager: NotificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    /**
     * 刷新页面
     * what：0切换数据，1动态色彩，2、3、4数据更新
     */
    @SuppressLint("RestrictedApi")
    private fun setHandler() {
        //接收消息
        handler = Handler(Looper.getMainLooper(), Handler.Callback {
            //结束应用
            if (it.what == 404) {
                val pid: Int = Process.myPid()
                Process.killProcess(pid)
            }
            try {
                //关闭其他数据库连接
                AppBasicDatabase.close()
                //重启应用
                val intent = Intent(this, MainActivity::class.java)
                intent.action = Intent.ACTION_MAIN
                finish()
                startActivity(intent)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    overrideActivityTransition(
                        Activity.OVERRIDE_TRANSITION_CLOSE,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                    )
                } else {
                    @Suppress("DEPRECATION")
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }
                //数据下载完成，振动提示
                if (it.what > 1) {
                    VibrateUtil(this).done()
                }
            } catch (e: Exception) {
                LogReportUtil.upload(e, Constants.EXCEPTION_DATA_CHANGE)
                ToastUtil.short(getString(R.string.change_failed))
            }
            return@Callback true
        })
    }
}

