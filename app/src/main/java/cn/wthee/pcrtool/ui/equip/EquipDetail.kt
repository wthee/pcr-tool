package cn.wthee.pcrtool.ui.equip

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.Attr
import cn.wthee.pcrtool.data.db.view.EquipmentMaxData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.EquipmentMaterial
import cn.wthee.pcrtool.data.model.FilterEquipment
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.ImageResourceHelper
import cn.wthee.pcrtool.utils.ImageResourceHelper.Companion.UNKNOWN_EQUIP_ID
import cn.wthee.pcrtool.utils.spanCount
import cn.wthee.pcrtool.viewmodel.EquipmentViewModel


/**
 * 装备详情
 *
 * @param equipId 装备编号
 */
@Composable
fun EquipMainInfo(
    equipId: Int,
    toEquipMaterial: (Int) -> Unit,
    equipmentViewModel: EquipmentViewModel = hiltViewModel()
) {
    val equipMaxData =
        equipmentViewModel.getEquip(equipId).collectAsState(initial = EquipmentMaxData()).value
    val materialList =
        equipmentViewModel.getEquipInfos(equipMaxData).collectAsState(initial = arrayListOf()).value
    val starIds = FilterEquipment.getStarIdList()
    val loved = remember {
        mutableStateOf(starIds.contains(equipId))
    }

    EquipDetail(equipId, equipMaxData, materialList, loved, toEquipMaterial)
}

@Composable
private fun EquipDetail(
    equipId: Int,
    equipMaxData: EquipmentMaxData,
    materialList: ArrayList<EquipmentMaterial>,
    loved: MutableState<Boolean>,
    toEquipMaterial: (Int) -> Unit
) {
    val text = if (loved.value) "" else stringResource(id = R.string.love_equip)

    Box(
        modifier = Modifier
            .padding(top = Dimen.largePadding)
            .fillMaxSize()
    ) {

        Column {
            if (equipMaxData.equipmentId != UNKNOWN_EQUIP_ID) {
                if (BuildConfig.DEBUG) {
                    Subtitle1(
                        text = equipMaxData.equipmentId.toString(),
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )
                }
                MainText(
                    text = equipMaxData.equipmentName,
                    color = if (loved.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    selectable = true
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimen.largePadding)
                ) {
                    IconCompose(
                        data = ImageResourceHelper.getInstance().getEquipPic(equipId)
                    )
                    Subtitle2(
                        text = equipMaxData.getDesc(),
                        modifier = Modifier.padding(start = Dimen.mediumPadding),
                        selectable = true
                    )
                }
                //属性
                AttrList(attrs = equipMaxData.attr.allNotZero(isPreview = LocalInspectionMode.current))

            }
            //合成素材
            EquipMaterialList(materialList, toEquipMaterial)
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
            FilterEquipment.addOrRemove(equipId)
            loved.value = !loved.value
        }
    }
}

/**
 * 装备合成素材
 *
 * @param materialList 装备素材信息
 */
@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun EquipMaterialList(
    materialList: ArrayList<EquipmentMaterial>,
    toEquipMaterial: (Int) -> Unit
) {
    val starIds = remember {
        mutableStateOf(arrayListOf<Int>())
    }
    if (!LocalInspectionMode.current) {
        LaunchedEffect(MainActivity.navSheetState.currentValue) {
            starIds.value = FilterEquipment.getStarIdList()
        }
    }


    Column {
        MainText(
            text = stringResource(R.string.equip_material),
            modifier = Modifier
                .padding(top = Dimen.largePadding, bottom = Dimen.smallPadding)
                .align(Alignment.CenterHorizontally)
        )
        //装备合成素材
        VerticalGrid(spanCount = (Dimen.iconSize + Dimen.largePadding * 2).spanCount) {
            materialList.forEach { material ->
                val loved = starIds.value.contains(material.id)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = Dimen.largePadding,
                            end = Dimen.largePadding,
                            bottom = Dimen.largePadding
                        ), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconCompose(
                        data = ImageResourceHelper.getInstance().getEquipPic(material.id)
                    ) {
                        toEquipMaterial(material.id)
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


@CombinedPreviews
@Composable
private fun EquipDetailPreview() {
    val loved = remember {
        mutableStateOf(true)
    }
    PreviewLayout {
        EquipDetail(
            equipId = 0,
            equipMaxData = EquipmentMaxData(1001, "?", "", "?", 1, attr = Attr().random()),
            materialList = arrayListOf(EquipmentMaterial()),
            loved = loved,
            toEquipMaterial = {})
    }
}