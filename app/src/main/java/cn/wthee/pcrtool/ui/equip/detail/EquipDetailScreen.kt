package cn.wthee.pcrtool.ui.equip.detail

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.Attr
import cn.wthee.pcrtool.data.db.view.EquipmentMaxData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.EquipmentMaterial
import cn.wthee.pcrtool.ui.components.*
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.ImageRequestHelper.Companion.UNKNOWN_EQUIP_ID
import kotlinx.coroutines.launch


/**
 * 装备详情
 *
 * @param equipId 装备编号
 */
@Composable
fun EquipDetailScreen(
    equipId: Int,
    toEquipMaterial: (Int, String) -> Unit,
    toEquipUnit: (Int) -> Unit,
    equipDetailViewModel: EquipDetailViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val uiState by equipDetailViewModel.uiState.collectAsStateWithLifecycle()


    //初始收藏信息
    LifecycleEffect(Lifecycle.Event.ON_RESUME) {
        equipDetailViewModel.reloadFavoriteList()
    }

    MainScaffold(
        fab = {
            //装备收藏
            MainSmallFab(
                iconType = if (uiState.favorite) MainIconType.FAVORITE_FILL else MainIconType.FAVORITE_LINE,
                text = if (uiState.favorite) "" else stringResource(id = R.string.favorite_equip)
            ) {
                scope.launch {
                    equipDetailViewModel.updateFavoriteId()
                }
            }

            //关联角色
            if (uiState.unitIdList.isNotEmpty()) {
                MainSmallFab(
                    iconType = MainIconType.CHARACTER,
                    text = uiState.unitIdList.size.toString()
                ) {
                    toEquipUnit(equipId)
                }
            }
        }
    ) {
        Column {
            StateBox(stateType = uiState.loadingState) {
                uiState.equipData?.let {
                    EquipDetailContent(
                        equipId = equipId,
                        equipMaxData = it,
                        favorite = uiState.favorite
                    )
                }
            }

            //合成素材
            StateBox(stateType = uiState.materialLoadingState) {
                EquipMaterialListContent(
                    materialList = uiState.materialList,
                    favoriteIdList = uiState.favoriteIdList,
                    toEquipMaterial = toEquipMaterial
                )
            }
        }

    }
}

@Composable
private fun EquipDetailContent(
    equipId: Int,
    equipMaxData: EquipmentMaxData,
    favorite: Boolean,
) {
    Column {
        if (equipId != UNKNOWN_EQUIP_ID) {
            MainText(
                text = equipMaxData.equipmentName,
                color = if (favorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(top = Dimen.largePadding)
                    .align(Alignment.CenterHorizontally),
                selectable = true
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimen.largePadding)
            ) {
                MainIcon(
                    data = ImageRequestHelper.getInstance()
                        .getUrl(ImageRequestHelper.ICON_EQUIPMENT, equipId)
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
    }
}

/**
 * 装备合成素材
 *
 * @param materialList 装备素材信息
 */
@SuppressLint("MutableCollectionMutableState")
@Composable
private fun EquipMaterialListContent(
    materialList: List<EquipmentMaterial>,
    favoriteIdList: List<Int>,
    toEquipMaterial: (Int, String) -> Unit,
) {

    Column(modifier = Modifier.padding(horizontal = Dimen.commonItemPadding)) {
        MainText(
            text = stringResource(R.string.equip_material),
            modifier = Modifier
                .padding(top = Dimen.largePadding, bottom = Dimen.mediumPadding)
                .align(Alignment.CenterHorizontally)
        )
        //装备合成素材
        VerticalStaggeredGrid(itemWidth = Dimen.iconSize, contentPadding = Dimen.mediumPadding) {
            materialList.forEach { material ->
                val favorite = favoriteIdList.contains(material.id)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MainIcon(
                        data = ImageRequestHelper.getInstance()
                            .getUrl(ImageRequestHelper.ICON_EQUIPMENT, material.id)
                    ) {
                        toEquipMaterial(material.id, material.name)
                    }
                    SelectText(
                        selected = favorite,
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
    PreviewLayout {
        EquipDetailContent(
            equipId = 0,
            equipMaxData = EquipmentMaxData(1001, "?", "", "?", 1, attr = Attr().random()),
            favorite = true
        )
    }
}

@CombinedPreviews
@Composable
private fun EquipmentMaterialPreview() {
    PreviewLayout {
        EquipMaterialListContent(
            materialList = arrayListOf(EquipmentMaterial(id = 1)),
            favoriteIdList = arrayListOf(1),
            toEquipMaterial = { _, _ -> }
        )
    }
}