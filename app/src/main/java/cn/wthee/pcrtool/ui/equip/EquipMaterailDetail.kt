package cn.wthee.pcrtool.ui.equip

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
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
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.GsonUtil
import cn.wthee.pcrtool.utils.ImageResourceHelper
import cn.wthee.pcrtool.utils.intArrayList
import cn.wthee.pcrtool.viewmodel.EquipmentViewModel
import cn.wthee.pcrtool.viewmodel.RandomEquipAreaViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import kotlinx.coroutines.launch


/**
 * 装备素材信息
 *
 * @param equipId 装备编号
 */
@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalFoundationApi
@Composable
fun EquipMaterialDeatil(
    equipId: Int,
    equipmentViewModel: EquipmentViewModel = hiltViewModel(),
    randomEquipAreaViewModel: RandomEquipAreaViewModel = hiltViewModel(),
) {

    val dropInfoList =
        equipmentViewModel.getDropInfos(equipId).collectAsState(initial = null).value
    val basicInfo =
        equipmentViewModel.getEquip(equipId).collectAsState(initial = EquipmentMaxData()).value
    val areaList =
        randomEquipAreaViewModel.getEquipArea(equipId).collectAsState(initial = null).value
    val filter = navViewModel.filterEquip.observeAsState()
    val loved = remember {
        mutableStateOf(false)
    }
    val text = if (loved.value) "" else stringResource(id = R.string.love_equip_material)
    val scope = rememberCoroutineScope()
    val randomDrop = stringResource(id = R.string.random_area)

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
            MainText(
                text = basicInfo.equipmentName,
                modifier = Modifier
                    .padding(top = Dimen.largePadding),
                color = if (loved.value) MaterialTheme.colorScheme.primary else Color.Unspecified,
                selectable = true
            )
            if (dropInfoList != null && areaList != null) {
                if (dropInfoList.isNotEmpty()) {
                    var pagerCount = 0
                    val lists = arrayListOf(
                        dropInfoList.filter { it.questId / 1000000 == 11 },
                        dropInfoList.filter { it.questId / 1000000 == 12 },
                        dropInfoList.filter { it.questId / 1000000 == 13 },
                        areaList,
                    )
                    val tabs = arrayListOf<String>()
                    //颜色
                    val colorList = arrayListOf<Int>()

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
                                    0 -> R.color.color_map_n
                                    1 -> R.color.color_map_h
                                    2 -> R.color.color_map_vh
                                    3 -> R.color.color_rank_21_23
                                    else -> R.color.black
                                }
                            )
                        }
                    }
                    val pagerState = rememberPagerState()
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
                                ),
                            selectedTabIndex = pagerState.currentPage,
                            backgroundColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.primary,
                            indicator = { tabPositions ->
                                TabRowDefaults.Indicator(
                                    Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
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
                                    Text(
                                        text = s,
                                        color = colorResource(id = colorList[index]),
                                        modifier = Modifier.padding(bottom = Dimen.smallPadding)
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
                                    when (tabs[pagerIndex]) {
                                        "Normal" -> lists[0]
                                        "Hard" -> lists[1]
                                        "Very Hard" -> lists[2]
                                        else -> lists[3]
                                    }
                                ) {
                                    if (tabs[pagerIndex] != randomDrop) {
                                        AreaItem(
                                            equipId,
                                            (it as EquipmentDropInfo).getAllOdd(),
                                            it.getNum(),
                                            colorResource(id = colorList[pagerIndex])
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
                                            colorResource(id = colorList[pagerIndex])
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
            } else {
                val odds = arrayListOf<EquipmentIdWithOdd>()
                for (i in 0..9) {
                    odds.add(EquipmentIdWithOdd())
                }
                LazyColumn {
                    items(10) {
                        AreaItem(
                            -1,
                            odds,
                            "30-15",
                            Color.White
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
 */
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun AreaItem(
    selectedId: Int,
    odds: List<EquipmentIdWithOdd>,
    num: String,
    color: Color
) {
    val placeholder = selectedId == -1

    Column(
        modifier = Modifier.padding(
            horizontal = Dimen.largePadding,
            vertical = Dimen.mediumPadding
        )
    ) {
        //标题
        MainTitleText(
            text = num,
            backgroundColor = color,
            modifier = Modifier
                .padding(bottom = Dimen.mediumPadding)
                .placeholder(
                    visible = placeholder,
                    highlight = PlaceholderHighlight.shimmer()
                )
        )


        MainCard(
            modifier = Modifier
                .placeholder(
                    visible = placeholder,
                    highlight = PlaceholderHighlight.shimmer()
                )
        ) {
            Column {
                VerticalGrid(
                    modifier = Modifier.padding(
                        top = Dimen.mediumPadding,
                        start = Dimen.mediumPadding,
                        end = Dimen.mediumPadding
                    ),
                    maxColumnWidth = Dimen.iconSize + Dimen.mediumPadding * 2
                ) {
                    odds.forEach {
                        Column(
                            modifier = Modifier
                                .padding(
                                    bottom = Dimen.mediumPadding
                                )
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val selected = selectedId == it.eid
                            IconCompose(
                                data = ImageResourceHelper.getInstance()
                                    .getUrl(ImageResourceHelper.ICON_EQUIPMENT, it.eid)
                            )
                            if (selectedId != ImageResourceHelper.UNKNOWN_EQUIP_ID) {
                                SelectText(
                                    selected = selected,
                                    text = if (it.odd > 0) {
                                        "${it.odd}%"
                                    } else {
                                        if (selected) "✓" else ""
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}