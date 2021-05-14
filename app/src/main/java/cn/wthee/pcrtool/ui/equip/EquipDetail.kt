package cn.wthee.pcrtool.ui.equip

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import cn.wthee.pcrtool.data.db.view.EquipmentIdWithOdd
import cn.wthee.pcrtool.data.db.view.EquipmentMaxData
import cn.wthee.pcrtool.data.db.view.allNotZero
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shapes
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
    //收藏状态
    val filter = navViewModel.filterEquip.observeAsState()
    val loved = remember {
        mutableStateOf(filter.value?.starIds?.contains(equipId) ?: false)
    }
    val text = if (loved.value) "" else stringResource(id = R.string.title_love)

    equipMaxData?.let {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(Dimen.smallPadding)) {
                MainText(
                    text = it.equipmentName,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimen.smallPadding)
                ) {
                    IconCompose(data = getEquipIconUrl(equipId))
                    Subtitle2(
                        text = it.getDesc(),
                        modifier = Modifier.padding(start = Dimen.mediuPadding)
                    )
                }
                //属性
                AttrList(attrs = it.attr.allNotZero())
                //合成素材
                SlideAnimation {
                    EquipMaterialList(it)
                }
            }
            Box(modifier = Modifier.align(Alignment.BottomEnd)) {
                //装备收藏
                FabCompose(
                    iconType = if (loved.value) MainIconType.LOVE_FILL else MainIconType.LOVE_LINE,
                    modifier = Modifier.padding(
                        end = Dimen.fabMarginEnd,
                        start = Dimen.fabMargin,
                        top = Dimen.fabMargin,
                        bottom = Dimen.fabMargin,
                    ),
                    text = text
                ) {
                    filter.value?.addOrRemove(equipId)
                    loved.value = !loved.value
                }
            }

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
            itemsIndexed(data) { _, material ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    //选中判断
                    val color =
                        if (clickId.value == material.id) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface
                    Box {
                        IconCompose(data = getEquipIconUrl(material.id)) {
                            clickId.value = material.id
                            equipmentViewModel.getDropInfos(material.id)
                        }
                        if (clickId.value == material.id) {
                            Spacer(
                                modifier = Modifier
                                    .size(Dimen.iconSize)
                                    .background(
                                        colorResource(id = R.color.alpha_primary),
                                        Shapes.small
                                    )
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
    val alpha = if (loading) 1f else 0f
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        LinearProgressIndicator(
            modifier = Modifier
                .width(Dimen.iconSize)
                .alpha(alpha)
        )
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
    val spanCount = 6
    val placeholder = EquipmentIdWithOdd()
    val newList = getGridData(spanCount = spanCount, list = odds, placeholder = placeholder)
    Column(Modifier.padding(top = Dimen.mediuPadding)) {
        newList.forEachIndexed { index, _ ->
            if (index % spanCount == 0) {
                AreaEquipItem(selectedId, newList.subList(index, index + spanCount), placeholder)
            }
        }
    }
}

@Composable
private fun AreaEquipItem(
    selectedId: Int,
    odds: List<EquipmentIdWithOdd>,
    placeholder: EquipmentIdWithOdd
) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Dimen.smallPadding)
    ) {
        odds.forEach {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val alpha = if (it == placeholder) 0f else 1f
                val selected = selectedId == it.eid
                Box {
                    IconCompose(
                        data = getEquipIconUrl(it.eid),
                        modifier = Modifier.alpha(alpha)
                    )
                    if (selected) {
                        Spacer(
                            modifier = Modifier
                                .size(Dimen.iconSize)
                                .background(colorResource(id = R.color.alpha_primary), Shapes.small)
                        )
                    }
                }
                Text(
                    text = "${it.odd}%",
                    color = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface,
                    fontWeight = if (selected) FontWeight.Black else FontWeight.Light,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.alpha(alpha)
                )
            }
        }
    }
}