package cn.wthee.pcrtool.ui.character

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.UnitPromotion
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.PreviewBox
import cn.wthee.pcrtool.ui.common.CommonSpacer
import cn.wthee.pcrtool.ui.common.IconCompose
import cn.wthee.pcrtool.ui.common.MainCard
import cn.wthee.pcrtool.ui.common.RankText
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shape
import cn.wthee.pcrtool.ui.theme.defaultSpring
import cn.wthee.pcrtool.utils.ImageResourceHelper
import cn.wthee.pcrtool.viewmodel.EquipmentViewModel

/**
 * 角色 RANK 选择装备列表
 *
 * @param unitId 角色编号
 */
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun RankEquipList(
    unitId: Int,
    toEquipDetail: (Int) -> Unit,
    equipmentViewModel: EquipmentViewModel = hiltViewModel(),
) {
    val allRankEquip =
        equipmentViewModel.getAllRankEquipList(unitId).collectAsState(initial = arrayListOf()).value
    val selectedRank = remember {
        mutableStateOf(navViewModel.currentValue.value?.rank ?: 2)
    }
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        cells = GridCells.Adaptive(Dimen.iconSize * 2 + Dimen.mediumPadding * 2 + Dimen.smallPadding * 3),
        contentPadding = PaddingValues(Dimen.mediumPadding)
    ) {
        items(allRankEquip) {
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
@ExperimentalMaterialApi
@Composable
fun RankEquipListItem(
    unitPromotion: UnitPromotion,
    selectedRank: MutableState<Int>,
    toEquipDetail: (Int) -> Unit
) {
    val colorAnim = animateColorAsState(
        targetValue = if (unitPromotion.promotionLevel == selectedRank.value)
            colorResource(id = R.color.alpha_primary)
        else
            MaterialTheme.colorScheme.surface,
        animationSpec = defaultSpring()
    )


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
                .fillMaxWidth()
                .background(colorAnim.value, Shape.medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //RANK
            RankText(
                rank = unitPromotion.promotionLevel,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(Dimen.mediumPadding)
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
                        IconCompose(
                            data = ImageResourceHelper.getInstance()
                                .getEquipPic(allIds[index])
                        ) {
                            toEquipDetail(allIds[index])
                        }
                        Spacer(modifier = Modifier.width(Dimen.smallPadding))
                        IconCompose(
                            data = ImageResourceHelper.getInstance()
                                .getEquipPic(allIds[index + 1])
                        ) {
                            toEquipDetail(allIds[index + 1])
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalFoundationApi
@Preview
@ExperimentalMaterialApi
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
            cells = GridCells.Fixed(3),
            contentPadding = PaddingValues(Dimen.mediumPadding)
        ) {
            items(allRankEquip) {
                RankEquipListItem(it, selectedRank) { }
            }
        }

    }
}