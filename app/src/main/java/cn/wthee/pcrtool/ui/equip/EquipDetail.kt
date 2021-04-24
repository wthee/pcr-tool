package cn.wthee.pcrtool.ui.equip

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.view.EquipmentIdWithOdd
import cn.wthee.pcrtool.data.view.EquipmentMaxData
import cn.wthee.pcrtool.data.view.allNotZero
import cn.wthee.pcrtool.ui.compose.AttrList
import cn.wthee.pcrtool.ui.compose.IconCompose
import cn.wthee.pcrtool.ui.compose.MainTitleText
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
            //合成素材
            EquipMaterialList(it)
        }
    }
}

/**
 * 装备合成素材
 */
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
private fun EquipMaterialList(
    equip: EquipmentMaxData,
    equipmentViewModel: EquipmentViewModel = hiltNavGraphViewModel()
) {
    equipmentViewModel.getEquipInfos(equip)
    val data = equipmentViewModel.equipMaterialInfos.observeAsState().value ?: listOf()
    //点击查看的装备素材
    val clickId = remember { mutableStateOf(Constants.UNKNOWN_EQUIP_ID) }
    //默认显示第一个装备掉落信息
    if (data.isNotEmpty()) {
        clickId.value = data[0].id
        equipmentViewModel.getDropInfos(data[0].id)
    }

    Column {
        MainTitleText(
            text = stringResource(id = R.string.title_material),
            modifier = Modifier.padding(
                start = Dimen.smallPadding,
                top = Dimen.largePadding,
                bottom = Dimen.mediuPadding
            )
        )
        //装备合成素材
        LazyVerticalGrid(
            cells = GridCells.Fixed(6),
            modifier = Modifier.padding(top = Dimen.mediuPadding)
        ) {
            itemsIndexed(data) { index, material ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    //选中判断
                    val color =
                        if (clickId.value == material.id) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface
                    Box {
                        IconCompose(
                            data = getEquipIconUrl(material.id),
                            modifier = Modifier
                                .size(Dimen.smallIconSize)
                                .clickable {
                                    clickId.value = material.id
                                    equipmentViewModel.getDropInfos(material.id)
                                }
                        )
                        androidx.compose.animation.AnimatedVisibility(visible = clickId.value == material.id) {
                            Spacer(
                                modifier = Modifier
                                    .size(Dimen.smallIconSize)
                                    .background(colorResource(id = R.color.alpha_primary))
                            )
                        }
                    }
                    Text(
                        text = material.count.toString(),
                        color = color,
                        style = MaterialTheme.typography.caption
                    )
                }
            }
        }
        //装备素材掉落信息
        MainTitleText(
            text = stringResource(id = R.string.drop_info),
            modifier = Modifier.padding(
                start = Dimen.smallPadding,
                top = Dimen.largePadding,
                bottom = Dimen.mediuPadding
            )
        )
        EquipDropAreaList()
    }

}

/**
 * 装备掉落的地区列表
 */
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
private fun EquipDropAreaList(
    equipmentViewModel: EquipmentViewModel = hiltNavGraphViewModel()
) {
    val dropInfoList = equipmentViewModel.dropInfo.observeAsState().value ?: listOf()
    val selectId =
        equipmentViewModel.selectId.observeAsState().value ?: Constants.UNKNOWN_EQUIP_ID
    val loading = equipmentViewModel.loading.observeAsState().value ?: false
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        AnimatedVisibility(visible = loading) {
            LinearProgressIndicator(modifier = Modifier.width(Dimen.iconSize))
        }
        LazyColumn {
            items(dropInfoList) {
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
                    AreaEquipList(selectId, it.getAllOdd())
                }
            }
        }
    }

}

/**
 *  地区的装备掉落列表
 */
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
private fun AreaEquipList(
    selectedId: Int,
    odds: ArrayList<EquipmentIdWithOdd>
) {
    Column {
        AreaEquipItem(selectedId, odds.subList(0, 6))
        AreaEquipItem(selectedId, odds.subList(6, 12))
    }
}

@ExperimentalAnimationApi
@Composable
private fun AreaEquipItem(
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
                Box {
                    IconCompose(
                        data = getEquipIconUrl(equipmentIdWithOdd.eid),
                        modifier = Modifier
                            .size(Dimen.smallIconSize)
                            .alpha(alpha)
                    )
                    androidx.compose.animation.AnimatedVisibility(visible = selected) {
                        Spacer(
                            modifier = Modifier
                                .size(Dimen.smallIconSize)
                                .background(colorResource(id = R.color.alpha_primary))
                        )
                    }
                }
                Text(
                    text = "${equipmentIdWithOdd.odd}%",
                    color = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface,
                    fontWeight = if (selected) FontWeight.Black else FontWeight.Light,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.alpha(alpha)
                )
            }
        }
    }
}