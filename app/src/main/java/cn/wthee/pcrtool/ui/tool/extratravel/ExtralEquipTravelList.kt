package cn.wthee.pcrtool.ui.tool.extratravel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.ExtraEquipQuestData
import cn.wthee.pcrtool.data.db.view.ExtraEquipTravelData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.components.CenterTipText
import cn.wthee.pcrtool.ui.components.CommonGroupTitle
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.CommonTitleContentText
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.Subtitle1
import cn.wthee.pcrtool.ui.components.VerticalGrid
import cn.wthee.pcrtool.ui.components.getItemWidth
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.ImageRequestHelper.Companion.ICON_EXTRA_EQUIPMENT_TRAVEL_MAP
import cn.wthee.pcrtool.utils.toTimeText
import cn.wthee.pcrtool.viewmodel.ExtraEquipmentViewModel
import kotlinx.coroutines.launch

/**
 * ex冒险区域
 */
@Composable
fun ExtraEquipTravelList(
    scrollState: LazyListState,
    toExtraEquipTravelAreaDetail: (Int) -> Unit,
    extraEquipmentViewModel: ExtraEquipmentViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()

    val areaListFlow = remember {
        extraEquipmentViewModel.getTravelAreaList()
    }
    val areaList by areaListFlow.collectAsState(initial = null)


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        if (areaList?.isNotEmpty() == true) {
            LazyColumn(state = scrollState) {
                items(areaList!!) {
                    TravelItem(it, toExtraEquipTravelAreaDetail)
                }
                item {
                    CommonSpacer()
                }
            }
        } else {
            if (areaList == null) {
                //功能未实装
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CenterTipText(
                        stringResource(id = R.string.not_installed)
                    )
                }
            } else {
                CenterTipText(
                    stringResource(id = R.string.no_data)
                )
            }
        }

        //回到顶部
        MainSmallFab(
            iconType = MainIconType.EXTRA_EQUIP_DROP,
            text = stringResource(id = R.string.tool_travel),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
        ) {
            scope.launch {
                try {
                    scrollState.scrollToItem(0)
                } catch (_: Exception) {
                }
            }
        }
    }
}

/**
 * 冒险区域item
 */
@Composable
private fun TravelItem(
    travelData: ExtraEquipTravelData,
    toExtraEquipTravelAreaDetail: (Int) -> Unit
) {

    //area
    CommonGroupTitle(
        iconData = ImageRequestHelper.getInstance()
            .getUrl(ICON_EXTRA_EQUIPMENT_TRAVEL_MAP, travelData.travelAreaId),
        iconSize = Dimen.menuIconSize,
        titleStart = travelData.travelAreaName,
        titleEnd = travelData.questCount.toString(),
        modifier = Modifier.padding(Dimen.mediumPadding)
    )

    //quest列表
    VerticalGrid(
        itemWidth = getItemWidth() / 2,
        contentPadding = Dimen.largePadding,
        modifier = Modifier.padding(
            start = Dimen.commonItemPadding,
            end = Dimen.commonItemPadding
        ),
    ) {
        travelData.questList.forEachIndexed { _, questData ->
            MainCard(modifier = Modifier.padding(Dimen.mediumPadding),
                onClick = {
                    toExtraEquipTravelAreaDetail(questData.travelQuestId)
                }
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    TravelQuestHeader(
                        questData = questData
                    )
                }
            }
        }
    }
}

/**
 * ex冒险区域公用头部布局
 * @param showTitle 查看掉落列表时，不显示标题
 */
@Composable
fun TravelQuestHeader(
    questData: ExtraEquipQuestData,
    showTitle: Boolean = true
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .padding(vertical = Dimen.mediumPadding)
    ) {
        //图标
        MainIcon(
            data = ImageRequestHelper.getInstance()
                .getUrl(ICON_EXTRA_EQUIPMENT_TRAVEL_MAP, questData.travelQuestId),
        )
        //标题
        if (showTitle) {
            Subtitle1(text = questData.getQuestName())
        }
        //其它参数
        VerticalGrid(
            contentPadding = Dimen.smallPadding,
            itemWidth = getItemWidth() / 2
        ) {
            CommonTitleContentText(
                stringResource(id = R.string.travel_limit_unit_num),
                questData.limitUnitNum.toString()
            )
            CommonTitleContentText(
                stringResource(id = R.string.travel_need_power),
                stringResource(id = R.string.value_10_k, questData.needPower / 10000)
            )
            CommonTitleContentText(
                stringResource(id = R.string.travel_time),
                toTimeText(questData.travelTime * 1000)
            )
            CommonTitleContentText(
                stringResource(id = R.string.travel_time_decrease_limit),
                toTimeText(questData.travelTimeDecreaseLimit * 1000)
            )
        }
    }
}

@CombinedPreviews
@Composable
private fun TravelQuestHeaderPreview() {
    PreviewLayout {
        TravelQuestHeader(
            questData = ExtraEquipQuestData(
                0, 0, "?", 10, 10000, 500, 1, 1000, 1
            )
        )
    }
}

