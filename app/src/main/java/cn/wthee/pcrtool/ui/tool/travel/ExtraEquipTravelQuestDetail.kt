package cn.wthee.pcrtool.ui.tool.travel

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.ExtraEquipQuestData
import cn.wthee.pcrtool.data.db.view.ExtraEquipSubRewardData
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.ImageResourceHelper
import cn.wthee.pcrtool.utils.intArrayList
import cn.wthee.pcrtool.viewmodel.ExtraEquipmentViewModel

@Composable
fun ExtraEquipTravelQuestDetail(
    questId: Int,
    toExtraEquipDetail: (Int) -> Unit,
    extraEquipmentViewModel: ExtraEquipmentViewModel = hiltViewModel()
) {
    val questData =
        extraEquipmentViewModel.getTravelQuest(questId).collectAsState(initial = null).value

    Box(modifier = Modifier.fillMaxSize()) {
        questData?.let {
            TravelQuestItem(
                selectedId = 0,
                questData = questData,
                extraEquipmentViewModel = extraEquipmentViewModel,
                toExtraEquipDetail = toExtraEquipDetail
            )
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
        modifier = Modifier.padding(
            horizontal = Dimen.largePadding,
            vertical = Dimen.mediumPadding
        )
    ) {
        //标题
        TravelQuestHeader(0, questData)
        //主要掉落
        ExtraEquipDropIcon(questData.mainRewardIds.intArrayList, selectedId, toExtraEquipDetail)
        //次要掉落
        ExtraEquipSubRewardIcon(subRewardList, selectedId, toExtraEquipDetail)

        CommonSpacer()
    }

}

/**
 * 掉落列表——ex装备图标
 */
@Composable
private fun ExtraEquipDropIcon(
    iconIds: List<Int>,
    selectedId: Int,
    toExtraEquipDetail: ((Int) -> Unit)? = null
) {

    MainTitleText(text = stringResource(R.string.extra_equip_main_reward))

    VerticalGrid(
        modifier = Modifier.padding(top = Dimen.mediumPadding),
        maxColumnWidth = Dimen.iconSize + Dimen.mediumPadding * 2
    ) {
        iconIds.forEach {
            Column(
                modifier = Modifier
                    .padding(
                        bottom = Dimen.mediumPadding
                    )
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val selected = selectedId == it
                Box(contentAlignment = Alignment.Center) {
                    IconCompose(
                        data = ImageResourceHelper.getInstance()
                            .getUrl(ImageResourceHelper.ICON_EXTRA_EQUIPMENT, it)
                    ) {
                        if (toExtraEquipDetail != null) {
                            toExtraEquipDetail(it)
                        }
                    }
                    if (selectedId != ImageResourceHelper.UNKNOWN_EQUIP_ID) {
                        SelectText(
                            selected = selected,
                            text = if (selected) "✓" else ""
                        )
                    }
                }

            }
        }
    }

}


/**
 * 次要掉落装备
 */
@Composable
private fun ExtraEquipSubRewardIcon(
    subRewardList: List<ExtraEquipSubRewardData>,
    selectedId: Int,
    toExtraEquipDetail: ((Int) -> Unit)? = null
) {

    MainTitleText(text = stringResource(R.string.extra_equip_sub_reward))

    subRewardList.forEach { subRewardData ->
//        MainTitleText(text = subRewardData.categoryName)

        VerticalGrid(
            modifier = Modifier.padding(top = Dimen.mediumPadding),
            maxColumnWidth = Dimen.iconSize + Dimen.mediumPadding * 2
        ) {
            subRewardData.subRewardIds.intArrayList.forEach {
                Column(
                    modifier = Modifier
                        .padding(
                            bottom = Dimen.mediumPadding
                        )
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val selected = selectedId == it
                    Box(contentAlignment = Alignment.Center) {
                        IconCompose(
                            data = ImageResourceHelper.getInstance()
                                .getUrl(ImageResourceHelper.ICON_EXTRA_EQUIPMENT, it)
                        ) {
                            if (toExtraEquipDetail != null) {
                                toExtraEquipDetail(it)
                            }
                        }
                        if (selectedId != ImageResourceHelper.UNKNOWN_EQUIP_ID) {
                            SelectText(
                                selected = selected,
                                text = if (selected) "✓" else ""
                            )
                        }
                    }

                }
            }
        }
    }
}