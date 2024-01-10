package cn.wthee.pcrtool.ui.tool.pvp

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.ActivityHelper
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.LogReportUtil
import cn.wthee.pcrtool.utils.NotificationUtil
import cn.wthee.pcrtool.utils.ScreenUtil
import cn.wthee.pcrtool.utils.dp2px
import kotlin.math.max

private const val ACTION_FINISH = "pvp_service_finish"

/**
 * 竞技场查询（悬浮窗服务）
 */
class PvpFloatService : LifecycleService() {
    private lateinit var windowManager: WindowManager
    private val activity = ActivityHelper.instance.currentActivity
    private var floatRootView: View? = null
    private var spanCount = 5

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        //数据
//        spanCount = intent?.getIntExtra("spanCount", 5) ?: 5
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //前台通知
            val finishIntent = Intent(this, FinishReceiver::class.java)
            finishIntent.action = ACTION_FINISH
            val finishPendingIntent =
                PendingIntent.getBroadcast(
                    this,
                    0,
                    finishIntent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )

            val notificationManager: NotificationManager =
                MyApplication.context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val notice = NotificationUtil.createNotice(
                MyApplication.context,
                "2",
                getString(R.string.pvp_service),
                getString(R.string.pvp_service_running),
                notificationManager
            )
                .addAction(
                    NotificationCompat.Action(
                        R.drawable.unknown_gray,
                        getString(R.string.close_app),
                        finishPendingIntent
                    )
                )
                .build()

            startForeground(1, notice)
        }
        //初始化加载
        initWindow()
        initObserve()
    }

    override fun onDestroy() {
        try {
            windowManager.removeView(floatRootView)
        } catch (_: Exception) {

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        super.onDestroy()
    }

    //数据监听
    private fun initObserve() {
        try {
            navViewModel.apply {
                floatServiceRun.observe(this@PvpFloatService) {
                    if (it == false) {
                        activity?.finish()
                    }
                }
                floatSearchMin.observe(this@PvpFloatService) {
                    windowManager.updateViewLayout(floatRootView, getParam(it ?: false))
                }
            }
        } catch (e: Exception) {
            LogReportUtil.upload(e, Constants.EXCEPTION_PVP_SERVICE)
        }
    }

    private fun initWindow() {
        try {
            windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
            activity?.let {
                floatRootView = ComposeView(it).apply {
                    setViewTreeLifecycleOwner(this@PvpFloatService)
                    setViewTreeSavedStateRegistryOwner(it)
                    setViewTreeViewModelStoreOwner(it)
                    setContent {
                        PvpFloatSearch(spanCount = spanCount)
                    }
                }
                windowManager.addView(floatRootView, getParam(false))
            }
        } catch (e: Exception) {
            LogReportUtil.upload(e, Constants.EXCEPTION_PVP_SERVICE)
        }
    }

    private fun getParam(min: Boolean) = WindowManager.LayoutParams().apply {
        type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }
        format = PixelFormat.RGBA_8888
        flags =
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        //位置大小设置
        val minSize = dp2px((Dimen.fabSize + Dimen.mediumPadding * 3).value)


        height =
            if (min) {
                minSize
            } else {
                getFloatWindowHeight()
            }
        width =
            if (min) {
                minSize
            } else {
                max(
                    dp2px(spanCount * (Dimen.mediumIconSize + Dimen.mediumPadding * 2).value),
                    (height * 9 * 1.0f / 16).toInt()
                ) + minSize
            }
        gravity = Gravity.START or Gravity.TOP
        x = 0
        y = 0
    }


    /**
     * 结束应用
     */
    class FinishReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                when (it.action) {
                    ACTION_FINISH -> {
                        ActivityHelper.instance.currentActivity?.finish()
                    }

                    else -> {}
                }
            }
        }
    }
}

fun getFloatWindowHeight(): Int {
    val width = ScreenUtil.getWidth()
    val height = ScreenUtil.getHeight()
    return (if (width > height) height else width) - dp2px(Dimen.smallIconSize.value)
}
