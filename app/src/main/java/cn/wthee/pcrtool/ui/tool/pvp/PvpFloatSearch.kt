package cn.wthee.pcrtool.ui.tool.pvp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.FixedScale
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.navigation.NavActions
import cn.wthee.pcrtool.ui.MainActivity.Companion.navController
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.SCALE_LOGO
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PCRToolComposeTheme
import kotlinx.coroutines.launch

/**
 * 竞技场查询（悬浮窗）
 */
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
                        R.drawable.ic_launcher_foreground
                    } else {
                        MainIconType.FLOAT_MIN
                    },
                    vibrate = !showResult || !min,
                    iconScale = FixedScale(SCALE_LOGO),
                    onClick = {
                        navViewModel.floatSearchMin.postValue(!min)
                    }
                )
                //退出
                if (!min) {
                    MainSmallFab(
                        iconType = MainIconType.FLOAT_CLOSE,
                        onClick = {
                            navViewModel.floatServiceRun.postValue(false)
                        }
                    )
                }
                if (!min) {
                    MainSmallFab(
                        iconType = if (showResult) MainIconType.CLOSE else MainIconType.PVP_SEARCH,
                        onClick = {
                            if (showResult) {
                                //返回
                                navViewModel.showResult.postValue(false)
                                pvpViewModel.changeRequesting(false)
                            } else {
                                //查询
                                try {
                                    scope.launch {
                                        resultListState.scrollToItem(0)
                                    }
                                } catch (_: Exception) {

                                }
                                pvpViewModel.searchByCharacterList()
                            }
                        }
                    )
                }
            }

            if (!min) {
                MainCard(modifier = Modifier) {
                    PvpSearchScreen(
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