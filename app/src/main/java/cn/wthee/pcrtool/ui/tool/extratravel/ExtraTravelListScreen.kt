package cn.wthee.pcrtool.ui.tool.extratravel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.ExtraEquipQuestData
import cn.wthee.pcrtool.data.db.view.ExtraTravelData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.components.CenterTipText
import cn.wthee.pcrtool.ui.components.CommonGroupTitle
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.CommonTitleContentText
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.StateBox
import cn.wthee.pcrtool.ui.components.Subtitle1
import cn.wthee.pcrtool.ui.components.VerticalStaggeredGrid
import cn.wthee.pcrtool.ui.components.getItemWidth
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.ImageRequestHelper.Companion.ICON_EXTRA_EQUIPMENT_TRAVEL_MAP
import cn.wthee.pcrtool.utils.toTimeText
import kotlinx.coroutines.launch

/**
 * ex冒险区域
 */
@Composable
fun ExtraTravelListScreen(
    toExtraEquipTravelAreaDetail: (Int) -> Unit,
    extraTravelListViewModel: ExtraTravelListViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()
    val uiState by extraTravelListViewModel.uiState.collectAsStateWithLifecycle()

    MainScaffold(
        fab = {
            //回到顶部
            MainSmallFab(
                iconType = MainIconType.EXTRA_EQUIP_DROP,
                text = stringResource(id = R.string.tool_travel),
                onClick = {
                    scope.launch {
                        try {
                            scrollState.scrollToItem(0)
                        } catch (_: Exception) {
                        }
                    }
                }
            )
        }
    ) {
        StateBox(
            stateType = uiState.loadState,
            errorContent = {
                CenterTipText(text = stringResource(R.string.not_installed))
            }
        ) {
            uiState.areaList?.let { areaList ->
                LazyColumn(state = scrollState) {
                    items(areaList) {
                        TravelItem(it, toExtraEquipTravelAreaDetail)
                    }
                    item {
                        CommonSpacer()
                    }
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
    travelData: ExtraTravelData,
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
    VerticalStaggeredGrid(
        itemWidth = getItemWidth() / 2,
        contentPadding = Dimen.mediumPadding,
        modifier = Modifier.padding(horizontal = Dimen.commonItemPadding),
    ) {
        travelData.questList.forEachIndexed { _, questData ->
            MainCard(
                onClick = {
                    toExtraEquipTravelAreaDetail(questData.travelQuestId)
                }
            ) {
                TravelQuestHeader(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    questData = questData
                )
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
    modifier: Modifier = Modifier,
    questData: ExtraEquipQuestData,
    showTitle: Boolean = true
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .padding(vertical = Dimen.mediumPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //图标
        MainIcon(
            data = ImageRequestHelper.getInstance()
                .getUrl(ICON_EXTRA_EQUIPMENT_TRAVEL_MAP, questData.travelQuestId),
        )
        //标题
        if (showTitle) {
            Subtitle1(
                text = questData.getQuestName(),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = Dimen.smallPadding)
            )
        }
        //其它参数
        VerticalStaggeredGrid(
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
                toTimeText(questData.travelTime * 1000, context)
            )
            CommonTitleContentText(
                stringResource(id = R.string.travel_time_decrease_limit),
                toTimeText(questData.travelTimeDecreaseLimit * 1000, context)
            )
        }
    }
}

@CombinedPreviews
@Composable
private fun TravelItemPreview() {
    PreviewLayout {
        val quest = ExtraEquipQuestData(
            1,
            1,
            stringResource(id = R.string.debug_short_text),
            10,
            1000,
            2000,
            1,
            1,
            1
        )

        TravelItem(
            travelData = ExtraTravelData(
                travelAreaId = 1,
                travelAreaName = stringResource(id = R.string.debug_short_text),
                questCount = 1,
                questList = arrayListOf(
                    quest, quest, quest
                )
            ),
            toExtraEquipTravelAreaDetail = {}
        )
    }
}

