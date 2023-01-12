package cn.wthee.pcrtool.ui

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.work.WorkManager
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.MyApplication.Companion.context
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.SettingSwitchType
import cn.wthee.pcrtool.database.AppDatabaseCN
import cn.wthee.pcrtool.database.AppDatabaseJP
import cn.wthee.pcrtool.database.AppDatabaseTW
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.ui.MainActivity.Companion.navController
import cn.wthee.pcrtool.ui.MainActivity.Companion.navSheetState
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.MainActivity.Companion.r6Ids
import cn.wthee.pcrtool.ui.common.CircularProgressCompose
import cn.wthee.pcrtool.ui.common.FabCompose
import cn.wthee.pcrtool.ui.common.IconCompose
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PCRToolComposeTheme
import cn.wthee.pcrtool.ui.tool.SettingCommonItem
import cn.wthee.pcrtool.ui.tool.SettingSwitchCompose
import cn.wthee.pcrtool.ui.tool.pvp.PvpFloatService
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.viewmodel.NoticeViewModel
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
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
fun settingSP(mContext: Context = context): SharedPreferences =
    mContext.getSharedPreferences("setting", Context.MODE_PRIVATE)!!


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        lateinit var handler: Handler

        lateinit var navViewModel: NavViewModel

        @OptIn(ExperimentalMaterialApi::class)
        lateinit var navSheetState: ModalBottomSheetState

        @SuppressLint("StaticFieldLeak")
        lateinit var navController: NavHostController

        var vibrateOnFlag = true
        var animOnFlag = true
        var dynamicColorOnFlag = true
        var r6Ids = listOf<Int>()
        var regionType = 2
    }

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
                ui.setStatusBarColor(Color.Transparent, darkIcons = isLight)
                Home()
            }
        }

        ActivityHelper.instance.currentActivity = this
        //设置 handler
        setHandler()
        //用户设置信息
        val sp = settingSP()
        vibrateOnFlag = sp.getBoolean(Constants.SP_VIBRATE_STATE, true)
        animOnFlag = sp.getBoolean(Constants.SP_ANIM_STATE, true)
        dynamicColorOnFlag = sp.getBoolean(Constants.SP_COLOR_STATE, true)
        regionType = sp.getInt(Constants.SP_DATABASE_TYPE, 2)
        //校验数据库版本
        MainScope().launch {
            DatabaseUpdater.checkDBVersion()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(context, PvpFloatService::class.java))
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

    /**
     * 刷新页面
     * what：0切换数据，1动态色彩，2、3、4数据更新
     */
    @SuppressLint("RestrictedApi")
    private fun setHandler() {
        //接收消息
        handler = Handler(Looper.getMainLooper(), Handler.Callback {
            try {
                //关闭其他数据库连接
                AppDatabaseCN.close()
                AppDatabaseTW.close()
                AppDatabaseJP.close()
                //重启应用
                val intent = Intent(this, MainActivity::class.java)
                finish()
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                //数据下载完成，振动提示
                if (it.what > 1) {
                    VibrateUtil(this).done()
                }
            } catch (e: Exception) {
                LogReportUtil.upload(e, Constants.EXCEPTION_DATA_CHANGE)
                ToastUtil.short(getString(R.string.change_failed))
            }
            return@Callback true
        })
    }
}


@OptIn(
    ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun Home(
    mNavViewModel: NavViewModel = hiltViewModel()
) {
    //bottom sheet 导航
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    navSheetState = sheetState
    val bottomSheetNavigator = remember(sheetState) {
        BottomSheetNavigator(sheetState)
    }
    navController = rememberAnimatedNavController(bottomSheetNavigator)

    val actions = remember(navController) { NavActions(navController) }
    navViewModel = mNavViewModel

    val loading = navViewModel.loading.observeAsState().value ?: false
    val r6IdList = navViewModel.r6Ids.observeAsState()

    if (r6IdList.value != null) {
        r6Ids = r6IdList.value!!
    }


    LaunchedEffect(navSheetState.currentValue) {
        if (navController.currentDestination?.route == Navigation.HOME) {
            navViewModel.fabMainIcon.postValue(MainIconType.MAIN)
        }
    }

    Box(
        modifier = Modifier
            .navigationBarsPadding()
            .fillMaxSize()
    ) {
        //页面导航
        NavGraph(bottomSheetNavigator, navController, navViewModel, actions)
        Column(modifier = Modifier.align(Alignment.BottomEnd)) {
            //菜单
            SettingDropMenu(actions)
            //Home 按钮
            FabMain(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(Dimen.fabMargin)
            )
        }
        if (loading) {
            CircularProgressCompose(Modifier.align(Alignment.Center))
        }
    }
}

@Composable
fun FabMain(modifier: Modifier = Modifier) {
    val icon = navViewModel.fabMainIcon.observeAsState().value ?: MainIconType.MAIN

    FabCompose(
        if (icon == MainIconType.MAIN) {
            MainIconType.SETTING
        } else {
            icon
        }, modifier = modifier
    ) {
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
 * 设置页面
 */
@Composable
private fun SettingDropMenu(actions: NavActions, viewModel: NoticeViewModel = hiltViewModel()) {
    val fabMainIcon = navViewModel.fabMainIcon.observeAsState().value ?: MainIconType.OK
    val context = LocalContext.current
    val sp = settingSP()
    val region = MainActivity.regionType
    val updateDb = viewModel.updateDb.observeAsState().value ?: ""
    LaunchedEffect(MainActivity.regionType) {
        viewModel.getDbDiff()
    }

    //数据库版本
    val typeName = getRegionName(region)
    val localVersion = sp.getString(
        when (region) {
            2 -> Constants.SP_DATABASE_VERSION_CN
            3 -> Constants.SP_DATABASE_VERSION_TW
            else -> Constants.SP_DATABASE_VERSION_JP
        },
        ""
    )
    val dbVersionCode = if (localVersion != null) {
        localVersion.split("/")[0]
    } else {
        ""
    }

    //调整圆角
    PCRToolComposeTheme(
        shapes = MaterialTheme.shapes.copy(
            extraSmall = MaterialTheme.shapes.medium
        )
    ) {
        DropdownMenu(
            expanded = fabMainIcon == MainIconType.DOWN,
            onDismissRequest = {
                VibrateUtil(context).single()
                navViewModel.fabMainIcon.postValue(MainIconType.MAIN)
            },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant),
            offset = DpOffset(Dimen.fabMargin, 0.dp),
        ) {
            DropdownMenuItem(
                text = {
                    SettingSwitchCompose(type = SettingSwitchType.VIBRATE, showSummary = false)
                },
                onClick = {}
            )
            DropdownMenuItem(
                text = {
                    SettingSwitchCompose(type = SettingSwitchType.ANIMATION, showSummary = false)
                },
                onClick = {}
            )
            //- 动态色彩，仅 Android 12 及以上可用
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S || BuildConfig.DEBUG) {
                DropdownMenuItem(
                    text = {
                        SettingSwitchCompose(
                            type = SettingSwitchType.DYNAMIC_COLOR,
                            showSummary = false
                        )
                    },
                    onClick = {}
                )
            }
            //应用信息
            DropdownMenuItem(
                text = {
                    SettingCommonItem(
                        iconType = R.drawable.ic_logo_large,
                        iconSize = Dimen.mediumIconSize,
                        title = "v" + BuildConfig.VERSION_NAME,
                        summary = stringResource(
                            id = R.string.db_diff,
                            typeName,
                            dbVersionCode,
                            updateDb
                        ),
                        titleColor = MaterialTheme.colorScheme.primary,
                        summaryColor = MaterialTheme.colorScheme.onSurface,
                        padding = Dimen.smallPadding,
                        onClick = {
                            actions.toSetting()
                        }
                    ) {
                        IconCompose(data = MainIconType.MORE, size = Dimen.fabIconSize)
                    }
                },
                onClick = {}
            )
        }
    }
}