package cn.wthee.pcrtool.ui.equip

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.EquipmentMaxData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.EquipmentIdWithOdd
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.mainSP
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.colorWhite
import cn.wthee.pcrtool.ui.tool.quest.QuestPager
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.GsonUtil
import cn.wthee.pcrtool.utils.ImageResourceHelper
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

    val filter = navViewModel.filterEquip.observeAsState()
    val loved = remember {
        mutableStateOf(false)
    }
    val text = if (loved.value) "" else stringResource(id = R.string.love_equip_material)

    filter.value?.let { filterValue ->
        filterValue.starIds =
            GsonUtil.fromJson(mainSP().getString(Constants.SP_STAR_EQUIP, "")) ?: arrayListOf()
        loved.value = filterValue.starIds.contains(equipId)
    }

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
            filter.value?.addOrRemove(equipId)
            loved.value = !loved.value
        }
    }

}



/**
 * 掉落区域信息
 * @param selectedId unknow 随机掉落；非0 主线掉落，titleEnd显示概率；0 隐藏titleEnd
 */
@Composable
fun AreaItem(
    selectedId: Int,
    odds: List<EquipmentIdWithOdd>,
    num: String,
    color: Color
) {
    val placeholder = selectedId == -1

    val selectedOdd = odds.find {
        it.equipId == selectedId
    }
    //标题显示概率文本
    val titleEnd = if (selectedId != 0) {
        (if (selectedOdd != null && selectedOdd.odd != 0) {
            "${selectedOdd.odd}"
        } else {
            Constants.UNKNOWN
        }) + "%"
    } else {
        ""
    }

    //标题
    CommonGroupTitle(
        titleStart = num,
        titleEnd = titleEnd,
        backgroundColor = color,
        modifier = Modifier
            .padding(horizontal = Dimen.mediumPadding, vertical = Dimen.largePadding)
            .commonPlaceholder(placeholder)
    )

    VerticalGrid(
        modifier = Modifier
            .padding(
                bottom = Dimen.largePadding,
                start = Dimen.commonItemPadding,
                end = Dimen.commonItemPadding
            )
            .commonPlaceholder(placeholder),
        maxColumnWidth = Dimen.iconSize + Dimen.mediumPadding * 2
    ) {
        odds.forEach {
            Column(
                modifier = Modifier
                    .padding(bottom = Dimen.mediumPadding)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val selected = selectedId == it.equipId
                Box(contentAlignment = Alignment.Center) {
                    IconCompose(
                        data = ImageResourceHelper.getInstance()
                            .getUrl(ImageResourceHelper.ICON_EQUIPMENT, it.equipId)
                    )
                    if (selectedId != ImageResourceHelper.UNKNOWN_EQUIP_ID && it.odd == 0) {
                        SelectText(
                            selected = selected,
                            text = if (selected) "✓" else "",
                            margin = 0.dp
                        )
                    }
                }
                if (selectedId != ImageResourceHelper.UNKNOWN_EQUIP_ID && it.odd > 0) {
                    SelectText(
                        selected = selected,
                        text = "${it.odd}%"
                    )
                }
            }
        }
    }

}