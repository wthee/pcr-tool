package cn.wthee.pcrtool.ui.tool.gacha

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.GachaInfo
import cn.wthee.pcrtool.data.enums.GachaType
import cn.wthee.pcrtool.data.enums.IconResourceType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.MockGachaType
import cn.wthee.pcrtool.navigation.navigateUp
import cn.wthee.pcrtool.ui.components.CaptionText
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.DateRangePickerCompose
import cn.wthee.pcrtool.ui.components.EventTitle
import cn.wthee.pcrtool.ui.components.GridIconList
import cn.wthee.pcrtool.ui.components.IconTextButton
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.StateBox
import cn.wthee.pcrtool.ui.components.Subtitle1
import cn.wthee.pcrtool.ui.components.getDatePickerYearRange
import cn.wthee.pcrtool.ui.components.getItemWidth
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.colorGold
import cn.wthee.pcrtool.ui.theme.colorGreen
import cn.wthee.pcrtool.ui.theme.colorOrange
import cn.wthee.pcrtool.ui.theme.colorRed
import cn.wthee.pcrtool.utils.fixJpTime
import cn.wthee.pcrtool.utils.formatTime
import cn.wthee.pcrtool.utils.intArrayList
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * 角色卡池页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GachaListScreen(
    toCharacterDetail: (Int) -> Unit,
    toMockGachaFromList: (Int, String) -> Unit,
    gachaListViewModel: GachaListViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val uiState by gachaListViewModel.uiState.collectAsStateWithLifecycle()
    val dateRangePickerState = rememberDateRangePickerState(yearRange = getDatePickerYearRange())
    val scrollState = rememberLazyStaggeredGridState()


    MainScaffold(
        enableClickClose = uiState.openDialog,
        onCloseClick = {
            gachaListViewModel.changeDialog(false)
        },
        secondLineFab = {
            //日期选择
            DateRangePickerCompose(
                dateRangePickerState = dateRangePickerState,
                dateRange = uiState.dateRange,
                openDialog = uiState.openDialog,
                changeRange = gachaListViewModel::changeRange,
                changeDialog = gachaListViewModel::changeDialog
            )
        },
        fab = {
            //重置
            if (uiState.dateRange.hasFilter()) {
                MainSmallFab(iconType = MainIconType.RESET) {
                    gachaListViewModel.reset()
                    dateRangePickerState.setSelection(null, null)
                }
            }

            //回到顶部
            MainSmallFab(
                iconType = MainIconType.GACHA,
                text = stringResource(id = R.string.tool_gacha),
            ) {
                coroutineScope.launch {
                    try {
                        scrollState.scrollToItem(0)
                    } catch (_: Exception) {
                    }
                }
            }
        },
        mainFabIcon = if (uiState.openDialog) MainIconType.CLOSE else MainIconType.BACK,
        onMainFabClick = {
            if (uiState.openDialog) {
                gachaListViewModel.changeDialog(false)
            } else {
                navigateUp()
            }
        }
    ) {
        StateBox(stateType = uiState.loadingState) {
            GachaListContent(
                scrollState = scrollState,
                gachaList = uiState.gachaList!!,
                fesUnitIdList = uiState.fesUnitIdList,
                toCharacterDetail = toCharacterDetail,
                toMockGachaFromList = toMockGachaFromList
            )
        }

    }

}


@Composable
private fun GachaListContent(
    scrollState: LazyStaggeredGridState,
    gachaList: List<GachaInfo>,
    fesUnitIdList: List<Int>,
    toCharacterDetail: (Int) -> Unit,
    toMockGachaFromList: (Int, String) -> Unit,
) {
    LazyVerticalStaggeredGrid(
        state = scrollState,
        columns = StaggeredGridCells.Adaptive(getItemWidth())
    ) {
        items(
            items = gachaList,
            key = {
                it.gachaId
            }
        ) {
            GachaItem(
                gachaInfo = it,
                fesUnitIdList = fesUnitIdList,
                toCharacterDetail = toCharacterDetail,
                toMockGachaFromList = toMockGachaFromList
            )
        }
        items(2) {
            CommonSpacer()
        }
    }
}

/**
 * 单个卡池
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GachaItem(
    gachaInfo: GachaInfo,
    fesUnitIdList: List<Int>,
    toCharacterDetail: (Int) -> Unit,
    toMockGachaFromList: (Int, String) -> Unit,
) {
    val idList = gachaInfo.unitIds.intArrayList
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
        idList.find { !fesUnitIdList.contains(it) } != null && idList.find {
            fesUnitIdList.contains(
                it
            )
        } != null
    val mockGachaType = when (type) {
        GachaType.FES -> MockGachaType.FES
        GachaType.RE_LIMIT_PICK -> MockGachaType.PICK_UP_SINGLE
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
            verticalArrangement = Arrangement.Center
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
            //图标/描述
            if (idList.isEmpty()) {
                Subtitle1(
                    text = gachaInfo.getDesc(),
                    modifier = Modifier.padding(
                        top = Dimen.mediumPadding,
                        start = Dimen.mediumPadding,
                        end = Dimen.mediumPadding
                    )
                )
            } else {
                GridIconList(
                    idList = idList,
                    iconResourceType = IconResourceType.CHARACTER,
                    onClickItem = toCharacterDetail
                )
            }

            Row(
                modifier = Modifier
                    .padding(
                        start = Dimen.smallPadding,
                        end = Dimen.mediumPadding,
                        bottom = Dimen.smallPadding
                    )
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                //模拟抽卡 fixme 暂不支持普通和fes角色混合池
                if (!isMixedGachaPool) {
                    IconTextButton(
                        icon = MainIconType.MOCK_GACHA,
                        text = stringResource(R.string.tool_mock_gacha)
                    ) {
                        //跳转
                        toMockGachaFromList(
                            mockGachaType.type,
                            Json.encodeToString(gachaInfo.getMockGachaPickUpUnitList())
                        )
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


@CombinedPreviews
@Composable
private fun GachaItemPreview() {
    PreviewLayout {
        GachaItem(
            gachaInfo = GachaInfo(),
            toCharacterDetail = {},
            toMockGachaFromList = { _, _ -> },
            fesUnitIdList = arrayListOf()
        )
    }
}