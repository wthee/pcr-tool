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
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.CharacterExtraEquipData
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.common.*
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
                    //标题
                    MainText(
                        text = stringResource(R.string.unit_extra_equip_slot),
                        modifier = Modifier
                            .padding(Dimen.largePadding)
                            .align(Alignment.Center)
                    )
                }
                items(equipList) {
                    CharacterExtraEquipItem(it, toExtraEquipDetail)
                }
                item {
                    CommonSpacer()
                }
            }
        }else {
            //功能未实装
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CenterTipText(
                    stringResource(
                        id = R.string.not_installed,
                        getRegionName(MainActivity.regionType)
                    )
                )
            }
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