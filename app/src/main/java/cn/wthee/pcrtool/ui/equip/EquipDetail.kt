package cn.wthee.pcrtool.ui.equip

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.Attr
import cn.wthee.pcrtool.data.db.view.EquipmentMaxData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.FilterEquipment
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.PreviewBox
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.FadeAnimation
import cn.wthee.pcrtool.utils.ImageResourceHelper
import cn.wthee.pcrtool.utils.ImageResourceHelper.Companion.UNKNOWN_EQUIP_ID
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
    EquipDetail(equipId, equipMaxData, toEquipMaterial)
}

@Composable
private fun EquipDetail(
    equipId: Int,
    equipMaxData: EquipmentMaxData,
    toEquipMaterial: (Int) -> Unit
) {
    val starIds = FilterEquipment.getStarIdList()
    val loved = remember {
        mutableStateOf(starIds.contains(equipId))
    }

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
                AttrList(attrs = equipMaxData.attr.allNotZero())

            }
            //合成素材
            EquipMaterialList(equipMaxData, toEquipMaterial)
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
 * @param equip 装备信息
 */
@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun EquipMaterialList(
    equip: EquipmentMaxData,
    toEquipMaterial: (Int) -> Unit,
    equipmentViewModel: EquipmentViewModel = hiltViewModel()
) {
    val materialList =
        equipmentViewModel.getEquipInfos(equip).collectAsState(initial = arrayListOf()).value
    val starIds = remember {
        mutableStateOf(arrayListOf<Int>())
    }
    LaunchedEffect(MainActivity.navSheetState.currentValue) {
        starIds.value = FilterEquipment.getStarIdList()
    }

    Column {
        MainText(
            text = stringResource(R.string.equip_material),
            modifier = Modifier
                .padding(top = Dimen.largePadding, bottom = Dimen.smallPadding)
                .align(Alignment.CenterHorizontally)
        )
        //装备合成素材
        FadeAnimation(visible = equip.equipmentId != UNKNOWN_EQUIP_ID) {
            VerticalGrid(maxColumnWidth = Dimen.iconSize * 2) {
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
}


@Preview
@Composable
private fun EquipDetailPreview() {
    PreviewBox {
        EquipDetail(
            equipId = 0,
            equipMaxData = EquipmentMaxData(1, "?", "", "?", 1, attr = Attr().random()),
            toEquipMaterial = {})
    }
}