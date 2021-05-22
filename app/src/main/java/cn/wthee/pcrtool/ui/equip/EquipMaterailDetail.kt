package cn.wthee.pcrtool.ui.equip

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.EquipmentIdWithOdd
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.mainSP
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shapes
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.GsonUtil
import cn.wthee.pcrtool.viewmodel.EquipmentViewModel
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.SizeMode


/**
 * 装备素材信息
 */
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun EquipMaterialDeatil(
    equipId: Int,
    equipmentViewModel: EquipmentViewModel = hiltViewModel()
) {
    equipmentViewModel.getDropInfos(equipId)
    val dropInfoList = equipmentViewModel.dropInfo.observeAsState()
    navViewModel.loading.postValue(dropInfoList.value == null)
    val filter = navViewModel.filterEquip.observeAsState()
    val loved = remember {
        mutableStateOf(false)
    }
    val text = if (loved.value) "" else stringResource(id = R.string.love_equip_material)

    filter.value?.let { filterValue ->
        filterValue.starIds =
            GsonUtil.fromJson(mainSP().getString(Constants.SP_STAR_EQUIP, "")) ?: arrayListOf()
        loved.value = filterValue.starIds.contains(equipId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        dropInfoList.value?.let { list ->
            LazyColumn {
                items(list) {
                    val pre = when (it.questId / 1000000) {
                        11 -> stringResource(id = R.string.normal)
                        12 -> stringResource(id = R.string.hard)
                        13 -> stringResource(id = R.string.very_hard)
                        else -> "？"
                    }
                    //颜色
                    val color = when (it.questId / 1000000) {
                        11 -> R.color.color_map_n
                        12 -> R.color.color_map_h
                        13 -> R.color.color_map_vh
                        else -> R.color.color_map_n
                    }
                    Row(
                        modifier = Modifier.padding(
                            start = Dimen.mediuPadding,
                            top = Dimen.largePadding,
                        )
                    ) {
                        MainTitleText(
                            text = it.getNum(),
                        )
                        MainTitleText(
                            text = pre,
                            modifier = Modifier.padding(start = Dimen.mediuPadding),
                            backgroundColor = colorResource(id = color)
                        )
                    }
                    AreaEquipList(equipId, it.getAllOdd())
                }
            }
        }
        //装备收藏
        FabCompose(
            iconType = if (loved.value) MainIconType.LOVE_FILL else MainIconType.LOVE_LINE,
            modifier = Modifier
                .padding(
                    end = Dimen.fabMarginEnd,
                    start = Dimen.fabMargin,
                    top = Dimen.fabMargin,
                    bottom = Dimen.fabMargin,
                )
                .align(Alignment.BottomEnd),
            text = text
        ) {
            filter.value?.addOrRemove(equipId)
            loved.value = !loved.value
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

    FlowRow(
        modifier = Modifier.padding(Dimen.mediuPadding),
        mainAxisSize = SizeMode.Expand,
        mainAxisSpacing = Dimen.largePadding,
        crossAxisSpacing = Dimen.mediuPadding,
    ) {
        newList.forEach {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val alpha = if (it == placeholder) 0f else 1f
                val selected = selectedId == it.eid
                Box {
                    if (alpha == 1f) {
                        IconCompose(data = getEquipIconUrl(it.eid))
                    } else {
                        CommonIconSpacer()
                    }
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
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.alpha(alpha)
                )
            }
        }
    }
}