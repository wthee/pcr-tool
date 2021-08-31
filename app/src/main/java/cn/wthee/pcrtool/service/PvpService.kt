package cn.wthee.pcrtool.service

import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.ViewTreeViewModelStoreOwner
import androidx.savedstate.ViewTreeSavedStateRegistryOwner
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.MainActivity.Companion.mFloatingWindowHeight
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.tool.PvpFloatSearch
import cn.wthee.pcrtool.utils.*
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.pager.ExperimentalPagerApi

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalAnimationApi
class PvpService : LifecycleService() {
    private lateinit var windowManager: WindowManager
    private val activity = ActivityHelper.instance.currentActivity
    private var floatRootView: View? = null

    @ExperimentalCoilApi
    override fun onCreate() {
        super.onCreate()
        initWindow()
        initObserve()
    }

    private fun initObserve() {
        try {
            MainActivity.navViewModel.apply {
                floatServiceRun.observe(this@PvpService) {
                    if (it == false) {
                        windowManager.removeView(floatRootView)
                        this@PvpService.stopSelf()
                    }
                }
                floatSearchMin.observe(this@PvpService) {
                    windowManager.updateViewLayout(floatRootView, getParam(it ?: false))
                }
            }
        } catch (e: Exception) {
            UMengLogUtil.upload(e, Constants.EXCEPTION_PVP_SERVICE)
        }
    }

    @ExperimentalCoilApi
    private fun initWindow() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        floatRootView = ComposeView(this).apply {
            ViewTreeLifecycleOwner.set(this, this@PvpService)
            ViewTreeSavedStateRegistryOwner.set(this, activity)
            ViewTreeViewModelStoreOwner.set(this, activity)
            setContent {
                PvpFloatSearch()
            }
        }
        windowManager.addView(floatRootView, getParam(false))
    }

    private fun getParam(min: Boolean) = WindowManager.LayoutParams().apply {
        mFloatingWindowHeight = getFloatWindowHeight()
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
                (getFloatWindowHeight() * 0.618f).toInt() + minSize
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