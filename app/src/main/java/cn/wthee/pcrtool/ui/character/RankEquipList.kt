package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.data.db.view.UnitPromotion
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
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
    currentRank: Int,
    equipmentViewModel: EquipmentViewModel = hiltViewModel(),
) {
    val allRankEquip =
        equipmentViewModel.getAllRankEquipList(unitId).collectAsState(initial = arrayListOf()).value


    val currentValueState = remember {
        mutableStateOf(currentRank)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (allRankEquip.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(Dimen.iconSize * 2 + Dimen.smallPadding * 4),
                contentPadding = PaddingValues(Dimen.mediumPadding),
                state = rememberLazyGridState()
            ) {
                items(
                    items = allRankEquip,
                    key = {
                        it.promotionLevel
                    }
                ) {
                    RankEquipListItem(currentValueState, it)
                }
                items(2) {
                    CommonSpacer()
                }
            }
        }
    }


}

/**
 * RANK 装备图标列表
 */
@Composable
fun RankEquipListItem(
    currentRank: MutableState<Int>,
    unitPromotion: UnitPromotion
) {
    var dataState by remember { mutableStateOf(unitPromotion) }
    if (dataState != unitPromotion) {
        dataState = unitPromotion
    }
    val equipIconList: @Composable () -> Unit by remember {
        mutableStateOf(
            {
                val allIds = dataState.getAllOrderIds()
                VerticalGrid(fixCount = 2) {
                    allIds.forEach {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            IconCompose(
                                modifier = Modifier.padding(Dimen.smallPadding),
                                data = ImageResourceHelper.getInstance().getEquipPic(it)
                            )
                        }
                    }
                }
            }
        )
    }

    MainCard(
        modifier = Modifier.padding(Dimen.mediumPadding),
        onClick = {
            currentRank.value = dataState.promotionLevel
            navViewModel.rankEquipSelected.value = dataState.promotionLevel
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
                rank = dataState.promotionLevel,
                modifier = Modifier.padding(Dimen.mediumPadding),
                type = if (dataState.promotionLevel == currentRank.value) 1 else 0
            )

            equipIconList()
        }
    }
}

@CombinedPreviews
@Composable
private fun RankEquipListItemPreview() {
    val allRankEquip = arrayListOf(
        UnitPromotion(promotionLevel = 24),
        UnitPromotion(promotionLevel = 21),
        UnitPromotion(promotionLevel = 18),
        UnitPromotion(promotionLevel = 11)
    )

    PreviewLayout {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(Dimen.mediumPadding)
        ) {
            items(allRankEquip) {
                RankEquipListItem(remember {
                    mutableStateOf(1)
                }, it)
            }
        }

    }
}