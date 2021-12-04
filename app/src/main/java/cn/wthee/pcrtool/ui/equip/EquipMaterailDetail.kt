package cn.wthee.pcrtool.ui.equip

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.EquipmentIdWithOdd
import cn.wthee.pcrtool.data.db.view.EquipmentMaxData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.PreviewBox
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.mainSP
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.GsonUtil
import cn.wthee.pcrtool.utils.ImageResourceHelper
import cn.wthee.pcrtool.utils.ImageResourceHelper.Companion.ICON_EQUIPMENT
import cn.wthee.pcrtool.viewmodel.EquipmentViewModel
import com.google.accompanist.flowlayout.FlowRow
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
    equipmentViewModel: EquipmentViewModel = hiltViewModel()
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
    val scope = rememberCoroutineScope()

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
            if (dropInfoList != null) {
                if (dropInfoList.isNotEmpty()) {
                    var pagerCount = 0
                    val lists = arrayListOf(
                        dropInfoList.filter { it.questId / 1000000 == 11 },
                        dropInfoList.filter { it.questId / 1000000 == 12 },
                        dropInfoList.filter { it.questId / 1000000 == 13 },
                    )
                    val tabs = arrayListOf<String>()
                    //颜色
                    val color =
                        listOf(R.color.color_map_n, R.color.color_map_h, R.color.color_map_vh)
                    lists.forEachIndexed { index, data ->
                        if (data.isNotEmpty()) {
                            pagerCount++
                            tabs.add(
                                when (index) {
                                    0 -> "Normal"
                                    1 -> "Hard"
                                    2 -> "Very Hard"
                                    else -> "？"
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
                                .padding(top = Dimen.mediumPadding)
                                .fillMaxWidth(0.8f),
                            selectedTabIndex = pagerState.currentPage,
                            backgroundColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.primary,
                            indicator = { tabPositions ->
                                TabRowDefaults.Indicator(
                                    Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                                )
                            },
                            divider = {
                                TabRowDefaults.Divider(color = Color.Transparent)
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
                                        color = colorResource(id = color[index]),
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
                            LazyVerticalGrid(
                                modifier = Modifier.fillMaxSize(),
                                cells = GridCells.Adaptive(getItemWidth())
                            ) {
                                items(
                                    when (tabs[pagerIndex]) {
                                        "Normal" -> lists[0]
                                        "Hard" -> lists[1]
                                        else -> lists[2]
                                    }
                                ) {
                                    AreaEquipList(
                                        equipId,
                                        it.getAllOdd(),
                                        it.getNum(),
                                        colorResource(id = color[pagerIndex])
                                    )
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
                LazyVerticalGrid(cells = GridCells.Adaptive(getItemWidth())) {
                    items(10) {
                        AreaEquipList(
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
 *  地区的装备掉落列表
 *
 *  @param selectedId 选择的装备
 */
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
private fun AreaEquipList(
    selectedId: Int,
    odds: ArrayList<EquipmentIdWithOdd>,
    num: String,
    color: Color
) {
    val placeholder = selectedId == -1

    Column(
        modifier = Modifier.padding(
            horizontal = Dimen.largePadding,
            vertical = Dimen.mediumPadding
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = num,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(bottom = Dimen.mediumPadding)
                .placeholder(
                    visible = placeholder,
                    highlight = PlaceholderHighlight.shimmer()
                ),
            color = color
        )

        MainCard(
            modifier = Modifier
                .padding(bottom = Dimen.mediumPadding)
                .placeholder(
                    visible = placeholder,
                    highlight = PlaceholderHighlight.shimmer()
                )
        ) {
            FlowRow(modifier = Modifier.padding(top = Dimen.mediumPadding)) {
                odds.forEach { oddData ->
                    Column(
                        modifier = Modifier.padding(
                            horizontal = Dimen.mediumPadding,
                            vertical = Dimen.smallPadding
                        ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val selected = selectedId == oddData.eid
                        IconCompose(
                            data = ImageResourceHelper.getInstance()
                                .getUrl(ICON_EQUIPMENT, oddData.eid)
                        )
                        SelectText(
                            selected = selected,
                            text = "${oddData.odd}%"
                        )
                    }
                }
            }
        }
    }

}

@ExperimentalMaterialApi
@Preview
@ExperimentalFoundationApi
@Composable
private fun AreaEquipListPreview() {
    val mockData = arrayListOf<EquipmentIdWithOdd>()
    for (i in 0..7) {
        mockData.add(EquipmentIdWithOdd())
    }
    PreviewBox {
        AreaEquipList(selectedId = 1, odds = mockData, num = "1", color = Color.Red)
    }
}