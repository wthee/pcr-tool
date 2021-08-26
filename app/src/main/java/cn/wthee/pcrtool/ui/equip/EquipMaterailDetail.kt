package cn.wthee.pcrtool.ui.equip

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.EquipmentIdWithOdd
import cn.wthee.pcrtool.data.db.view.EquipmentMaxData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.PreviewBox
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.mainSP
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.GsonUtil
import cn.wthee.pcrtool.viewmodel.EquipmentViewModel
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch


/**
 * 装备素材信息
 *
 * @param equipId 装备编号
 */
@ExperimentalCoilApi
@ExperimentalPagerApi
@ExperimentalAnimationApi
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
    navViewModel.loading.postValue(dropInfoList == null)

    filter.value?.let { filterValue ->
        filterValue.starIds =
            GsonUtil.fromJson(mainSP().getString(Constants.SP_STAR_EQUIP, "")) ?: arrayListOf()
        loved.value = filterValue.starIds.contains(equipId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        dropInfoList?.let {
            if (dropInfoList.isNotEmpty()) {
                var pagerCount = 0
                val lists = arrayListOf(
                    dropInfoList.filter { it.questId / 1000000 == 11 },
                    dropInfoList.filter { it.questId / 1000000 == 12 },
                    dropInfoList.filter { it.questId / 1000000 == 13 },
                )
                val tabs = arrayListOf<String>()
                //颜色
                val color = listOf(R.color.color_map_n, R.color.color_map_h, R.color.color_map_vh)
                lists.forEachIndexed { index, it ->
                    if (it.isNotEmpty()) {
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
                val pagerState = rememberPagerState(pageCount = pagerCount)
                //按照地图难度分类
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MainText(
                        text = basicInfo.equipmentName,
                        modifier = Modifier
                            .padding(top = Dimen.largePadding),
                        color = if (loved.value) MaterialTheme.colors.primary else Color.Unspecified,
                        selectable = true
                    )
                    //Tab
                    TabRow(
                        modifier = Modifier
                            .padding(top = Dimen.mediumPadding)
                            .fillMaxWidth(0.8f),
                        selectedTabIndex = pagerState.currentPage,
                        backgroundColor = Color.Transparent,
                        contentColor = MaterialTheme.colors.primary,
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
                    HorizontalPager(state = pagerState) { pagerIndex ->
                        LazyColumn(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            contentPadding = PaddingValues(top = Dimen.mediumPadding),
                            modifier = Modifier.fillMaxSize()
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
            } else {
                //暂无掉落信息
                Text(
                    text = stringResource(R.string.unknown_area),
                    color = MaterialTheme.colors.primary,
                    style = MaterialTheme.typography.h6,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(Dimen.largePadding)
                )
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
@ExperimentalCoilApi
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
private fun AreaEquipList(
    selectedId: Int,
    odds: ArrayList<EquipmentIdWithOdd>,
    num: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = num,
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(
                bottom = Dimen.mediumPadding,
                top = Dimen.mediumPadding
            ),
            color = color
        )
        VerticalGrid(maxColumnWidth = Dimen.iconSize * 2) {
            odds.forEach {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = Dimen.largePadding,
                            end = Dimen.largePadding,
                            bottom = Dimen.largePadding
                        ), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val selected = selectedId == it.eid
                    IconCompose(data = getEquipIconUrl(it.eid))
                    SelectText(
                        selected = selected,
                        text = "${it.odd}%"
                    )
                }
            }
        }
    }

}

@Preview
@ExperimentalCoilApi
@ExperimentalAnimationApi
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