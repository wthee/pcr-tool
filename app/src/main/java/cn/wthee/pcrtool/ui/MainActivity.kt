package cn.wthee.pcrtool.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.paging.ExperimentalPagingApi
import androidx.work.WorkManager
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.ui.MainActivity.Companion.actions
import cn.wthee.pcrtool.ui.MainActivity.Companion.navController
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.MainActivity.Companion.noticeViewModel
import cn.wthee.pcrtool.ui.MainActivity.Companion.r6Ids
import cn.wthee.pcrtool.ui.common.FabCompose
import cn.wthee.pcrtool.ui.home.MoreFabCompose
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PCRToolComposeTheme
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.viewmodel.NoticeViewModel
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * 本地存储：收藏信息
 */
fun mainSP(): SharedPreferences =
    MyApplication.context.getSharedPreferences("main", Context.MODE_PRIVATE)!!

/**
 * 本地存储：版本、设置信息
 */
fun settingSP(): SharedPreferences =
    MyApplication.context.getSharedPreferences("setting", Context.MODE_PRIVATE)!!

fun settingSP(context: Context): SharedPreferences =
    context.getSharedPreferences("setting", Context.MODE_PRIVATE)!!


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        lateinit var handler: Handler
        lateinit var navViewModel: NavViewModel
        lateinit var noticeViewModel: NoticeViewModel
        lateinit var actions: NavActions
        var mFloatingWindowHeight = 0

        @SuppressLint("StaticFieldLeak")
        lateinit var navController: NavHostController
        var vibrateOn = true
        var animOn = true
        var r6Ids = listOf<Int>()
    }


    @ExperimentalAnimationApi
    @ExperimentalComposeUiApi
    @ExperimentalPagingApi
    @ExperimentalMaterialApi
    @ExperimentalPagerApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            PCRToolComposeTheme {
                ProvideWindowInsets {
                    //状态栏、导航栏适配
                    val ui = rememberSystemUiController()
                    val isLight = !isSystemInDarkTheme()
                    ui.setNavigationBarColor(
                        colorResource(id = if (isLight) R.color.alpha_white else R.color.alpha_black),
                        darkIcons = isLight
                    )
                    ui.setStatusBarColor(MaterialTheme.colorScheme.background, darkIcons = isLight)
                    Surface(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                        Home()
                    }
                }
            }
        }
        ActivityHelper.instance.currentActivity = this
        //设置 handler
        setHandler()
        UMengInitializer().create(this)
        val sp = settingSP()
        vibrateOn = sp.getBoolean(Constants.SP_VIBRATE_STATE, true)
        animOn = sp.getBoolean(Constants.SP_ANIM_STATE, true)
        MainScope().launch {
            DatabaseUpdater.checkDBVersion()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        WorkManager.getInstance(MyApplication.context).cancelAllWork()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        val width = ScreenUtil.getWidth()
        val height = ScreenUtil.getHeight()
        mFloatingWindowHeight = if (width > height) height else width
        super.onConfigurationChanged(newConfig)
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
            try {
                navController.popBackStack()
                viewModelStore.clear()
                recreate()
            } catch (e: Exception) {
                UMengLogUtil.upload(e, Constants.EXCEPTION_DATA_CHANGE)
            }
            return@Callback true
        })
    }
}


@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalPagingApi
@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalFoundationApi
@Composable
fun Home(
    mNavViewModel: NavViewModel = hiltViewModel(),
    mNoticeViewModel: NoticeViewModel = hiltViewModel()
) {
    navController = rememberAnimatedNavController()
    actions = remember(navController) { NavActions(navController) }
    navViewModel = mNavViewModel
    noticeViewModel = mNoticeViewModel

    LaunchedEffect({}) {
        noticeViewModel.check()
    }
    val loading = navViewModel.loading.observeAsState().value ?: false
    val r6IdList = navViewModel.r6Ids.observeAsState()

    if (r6IdList.value != null) {
        r6Ids = r6IdList.value!!
    }
    val statusBarHeight =
        rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars
        ).calculateTopPadding()

    Box(
        modifier = Modifier
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
                    .align(Alignment.Center)
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
                navController.navigateUp()
                navViewModel.loading.postValue(false)
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
                    modifier = Modifier.background(MaterialTheme.colorScheme.background),
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
                    modifier = Modifier.background(MaterialTheme.colorScheme.background),
                    content = content
                )
            }
        }
    }
}
