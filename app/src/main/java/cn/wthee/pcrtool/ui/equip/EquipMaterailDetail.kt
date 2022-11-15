package cn.wthee.pcrtool.ui.equip

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.EquipmentDropInfo
import cn.wthee.pcrtool.data.db.view.EquipmentIdWithOdd
import cn.wthee.pcrtool.data.db.view.EquipmentMaxData
import cn.wthee.pcrtool.data.db.view.equipCompare
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.RandomEquipDropArea
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.mainSP
import cn.wthee.pcrtool.ui.theme.*
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.GsonUtil
import cn.wthee.pcrtool.utils.ImageResourceHelper
import cn.wthee.pcrtool.utils.intArrayList
import cn.wthee.pcrtool.viewmodel.EquipmentViewModel
import cn.wthee.pcrtool.viewmodel.RandomEquipAreaViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch


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
                    EquipDropPager(dropInfoList, equipId)
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        MainText(text = stringResource(id = R.string.tip_no_equip_get_area))
                    }
                }
            } else {
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
 * 装备掉落信息
 */
@Composable
@OptIn(ExperimentalPagerApi::class)
private fun EquipDropPager(
    dropInfoList: List<EquipmentDropInfo>,
    equipId: Int,
    randomEquipAreaViewModel: RandomEquipAreaViewModel = hiltViewModel()
) {
    val areaList =
        randomEquipAreaViewModel.getEquipArea(equipId).collectAsState(initial = null).value

    val scope = rememberCoroutineScope()
    val randomDrop = stringResource(id = R.string.random_area)
    val pagerState = rememberPagerState()
    var pagerCount = 0

    val lists = arrayListOf<List<Any>>(
        dropInfoList.filter { it.questId / 1000000 == 11 },
        dropInfoList.filter { it.questId / 1000000 == 12 },
        dropInfoList.filter { it.questId / 1000000 == 13 },
    )
    //随机掉落列表
    if (areaList != null) {
        lists.add(areaList)
    }
    val tabs = arrayListOf<String>()
    //颜色
    val colorList = arrayListOf<Color>()

    lists.forEachIndexed { index, data ->
        if (data.isNotEmpty()) {
            pagerCount++
            tabs.add(
                when (index) {
                    0 -> "Normal"
                    1 -> "Hard"
                    2 -> "Very Hard"
                    3 -> randomDrop
                    else -> "？"
                }
            )
            colorList.add(
                when (index) {
                    0 -> colorBlue
                    1 -> colorRed
                    2 -> colorPurple
                    else -> colorGreen
                }
            )
        }
    }

    //按照地图难度分类
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        //Tab
        TabRow(
            modifier = Modifier
                .padding(
                    top = Dimen.mediumPadding,
                    start = Dimen.largePadding,
                    end = Dimen.largePadding
                )
                .fillMaxWidth(tabs.size * 0.25f),
            selectedTabIndex = pagerState.currentPage,
            containerColor = Color.Transparent,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    color = colorList[pagerState.currentPage]
                )
            }
        ) {
            tabs.forEachIndexed { index, s ->
                Tab(
                    selected = index == pagerState.currentPage,
                    onClick = {
                        scope.launch {
                            pagerState.scrollToPage(index)
                        }
                    }
                ) {
                    Subtitle1(
                        text = s,
                        modifier = Modifier.padding(Dimen.smallPadding),
                        color = colorList[index],
                    )
                }
            }
        }
        //pager
        HorizontalPager(
            count = pagerCount,
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pagerIndex ->
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(
                    items = when (tabs[pagerIndex]) {
                        "Normal" -> lists[0]
                        "Hard" -> lists[1]
                        "Very Hard" -> lists[2]
                        else -> lists[3]
                    },
                    key = {
                        if (tabs[pagerIndex] != randomDrop) {
                            (it as EquipmentDropInfo)
                            it.questId
                        } else {
                            it as RandomEquipDropArea
                            it.area
                        }
                    }
                ) {
                    if (tabs[pagerIndex] != randomDrop) {
                        AreaItem(
                            equipId,
                            (it as EquipmentDropInfo).getAllOdd(),
                            it.questName,
                            colorList[pagerIndex]
                        )
                    } else {
                        it as RandomEquipDropArea
                        val odds = arrayListOf<EquipmentIdWithOdd>()
                        it.equipIds.intArrayList.forEach { id ->
                            odds.add(EquipmentIdWithOdd(id, 0))
                        }
                        odds.sortWith(equipCompare())
                        AreaItem(
                            equipId,
                            odds,
                            "区域 ${it.area}",
                            colorList[pagerIndex]
                        )
                    }

                }
                item {
                    CommonSpacer()
                }
            }
        }
    }
}


/**
 * 掉落区域信息
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

    //标题
    CommonGroupTitle(
        titleStart = num,
        titleEnd = (if (selectedOdd != null && selectedOdd.odd != 0) {
            "${selectedOdd.odd}"
        } else {
            Constants.UNKNOWN
        }) + "%",
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