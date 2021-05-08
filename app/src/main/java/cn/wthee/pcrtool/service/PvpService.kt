package cn.wthee.pcrtool.service

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.savedstate.ViewTreeSavedStateRegistryOwner
import cn.wthee.pcrtool.ui.tool.PvpSearchWindow
import cn.wthee.pcrtool.utils.ActivityHelper
import cn.wthee.pcrtool.utils.NotificationUtil
import cn.wthee.pcrtool.utils.ScreenUtil
import cn.wthee.pcrtool.utils.dp2px
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.AndroidEntryPoint


/**
 * 悬浮窗服务
 */
@AndroidEntryPoint
class PvpService : Service() {


    private var windowManager: WindowManager? = null
    private var params: WindowManager.LayoutParams? = null


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @ExperimentalFoundationApi
    @ExperimentalMaterialApi
    @ExperimentalPagerApi
    @Suppress("UNCHECKED_CAST")
    override fun onStartCommand(intent: Intent?, flg: Int, startId: Int): Int {
        val activity = ActivityHelper.instance.currentActivity
        //窗口设置
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager?
        params = WindowManager.LayoutParams().apply {
            format = PixelFormat.TRANSLUCENT
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            type = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ->
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                else -> WindowManager.LayoutParams.TYPE_TOAST
            }
            gravity = Gravity.TOP or Gravity.START
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = ScreenUtil.getWidth() - 40f.dp2px
        }
        //加载布局
        activity?.let {
            windowManager!!.addView(ComposeView(this).apply {
                ViewTreeLifecycleOwner.set(this, activity)
                ViewTreeSavedStateRegistryOwner.set(this, activity)
                setContent { PvpSearchWindow() }
            }, params)
        }

        //前台通知
        NotificationUtil.createForeground(this, "竞技场查询服务运行中...")
        return super.onStartCommand(intent, flg, startId)
    }
}
