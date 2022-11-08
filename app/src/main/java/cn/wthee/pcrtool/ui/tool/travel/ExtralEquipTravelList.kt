package cn.wthee.pcrtool.ui.tool.travel

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.ExtraEquipQuestData
import cn.wthee.pcrtool.data.db.view.ExtraEquipTravelData
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.ImageResourceHelper
import cn.wthee.pcrtool.utils.ImageResourceHelper.Companion.ICON_EXTRA_EQUIPMENT_TRAVEL_MAP
import cn.wthee.pcrtool.utils.intArrayList
import cn.wthee.pcrtool.utils.toTimeText
import cn.wthee.pcrtool.viewmodel.ExtraEquipmentViewModel
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowRow

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
        //标题
        FlowRow(
            modifier = Modifier.padding(bottom = Dimen.mediumPadding),
            crossAxisAlignment = FlowCrossAxisAlignment.Center
        ) {
            MainTitleText(
                text = travelData.travelAreaName
            )
            MainTitleText(
                text = "${travelData.questCount}",
                modifier = Modifier.padding(start = Dimen.smallPadding)
            )
        }

        MainCard {
            Row(
                modifier = Modifier.padding(bottom = Dimen.mediumPadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                //area
                IconCompose(
                    data = ImageResourceHelper.getInstance()
                        .getUrl(ICON_EXTRA_EQUIPMENT_TRAVEL_MAP, travelData.travelAreaId),
                    size = Dimen.toolMenuWidth
                )
                //quest图标
                Column(
                    modifier = Modifier
                        .padding(Dimen.largePadding)
                        .fillMaxWidth()
                ) {
                    travelData.questIds.intArrayList.forEachIndexed { _, questId ->
                        TravelQuestHeader(
                            questId = questId,
                            toExtraEquipTravelAreaDetail = toExtraEquipTravelAreaDetail
                        )
                    }
                }
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
    val mQuestData = if (questId == 0) {
        questData
    } else {
        extraEquipmentViewModel.getTravelQuest(questId)
            .collectAsState(initial = null).value
    }
    mQuestData?.let {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(Dimen.smallPadding)
                .clip(MaterialTheme.shapes.medium)
                .clickable {
                    if (toExtraEquipTravelAreaDetail != null) {
                        toExtraEquipTravelAreaDetail(questId)
                    }
                }
        ) {
            IconCompose(
                data = ImageResourceHelper.getInstance()
                    .getUrl(ICON_EXTRA_EQUIPMENT_TRAVEL_MAP, mQuestData.travelQuestId)
            )
            Column {
                //标题
                Subtitle1(text = mQuestData.travelQuestName.replace("\\n", "·"))
                //其它参数
                FlowRow {
                    CaptionText(
                        text = stringResource(
                            id = R.string.travel_limit_unit_num,
                            mQuestData.limitUnitNum
                        ),
                        modifier = Modifier.padding(start = Dimen.smallPadding)
                    )
                    CaptionText(
                        text = stringResource(
                            id = R.string.travel_need_power,
                            mQuestData.needPower
                        ),
                        modifier = Modifier.padding(start = Dimen.smallPadding)
                    )
                    CaptionText(
                        text = stringResource(
                            id = R.string.travel_time,
                            toTimeText(mQuestData.travelTime * 1000)
                        ),
                        modifier = Modifier.padding(start = Dimen.smallPadding)
                    )
                    CaptionText(
                        text = stringResource(
                            id = R.string.travel_time_decrease_limit,
                            toTimeText(mQuestData.travelTimeDecreaseLimit * 1000)
                        ),
                        modifier = Modifier.padding(start = Dimen.smallPadding)
                    )
                }
            }
        }
    }
}