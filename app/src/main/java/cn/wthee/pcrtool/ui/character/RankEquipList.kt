package cn.wthee.pcrtool.ui.character

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.data.db.entity.UnitPromotion
import cn.wthee.pcrtool.ui.NavViewModel
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.getFormatText
import cn.wthee.pcrtool.viewmodel.EquipmentViewModel

/**
 * 角色 RANK 选择装备列表
 *
 * @param unitId 角色编号
 */
@ExperimentalAnimationApi
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
    SlideAnimation(visible = allRankEquip.isNotEmpty()) {
        LazyColumn() {
            items(allRankEquip) {
                RankEquipListItem(it, selectedRank, toEquipDetail, navViewModel)
            }
            item {
                CommonSpacer()
            }
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
    toEquipDetail: (Int) -> Unit,
    navViewModel: NavViewModel,
) {
    val context = LocalContext.current

    //RANK
    val selected = unitPromotion.promotionLevel == selectedRank.value
    val rank = unitPromotion.promotionLevel
    Column(
        modifier = Modifier
            .clickable {
                VibrateUtil(context).single()
                selectedRank.value = unitPromotion.promotionLevel
                navViewModel.selectRank.postValue(unitPromotion.promotionLevel)
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SelectText(
            selected = selected,
            text = getFormatText(rank),
            textColor = if (selected) MaterialTheme.colors.onPrimary else getRankColor(rank = rank),
            selectedColor = getRankColor(rank = rank),
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(top = Dimen.mediuPadding)
        )
        //图标列表
        val allIds = unitPromotion.getRowIds()
        Row(
            modifier = Modifier
                .padding(Dimen.mediuPadding)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            allIds.forEach {
                IconCompose(data = getEquipIconUrl(it)) {
                    toEquipDetail(it)
                }
            }
        }
    }

}