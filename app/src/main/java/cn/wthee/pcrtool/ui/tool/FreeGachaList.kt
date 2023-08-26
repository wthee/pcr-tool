package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.FreeGachaInfo
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.components.*
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.fixJpTime
import cn.wthee.pcrtool.viewmodel.EventViewModel
import kotlinx.coroutines.launch

/**
 * 免费十连页面
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FreeGachaList(
    scrollState: LazyStaggeredGridState,
    eventViewModel: EventViewModel = hiltViewModel()
) {
    val gachaList =
        eventViewModel.getFreeGachaHistory().collectAsState(initial = arrayListOf()).value
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        if (gachaList.isNotEmpty()) {
            LazyVerticalStaggeredGrid(
                state = scrollState,
                columns = StaggeredGridCells.Adaptive(getItemWidth()),
            ) {
                items(
                    items = gachaList,
                    key = {
                        it.id
                    }
                ) {
                    FreeGachaItem(it)
                }
                item {
                    CommonSpacer()
                }
            }
        }
        //回到顶部
        MainSmallFab(
            iconType = MainIconType.FREE_GACHA,
            text = stringResource(id = R.string.tool_free_gacha),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
        ) {
            coroutineScope.launch {
                try {
                    scrollState.scrollToItem(0)
                } catch (_: Exception) {
                }
            }
        }
    }


}

/**
 * 免费十连
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FreeGachaItem(freeGachaInfo: FreeGachaInfo) {

    Column(
        modifier = Modifier.padding(
            horizontal = Dimen.largePadding,
            vertical = Dimen.mediumPadding
        )
    ) {
        //标题
        FlowRow(
            modifier = Modifier.padding(bottom = Dimen.mediumPadding),
            verticalArrangement = Arrangement.Center
        ) {
            EventTitle(startTime = freeGachaInfo.startTime, endTime = freeGachaInfo.endTime)
        }

        MainCard {
            Column(modifier = Modifier.padding(bottom = Dimen.mediumPadding)) {
                //描述
                MainContentText(
                    text = freeGachaInfo.getDescComposable(),
                    modifier = Modifier.padding(
                        top = Dimen.mediumPadding,
                        start = Dimen.mediumPadding,
                        end = Dimen.mediumPadding
                    ),
                    textAlign = TextAlign.Start
                )

                //结束日期
                CaptionText(
                    text = freeGachaInfo.endTime.fixJpTime,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = Dimen.mediumPadding)

                )
            }
        }
    }

}


@CombinedPreviews
@Composable
private fun FreeGachaItemPreview() {
    PreviewLayout {
        FreeGachaItem(FreeGachaInfo(maxCount = 14))
    }
}