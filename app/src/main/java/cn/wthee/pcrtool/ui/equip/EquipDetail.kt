package cn.wthee.pcrtool.ui.equip

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.EquipmentMaxData
import cn.wthee.pcrtool.data.db.view.allNotZero
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.FilterEquipment
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.mainSP
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.GsonUtil
import cn.wthee.pcrtool.viewmodel.EquipmentViewModel
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.SizeMode


/**
 * 装备详情
 */
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun EquipMainInfo(
    equipId: Int,
    toEquipMaterail: (Int) -> Unit, equipmentViewModel: EquipmentViewModel = hiltViewModel()
) {
    equipmentViewModel.getEquip(equipId)
    val equipMaxData = equipmentViewModel.equip.observeAsState().value
    //收藏状态
    val filter = navViewModel.filterEquip.observeAsState()
    val loved = remember {
        mutableStateOf(filter.value?.starIds?.contains(equipId) ?: false)
    }
    filter.value?.let { filterValue ->
        filterValue.starIds =
            GsonUtil.fromJson(mainSP().getString(Constants.SP_STAR_EQUIP, "")) ?: arrayListOf()
        loved.value = filterValue.starIds.contains(equipId)
    }
    val text = if (loved.value) "" else stringResource(id = R.string.love_equip)

    Box(
        modifier = Modifier
            .padding(top = Dimen.mediuPadding)
            .fillMaxSize()
    ) {

        Column {
            equipMaxData?.let {
                MainText(
                    text = it.equipmentName,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimen.mediuPadding)
                ) {
                    IconCompose(data = getEquipIconUrl(equipId))
                    Subtitle2(
                        text = it.getDesc(),
                        modifier = Modifier.padding(start = Dimen.mediuPadding)
                    )
                }
                //属性
                AttrList(attrs = it.attr.allNotZero())
            }
            SlideAnimation(visible = equipMaxData != null) {
                //合成素材
                if (equipMaxData != null && filter.value != null) {
                    EquipMaterialList(equipMaxData, filter.value!!, toEquipMaterail)
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
 * 装备合成素材
 */
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
private fun EquipMaterialList(
    equip: EquipmentMaxData,
    filter: FilterEquipment,
    toEquipMaterail: (Int) -> Unit,
    equipmentViewModel: EquipmentViewModel = hiltViewModel()
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
        Spacer(
            modifier = Modifier
                .padding(Dimen.largePadding)
                .width(Dimen.smallIconSize)
                .height(Dimen.lineHeight)
                .background(MaterialTheme.colors.primary)
                .align(Alignment.CenterHorizontally)
        )
        //装备合成素材
        FlowRow(
            modifier = Modifier.padding(Dimen.mediuPadding),
            mainAxisSize = SizeMode.Expand,
            mainAxisSpacing = Dimen.largePadding,
            crossAxisSpacing = Dimen.mediuPadding
        ) {
            data.forEach { material ->
                val loved = filter.starIds.contains(material.id)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconCompose(data = getEquipIconUrl(material.id)) {
                        clickId.value = material.id
                        toEquipMaterail(material.id)
                    }
                    SelectText(
                        selected = loved,
                        text = material.count.toString()
                    )
                }
            }
        }
    }
}