package cn.wthee.pcrtool.ui.tool.pvp

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.navigation.NavActions
import cn.wthee.pcrtool.ui.FabMain
import cn.wthee.pcrtool.ui.MainActivity.Companion.navController
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PCRToolComposeTheme
import cn.wthee.pcrtool.viewmodel.PvpViewModel
import kotlinx.coroutines.launch

/**
 * 竞技场查询（悬浮窗）
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PvpFloatSearch(spanCount: Int, pvpViewModel: PvpViewModel = hiltViewModel()) {
    val scope = rememberCoroutineScope()
    val min = navViewModel.floatSearchMin.observeAsState().value ?: false
    val showResult = navViewModel.showResult.observeAsState().value ?: false
    val pagerState = rememberPagerState { 4 }
    val selectListState = rememberLazyGridState()
    val usedListState = rememberLazyGridState()
    val resultListState = rememberLazyGridState()
    val favoritesListState = rememberLazyGridState()
    val historyListState = rememberLazyGridState()
    val actions = NavActions(navController)

    PCRToolComposeTheme {
        Row(modifier = Modifier.padding(Dimen.mediumPadding)) {
            Column {
                //最大/小化
                MainSmallFab(
                    iconType = if (min) {
                        R.drawable.ic_logo_large
                    } else {
                        MainIconType.FLOAT_MIN
                    },
                    vibrate = !showResult || !min
                ) {
                    navViewModel.floatSearchMin.postValue(!min)
                }
                //退出
                if (!min) {
                    MainSmallFab(
                        iconType = MainIconType.FLOAT_CLOSE
                    ) {
                        navViewModel.floatServiceRun.postValue(false)
                    }
                }
                //查询
                if (!min && !showResult) {
                    MainSmallFab(
                        iconType = MainIconType.PVP_SEARCH
                    ) {
                        try {
                            scope.launch {
                                resultListState.scrollToItem(0)
                            }
                        } catch (_: Exception) {

                        }
                        pvpViewModel.pvpResult.postValue(null)
                        navViewModel.showResult.postValue(true)
                    }
                }
                //返回
                if (!min && showResult) {
                    FabMain()
                }
            }

            if (!min) {
                MainCard(modifier = Modifier) {
                    PvpSearchCompose(
                        floatWindow = true,
                        initSpanCount = spanCount,
                        pagerState = pagerState,
                        selectListState = selectListState,
                        usedListState = usedListState,
                        resultListState = resultListState,
                        favoritesListState = favoritesListState,
                        historyListState = historyListState,
                        toCharacter = actions.toCharacterDetail
                    )
                }
            }

        }
    }
}