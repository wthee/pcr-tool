package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.data.db.view.EquipmentMaterial
import cn.wthee.pcrtool.data.model.FilterEquipment
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.utils.ImageResourceHelper
import cn.wthee.pcrtool.viewmodel.EquipmentViewModel

@ExperimentalFoundationApi
@Composable
fun AllCharacterRankEquipCount(
    toEquipMaterial: (Int) -> Unit,
    equipmentViewModel: EquipmentViewModel = hiltViewModel()
) {
    val rankEquipMaterials =
        equipmentViewModel.getEquipByRank(-1, 1, 2).collectAsState(
            initial = arrayListOf()
        ).value

    if (rankEquipMaterials.isEmpty()) {
        navViewModel.loading.postValue(true)
    }

    if (rankEquipMaterials.isNotEmpty()) {
        navViewModel.loading.postValue(false)
        LazyColumn {
            items(items = rankEquipMaterials) { item ->
                EquipCountItem(item, FilterEquipment(), toEquipMaterial)
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
    filter: FilterEquipment,
    toEquipMaterial: (Int) -> Unit
) {
    val loved = filter.starIds.contains(item.id)
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconCompose(
            data = ImageResourceHelper.getInstance()
                .getUrl(ImageResourceHelper.ICON_EQUIPMENT, item.id)
        ) {
            toEquipMaterial(item.id)
        }
        Column {
            Subtitle2(text = item.id.toString())
            Subtitle1(text = item.name)
            SelectText(
                selected = loved,
                text = item.count.toString()
            )
        }
    }

}