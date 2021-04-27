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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.compose.MenuContent
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PcrtoolcomposeTheme
import cn.wthee.pcrtool.utils.ActivityHelper
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.ToastUtil
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        lateinit var handler: Handler
        lateinit var navViewModel: NavViewModel
    }

    @InternalCoroutinesApi
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
            val pageLevel = navViewModel.pageLevel.value ?: 0
            if (pageLevel == 0) {
                return super.onKeyDown(keyCode, event)
            } else if (pageLevel == -1) {
                navViewModel.goback(null)
                return true
            } else {
                navViewModel.goback(null)
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    //TODO 刷新页面
    @SuppressLint("RestrictedApi")
    private fun setHandler() {
        //接收消息
        handler = Handler(Looper.getMainLooper(), Handler.Callback {
            viewModelStore.clear()
//            val fm = supportFragmentManager
//            for (i in 0..fm.backStackEntryCount) {
//                fm.popBackStack()
//            }
//            val navHostController = findNavController(R.id.nav_host_fragment)
//            for (i in 0..navHostController.backStack.size) {
//                navHostController.popBackStack()
//            }
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

@InternalCoroutinesApi
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
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
                navController, navViewModel, modifier = Modifier
                    .align(Alignment.End)
                    .padding(Dimen.fabMargin)
            )
        }
    }
}

@Composable
fun FabMain(navController: NavHostController, viewModel: NavViewModel, modifier: Modifier) {
    val pageLevel = viewModel.pageLevel.observeAsState()
    val iconId = viewModel.fabMainIcon.observeAsState().value ?: R.drawable.ic_function

    FloatingActionButton(
        onClick = {
            if (iconId == R.drawable.ic_ok) {
                viewModel.fabOK.postValue(true)
            } else {
                viewModel.goback(navController)
            }
        },
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = Dimen.fabElevation),
        backgroundColor = MaterialTheme.colors.background,
        contentColor = MaterialTheme.colors.primary,
        modifier = modifier
            .size(Dimen.fabSize),
    ) {
        val icon =
            painterResource(
                id = if (pageLevel.value == null || pageLevel.value!! == 0)
                    iconId
                else
                    R.drawable.ic_left
            )
        Icon(icon, "", modifier = Modifier.padding(Dimen.fabPadding))
    }
}

