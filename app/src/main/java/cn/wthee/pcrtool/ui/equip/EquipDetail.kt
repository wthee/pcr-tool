package cn.wthee.pcrtool.ui.equip

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.view.EquipmentIdWithOdd
import cn.wthee.pcrtool.data.view.EquipmentMaterial
import cn.wthee.pcrtool.data.view.EquipmentMaxData
import cn.wthee.pcrtool.data.view.allNotZero
import cn.wthee.pcrtool.ui.compose.AttrList
import cn.wthee.pcrtool.ui.compose.IconCompose
import cn.wthee.pcrtool.ui.compose.getEquipIconUrl
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.viewmodel.EquipmentViewModel


/**
 * 装备详情
 */
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun EquipMainInfo(equipId: Int, equipmentViewModel: EquipmentViewModel = hiltNavGraphViewModel()) {
    equipmentViewModel.getEquip(equipId)
    val equipMaxData = equipmentViewModel.equip.observeAsState().value
    //点击查看的装备素材
    val materialState = remember { mutableStateOf(EquipmentMaterial()) }
    val loadedState = remember { mutableStateOf(false) }

    equipMaxData?.let {
        Column(modifier = Modifier.padding(Dimen.smallPadding)) {
            Text(
                text = it.equipmentName,
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.primary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimen.smallPadding)
            ) {
                IconCompose(
                    data = getEquipIconUrl(equipId),
                    modifier = Modifier.size(Dimen.iconSize)
                )
                Text(
                    text = it.getDesc(),
                    style = MaterialTheme.typography.subtitle2,
                    modifier = Modifier.padding(start = Dimen.mediuPadding)
                )
            }
            //属性
            AttrList(attrs = it.attr.allNotZero())
            //合成素材，点击时更新 materialState
            EquipMaterialList(materialState, loadedState, it, equipmentViewModel)
            //素材掉落
            EquipDropAreaList(materialState, loadedState)
        }
    }
}

/**
 * 装备合成素材
 */
@ExperimentalFoundationApi
@Composable
private fun EquipMaterialList(
    materialState: MutableState<EquipmentMaterial>,
    loadedState: MutableState<Boolean>,
    equip: EquipmentMaxData,
    equipmentViewModel: EquipmentViewModel = hiltNavGraphViewModel()
) {
    equipmentViewModel.getEquipInfos(equip)
    val data = equipmentViewModel.equipMaterialInfos.observeAsState().value ?: listOf()
    LazyVerticalGrid(
        cells = GridCells.Fixed(5),
        modifier = Modifier.padding(top = Dimen.mediuPadding)
    ) {
        itemsIndexed(data) { index, material ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(Dimen.mediuPadding)
            ) {
                IconCompose(
                    data = getEquipIconUrl(material.id),
                    modifier = Modifier
                        .size(Dimen.iconSize)
                        .clickable {
                            loadedState.value = false
                            materialState.value = material
                        }
                )
                Text(
                    text = material.count.toString(),
                    color = if (materialState.value.id == material.id) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface,
                    style = MaterialTheme.typography.caption
                )
            }
            //默认显示第一个装备素材掉落信息
            if (materialState.value.id == Constants.UNKNOWN_EQUIP_ID) materialState.value = material
        }
    }
}

/**
 * 装备掉落的地区列表
 */
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
private fun EquipDropAreaList(
    materialState: MutableState<EquipmentMaterial>,
    loadedState: MutableState<Boolean>,
    equipmentViewModel: EquipmentViewModel = hiltNavGraphViewModel()
) {
    equipmentViewModel.getDropInfos(materialState.value.id)
    val dropInfo = equipmentViewModel.dropInfo.observeAsState().value ?: listOf()
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        AnimatedVisibility(visible = !loadedState.value) {
            CircularProgressIndicator(
                modifier = Modifier.size(Dimen.fabIconSize),
                strokeWidth = 2.dp
            )
        }
        LazyColumn {
            items(dropInfo) {
                val pre = when (it.questId / 1000000) {
                    11 -> "N"
                    12 -> "H"
                    13 -> "VH"
                    else -> ""
                }
                //颜色
                val color = when (it.questId / 1000000) {
                    11 -> R.color.color_map_n
                    12 -> R.color.color_map_h
                    13 -> R.color.color_map_vh
                    else -> R.color.color_map_n
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = pre + "-" + it.getNum(),
                        style = MaterialTheme.typography.h6,
                        color = colorResource(id = color)
                    )
                    AreaEquipList(loadedState, materialState.value.id, it.getAllOdd())
                }

            }
        }
    }

}

/**
 *  地区的装备掉落列表
 */
@ExperimentalFoundationApi
@Composable
private fun AreaEquipList(
    loadedState: MutableState<Boolean>,
    selectedId: Int,
    odds: ArrayList<EquipmentIdWithOdd>
) {
    Column {
        AreaEquipItem(loadedState, selectedId, odds.subList(0, 5))
        AreaEquipItem(loadedState, selectedId, odds.subList(5, 10))
    }
}

@Composable
private fun AreaEquipItem(
    loadedState: MutableState<Boolean>,
    selectedId: Int,
    odds: List<EquipmentIdWithOdd>
) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Dimen.smallPadding)
    ) {
        odds.forEachIndexed { index, equipmentIdWithOdd ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val alpha = if (equipmentIdWithOdd.odd > 0) 1f else 0f
                val selected = selectedId == equipmentIdWithOdd.eid
                IconCompose(
                    data = getEquipIconUrl(equipmentIdWithOdd.eid),
                    modifier = Modifier
                        .size(Dimen.smallIconSize)
                        .alpha(alpha)
                )
                Text(
                    text = "${equipmentIdWithOdd.odd}%",
                    color = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface,
                    fontWeight = if (selected) FontWeight.Black else FontWeight.Light,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.alpha(alpha)
                )
            }
            //fixme 加载完成监听
            if (index == 4) {
                loadedState.value = true
            }
        }
    }
}