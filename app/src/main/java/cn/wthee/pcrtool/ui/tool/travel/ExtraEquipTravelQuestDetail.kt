package cn.wthee.pcrtool.ui.tool.travel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
        ExtraEquipMainRewardList(questData.mainRewardIds.intArrayList, selectedId, toExtraEquipDetail)
        //次要掉落
        ExtraEquipSubRewardList(subRewardList, selectedId, toExtraEquipDetail)
    }

}

/**
 * 掉落列表——ex装备图标
 */
@Composable
private fun ExtraEquipMainRewardList(
    iconIds: List<Int>,
    selectedId: Int,
    toExtraEquipDetail: ((Int) -> Unit)? = null
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = Dimen.largePadding)
    ) {
        MainText(text = stringResource(R.string.extra_equip_main_reward))

        VerticalGrid(
            modifier = Modifier.padding(
                top = Dimen.largePadding,
                bottom = Dimen.largePadding,
                start = Dimen.commonItemPadding,
                end = Dimen.commonItemPadding
            ),
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

}


/**
 * 次要掉落装备
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
    ){
        Subtitle2(text = stringResource(R.string.extra_equip_sub_reward))

        subRewardList.forEach { subRewardData ->
            val equipIdList = subRewardData.subRewardIds.intArrayList

            CommonGroupTitle(
                iconData = ImageResourceHelper.getInstance()
                    .getUrl(
                        ImageResourceHelper.ICON_EXTRA_EQUIPMENT_CATEGORY,
                        subRewardData.category
                    ),
                iconSize = Dimen.smallIconSize,
                titleStart = subRewardData.categoryName,
                titleEnd = equipIdList.size.toString(),
                modifier = Modifier.padding(Dimen.largePadding)
            )

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

}