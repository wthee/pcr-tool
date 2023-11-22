package cn.wthee.pcrtool.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import cn.wthee.pcrtool.navigation.NavActions
import cn.wthee.pcrtool.navigation.NavGraph
import cn.wthee.pcrtool.navigation.NavViewModel
import cn.wthee.pcrtool.ui.components.CircularProgressCompose
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.theme.PCRToolComposeTheme
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController

/**
 * 应用
 */
@Composable
fun PCRToolApp() {
    PCRToolComposeTheme {
        //状态栏、导航栏适配
        val ui = rememberSystemUiController()
        val isLight = !isSystemInDarkTheme()
        ui.setNavigationBarColor(
            color = MaterialTheme.colorScheme.surface,
            darkIcons = isLight
        )
        ui.setStatusBarColor(
            color = Color.Transparent,
            darkIcons = isLight
        )

        Home()
    }
}


@OptIn(
    ExperimentalMaterialNavigationApi::class,
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
