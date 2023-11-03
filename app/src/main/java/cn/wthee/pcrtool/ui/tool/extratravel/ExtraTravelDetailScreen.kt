package cn.wthee.pcrtool.ui.tool.extratravel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.ExtraEquipQuestData
import cn.wthee.pcrtool.data.db.view.ExtraEquipSubRewardData
import cn.wthee.pcrtool.ui.components.CommonGroupTitle
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.SelectText
import cn.wthee.pcrtool.ui.components.VerticalGrid
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.colorWhite
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.intArrayList

/**
 * ex冒险区域详情
 */
@Composable
fun ExtraTravelDetailScreen(
    toExtraEquipDetail: (Int) -> Unit,
    extraTravelDetailViewModel: ExtraTravelDetailViewModel = hiltViewModel()
) {
    val uiState by extraTravelDetailViewModel.uiState.collectAsStateWithLifecycle()


    MainScaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            TravelQuestItem(
                selectedId = 0,
                questData = uiState.questData,
                subRewardList = uiState.subRewardList,
                toExtraEquipDetail = toExtraEquipDetail
            )
        }
    }

}


/**
 * 区域详情信息、ex装备掉落信息
 * @param selectedId 0：无选中装备（区域详情信息）；非0：查看ex装备掉落信息时
 */
@Composable
fun TravelQuestItem(
    selectedId: Int,
    questData: ExtraEquipQuestData?,
    subRewardList: List<ExtraEquipSubRewardData>?,
    toExtraEquipDetail: ((Int) -> Unit)? = null
) {

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        //标题
        questData?.let { TravelQuestHeader(it, showTitle = selectedId == 0) }
        //掉落
        subRewardList?.forEach { subRewardData ->
            ExtraEquipGroup(
                subRewardData.category,
                subRewardData.categoryName,
                subRewardData.subRewardIds.intArrayList,
                subRewardData.subRewardDrops.intArrayList,
                selectedId,
                toExtraEquipDetail
            )
        }
        CommonSpacer()
    }

}

/**
 * 装备掉落分组、角色适用装备分组
 */
@Composable
fun ExtraEquipGroup(
    category: Int,
    categoryName: String,
    equipIdList: List<Int>,
    dropOddsList: List<Int> = arrayListOf(),
    selectedId: Int,
    toExtraEquipDetail: ((Int) -> Unit)?
) {
    val containsSelectedId = equipIdList.contains(selectedId)

    CommonGroupTitle(
        iconData = ImageRequestHelper.getInstance()
            .getUrl(
                ImageRequestHelper.ICON_EXTRA_EQUIPMENT_CATEGORY,
                category
            ),
        iconSize = Dimen.smallIconSize,
        titleStart = categoryName,
        titleEnd = equipIdList.size.toString(),
        modifier = Modifier.padding(Dimen.mediumPadding),
        backgroundColor = if (containsSelectedId) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        textColor = if (containsSelectedId) {
            colorWhite
        } else {
            MaterialTheme.colorScheme.onSurface
        }
    )

    ExtraEquipRewardIconGrid(equipIdList, dropOddsList, selectedId, toExtraEquipDetail)

}


/**
 * 带选中标记的ex装备图标
 */
@Composable
private fun ExtraEquipRewardIconGrid(
    equipIdList: List<Int>,
    dropOddList: List<Int>,
    selectedId: Int,
    toExtraEquipDetail: ((Int) -> Unit)?
) {
    VerticalGrid(
        modifier = Modifier.padding(
            start = Dimen.commonItemPadding,
            end = Dimen.commonItemPadding
        ),
        itemWidth = Dimen.iconSize,
        contentPadding = Dimen.mediumPadding
    ) {
        equipIdList.forEachIndexed { index, equipId ->
            val selected = selectedId == equipId
            Column(
                modifier = Modifier
                    .padding(bottom = Dimen.mediumPadding)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MainIcon(
                    data = ImageRequestHelper.getInstance()
                        .getUrl(ImageRequestHelper.ICON_EXTRA_EQUIPMENT, equipId),
                    onClick = if (toExtraEquipDetail != null) {
                        {
                            toExtraEquipDetail(equipId)
                        }
                    } else {
                        null
                    }
                )
                if (dropOddList.isNotEmpty()) {
                    SelectText(
                        selected = selected,
                        text = stringResource(
                            id = R.string.ex_equip_drop_odd,
                            dropOddList[index] / 10000f
                        )
                    )
                }

            }
        }
    }
}


@CombinedPreviews
@Composable
private fun ExtraEquipSubGroupPreview() {
    PreviewLayout {
        ExtraEquipGroup(
            1,
            "selected",
            arrayListOf(1, 2, 3, 4),
            arrayListOf(1000, 20000, 3333, 444444),
            2
        ) { }
    }
}