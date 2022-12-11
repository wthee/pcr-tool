package cn.wthee.pcrtool.ui.equip

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.EquipmentMaxData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.EquipmentIdWithOdd
import cn.wthee.pcrtool.data.model.FilterEquipment
import cn.wthee.pcrtool.ui.common.CommonSpacer
import cn.wthee.pcrtool.ui.common.FabCompose
import cn.wthee.pcrtool.ui.common.MainText
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.colorWhite
import cn.wthee.pcrtool.ui.tool.quest.AreaItem
import cn.wthee.pcrtool.ui.tool.quest.QuestPager
import cn.wthee.pcrtool.viewmodel.EquipmentViewModel


/**
 * 装备素材掉落信息
 *
 * @param equipId 装备编号
 */
@Composable
fun EquipMaterialDetail(
    equipId: Int,
    equipmentViewModel: EquipmentViewModel = hiltViewModel(),
) {

    val dropInfoList =
        equipmentViewModel.getDropInfos(equipId).collectAsState(initial = null).value
    val basicInfo =
        equipmentViewModel.getEquip(equipId).collectAsState(initial = EquipmentMaxData()).value

    val starIds = FilterEquipment.getStarIdList()
    val loved = remember {
        mutableStateOf(starIds.contains(equipId))
    }
    val text = if (loved.value) "" else stringResource(id = R.string.love_equip_material)


    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //基本信息
            MainText(
                text = basicInfo.equipmentName,
                modifier = Modifier
                    .padding(top = Dimen.largePadding),
                color = if (loved.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                selectable = true
            )
            //掉落信息
            if (dropInfoList != null) {
                if (dropInfoList.isNotEmpty()) {
                    QuestPager(dropInfoList, equipId)
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        MainText(text = stringResource(id = R.string.tip_no_equip_get_area))
                    }
                }
            } else {
                //加载中
                val odds = arrayListOf<EquipmentIdWithOdd>()
                for (i in 0..9) {
                    odds.add(EquipmentIdWithOdd())
                }
                LazyColumn {
                    items(odds.size) {
                        AreaItem(
                            -1,
                            odds,
                            "30-15",
                            colorWhite
                        )
                    }
                    item {
                        CommonSpacer()
                    }
                }
            }
        }


        //装备素材收藏
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
