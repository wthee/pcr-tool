package cn.wthee.pcrtool.ui

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Process
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.work.WorkManager
import cn.wthee.pcrtool.MyApplication.Companion.context
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.RegionType
import cn.wthee.pcrtool.database.*
import cn.wthee.pcrtool.navigation.NavViewModel
import cn.wthee.pcrtool.ui.tool.pvp.PvpFloatService
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.viewmodel.NoticeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * 本地存储：收藏信息
 */
fun mainSP(): SharedPreferences =
    context.getSharedPreferences("main", Context.MODE_PRIVATE)!!

/**
 * 本地存储：版本、设置信息
 */
fun settingSP(mContext: Context = context): SharedPreferences =
    mContext.getSharedPreferences("setting", Context.MODE_PRIVATE)!!


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mNoticeViewModel: NoticeViewModel by viewModels()

    companion object {
        lateinit var handler: Handler

        lateinit var navViewModel: NavViewModel
        lateinit var noticeViewModel: NoticeViewModel

        @OptIn(ExperimentalMaterialApi::class)
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
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            PCRToolApp()
        }

        ActivityHelper.instance.currentActivity = this
        //设置 handler
        setHandler()
        //用户设置信息
        val sp = settingSP()
        vibrateOnFlag = sp.getBoolean(Constants.SP_VIBRATE_STATE, true)
        animOnFlag = sp.getBoolean(Constants.SP_ANIM_STATE, true)
        dynamicColorOnFlag = sp.getBoolean(Constants.SP_COLOR_STATE, true)
        regionType =
            RegionType.getByValue(sp.getInt(Constants.SP_DATABASE_TYPE, RegionType.CN.value))

    }

    override fun onResume() {
        super.onResume()
        //校验数据库版本
        MainScope().launch {
            DatabaseUpdater.checkDBVersion()
        }
        //更新通知
        noticeViewModel = mNoticeViewModel
        noticeViewModel.check()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(context, PvpFloatService::class.java))
        WorkManager.getInstance(context).cancelAllWork()
        val notificationManager: NotificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    //返回拦截
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            navViewModel.loading.postValue(false)
            when (navViewModel.fabMainIcon.value ?: MainIconType.MAIN) {
                MainIconType.MAIN -> {
                    return super.onKeyDown(keyCode, event)
                }
                MainIconType.DOWN -> {
                    navViewModel.fabMainIcon.postValue(MainIconType.MAIN)
                    return true
                }
                MainIconType.CLOSE -> {
                    navViewModel.fabCloseClick.postValue(true)
                    return true
                }
                MainIconType.OK -> {
                    navViewModel.fabOKClick.postValue(true)
                    return true
                }
                else -> {
                    navViewModel.fabMainIcon.postValue(MainIconType.BACK)
                }
            }
        }
        return super.onKeyDown(keyCode, event)
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
                finish()
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
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

