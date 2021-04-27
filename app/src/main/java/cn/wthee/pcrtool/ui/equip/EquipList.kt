package cn.wthee.pcrtool.ui.equip

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import cn.wthee.pcrtool.data.model.FilterEquipment
import cn.wthee.pcrtool.ui.compose.IconCompose
import cn.wthee.pcrtool.ui.compose.getEquipIconUrl
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.viewmodel.EquipmentViewModel

/**
 * 装备列表
 */
@ExperimentalFoundationApi
@Composable
fun EquipList(
    viewModel: EquipmentViewModel = hiltNavGraphViewModel(),
    toEquipDetail: (Int) -> Unit
) {
    val filter = FilterEquipment()
    viewModel.getEquips(filter, "")
    val equips = viewModel.equips.observeAsState().value ?: listOf()

    LazyVerticalGrid(cells = GridCells.Fixed(4)) {
        items(equips) { equip ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimen.smallPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconCompose(
                    data = getEquipIconUrl(equip.equipmentId),
                    modifier = Modifier
                        .size(Dimen.iconSize)
                        .clickable {
                            toEquipDetail(equip.equipmentId)
                        }
                )
                //装备名称
                Text(
                    text = equip.equipmentName,
                    style = MaterialTheme.typography.caption,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(Dimen.smallPadding)
                )
            }

        }
    }
}

