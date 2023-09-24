package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.data.db.view.UnitPromotion
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.RankText
import cn.wthee.pcrtool.ui.components.VerticalGrid
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.ImageRequestHelper
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
    val allRankEquipFlow = remember(unitId) {
        equipmentViewModel.getAllRankEquipList(unitId)
    }
    val allRankEquip by allRankEquipFlow.collectAsState(initial = arrayListOf())

    val currentValueState = remember {
        mutableIntStateOf(currentRank)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
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
    val allIds = unitPromotion.getAllOrderIds()

    MainCard(
        modifier = Modifier.padding(Dimen.mediumPadding),
        onClick = {
            currentRank.value = unitPromotion.promotionLevel
            navViewModel.rankEquipSelected.value = unitPromotion.promotionLevel
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
                type = if (unitPromotion.promotionLevel == currentRank.value) 1 else 0
            )

            VerticalGrid(fixCount = 2) {
                allIds.forEach {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        MainIcon(
                            modifier = Modifier.padding(Dimen.smallPadding),
                            data = ImageRequestHelper.getInstance().getEquipPic(it)
                        )
                    }
                }
            }
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
                    mutableIntStateOf(1)
                }, it)
            }
        }

    }
}