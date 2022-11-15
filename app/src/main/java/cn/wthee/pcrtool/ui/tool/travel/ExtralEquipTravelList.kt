package cn.wthee.pcrtool.ui.tool.travel

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
import cn.wthee.pcrtool.ui.theme.Dimen
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
                item{
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
) {

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

        //quest图标
        travelData.questIds.intArrayList.forEachIndexed { _, questId ->
            MainCard(modifier = Modifier.padding(vertical = Dimen.mediumPadding)) {
                TravelQuestHeader(
                    questId = questId,
                    toExtraEquipTravelAreaDetail = toExtraEquipTravelAreaDetail
                )
            }
        }
    }
}

/**
 * ex冒险区域公用头部布局
 * questId 为0时，从装备详情-掉落区域调整，不重新查询
 */
@Composable
fun TravelQuestHeader(
    questId: Int,
    questData: ExtraEquipQuestData? = null,
    toExtraEquipTravelAreaDetail: ((Int) -> Unit)? = null,
    extraEquipmentViewModel: ExtraEquipmentViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    val mQuestData = if (questId == 0) {
        questData
    } else {
        extraEquipmentViewModel.getTravelQuest(questId)
            .collectAsState(initial = null).value
    }

    mQuestData?.let {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .clickable(enabled = questId != 0) {
                    if (toExtraEquipTravelAreaDetail != null) {
                        VibrateUtil(context).single()
                        toExtraEquipTravelAreaDetail(questId)
                    }
                }
                .padding(vertical = Dimen.mediumPadding)
        ) {
            //图标
            IconCompose(
                data = ImageResourceHelper.getInstance()
                    .getUrl(ICON_EXTRA_EQUIPMENT_TRAVEL_MAP, mQuestData.travelQuestId)
            )
            //标题
            Subtitle1(text = mQuestData.travelQuestName.replace("\\n", "·"))
            //其它参数
            VerticalGrid(
                modifier = Modifier.padding(horizontal = Dimen.mediumPadding),
                spanCount = ScreenUtil.getWidth() / getItemWidth().value.dp2px * 2
            ) {
                CommonTitleContentText(
                    stringResource(id = R.string.travel_limit_unit_num),
                    mQuestData.limitUnitNum.toString()
                )
                CommonTitleContentText(
                    stringResource(id = R.string.travel_need_power),
                    mQuestData.needPower.toString()
                )
                CommonTitleContentText(
                    stringResource(id = R.string.travel_time),
                    toTimeText(mQuestData.travelTime * 1000)
                )
                CommonTitleContentText(
                    stringResource(id = R.string.travel_time_decrease_limit),
                    toTimeText(mQuestData.travelTimeDecreaseLimit * 1000)
                )
            }
        }
    }
}