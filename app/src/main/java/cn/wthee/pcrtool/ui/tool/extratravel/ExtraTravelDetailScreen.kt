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
import cn.wthee.pcrtool.ui.components.VerticalGridList
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
        questData?.let {
            TravelQuestHeader(
                questData = it,
                showTitle = selectedId == 0
            )
        }
        //掉落
        subRewardList?.forEach { subRewardData ->
            ExtraEquipGroup(
                category = subRewardData.category,
                categoryName = subRewardData.categoryName,
                equipIdList = subRewardData.subRewardIds.intArrayList,
                dropOddsList = subRewardData.subRewardDrops.intArrayList,
                selectedId = selectedId,
                toExtraEquipDetail = toExtraEquipDetail
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

    ExtraEquipRewardIconGrid(
        equipIdList = equipIdList,
        dropOddList = dropOddsList,
        selectedId = selectedId,
        toExtraEquipDetail = toExtraEquipDetail
    )

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
    VerticalGridList(
        modifier = Modifier.padding(
            horizontal = Dimen.commonItemPadding
        ),
        itemCount = equipIdList.size,
        itemWidth = Dimen.iconSize,
        contentPadding = Dimen.commonItemPadding
    ) {
        val equipId = equipIdList[it]
        val selected = selectedId == equipId
        Column(
            modifier = Modifier.fillMaxWidth(),
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
                        dropOddList[it] / 10000f
                    )
                )
            }

        }
    }
}


@CombinedPreviews
@Composable
private fun ExtraEquipSubGroupPreview() {
    PreviewLayout {
        ExtraEquipGroup(
            category = 1,
            categoryName = stringResource(id = R.string.debug_short_text),
            equipIdList = arrayListOf(1, 2, 3, 4),
            dropOddsList = arrayListOf(1000, 20000, 3333, 444444),
            selectedId = 2
        ) { }
    }
}