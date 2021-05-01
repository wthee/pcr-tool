package cn.wthee.pcrtool.ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.MainActivity.Companion.r6Ids
import cn.wthee.pcrtool.ui.compose.DownloadCompose
import cn.wthee.pcrtool.ui.compose.FabCompose
import cn.wthee.pcrtool.ui.compose.MenuContent
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PcrtoolcomposeTheme
import cn.wthee.pcrtool.utils.ActivityHelper
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.ToastUtil
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        lateinit var handler: Handler
        lateinit var navViewModel: NavViewModel
        var r6Ids = listOf<Int>()
    }

    @ExperimentalAnimationApi
    @ExperimentalMaterialApi
    @ExperimentalPagerApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PcrtoolcomposeTheme {
                Home()
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }
        ActivityHelper.instance.currentActivity = this
        //设置 handler
        setHandler()
    }

    //返回拦截
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            when (navViewModel.fabMainIcon.value ?: 0) {
                R.drawable.ic_function -> {
                    return super.onKeyDown(keyCode, event)
                }
                R.drawable.ic_down -> {
                    navViewModel.fabMainIcon.postValue(R.drawable.ic_function)
                    return true
                }
                else -> {
                    navViewModel.fabMainIcon.postValue(R.drawable.ic_back)
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    //刷新页面
    @SuppressLint("RestrictedApi")
    private fun setHandler() {
        //接收消息
        handler = Handler(Looper.getMainLooper(), Handler.Callback {
            viewModelStore.clear()
            recreate()
            when (it.what) {
                //正常更新
                -1, 0 -> {
                    ToastUtil.short(Constants.NOTICE_TOAST_SUCCESS)
                }
                //数据切换
                1 -> {
                    ToastUtil.short(Constants.NOTICE_TOAST_CHANGE_SUCCESS)
                }
            }
            return@Callback true
        })
    }
}

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalFoundationApi
@Composable
fun Home() {
    val navController = rememberNavController()
    val actions = remember(navController) { NavActions(navController) }
    navViewModel = hiltNavGraphViewModel()
    val scope = rememberCoroutineScope()
    val loading = navViewModel.loading.observeAsState().value ?: false
    val r6IdList = navViewModel.r6Ids.observeAsState()
    if (r6IdList.value != null) {
        r6Ids = r6IdList.value!!
    }


    Box(modifier = Modifier.fillMaxSize()) {
        //数据库版本检查
        scope.launch {
            DatabaseUpdater(navViewModel).checkDBVersion()
        }
        NavGraph(navController, navViewModel, actions)
        //菜单
        MenuContent(navViewModel, actions)
        Column(modifier = Modifier.align(Alignment.BottomEnd)) {
            DownloadCompose(navViewModel)
            FabMain(
                navController,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(Dimen.fabMargin)
            )
        }
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(Dimen.topBarIconSize)
                    .align(Alignment.Center)
            )
        }
    }
}

@Composable
fun FabMain(navController: NavHostController, modifier: Modifier) {
    val iconId =
        navViewModel.fabMainIcon.observeAsState().value ?: R.drawable.ic_function

    FabCompose(iconId, modifier = modifier) {
        when (iconId) {
            R.drawable.ic_ok -> navViewModel.fabOK.postValue(true)
            R.drawable.ic_cancel -> navViewModel.fabClose.postValue(true)
            R.drawable.ic_function -> {
                navViewModel.fabMainIcon.postValue(R.drawable.ic_down)
            }
            R.drawable.ic_down -> navViewModel.fabMainIcon.postValue(R.drawable.ic_function)
            else -> {
                navController.navigateUp()
                navViewModel.loading.postValue(false)
            }
        }
    }
}

