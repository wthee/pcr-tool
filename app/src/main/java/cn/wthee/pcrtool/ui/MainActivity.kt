package cn.wthee.pcrtool.ui

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.paging.ExperimentalPagingApi
import androidx.work.WorkManager
import cn.wthee.pcrtool.MyApplication.Companion.context
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.database.AppDatabaseCN
import cn.wthee.pcrtool.database.AppDatabaseJP
import cn.wthee.pcrtool.database.AppDatabaseTW
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.ui.MainActivity.Companion.navController
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.MainActivity.Companion.r6Ids
import cn.wthee.pcrtool.ui.common.FabCompose
import cn.wthee.pcrtool.ui.home.MoreFabCompose
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PCRToolComposeTheme
import cn.wthee.pcrtool.utils.ActivityHelper
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.LogReportUtil
import cn.wthee.pcrtool.utils.ToastUtil
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
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
fun settingSP(): SharedPreferences =
    context.getSharedPreferences("setting", Context.MODE_PRIVATE)!!

fun settingSP(context: Context): SharedPreferences =
    context.getSharedPreferences("setting", Context.MODE_PRIVATE)!!


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        lateinit var handler: Handler
        lateinit var navViewModel: NavViewModel

        @SuppressLint("StaticFieldLeak")
        lateinit var navController: NavHostController
        var vibrateOnFlag = true
        var animOnFlag = true
        var r6Ids = listOf<Int>()
    }


    @OptIn(
        ExperimentalComposeUiApi::class,
        ExperimentalPagingApi::class,
        ExperimentalAnimationApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            PCRToolComposeTheme {
                //状态栏、导航栏适配
                val ui = rememberSystemUiController()
                val isLight = !isSystemInDarkTheme()
                ui.setNavigationBarColor(
                    MaterialTheme.colorScheme.surface,
                    darkIcons = isLight
                )
                ui.setStatusBarColor(MaterialTheme.colorScheme.surface, darkIcons = isLight)
                Surface {
                    Home()
                }
            }
        }
        ActivityHelper.instance.currentActivity = this
        //设置 handler
        setHandler()
        //用户设置信息
        val sp = settingSP()
        vibrateOnFlag = sp.getBoolean(Constants.SP_VIBRATE_STATE, true)
        animOnFlag = sp.getBoolean(Constants.SP_ANIM_STATE, true)
        //校验数据库版本
        MainScope().launch {
            DatabaseUpdater.checkDBVersion()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
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
                    navViewModel.fabOKCilck.postValue(true)
                    return true
                }
                else -> {
                    navViewModel.fabMainIcon.postValue(MainIconType.BACK)
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    //刷新页面 fixme 偶尔闪退
    @SuppressLint("RestrictedApi")
    private fun setHandler() {
        //接收消息
        handler = Handler(Looper.getMainLooper(), Handler.Callback {
            //关闭其他数据库连接
            val isHome = navViewModel.fabMainIcon.value == MainIconType.MAIN
            if (isHome) {
                AppDatabaseCN.close()
                AppDatabaseTW.close()
                AppDatabaseJP.close()
                try {
                    navController.popBackStack()
                    viewModelStore.clear()
                    recreate()
                } catch (e: Exception) {
                    LogReportUtil.upload(e, Constants.EXCEPTION_DATA_CHANGE)
                }
            } else {
                ToastUtil.short(getString(R.string.tip_download_finish))
            }

            return@Callback true
        })
    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Home(
    mNavViewModel: NavViewModel = hiltViewModel()
) {
    navController = rememberAnimatedNavController()
    val actions = remember(navController) { NavActions(navController) }
    navViewModel = mNavViewModel

    val loading = navViewModel.loading.observeAsState().value ?: false
    val r6IdList = navViewModel.r6Ids.observeAsState()

    if (r6IdList.value != null) {
        r6Ids = r6IdList.value!!
    }
    val statusBarHeight = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()

    Box(
        modifier = Modifier
            .navigationBarsPadding()
            .fillMaxSize()
            .padding(top = statusBarHeight)
    ) {
        NavGraph(navController, navViewModel, actions)
        //菜单
        MoreFabCompose(navViewModel)
        Column(modifier = Modifier.align(Alignment.BottomEnd)) {
            FabMain(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(Dimen.fabMargin)
            )
        }
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(Dimen.fabIconSize)
                    .align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
fun FabMain(modifier: Modifier = Modifier) {
    val icon = navViewModel.fabMainIcon.observeAsState().value ?: MainIconType.MAIN

    FabCompose(icon, modifier = modifier) {
        when (icon) {
            MainIconType.OK -> navViewModel.fabOKCilck.postValue(true)
            MainIconType.CLOSE -> navViewModel.fabCloseClick.postValue(true)
            MainIconType.MAIN -> navViewModel.fabMainIcon.postValue(MainIconType.DOWN)
            MainIconType.DOWN -> navViewModel.fabMainIcon.postValue(MainIconType.MAIN)
            else -> {
                navViewModel.loading.postValue(false)
                navController.navigateUp()
            }
        }
    }
}

/**
 * 预览
 */
@Composable
fun PreviewBox(themeType: Int = 0, content: @Composable () -> Unit) {
    Column {
        if (themeType == 0 || themeType == 1) {
            PCRToolComposeTheme(darkTheme = false) {
                Surface(
                    content = content
                )
            }
        }
        if (themeType == 0) {
            Spacer(
                modifier = Modifier.height(Dimen.largePadding)
            )
        }
        if (themeType == 0 || themeType == 2) {
            PCRToolComposeTheme(darkTheme = true) {
                Surface(
                    content = content
                )
            }
        }
    }
}
