package cn.wthee.pcrtool.ui.character

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.data.db.entity.UnitPromotion
import cn.wthee.pcrtool.ui.NavViewModel
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shapes
import cn.wthee.pcrtool.viewmodel.EquipmentViewModel

/**
 * 角色 RANK 选择装备列表
 *
 * @param unitId 角色编号
 */
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun RankEquipList(
    unitId: Int,
    toEquipDetail: (Int) -> Unit,
    navViewModel: NavViewModel,
    equipmentViewModel: EquipmentViewModel = hiltViewModel(),
) {
    equipmentViewModel.getAllRankEquipList(unitId)
    val allRankEquip = equipmentViewModel.allRankEquipList.observeAsState().value ?: arrayListOf()
    val selectedRank = remember {
        mutableStateOf(navViewModel.selectRank.value ?: 2)
    }
    val spanCount = 3
    SlideAnimation(visible = allRankEquip.isNotEmpty()) {
        LazyVerticalGrid(
            cells = GridCells.Fixed(spanCount),
            contentPadding = PaddingValues(Dimen.mediuPadding)
        ) {
            items(allRankEquip) {
                RankEquipListItem(it, selectedRank, toEquipDetail, navViewModel)
            }
            items(spanCount) {
                CommonSpacer()
            }
        }
    }

}

/**
 * RANK 装备图标列表
 */
@ExperimentalMaterialApi
@Composable
fun RankEquipListItem(
    unitPromotion: UnitPromotion,
    selectedRank: MutableState<Int>,
    toEquipDetail: (Int) -> Unit,
    navViewModel: NavViewModel,
) {
    val colorAnim = animateColorAsState(
        targetValue = if (unitPromotion.promotionLevel == selectedRank.value)
            MaterialTheme.colors.primary
        else
            MaterialTheme.colors.surface,
        animationSpec = defaultSpring()
    )


    MainCard(
        modifier = Modifier.padding(Dimen.mediuPadding),
        onClick = {
            selectedRank.value = unitPromotion.promotionLevel
            navViewModel.selectRank.postValue(unitPromotion.promotionLevel)
        }
    ) {
        //图标列表
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorAnim.value, Shapes.large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //RANK
            RankText(
                rank = unitPromotion.promotionLevel,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(Dimen.mediuPadding)
            )
            val allIds = unitPromotion.getAllOrderIds()
            allIds.forEachIndexed { index, _ ->
                if (index % 2 == 0) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier
                            .padding(Dimen.smallPadding)
                            .fillMaxWidth()
                    ) {
                        IconCompose(data = getEquipIconUrl(allIds[index])) {
                            toEquipDetail(allIds[index])
                        }
                        Spacer(modifier = Modifier.width(Dimen.smallPadding))
                        IconCompose(data = getEquipIconUrl(allIds[index + 1])) {
                            toEquipDetail(allIds[index + 1])
                        }
                    }
                }
            }
        }
    }

}