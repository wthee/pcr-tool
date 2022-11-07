package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.data.db.view.CharacterExtraEquipData
import cn.wthee.pcrtool.ui.common.CommonSpacer
import cn.wthee.pcrtool.ui.common.IconCompose
import cn.wthee.pcrtool.ui.common.Subtitle2
import cn.wthee.pcrtool.ui.common.VerticalGrid
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.colorWhite
import cn.wthee.pcrtool.utils.ImageResourceHelper
import cn.wthee.pcrtool.utils.ImageResourceHelper.Companion.ICON_EXTRA_EQUIPMENT
import cn.wthee.pcrtool.utils.intArrayList
import cn.wthee.pcrtool.viewmodel.ExtraEquipmentViewModel

@Composable
fun CharacterExtraEquip(
    scrollState: LazyListState,
    unitId: Int,
    toExtraEquipDetail: (Int) -> Unit,
    extraEquipmentViewModel: ExtraEquipmentViewModel = hiltViewModel()
) {

    val equipList = extraEquipmentViewModel.getCharacterExtraEquipList(unitId).collectAsState(
        initial = null
    ).value

    Box(modifier = Modifier.fillMaxSize()) {
        if(equipList != null){
            LazyColumn(state = scrollState) {
                item {
                    //TODO 适用装备
                }
                items(equipList) {
                    CharacterExtraEquipItem(it, toExtraEquipDetail)
                }
                item {
                    CommonSpacer()
                }
            }
        }else{
            //TODO 未实装提示
        }
    }
}

@Composable
private fun CharacterExtraEquipItem(
    data: CharacterExtraEquipData,
    toExtraEquipDetail: (Int) -> Unit,
) {

    val equipIdList = data.exEquipmentIds.intArrayList

    //分组标题
    Row(
        modifier = Modifier
            .padding(Dimen.largePadding)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconCompose(
            data = ImageResourceHelper.getInstance()
                .getUrl(
                    ImageResourceHelper.ICON_EXTRA_EQUIPMENT_CATEGORY,
                    data.category
                ),
            size = Dimen.smallIconSize,
        )
        Row(
            modifier = Modifier
                .padding(start = Dimen.smallPadding)
                .weight(1f)
                .background(
                    MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.extraSmall
                )
                .padding(horizontal = Dimen.mediumPadding)
        ) {
            Subtitle2(
                text = data.categoryName,
                color = colorWhite
            )
            Spacer(modifier = Modifier.weight(1f))
            Subtitle2(
                text = "${equipIdList.size}",
                color = colorWhite
            )
        }
    }
    VerticalGrid(
        modifier = Modifier.padding(
            top = Dimen.mediumPadding,
            start = Dimen.mediumPadding,
            end = Dimen.mediumPadding
        ),
        maxColumnWidth = Dimen.iconSize + Dimen.mediumPadding * 2
    ) {
        equipIdList.forEach { equipId ->
            Column(
                modifier = Modifier
                    .padding(
                        bottom = Dimen.mediumPadding
                    )
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconCompose(
                    data = ImageResourceHelper.getInstance().getUrl(ICON_EXTRA_EQUIPMENT, equipId)
                ) {
                    toExtraEquipDetail(equipId)
                }
            }
        }
    }
}