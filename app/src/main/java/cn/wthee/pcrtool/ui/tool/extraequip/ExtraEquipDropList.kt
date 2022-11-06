package cn.wthee.pcrtool.ui.tool.extraequip

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

/**
 * ex装备掉落信息
 */
@Composable
fun ExtraEquipDropList(
    equipId: Int,
    extraEquipmentViewModel: ExtraEquipmentViewModel = hiltViewModel()
) {
    val dropList = extraEquipmentViewModel.getDropAreaList(equipId)
        .collectAsState(initial = arrayListOf()).value

    Box {
        if (dropList.isNotEmpty()) {
            LazyColumn {
                items(dropList) {
                    DropAreaItem(equipId, it, extraEquipmentViewModel)
                }
            }
        } else {
            Column(
                modifier = Modifier.defaultMinSize(minHeight = Dimen.minSheetHeight),
            ) {
                CenterTipText(text = stringResource(id = R.string.extra_equip_no_drop_quest))
            }
        }
    }
}

/**
 * 掉落区域信息
 */
@Composable
fun DropAreaItem(
    selectedId: Int,
    dropData: ExtraEquipQuestData,
    extraEquipmentViewModel: ExtraEquipmentViewModel
) {
    val subRewardList =
        extraEquipmentViewModel.getSubRewardList(dropData.travelQuestId).collectAsState(
            initial = arrayListOf()
        ).value


    Column(
        modifier = Modifier.padding(
            horizontal = Dimen.largePadding,
            vertical = Dimen.mediumPadding
        )
    ) {
        //标题
        MainText(
            text = dropData.travelQuestName.replace("\\n", "·"),
            modifier = Modifier
                .padding(bottom = Dimen.mediumPadding)
                .align(Alignment.CenterHorizontally)
        )

        //主要掉落
        ExtraEquipDropIcon(dropData.main_reward_ids.intArrayList, selectedId)
        //次要掉落
        ExtraEquipSubRewardIcon(subRewardList, selectedId)

        CommonSpacer()
    }

}

/**
 * 掉落列表——ex装备图标
 */
@Composable
private fun ExtraEquipDropIcon(iconIds: List<Int>, selectedId: Int) {

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
                    )
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
private fun ExtraEquipSubRewardIcon(subRewardList: List<ExtraEquipSubRewardData>, selectedId: Int) {

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
                        )
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