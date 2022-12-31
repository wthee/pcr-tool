package cn.wthee.pcrtool.ui.tool.extratravel

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.ExtraEquipQuestData
import cn.wthee.pcrtool.data.db.view.ExtraEquipTravelData
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.utils.ImageResourceHelper.Companion.ICON_EXTRA_EQUIPMENT_TRAVEL_MAP
import cn.wthee.pcrtool.viewmodel.ExtraEquipmentViewModel

/**
 * ex冒险区域
 */
@Composable
fun ExtraEquipTravelList(
    scrollState: LazyListState,
    toExtraEquipTravelAreaDetail: (Int) -> Unit,
    extraEquipmentViewModel: ExtraEquipmentViewModel = hiltViewModel()
) {

    val areaList = extraEquipmentViewModel.getTravelAreaList().collectAsState(initial = null).value

    Box(modifier = Modifier.fillMaxSize()) {
        if (areaList != null) {
            LazyColumn(state = scrollState) {
                items(areaList) {
                    TravelItem(it, toExtraEquipTravelAreaDetail)
                }
                item {
                    CommonSpacer()
                }
            }
        } else {
            //功能未实装
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CenterTipText(
                    stringResource(
                        id = R.string.not_installed,
                        getRegionName(MainActivity.regionType)
                    )
                )
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
    toExtraEquipTravelAreaDetail: (Int) -> Unit,
    extraEquipmentViewModel: ExtraEquipmentViewModel = hiltViewModel(),
) {
    val questList = extraEquipmentViewModel.getTravelQuestList(travelData.travelAreaId)
        .collectAsState(initial = null).value
    Column(
        modifier = Modifier
            .padding(
                horizontal = Dimen.largePadding,
                vertical = Dimen.mediumPadding
            )
    ) {
        //area
        CommonGroupTitle(
            iconData = ImageResourceHelper.getInstance()
                .getUrl(ICON_EXTRA_EQUIPMENT_TRAVEL_MAP, travelData.travelAreaId),
            iconSize = Dimen.menuIconSize,
            titleStart = travelData.travelAreaName,
            titleEnd = travelData.questCount.toString(),
            modifier = Modifier.padding(vertical = Dimen.smallPadding)
        )

        //quest列表
        questList?.forEachIndexed { _, questData ->
            MainCard(modifier = Modifier.padding(vertical = Dimen.mediumPadding)) {
                TravelQuestHeader(
                    clickable = true,
                    questData = questData,
                    toExtraEquipTravelAreaDetail = toExtraEquipTravelAreaDetail
                )
            }
        }
    }
}

/**
 * ex冒险区域公用头部布局
 * @param clickable 列表项可点击；查看详情时不可点击
 * @param showTitle 查看掉落列表时，不显示标题
 */
@Composable
fun TravelQuestHeader(
    clickable: Boolean,
    questData: ExtraEquipQuestData,
    showTitle: Boolean = true,
    toExtraEquipTravelAreaDetail: ((Int) -> Unit)? = null,
) {
    val context = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(enabled = clickable) {
                if (toExtraEquipTravelAreaDetail != null) {
                    VibrateUtil(context).single()
                    toExtraEquipTravelAreaDetail(questData.travelQuestId)
                }
            }
            .padding(vertical = Dimen.largePadding)
    ) {
        //图标
        IconCompose(
            data = ImageResourceHelper.getInstance()
                .getUrl(ICON_EXTRA_EQUIPMENT_TRAVEL_MAP, questData.travelQuestId),
        )
        //标题
        if (showTitle) {
            Subtitle1(text = questData.getQuestName())
        }
        //其它参数
        VerticalGrid(
            modifier = Modifier.padding(
                horizontal = Dimen.mediumPadding,
                vertical = Dimen.smallPadding
            ),
            spanCount = ScreenUtil.getWidth() / getItemWidth().value.dp2px * 2
        ) {
            CommonTitleContentText(
                stringResource(id = R.string.travel_limit_unit_num),
                questData.limitUnitNum.toString()
            )
            CommonTitleContentText(
                stringResource(id = R.string.travel_need_power),
                questData.needPower.toString()
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
            clickable = false,
            questData = ExtraEquipQuestData(
                0, 0, "?", 10, 10000, 500, 1, 1000, 1
            ),
            toExtraEquipTravelAreaDetail = { }
        )
    }
}
