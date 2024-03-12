package cn.wthee.pcrtool.ui

import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.navigation.BottomSheetNavigator
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import cn.wthee.pcrtool.navigation.NavActions
import cn.wthee.pcrtool.navigation.NavGraph
import cn.wthee.pcrtool.navigation.NavViewModel
import cn.wthee.pcrtool.ui.components.AppResumeEffect
import cn.wthee.pcrtool.ui.components.CircularProgressCompose
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.theme.PCRToolComposeTheme

/**
 * 应用
 */
@Composable
fun PCRToolApp(
    navViewModel: NavViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel(),
) {
    AppResumeEffect {
        mainViewModel.loadSetting()
    }

    PCRToolComposeTheme {
        //bottom sheet
        val sheetState = rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true
        )
        MainActivity.navSheetState = sheetState
        //bottom sheet 导航  fixme 未使用 material3
        val bottomSheetNavigator = remember {
            BottomSheetNavigator(sheetState)
        }
        val navController = rememberNavController(bottomSheetNavigator)
        MainActivity.navController = navController

        val actions =
            remember(MainActivity.navController) { NavActions(MainActivity.navController) }
        MainActivity.navViewModel = navViewModel

        val loading = MainActivity.navViewModel.loading.observeAsState().value ?: false


        MainScaffold(
            hideMainFab = true
        ) {
            //页面导航
            NavGraph(
                bottomSheetNavigator,
                MainActivity.navController,
                actions
            )

            if (loading) {
                CircularProgressCompose(Modifier.align(Alignment.Center))
            }
        }
    }
}