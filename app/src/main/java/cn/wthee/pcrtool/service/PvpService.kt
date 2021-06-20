package cn.wthee.pcrtool.service

import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.ViewTreeViewModelStoreOwner
import androidx.savedstate.ViewTreeSavedStateRegistryOwner
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.theme.PcrtoolcomposeTheme
import cn.wthee.pcrtool.ui.tool.PvpSearchCompose
import cn.wthee.pcrtool.utils.ActivityHelper
import cn.wthee.pcrtool.utils.ScreenUtil
import cn.wthee.pcrtool.utils.dp2px
import com.google.accompanist.pager.ExperimentalPagerApi

class PvpService : LifecycleService() {
    private lateinit var windowManager: WindowManager
    val activity = ActivityHelper.instance.currentActivity

    @ExperimentalAnimationApi
    @ExperimentalPagerApi
    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    override fun onCreate() {
        super.onCreate()
//        initObserve()
        showWindow()
    }

//    private fun initObserve() {
//        ViewModleMain.apply {
//            isVisible.observe(this@SuspendwindowService, {
//                floatRootView?.visibility = if (it) View.VISIBLE else View.GONE
//            })
//            isShowSuspendWindow.observe(this@SuspendwindowService, {
//                if (it) {
//                    showWindow()
//                } else {
//                    if (!Utils.isNull(floatRootView)) {
//                        if (!Utils.isNull(floatRootView?.windowToken)) {
//                            if (!Utils.isNull(windowManager)) {
//                                windowManager?.removeView(floatRootView)
//                            }
//                        }
//                    }
//                }
//            })
//        }
//    }

    @ExperimentalFoundationApi
    @ExperimentalMaterialApi
    @ExperimentalPagerApi
    @ExperimentalAnimationApi
    private fun showWindow() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val layoutParam = WindowManager.LayoutParams().apply {
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            }
            format = PixelFormat.RGBA_8888
            flags =
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            //位置大小设置
            width = ScreenUtil.getWidth() / 2 + 50f.dp2px
            height = ScreenUtil.getWidth() - 50f.dp2px
            gravity = Gravity.START or Gravity.TOP
            //设置剧中屏幕显示
            x = 0
            y = 0
        }
        // 新建悬浮窗控件
        val view = ComposeView(this).apply {
            ViewTreeLifecycleOwner.set(this, this@PvpService)
            ViewTreeSavedStateRegistryOwner.set(this, activity)
            ViewTreeViewModelStoreOwner.set(this, activity)
            setContent {
                PcrtoolcomposeTheme {
                    Card {
                        PvpSearchCompose(
                            scrollState = rememberLazyListState(),
                            toFavorite = MainActivity.actions.toPvpFavorite,
                            floatWindow = true
                        )
                    }
                }
            }
        }
        // 将悬浮窗控件添加到WindowManager
        windowManager.addView(view, layoutParam)
    }
}