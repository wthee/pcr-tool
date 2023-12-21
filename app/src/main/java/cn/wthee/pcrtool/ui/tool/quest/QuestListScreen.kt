package cn.wthee.pcrtool.ui.tool.quest

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.QuestDetail
import cn.wthee.pcrtool.data.model.EquipmentIdWithOdds
import cn.wthee.pcrtool.data.model.RandomEquipDropArea
import cn.wthee.pcrtool.ui.LoadingState
import cn.wthee.pcrtool.ui.components.CaptionText
import cn.wthee.pcrtool.ui.components.CircularProgressCompose
import cn.wthee.pcrtool.ui.components.CommonGroupTitle
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainTabRow
import cn.wthee.pcrtool.ui.components.SelectText
import cn.wthee.pcrtool.ui.components.TabData
import cn.wthee.pcrtool.ui.components.VerticalGrid
import cn.wthee.pcrtool.ui.components.commonPlaceholder
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.colorCyan
import cn.wthee.pcrtool.ui.theme.colorGreen
import cn.wthee.pcrtool.ui.theme.colorPurple
import cn.wthee.pcrtool.ui.theme.colorRed
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.intArrayList

/**
 * 主线地图信息
 * @param searchEquipIds 搜索的装备编号，-分隔
 */
@Composable
fun QuestListScreen(
    searchEquipIds: String = "",
    questListViewModel: QuestListViewModel = hiltViewModel(),
) {
    val uiState by questListViewModel.uiState.collectAsStateWithLifecycle()


    MainScaffold {
        uiState.questList?.let {
            QuestPager(
                questList = it,
                equipId = 0,
                searchEquipIdList = searchEquipIds.intArrayList,
                randomDropList = uiState.randomDropList,
                loadingState = uiState.loadingState,
            )
        }
    }
}


/**
 * 装备掉落主线地图信息
 */
@Composable
@OptIn(ExperimentalFoundationApi::class)
fun QuestPager(
    questList: List<QuestDetail>,
    equipId: Int,
    searchEquipIdList: List<Int> = arrayListOf(),
    randomDropList: List<RandomEquipDropArea>?,
    loadingState: LoadingState
) {
    var pagerCount = 0
    //tab文本
    val tabs = arrayListOf<TabData>()

    //普通
    val normal = stringResource(id = R.string.normal)
    val normalList = questList.filterAndSortSearch(type = 1, searchEquipIdList = searchEquipIdList)
    if (normalList.isNotEmpty()) {
        pagerCount++
        tabs.add(TabData(tab = normal, color = colorCyan))
    }
    val normalListScrollState = rememberLazyListState()

    //困难
    val hard = stringResource(id = R.string.hard)
    val hardList = questList.filterAndSortSearch(type = 2, searchEquipIdList = searchEquipIdList)
    if (hardList.isNotEmpty()) {
        pagerCount++
        tabs.add(TabData(tab = hard, color = colorRed))
    }
    val hardListScrollState = rememberLazyListState()

    //非常困难
    val veryHard = stringResource(id = R.string.very_hard)
    val veryHardList =
        questList.filterAndSortSearch(type = 3, searchEquipIdList = searchEquipIdList)
    if (veryHardList.isNotEmpty()) {
        pagerCount++
        tabs.add(TabData(tab = veryHard, color = colorPurple))
    }
    val veryHardListScrollState = rememberLazyListState()

    //随机掉落
    val randomDrop = stringResource(id = R.string.random_area)
    val randomList =
        randomDropList?.filter {
            if (searchEquipIdList.isNotEmpty()) {
                getRandomQuestMatchCount(it, searchEquipIdList) > 0
            } else {
                true
            }
        }?.sortedByDescending {
            getRandomQuestMatchCount(it, searchEquipIdList)
        }
    if (randomList?.isNotEmpty() == true) {
        pagerCount++
        tabs.add(TabData(tab = randomDrop, color = colorGreen))
    }
    val randomListScrollState = rememberLazyListState()


    val pagerState = rememberPagerState { pagerCount }


    //按照地图难度分类
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (tabs.size == 0) {
            CircularProgressCompose()
        } else {
            if (searchEquipIdList.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .padding(
                            horizontal = Dimen.fabMargin,
                            vertical = Dimen.mediumPadding
                        )
                        .fillMaxWidth(searchEquipIdList.size * 0.2f),
                    horizontalArrangement = Arrangement.Center
                ) {
                    searchEquipIdList.forEach {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .padding(horizontal = Dimen.largePadding)
                                .weight(1f)
                        ) {
                            MainIcon(
                                data = ImageRequestHelper.getInstance().getEquipPic(it),
                            )
                        }
                    }
                }
            }

            //Tab
            Row(verticalAlignment = Alignment.CenterVertically) {
                MainTabRow(
                    pagerState = pagerState,
                    tabs = tabs,
                    modifier = Modifier
                        .padding(horizontal = Dimen.mediumPadding)
                        .fillMaxWidth(tabs.size * 0.25f)
                ) {
                    when (tabs[it].tab) {
                        normal -> normalListScrollState.scrollToItem(0)
                        hard -> hardListScrollState.scrollToItem(0)
                        veryHard -> veryHardListScrollState.scrollToItem(0)
                        randomDrop -> randomListScrollState.scrollToItem(0)
                    }
                }
                if (loadingState == LoadingState.Loading) {
                    CircularProgressCompose(
                        size = Dimen.smallIconSize,
                        strokeWidth = Dimen.smallStrokeWidth
                    )
                }
            }



            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { pagerIndex ->
                if (tabs[pagerIndex].tab == randomDrop) {
                    //随机掉落
                    if (loadingState == LoadingState.Success) {
                        RandomDropAreaContent(
                            selectId = equipId,
                            areaList = randomList!!,
                            searchEquipIdList = searchEquipIdList,
                            scrollState = randomListScrollState
                        )
                    }
                } else {
                    //主线掉落
                    val list: List<QuestDetail>
                    val scrollState: LazyListState
                    when (tabs[pagerIndex].tab) {
                        normal -> {
                            list = normalList
                            scrollState = normalListScrollState
                        }

                        hard -> {
                            list = hardList
                            scrollState = hardListScrollState
                        }

                        else -> {
                            list = veryHardList
                            scrollState = veryHardListScrollState
                        }
                    }
                    QuestList(
                        selectedId = equipId,
                        type = list[0].questType,
                        questList = list,
                        searchEquipIdList = searchEquipIdList,
                        scrollState = scrollState
                    )
                }
            }
        }

    }
}

/**
 * 获取额外掉落区域匹配装备的数量
 */
private fun getRandomQuestMatchCount(
    it: RandomEquipDropArea,
    searchEquipIdList: List<Int>
): Int {
    var searchMatchCount = 0
    it.equipIds.intArrayList.forEach { equipId ->
        if (searchEquipIdList.contains(equipId)) {
            searchMatchCount++
        }
    }
    return searchMatchCount
}

/**
 * 筛选区域
 */
@Composable
private fun List<QuestDetail>.filterAndSortSearch(
    type: Int,
    searchEquipIdList: List<Int>
): List<QuestDetail> {
    val filterList = this.filter {
        val questType = it.questType == type
        val searchFlag = if (searchEquipIdList.isNotEmpty()) {
            getMatchCount(it, searchEquipIdList) > 0
        } else {
            true
        }
        questType && searchFlag
    }
    return if (searchEquipIdList.isNotEmpty()) {
        filterList.sortedByDescending {
            getMatchCount(it, searchEquipIdList)
        }
    } else {
        filterList
    }
}

/**
 * 获取区域匹配装备的数量
 */
private fun getMatchCount(
    it: QuestDetail,
    searchEquipIdList: List<Int>
): Int {
    //按掉落数量
    var searchMatchCount = 0
    //掉落概率
    var searchMatchOdd = 0

    it.getAllOdd().forEach { oddData ->
        if (searchEquipIdList.contains(oddData.equipId)) {
            searchMatchCount++
            searchMatchOdd += oddData.odd
        }
    }

    return searchMatchCount * 1000 + searchMatchOdd
}

/**
 * 掉落区域列表
 * @param selectedId 非0 装备详情-查看掉落跳转，0 主线地图模块
 */
@Composable
fun QuestList(
    selectedId: Int,
    type: Int,
    questList: List<QuestDetail>,
    searchEquipIdList: List<Int> = arrayListOf(),
    scrollState: LazyListState = rememberLazyListState()
) {
    val color = when (type) {
        1 -> colorCyan
        2 -> colorRed
        else -> colorPurple
    }
    LazyColumn(modifier = Modifier.fillMaxSize(), state = scrollState) {
        items(
            items = questList,
            key = {
                it.questId
            }
        ) {
            AreaItem(
                selectedId,
                it.getAllOdd(),
                it.questName,
                searchEquipIdList,
                color
            )
        }
        item {
            CommonSpacer()
        }
    }
}


/**
 * 掉落区域信息
 * @param selectedId 随机掉落；非0 主线掉落，titleEnd显示概率；0 隐藏titleEnd
 * @param searchEquipIdList 搜索的装备编号列表
 */
@Composable
fun AreaItem(
    selectedId: Int,
    odds: List<EquipmentIdWithOdds>,
    num: String,
    searchEquipIdList: List<Int> = arrayListOf(),
    color: Color = MaterialTheme.colorScheme.primary
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
            .padding(Dimen.mediumPadding)
            .commonPlaceholder(placeholder)
    )

    VerticalGrid(
        modifier = Modifier
            .padding(
                start = Dimen.commonItemPadding,
                end = Dimen.commonItemPadding
            )
            .commonPlaceholder(placeholder),
        itemWidth = Dimen.iconSize,
        contentPadding = Dimen.mediumPadding
    ) {
        odds.forEach {
            EquipWithOddCompose(selectedId, it, searchEquipIdList)
        }
    }
}


/**
 * 带掉率装备图标
 */
@Composable
private fun EquipWithOddCompose(
    selectedId: Int,
    oddData: EquipmentIdWithOdds,
    searchEquipIdList: List<Int>
) {

    Column(
        modifier = Modifier
            .padding(bottom = Dimen.mediumPadding)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val selected =
            selectedId == oddData.equipId || searchEquipIdList.contains(oddData.equipId)
        Box(contentAlignment = Alignment.Center) {
            MainIcon(
                data = ImageRequestHelper.getInstance()
                    .getEquipPic(oddData.equipId)
            )
            if (selectedId != ImageRequestHelper.UNKNOWN_EQUIP_ID && oddData.odd == 0) {
                SelectText(
                    selected = selected,
                    text = if (selected) stringResource(id = R.string.selected_mark) else "",
                    margin = 0.dp
                )
            }
        }
        if (selectedId != ImageRequestHelper.UNKNOWN_EQUIP_ID && oddData.odd > 0) {
            SelectText(
                selected = selected,
                text = "${oddData.odd}%"
            )
        }

        if (BuildConfig.DEBUG) {
            CaptionText(text = oddData.equipId.toString())
        }
    }
}


@CombinedPreviews
@Composable
private fun AreaItemPreview() {
    PreviewLayout {
        AreaItem(
            1,
            arrayListOf(
                EquipmentIdWithOdds(1, 20),
                EquipmentIdWithOdds(0, 20),
                EquipmentIdWithOdds(0, 20),
                EquipmentIdWithOdds(0, 20),
                EquipmentIdWithOdds(0, 20),
                EquipmentIdWithOdds(0, 20),
                EquipmentIdWithOdds(0, 20),
            ),
            "1-1"
        )
    }
}