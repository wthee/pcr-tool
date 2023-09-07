package cn.wthee.pcrtool.ui

import android.os.Build
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.SettingSwitchType
import cn.wthee.pcrtool.navigation.NavActions
import cn.wthee.pcrtool.navigation.NavGraph
import cn.wthee.pcrtool.navigation.NavRoute
import cn.wthee.pcrtool.navigation.NavViewModel
import cn.wthee.pcrtool.ui.components.CircularProgressCompose
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PCRToolComposeTheme
import cn.wthee.pcrtool.ui.tool.SettingCommonItem
import cn.wthee.pcrtool.ui.tool.SettingSwitchCompose
import cn.wthee.pcrtool.utils.VibrateUtil
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController

/**
 * 应用
 */
@Composable
fun PCRToolApp(){
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


@OptIn(
    ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class,
    ExperimentalMaterialApi::class
)
@Composable
private fun Home(
    mNavViewModel: NavViewModel = hiltViewModel()
) {
    //bottom sheet 导航
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    MainActivity.navSheetState = sheetState
    val bottomSheetNavigator = remember(sheetState) {
        BottomSheetNavigator(sheetState)
    }
    MainActivity.navController = rememberNavController(bottomSheetNavigator)

    val actions = remember(MainActivity.navController) { NavActions(MainActivity.navController) }
    MainActivity.navViewModel = mNavViewModel

    val loading = MainActivity.navViewModel.loading.observeAsState().value ?: false
    val r6IdList = MainActivity.navViewModel.r6Ids.observeAsState()

    if (r6IdList.value != null) {
        MainActivity.r6Ids = r6IdList.value!!
    }

    //首页使用bottomsheet时，关闭时主按钮初始
    LaunchedEffect(MainActivity.navSheetState.isVisible) {
        if (MainActivity.navController.currentDestination?.route == NavRoute.HOME) {
            MainActivity.navViewModel.fabMainIcon.postValue(MainIconType.MAIN)
        }
    }

    Box(
        modifier = Modifier
            .navigationBarsPadding()
            .fillMaxSize()
    ) {
        //页面导航
        NavGraph(bottomSheetNavigator, MainActivity.navController, MainActivity.navViewModel, actions)
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
    val icon = MainActivity.navViewModel.fabMainIcon.observeAsState().value ?: MainIconType.MAIN

    MainSmallFab(
        if (icon == MainIconType.MAIN) {
            MainIconType.SETTING
        } else {
            icon
        }, modifier = modifier
    ) {
        when (icon) {
            MainIconType.OK -> MainActivity.navViewModel.fabOKClick.postValue(true)
            MainIconType.CLOSE -> MainActivity.navViewModel.fabCloseClick.postValue(true)
            MainIconType.MAIN -> MainActivity.navViewModel.fabMainIcon.postValue(MainIconType.DOWN)
            MainIconType.DOWN -> MainActivity.navViewModel.fabMainIcon.postValue(MainIconType.MAIN)
            else -> {
                MainActivity.navViewModel.loading.postValue(false)
                MainActivity.navController.navigateUp()
            }
        }
    }
}

/**
 * 设置页面
 */
@Composable
private fun SettingDropMenu(actions: NavActions) {
    val fabMainIcon = MainActivity.navViewModel.fabMainIcon.observeAsState().value ?: MainIconType.OK
    val context = LocalContext.current

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
                MainActivity.navViewModel.fabMainIcon.postValue(MainIconType.MAIN)
            },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer),
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
                        summary = stringResource(id = R.string.app_name),
                        titleColor = MaterialTheme.colorScheme.primary,
                        summaryColor = MaterialTheme.colorScheme.onSurface,
                        padding = Dimen.smallPadding,
                        tintColor = MaterialTheme.colorScheme.primary,
                        onClick = {
                            actions.toSetting()
                        }
                    ) {
                        MainIcon(data = MainIconType.MORE, size = Dimen.fabIconSize)
                    }
                },
                onClick = {}
            )
        }
    }
}