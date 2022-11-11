package cn.wthee.pcrtool.ui.tool.travel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.ExtraEquipQuestData
import cn.wthee.pcrtool.data.db.view.ExtraEquipSubRewardData
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.ImageResourceHelper
import cn.wthee.pcrtool.utils.intArrayList
import cn.wthee.pcrtool.viewmodel.ExtraEquipmentViewModel

/**
 * ex冒险区域详情
 */
@Composable
fun ExtraEquipTravelQuestDetail(
    questId: Int,
    toExtraEquipDetail: (Int) -> Unit,
    extraEquipmentViewModel: ExtraEquipmentViewModel = hiltViewModel()
) {
    val questData =
        extraEquipmentViewModel.getTravelQuest(questId).collectAsState(initial = null).value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        questData?.let {
            TravelQuestItem(
                selectedId = 0,
                questData = questData,
                extraEquipmentViewModel = extraEquipmentViewModel,
                toExtraEquipDetail = toExtraEquipDetail
            )
            CommonSpacer()
        }
    }
}


/**
 * 掉落区域信息
 * 装备掉落共用
 */
@Composable
fun TravelQuestItem(
    selectedId: Int,
    questData: ExtraEquipQuestData,
    extraEquipmentViewModel: ExtraEquipmentViewModel,
    toExtraEquipDetail: ((Int) -> Unit)? = null
) {
    val subRewardList =
        extraEquipmentViewModel.getSubRewardList(questData.travelQuestId).collectAsState(
            initial = arrayListOf()
        ).value

    Column(
        modifier = Modifier
            .padding(vertical = Dimen.largePadding)
            .fillMaxWidth()
    ) {
        //标题
        TravelQuestHeader(0, questData)
        //主要掉落
        ExtraEquipMainRewardList(
            questData.mainRewardIds.intArrayList,
            selectedId,
            toExtraEquipDetail
        )
        //次要掉落
        ExtraEquipSubRewardList(subRewardList, selectedId, toExtraEquipDetail)
    }

}

/**
 * 主要掉落奖励
 */
@Composable
private fun ExtraEquipMainRewardList(
    equipIdList: List<Int>,
    selectedId: Int,
    toExtraEquipDetail: ((Int) -> Unit)? = null
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = Dimen.largePadding)
    ) {
        MainText(
            text = stringResource(R.string.extra_equip_main_reward),
            modifier = Modifier.padding(bottom = Dimen.largePadding)
        )

        ExtraEquipRewardIconGrid(equipIdList, selectedId, toExtraEquipDetail)
    }

}

/**
 * 次要掉落奖励
 */
@Composable
private fun ExtraEquipSubRewardList(
    subRewardList: List<ExtraEquipSubRewardData>,
    selectedId: Int,
    toExtraEquipDetail: ((Int) -> Unit)? = null
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = Dimen.largePadding)
    ) {
        Subtitle2(text = stringResource(R.string.extra_equip_sub_reward))

        subRewardList.forEach { subRewardData ->
            ExtraEquipSubGroup(
                subRewardData.category,
                subRewardData.categoryName,
                subRewardData.subRewardIds.intArrayList,
                selectedId,
                toExtraEquipDetail
            )
        }
    }

}

/**
 * 装备次要掉落分组、角色适用周分组
 */
@Composable
fun ExtraEquipSubGroup(
    category: Int,
    categoryName: String,
    equipIdList: List<Int>,
    selectedId: Int,
    toExtraEquipDetail: ((Int) -> Unit)?
) {
    CommonGroupTitle(
        iconData = ImageResourceHelper.getInstance()
            .getUrl(
                ImageResourceHelper.ICON_EXTRA_EQUIPMENT_CATEGORY,
                category
            ),
        iconSize = Dimen.smallIconSize,
        titleStart = categoryName,
        titleEnd = equipIdList.size.toString(),
        modifier = Modifier.padding(horizontal = Dimen.mediumPadding, vertical = Dimen.largePadding),
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
        textColor = MaterialTheme.colorScheme.onSurface
    )

    ExtraEquipRewardIconGrid(equipIdList, selectedId, toExtraEquipDetail)

}


/**
 * 带选中标记的ex装备图标
 */
@Composable
private fun ExtraEquipRewardIconGrid(
    equipIdList: List<Int>,
    selectedId: Int,
    toExtraEquipDetail: ((Int) -> Unit)?
) {
    VerticalGrid(
        modifier = Modifier.padding(
            bottom = Dimen.largePadding,
            start = Dimen.commonItemPadding,
            end = Dimen.commonItemPadding
        ),
        maxColumnWidth = Dimen.iconSize + Dimen.mediumPadding * 2
    ) {
        equipIdList.forEach {
            Column(
                modifier = Modifier
                    .padding(bottom = Dimen.mediumPadding)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val selected = selectedId == it
                Box(contentAlignment = Alignment.Center) {
                    IconCompose(
                        data = ImageResourceHelper.getInstance()
                            .getUrl(ImageResourceHelper.ICON_EXTRA_EQUIPMENT, it),
                        onClick = if (toExtraEquipDetail != null) {
                            {
                                toExtraEquipDetail(it)
                            }
                        } else {
                            null
                        }
                    )
                    if (selectedId != ImageResourceHelper.UNKNOWN_EQUIP_ID) {
                        SelectText(
                            selected = selected,
                            text = if (selected) "✓" else "",
                            margin = 0.dp
                        )
                    }
                }

            }
        }
    }
}