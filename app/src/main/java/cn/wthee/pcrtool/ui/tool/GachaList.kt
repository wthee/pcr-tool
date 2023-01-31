package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.GachaInfo
import cn.wthee.pcrtool.data.enums.GachaType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.*
import cn.wthee.pcrtool.ui.tool.mockgacha.MockGachaType
import cn.wthee.pcrtool.utils.fixJpTime
import cn.wthee.pcrtool.utils.formatTime
import cn.wthee.pcrtool.utils.intArrayList
import cn.wthee.pcrtool.viewmodel.GachaViewModel
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.launch

/**
 * 角色卡池页面
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GachaList(
    scrollState: LazyStaggeredGridState,
    toCharacterDetail: (Int) -> Unit,
    toMockGacha: () -> Unit,
    gachaViewModel: GachaViewModel = hiltViewModel()
) {
    val dateRange = remember {
        mutableStateOf(DateRange())
    }
    val gachaList = gachaViewModel.getGachaHistory(dateRange.value)
        .collectAsState(initial = arrayListOf()).value
    val fesUnitIds =
        gachaViewModel.getGachaFesUnitList().collectAsState(initial = arrayListOf()).value
    val coroutineScope = rememberCoroutineScope()


    Box(modifier = Modifier.fillMaxSize()) {
        if (gachaList.isNotEmpty()) {
            LazyVerticalStaggeredGrid(
                state = scrollState, columns = StaggeredGridCells.Adaptive(getItemWidth())
            ) {
                items(
                    items = gachaList,
                    key = {
                        it.gachaId
                    }
                ) {
                    GachaItem(
                        gachaInfo = it,
                        fesUnitIds = fesUnitIds,
                        toCharacterDetail = toCharacterDetail,
                        toMockGacha = toMockGacha
                    )
                }
                item {
                    CommonSpacer()
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = Dimen.fabMarginEnd,
                    bottom = Dimen.fabMargin
                )
        ) {
            //日期选择
            DateRangePickerCompose(dateRange = dateRange)

            //回到顶部
            FabCompose(
                iconType = MainIconType.GACHA,
                text = stringResource(id = R.string.tool_gacha)
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


}

/**
 * 单个卡池
 */
@Composable
fun GachaItem(
    gachaInfo: GachaInfo,
    fesUnitIds: List<Int>,
    toCharacterDetail: (Int) -> Unit,
    toMockGacha: () -> Unit
) {
    val icons = gachaInfo.unitIds.intArrayList
    val type = gachaInfo.getType()
    val color = when (type) {
        GachaType.LIMIT, GachaType.NORMAL -> colorRed
        GachaType.RE_LIMIT, GachaType.RE_NORMAL, GachaType.RE_LIMIT_PICK -> colorGold
        GachaType.FES -> colorGreen
        GachaType.ANNIV -> colorOrange
        GachaType.UNKNOWN -> MaterialTheme.colorScheme.primary
    }
    //是否普通角色、fes混合卡池
    val isMixedGachaPool =
        icons.find { !fesUnitIds.contains(it) } != null && icons.find { fesUnitIds.contains(it) } != null
    val mockGachaType = when {
        icons.find { !fesUnitIds.contains(it) } == null -> MockGachaType.FES
        icons.size >= 6 -> MockGachaType.PICK_UP_SINGLE
        else -> MockGachaType.PICK_UP
    }

    Column(
        modifier = Modifier.padding(
            horizontal = Dimen.largePadding,
            vertical = Dimen.mediumPadding
        )
    ) {
        //标题
        FlowRow(
            modifier = Modifier.padding(bottom = Dimen.mediumPadding),
            crossAxisAlignment = FlowCrossAxisAlignment.Center
        ) {
            //类型
            MainTitleText(
                text = if (type.stringId != R.string.unknown) {
                    stringResource(id = type.stringId)
                } else {
                    gachaInfo.fixTypeName()
                },
                backgroundColor = color,
                modifier = Modifier.padding(end = Dimen.smallPadding)
            )
            EventTitle(startTime = gachaInfo.startTime, endTime = gachaInfo.endTime)
        }

        MainCard {
            Column(modifier = Modifier.padding(bottom = Dimen.smallPadding)) {
                //图标/描述
                if (icons.isEmpty()) {
                    MainContentText(
                        text = gachaInfo.getDesc(),
                        modifier = Modifier.padding(
                            top = Dimen.mediumPadding,
                            start = Dimen.mediumPadding,
                            end = Dimen.mediumPadding
                        ),
                        textAlign = TextAlign.Start
                    )
                } else {
                    GridIconListCompose(
                        icons = icons,
                        onClickItem = toCharacterDetail
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(start = Dimen.smallPadding, end = Dimen.mediumPadding)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    //模拟抽卡 fixme 暂不支持普通和fes角色混合池
                    if (!isMixedGachaPool) {
                        IconTextButton(
                            icon = MainIconType.MOCK_GACHA,
                            text = stringResource(R.string.tool_mock_gacha)
                        ) {
                            navViewModel.mockGachaType.postValue(mockGachaType)
                            navViewModel.pickUpList.postValue(gachaInfo.getMockGachaUnitList())
                            //跳转
                            toMockGacha()
                        }
                    }
                    //结束日期
                    CaptionText(
                        text = gachaInfo.endTime.formatTime.fixJpTime,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

}

@CombinedPreviews
@Composable
private fun GachaItemPreview() {
    PreviewLayout {
        GachaItem(
            gachaInfo = GachaInfo(),
            toCharacterDetail = {},
            toMockGacha = {},
            fesUnitIds = arrayListOf()
        )
    }
}