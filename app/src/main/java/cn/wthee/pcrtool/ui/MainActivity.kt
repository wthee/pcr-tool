package cn.wthee.pcrtool.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PcrtoolcomposeTheme
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.ToastUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        lateinit var handler: Handler
    }

    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PcrtoolcomposeTheme {
                Home()
            }
        }
        //设置 handler
        setHandler()
        //数据库版本检查
        GlobalScope.launch {
            DatabaseUpdater.checkDBVersion()
        }
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

@ExperimentalFoundationApi
@Composable
fun Home() {
    val navController = rememberNavController()
    val viewModel: NavViewModel = hiltNavGraphViewModel()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        NavGraph(navController, viewModel)
        FabMain(navController, viewModel, Modifier.align(Alignment.BottomEnd))
    }
}

@Composable
fun FabMain(navController: NavHostController, viewModel: NavViewModel, modifier: Modifier) {
    val pageLevel = viewModel.pageLevel.observeAsState()

    FloatingActionButton(
        onClick = {
            val currentPageLevel = pageLevel.value ?: 0
            if (currentPageLevel > 0) {
                viewModel.pageLevel.postValue(currentPageLevel - 1)
                navController.navigateUp()
            } else {
                //TODO 打开或关闭菜单
                val menuState = if (currentPageLevel == 0) {
                    //打开菜单
                    ToastUtil.short("打开")
                    -1
                } else {
                    //关闭菜单
                    ToastUtil.short("关闭")
                    0
                }
                viewModel.pageLevel.postValue(menuState)
            }
        },
        backgroundColor = MaterialTheme.colors.background,
        contentColor = MaterialTheme.colors.primary,
        modifier = modifier
            .padding(Dimen.fabMargin)
            .size(Dimen.fabSize),
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