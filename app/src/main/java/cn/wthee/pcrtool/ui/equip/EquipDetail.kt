package cn.wthee.pcrtool.ui.equip

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.Attr
import cn.wthee.pcrtool.data.db.view.EquipmentMaxData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.EquipmentMaterial
import cn.wthee.pcrtool.data.model.getStarEquipIdList
import cn.wthee.pcrtool.data.model.updateStarEquipId
import cn.wthee.pcrtool.ui.components.*
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.ImageRequestHelper.Companion.UNKNOWN_EQUIP_ID
import cn.wthee.pcrtool.viewmodel.EquipmentViewModel
import kotlinx.coroutines.launch


/**
 * 装备详情
 *
 * @param equipId 装备编号
 */
@Composable
fun EquipMainInfo(
    equipId: Int,
    toEquipMaterial: (Int) -> Unit,
    toEquipUnit: (Int) -> Unit,
    equipmentViewModel: EquipmentViewModel = hiltViewModel()
) {
    //装备属性
    val equipMaxDataFlow = remember {
        equipmentViewModel.getEquip(equipId)
    }
    val equipMaxData by equipMaxDataFlow.collectAsState(initial = EquipmentMaxData())
    //素材列表
    val materialListFlow = remember(equipMaxData) {
        equipmentViewModel.getEquipInfo(equipMaxData)
    }
    val materialList by materialListFlow.collectAsState(initial = arrayListOf())

    //收藏信息
    val loved = getStarEquipIdList().contains(equipId)

    //适用角色列表
    val unitIdsFlow = remember {
        equipmentViewModel.getEquipUnitList(equipId)
    }
    val unitIds by unitIdsFlow.collectAsState(initial = arrayListOf())


    EquipDetail(equipId, unitIds, equipMaxData, materialList, loved, toEquipMaterial, toEquipUnit)
}

@Composable
private fun EquipDetail(
    equipId: Int,
    unitIds: List<Int>,
    equipMaxData: EquipmentMaxData,
    materialList: ArrayList<EquipmentMaterial>,
    loved: Boolean,
    toEquipMaterial: (Int) -> Unit,
    toEquipUnit: (Int) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val text = if (loved) "" else stringResource(id = R.string.love_equip)

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(top = Dimen.largePadding)
            .fillMaxSize()
    ) {

        Column {
            if (equipId != UNKNOWN_EQUIP_ID) {
                if (BuildConfig.DEBUG) {
                    Subtitle1(
                        text = equipId.toString(),
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )
                }
                MainText(
                    text = equipMaxData.equipmentName,
                    color = if (loved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    selectable = true
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimen.largePadding)
                ) {
                    MainIcon(
                        data = ImageRequestHelper.getInstance().getEquipPic(equipId)
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

        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin),
            horizontalArrangement = Arrangement.End
        ) {
            //装备收藏
            MainSmallFab(
                iconType = if (loved) MainIconType.LOVE_FILL else MainIconType.LOVE_LINE,
                text = text
            ) {
                scope.launch {
                    updateStarEquipId(context, equipId)
                }
            }

            //关联角色
            if (unitIds.isNotEmpty()) {
                MainSmallFab(
                    iconType = MainIconType.CHARACTER,
                    text = unitIds.size.toString()
                ) {
                    toEquipUnit(equipId)
                }
            }

        }

    }
}

/**
 * 装备合成素材
 *
 * @param materialList 装备素材信息
 */
@SuppressLint("MutableCollectionMutableState")
@Composable
private fun EquipMaterialList(
    materialList: ArrayList<EquipmentMaterial>,
    toEquipMaterial: (Int) -> Unit
) {
    val starIds = getStarEquipIdList()

    Column(modifier = Modifier.padding(horizontal = Dimen.commonItemPadding)) {
        MainText(
            text = stringResource(R.string.equip_material),
            modifier = Modifier
                .padding(top = Dimen.largePadding, bottom = Dimen.mediumPadding)
                .align(Alignment.CenterHorizontally)
        )
        //装备合成素材
        VerticalGrid(itemWidth = Dimen.iconSize, contentPadding = Dimen.largePadding) {
            materialList.forEach { material ->
                val loved = starIds.contains(material.id)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            bottom = Dimen.largePadding
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MainIcon(
                        data = ImageRequestHelper.getInstance().getEquipPic(material.id)
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

/**
 * 可使用装备角色列表
 */
@Composable
fun EquipUnitList(
    equipId: Int,
    equipmentViewModel: EquipmentViewModel = hiltViewModel()
) {
    //适用角色列表
    val unitIdsFlow = remember {
        equipmentViewModel.getEquipUnitList(equipId)
    }
    val unitIds by unitIdsFlow.collectAsState(initial = arrayListOf())

    UnitList(unitIds)
}

@CombinedPreviews
@Composable
private fun EquipDetailPreview() {
    PreviewLayout {
        EquipDetail(
            equipId = 0,
            arrayListOf(),
            equipMaxData = EquipmentMaxData(1001, "?", "", "?", 1, attr = Attr().random()),
            materialList = arrayListOf(EquipmentMaterial()),
            loved = true,
            toEquipMaterial = {},
            {})
    }
}