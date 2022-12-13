package cn.wthee.pcrtool.ui.tool.quest

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.QuestDetail
import cn.wthee.pcrtool.data.model.EquipmentIdWithOdd
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.*
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.ImageResourceHelper
import cn.wthee.pcrtool.viewmodel.QuestViewModel
import cn.wthee.pcrtool.viewmodel.RandomEquipAreaViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

/**
 * 主线地图信息
 * fixme 暂不展示
 */
@Composable
fun AllQuestList(
    questViewModel: QuestViewModel = hiltViewModel(),
) {
    val questList = questViewModel.getQuestList().collectAsState(initial = arrayListOf()).value

    Box(modifier = Modifier.fillMaxSize()) {
        QuestPager(
            questList,
            0
        )
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
    randomEquipAreaViewModel: RandomEquipAreaViewModel = hiltViewModel()
) {
    val flow = remember(equipId) {
        randomEquipAreaViewModel.getEquipArea(equipId)
    }
    val randomDropResponseData = flow.collectAsState(initial = null).value

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    var pagerCount = 0
    //tab文本
    val tabs = arrayListOf<String>()
    //tab颜色
    val colorList = arrayListOf<Color>()

    //普通
    val normalList = questList.filter { it.questType == 1 }
    if (normalList.isNotEmpty()) {
        pagerCount++
        tabs.add("Normal")
        colorList.add(colorBlue)
    }

    //困难
    val hardList = questList.filter { it.questType == 2 }
    if (hardList.isNotEmpty()) {
        pagerCount++
        tabs.add("Hard")
        colorList.add(colorRed)
    }

    //非常困难
    val veryHardList = questList.filter { it.questType == 3 }
    if (veryHardList.isNotEmpty()) {
        pagerCount++
        tabs.add("Very Hard")
        colorList.add(colorPurple)
    }

    //随机掉落
    val randomDrop = stringResource(id = R.string.random_area)
    if (randomDropResponseData?.data?.isNotEmpty() == true) {
        pagerCount++
        tabs.add(randomDrop)
        colorList.add(colorGreen)
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
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = index == pagerState.currentPage,
                    onClick = {
                        scope.launch {
                            pagerState.scrollToPage(index)
                        }
                    }
                ) {
                    Subtitle1(
                        text = tab,
                        modifier = Modifier.padding(Dimen.smallPadding),
                        color = colorList[index],
                    )
                }
            }
        }

        HorizontalPager(
            count = pagerCount,
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pagerIndex ->
            if (tabs[pagerIndex] == randomDrop) {
                //随机掉落
                CommonResponseBox(responseData = randomDropResponseData) { data ->
                    RandomDropAreaList(selectId = equipId, areaList = data)
                }
            } else {
                //主线掉落
                val list = when (tabs[pagerIndex]) {
                    "Normal" -> normalList
                    "Hard" -> hardList
                    else -> veryHardList
                }
                QuestList(selectedId = equipId, type = list[0].questType, questList = list)
            }
        }
    }
}

/**
 * 掉落区域列表
 * @param selectedId 非0 装备详情-查看掉落跳转，0 主线地图模块
 */
@Composable
fun QuestList(selectedId: Int, type: Int, questList: List<QuestDetail>) {
    val color = when (type) {
        1 -> colorBlue
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
            EquipWithOddCompose(selectedId, it)
        }
    }
}


/**
 * 带掉率装备图标
 */
@Composable
private fun EquipWithOddCompose(
    selectedId: Int,
    oddData: EquipmentIdWithOdd
) {
    var dataState by remember { mutableStateOf(oddData) }
    if (dataState != oddData) {
        dataState = oddData
    }
    val equipIcon: @Composable () -> Unit by remember {
        mutableStateOf(
            {
                IconCompose(
                    data = ImageResourceHelper.getInstance()
                        .getUrl(ImageResourceHelper.ICON_EQUIPMENT, dataState.equipId)
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
        val selected = selectedId == dataState.equipId
        Box(contentAlignment = Alignment.Center) {
            equipIcon()
            if (selectedId != ImageResourceHelper.UNKNOWN_EQUIP_ID && dataState.odd == 0) {
                SelectText(
                    selected = selected,
                    text = if (selected) "✓" else "",
                    margin = 0.dp
                )
            }
        }
        if (selectedId != ImageResourceHelper.UNKNOWN_EQUIP_ID && dataState.odd > 0) {
            SelectText(
                selected = selected,
                text = "${dataState.odd}%"
            )
        }
    }
}
