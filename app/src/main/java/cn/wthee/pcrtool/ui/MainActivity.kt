package cn.wthee.pcrtool.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
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
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        lateinit var handler: Handler
        lateinit var navViewModel: NavViewModel
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
        ActivityHelper.instance.currentActivity = this
        //设置 handler
        setHandler()
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
                    .size(Dimen.fabSize)
            )
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun FabMain(navController: NavHostController, viewModel: NavViewModel, modifier: Modifier) {
    val pageLevel = viewModel.pageLevel.observeAsState()
    val show = viewModel.fabShow.observeAsState().value ?: true

    AnimatedVisibility(visible = show) {
        FloatingActionButton(
            onClick = {
                val currentPageLevel = pageLevel.value ?: 0
                if (currentPageLevel > 0) {
                    viewModel.pageLevel.postValue(currentPageLevel - 1)
                    navController.navigateUp()
                } else {
                    //打开或关闭菜单
                    val menuState = if (currentPageLevel == 0) -1 else 0
                    viewModel.pageLevel.postValue(menuState)
                }
            },
            backgroundColor = MaterialTheme.colors.background,
            contentColor = MaterialTheme.colors.primary,
            modifier = modifier,
        ) {
            val icon =
                painterResource(
                    id = if (pageLevel.value == null || pageLevel.value!! == 0)
                        R.drawable.ic_function
                    else
                        R.drawable.ic_left
                )
            Icon(icon, "", modifier = Modifier.padding(Dimen.fabPadding))
        }
    }

}