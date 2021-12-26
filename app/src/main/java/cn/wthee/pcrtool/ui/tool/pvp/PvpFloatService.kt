package cn.wthee.pcrtool.ui.tool.pvp

import android.app.NotificationManager
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.ViewTreeViewModelStoreOwner
import androidx.savedstate.ViewTreeSavedStateRegistryOwner
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.*
import com.google.accompanist.pager.ExperimentalPagerApi

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalPagerApi
class PvpFloatService : LifecycleService() {
    private lateinit var windowManager: WindowManager
    private val activity = ActivityHelper.instance.currentActivity
    private var floatRootView: View? = null
    private var spanCount = 5

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        //数据
        spanCount = intent?.getIntExtra("spanCount", 5) ?: 5
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //前台通知
            val notificationManager: NotificationManager =
                MyApplication.context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val notice = NotificationUtil.createNotice(
                MyApplication.context,
                "2",
                "竞技场查询服务",
                Constants.PVPSEARCH_NOTICE_TITLE,
                notificationManager
            ).build()
            startForeground(1, notice)
        }
        //初始化加载
        initWindow()
        initObserve()
    }

    override fun onDestroy() {
        try {
            navViewModel.floatServiceRun.postValue(false)
            windowManager.removeView(floatRootView)
        } catch (e: Exception) {

        }
        stopForeground(true)
        super.onDestroy()
    }

    //数据监听
    private fun initObserve() {
        try {
            navViewModel.apply {
                floatServiceRun.observe(this@PvpFloatService) {
                    if (it == false) {
                        windowManager.removeView(floatRootView)
                        this@PvpFloatService.stopSelf()
                    }
                }
                floatSearchMin.observe(this@PvpFloatService) {
                    windowManager.updateViewLayout(floatRootView, getParam(it ?: false))
                }
            }
        } catch (e: Exception) {
            Log.e("DEBUG", e.message ?: "")
            UMengLogUtil.upload(e, Constants.EXCEPTION_PVP_SERVICE)
        }
    }

    private fun initWindow() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        floatRootView = ComposeView(this).apply {
            ViewTreeLifecycleOwner.set(this, this@PvpFloatService)
            ViewTreeSavedStateRegistryOwner.set(this, activity)
            ViewTreeViewModelStoreOwner.set(this, activity)
            setContent {
                PvpFloatSearch(spanCount = spanCount)
            }
        }
        windowManager.addView(floatRootView, getParam(false))
    }

    private fun getParam(min: Boolean) = WindowManager.LayoutParams().apply {
        type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
        format = PixelFormat.RGBA_8888
        flags =
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        //位置大小设置
        val minSize = (Dimen.fabSize + Dimen.mediumPadding * 3).value.dp2px

        width =
            if (min) {
                minSize
            } else {
                (spanCount * (Dimen.mediumIconSize + Dimen.mediumPadding * 2).value.dp2px) + minSize
            }
        height =
            if (min) {
                minSize
            } else {
                getFloatWindowHeight()
            }
        gravity = Gravity.START or Gravity.TOP
        x = 0
        y = 0
    }
}

fun getFloatWindowHeight(): Int {
    val width = ScreenUtil.getWidth()
    val height = ScreenUtil.getHeight()
    return (if (width > height) height else width) - Dimen.smallIconSize.value.dp2px
}