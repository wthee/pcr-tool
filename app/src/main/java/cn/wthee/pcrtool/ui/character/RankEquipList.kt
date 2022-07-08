package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.data.db.view.UnitPromotion
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.PreviewBox
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.ImageResourceHelper
import cn.wthee.pcrtool.viewmodel.EquipmentViewModel

/**
 * 角色 RANK 选择装备列表
 *
 * @param unitId 角色编号
 */
@Composable
fun RankEquipList(
    unitId: Int,
    lazyGridState: LazyGridState,
    toEquipDetail: (Int) -> Unit,
    equipmentViewModel: EquipmentViewModel = hiltViewModel(),
) {
    val allRankEquip =
        equipmentViewModel.getAllRankEquipList(unitId).collectAsState(initial = arrayListOf()).value
    val selectedRank = remember {
        mutableStateOf(navViewModel.currentValue.value?.rank ?: 2)
    }
    LazyVerticalGrid(
        columns = GridCells.Adaptive(Dimen.iconSize * 2 + Dimen.smallPadding * 4),
        contentPadding = PaddingValues(Dimen.mediumPadding),
        state = lazyGridState
    ) {
        items(
            items = allRankEquip,
            key = {
                it.promotionLevel
            }
        ) {
            RankEquipListItem(it, selectedRank, toEquipDetail)
        }
        items(2) {
            CommonSpacer()
        }
    }
}

/**
 * RANK 装备图标列表
 */
@Composable
fun RankEquipListItem(
    unitPromotion: UnitPromotion,
    selectedRank: MutableState<Int>,
    toEquipDetail: (Int) -> Unit
) {

    MainCard(
        modifier = Modifier.padding(Dimen.mediumPadding),
        onClick = {
            selectedRank.value = unitPromotion.promotionLevel
            val value = navViewModel.currentValue.value
            navViewModel.currentValue.postValue(value?.update(rank = unitPromotion.promotionLevel))
        }
    ) {
        //图标列表
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //RANK
            RankText(
                rank = unitPromotion.promotionLevel,
                modifier = Modifier.padding(Dimen.mediumPadding),
                type = if (unitPromotion.promotionLevel == selectedRank.value) 1 else 0
            )

            val allIds = unitPromotion.getAllOrderIds()
            VerticalGrid(spanCount = 2) {
                allIds.forEach {
                    IconCompose(
                        modifier = Modifier.padding(Dimen.smallPadding),
                        data = ImageResourceHelper.getInstance().getEquipPic(it)
                    ) {
                        if (it != ImageResourceHelper.UNKNOWN_EQUIP_ID) {
                            toEquipDetail(it)
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun RankEquipListItemPreview() {
    val selectedRank = remember {
        mutableStateOf(2)
    }
    val allRankEquip = arrayListOf(
        UnitPromotion(),
        UnitPromotion(),
        UnitPromotion(),
        UnitPromotion()
    )
    PreviewBox {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(Dimen.mediumPadding)
        ) {
            items(allRankEquip) {
                RankEquipListItem(it, selectedRank) { }
            }
        }

    }
}