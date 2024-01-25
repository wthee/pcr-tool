package cn.wthee.pcrtool.ui.character.rankequip

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.data.db.view.UnitPromotion
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.RankText
import cn.wthee.pcrtool.ui.components.StateBox
import cn.wthee.pcrtool.ui.components.VerticalStaggeredGrid
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.ImageRequestHelper

/**
 * 角色Rank装备列表
 */
@Composable
fun RankEquipListScreen(
    rankEquipListViewModel: RankEquipListViewModel = hiltViewModel(),
) {
    val uiState by rankEquipListViewModel.uiState.collectAsStateWithLifecycle()


    MainScaffold(
        mainFabIcon = MainIconType.OK
    ) {
        StateBox(stateType = uiState.loadState) {
            uiState.rankEquipList?.let {
                RankEquipListContent(
                    currentRank = uiState.currentRank,
                    rankEquipList = it,
                    updateCurrentRank = rankEquipListViewModel::updateCurrentRank
                )
            }
        }
    }
}

@Composable
private fun RankEquipListContent(
    currentRank: Int,
    rankEquipList: List<UnitPromotion>,
    updateCurrentRank: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(Dimen.iconSize * 2 + Dimen.smallPadding * 4),
        contentPadding = PaddingValues(Dimen.mediumPadding),
        state = rememberLazyGridState()
    ) {
        items(
            items = rankEquipList,
            key = {
                it.promotionLevel
            }
        ) {
            RankEquipListItem(
                currentRank = currentRank,
                unitPromotion = it,
                updateCurrentRank = updateCurrentRank
            )
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
private fun RankEquipListItem(
    currentRank: Int,
    unitPromotion: UnitPromotion,
    updateCurrentRank: (Int) -> Unit
) {
    val allIds = unitPromotion.getAllOrderIds()

    //图标列表
    MainCard(
        modifier = Modifier.padding(Dimen.mediumPadding),
        onClick = {
            updateCurrentRank(unitPromotion.promotionLevel)
        }
    ) {
        //RANK
        RankText(
            rank = unitPromotion.promotionLevel,
            modifier = Modifier
                .padding(
                    horizontal = Dimen.exSmallPadding,
                    vertical = Dimen.mediumPadding
                )
                .align(Alignment.CenterHorizontally),
            type = if (unitPromotion.promotionLevel == currentRank) 1 else 0
        )

        VerticalStaggeredGrid(fixCount = 2) {
            allIds.forEach {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    MainIcon(
                        modifier = Modifier.padding(Dimen.smallPadding),
                        data = ImageRequestHelper.getInstance()
                            .getUrl(ImageRequestHelper.ICON_EQUIPMENT, it)
                    )
                }
            }
        }
    }
}

@CombinedPreviews
@Composable
private fun RankEquipListContentPreview() {
    val rankEquipList = arrayListOf(
        UnitPromotion(promotionLevel = 24),
        UnitPromotion(promotionLevel = 21),
        UnitPromotion(promotionLevel = 18),
        UnitPromotion(promotionLevel = 11)
    )

    PreviewLayout {
        RankEquipListContent(
            currentRank = 24,
            rankEquipList = rankEquipList,
            updateCurrentRank = {}
        )
    }
}