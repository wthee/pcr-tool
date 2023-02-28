package cn.wthee.pcrtool.ui.tool.quest

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.QuestDetail
import cn.wthee.pcrtool.data.model.EquipmentIdWithOdds
import cn.wthee.pcrtool.data.model.RandomEquipDropArea
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.*
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.intArrayList
import cn.wthee.pcrtool.viewmodel.QuestViewModel
import cn.wthee.pcrtool.viewmodel.RandomEquipAreaViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState

/**
 * 主线地图信息
 * @param searchEquipIds 搜索的装备编号，-分隔
 */
@Composable
fun AllQuestList(
    searchEquipIds: String = "",
    questViewModel: QuestViewModel = hiltViewModel(),
) {
    val questList = questViewModel.getQuestList().collectAsState(initial = null).value

    Box(modifier = Modifier.fillMaxSize()) {
        if (questList != null) {
            QuestPager(
                questList,
                0,
                searchEquipIds.intArrayList
            )
        }
    }

}


/**
 * 装备掉落主线地图信息
 */
@Composable
@OptIn(ExperimentalPagerApi::class)
fun QuestPager(
    questList: List<QuestDetail>,
    equipId: Int,
    searchEquipIdList: List<Int> = arrayListOf(),
    randomEquipAreaViewModel: RandomEquipAreaViewModel = hiltViewModel()
) {
    val flow = remember(equipId) {
        randomEquipAreaViewModel.getEquipArea(equipId)
    }
    val randomDropResponseData = flow.collectAsState(initial = null).value

    val pagerState = rememberPagerState()
    var pagerCount = 0
    //tab文本
    val tabs = arrayListOf<String>()
    //tab颜色
    val colorList = arrayListOf<Color>()

    //普通
    val normalList = questList.filterAndSortSearch(type = 1, searchEquipIdList = searchEquipIdList)
    if (normalList.isNotEmpty()) {
        pagerCount++
        tabs.add("Normal")
        colorList.add(colorCyan)
    }

    //困难
    val hardList = questList.filterAndSortSearch(type = 2, searchEquipIdList = searchEquipIdList)
    if (hardList.isNotEmpty()) {
        pagerCount++
        tabs.add("Hard")
        colorList.add(colorRed)
    }

    //非常困难
    val veryHardList =
        questList.filterAndSortSearch(type = 3, searchEquipIdList = searchEquipIdList)
    if (veryHardList.isNotEmpty()) {
        pagerCount++
        tabs.add("Very Hard")
        colorList.add(colorPurple)
    }

    //随机掉落
    val randomDrop = stringResource(id = R.string.random_area)
    val randomList =
        randomDropResponseData?.data?.filter {
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
        tabs.add(randomDrop)
        colorList.add(colorGreen)
    }


    //按照地图难度分类
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (tabs.size == 0) {
            CircularProgressCompose()
        } else {
            //Tab
            MainTabRow(
                pagerState = pagerState,
                tabs = tabs,
                colorList = colorList,
                modifier = Modifier
                    .padding(
                        top = Dimen.mediumPadding,
                        start = Dimen.largePadding,
                        end = Dimen.largePadding
                    )
                    .fillMaxWidth(tabs.size * 0.25f)
            )

            HorizontalPager(
                count = pagerCount,
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { pagerIndex ->
                if (tabs[pagerIndex] == randomDrop) {
                    //随机掉落
                    CommonResponseBox(responseData = randomDropResponseData) {
                        RandomDropAreaList(
                            selectId = equipId,
                            areaList = randomList!!,
                            searchEquipIdList = searchEquipIdList
                        )
                    }
                } else {
                    //主线掉落
                    val list = when (tabs[pagerIndex]) {
                        "Normal" -> normalList
                        "Hard" -> hardList
                        else -> veryHardList
                    }
                    QuestList(
                        selectedId = equipId,
                        type = list[0].questType,
                        questList = list,
                        searchEquipIdList = searchEquipIdList
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
    var searchMatchCount = 0
    it.getAllOdd().forEach { oddData ->
        if (searchEquipIdList.contains(oddData.equipId)) {
            searchMatchCount++
        }
    }
    return searchMatchCount
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
    searchEquipIdList: List<Int> = arrayListOf()
) {
    val color = when (type) {
        1 -> colorCyan
        2 -> colorRed
        else -> colorPurple
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
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
 * @param selectedId unknow 随机掉落；非0 主线掉落，titleEnd显示概率；0 隐藏titleEnd
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
            .padding(horizontal = Dimen.mediumPadding, vertical = Dimen.largePadding)
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
    var dataState by remember { mutableStateOf(oddData) }
    if (dataState != oddData) {
        dataState = oddData
    }
    val equipIcon: @Composable () -> Unit by remember {
        mutableStateOf(
            {
                IconCompose(
                    data = ImageRequestHelper.getInstance()
                        .getUrl(ImageRequestHelper.ICON_EQUIPMENT, dataState.equipId)
                )
            }
        )
    }

    Column(
        modifier = Modifier
            .padding(bottom = Dimen.mediumPadding)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val selected =
            selectedId == dataState.equipId || searchEquipIdList.contains(dataState.equipId)
        Box(contentAlignment = Alignment.Center) {
            equipIcon()
            if (selectedId != ImageRequestHelper.UNKNOWN_EQUIP_ID && dataState.odd == 0) {
                SelectText(
                    selected = selected,
                    text = if (selected) stringResource(id = R.string.selected_mark) else "",
                    margin = 0.dp
                )
            }
        }
        if (selectedId != ImageRequestHelper.UNKNOWN_EQUIP_ID && dataState.odd > 0) {
            SelectText(
                selected = selected,
                text = "${dataState.odd}%"
            )
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