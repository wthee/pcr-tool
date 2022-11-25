package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.data.model.EquipmentMaterial
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.common.CommonSpacer
import cn.wthee.pcrtool.ui.common.IconCompose
import cn.wthee.pcrtool.ui.common.Subtitle1
import cn.wthee.pcrtool.utils.ImageResourceHelper
import cn.wthee.pcrtool.viewmodel.EquipmentViewModel

/**
 * 全角色装备需求统计
 */
@Composable
fun AllCharacterRankEquipCount(
    toEquipMaterial: (Int) -> Unit,
    equipmentViewModel: EquipmentViewModel = hiltViewModel()
) {
    val rankEquipMaterials =
        equipmentViewModel.getEquipByRank(0, 0, 0).collectAsState(
            initial = arrayListOf()
        ).value

    if (rankEquipMaterials.isEmpty()) {
        navViewModel.loading.postValue(true)
    }

    if (rankEquipMaterials.isNotEmpty()) {
        navViewModel.loading.postValue(false)
        LazyVerticalGrid(columns = GridCells.Fixed(4)) {
            items(
                items = rankEquipMaterials,
                key = {
                    it.id
                }
            ) { item ->
                EquipCountItem(item, toEquipMaterial)
            }
            items(5) {
                CommonSpacer()
            }
        }
    }
}

@Composable
private fun EquipCountItem(
    item: EquipmentMaterial,
    toEquipMaterial: (Int) -> Unit
) {
    val equipIcon: @Composable () -> Unit by remember {
        mutableStateOf(
            {
                IconCompose(
                    data = ImageResourceHelper.getInstance()
                        .getUrl(ImageResourceHelper.ICON_EQUIPMENT, item.id)
                ) {
                    toEquipMaterial(item.id)
                }
            }
        )
    }

    val equipCount: @Composable () -> Unit by remember {
        mutableStateOf(
            {
                Subtitle1(text = item.count.toString())
            }
        )
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        equipIcon()
        equipCount()
    }

}