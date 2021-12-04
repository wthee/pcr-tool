package cn.wthee.pcrtool.ui.tool.pvp

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.FabMain
import cn.wthee.pcrtool.ui.MainActivity.Companion.actions
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.common.FabCompose
import cn.wthee.pcrtool.ui.common.MainCard
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PCRToolComposeTheme
import cn.wthee.pcrtool.viewmodel.PvpViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalPagerApi
@Composable
fun PvpFloatSearch(spanCount: Int, pvpViewModel: PvpViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val min = navViewModel.floatSearchMin.observeAsState().value ?: false
    val showResult = navViewModel.showResult.observeAsState().value ?: false
    val pagerState = rememberPagerState()
    val selectListState = rememberLazyListState()
    val resultListState = rememberLazyListState()
    val favoritesListState = rememberLazyListState()
    val historyListState = rememberLazyListState()


    PCRToolComposeTheme {
        Row(modifier = Modifier.padding(Dimen.mediumPadding)) {
            Column {
                //最大/小化
                FabCompose(
                    iconType = if (min) {
                        R.mipmap.ic_logo
                    } else {
                        MainIconType.FLOAT_MIN
                    }
                ) {
                    navViewModel.floatSearchMin.postValue(!min)
                }
                //退出
                if (!min) {
                    FabCompose(
                        iconType = MainIconType.FLOAT_CLOSE
                    ) {
                        navViewModel.floatServiceRun.postValue(false)
                    }
                }
                //查询
                if (!min && !showResult) {
                    FabCompose(
                        iconType = MainIconType.PVP_SEARCH
                    ) {
                        coroutineScope.launch {
                            try {
                                resultListState.scrollToItem(0)
                            } catch (ignore: Exception) {

                            }
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
            val modifier = if (min) {
                Modifier.size(0.dp)
            } else {
                Modifier
            }

            MainCard(modifier = modifier) {
                PvpSearchCompose(
                    floatWindow = true,
                    initSpanCount = spanCount,
                    pagerState = pagerState,
                    selectListState = selectListState,
                    resultListState = resultListState,
                    favoritesListState = favoritesListState,
                    historyListState = historyListState,
                    toCharacter = actions.toCharacterDetail
                )
            }
        }
    }
}