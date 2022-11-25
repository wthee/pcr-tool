package cn.wthee.pcrtool.ui.tool.quest

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.QuestDetail
import cn.wthee.pcrtool.ui.common.CommonSpacer
import cn.wthee.pcrtool.ui.common.Subtitle1
import cn.wthee.pcrtool.ui.equip.AreaItem
import cn.wthee.pcrtool.ui.theme.*
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
    val randomDropList =
        randomEquipAreaViewModel.getEquipArea(equipId).collectAsState(initial = null).value

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
    if (randomDropList?.isNotEmpty() == true) {
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
                RandomDropAreaList(selectId = equipId, areaList = randomDropList!!)
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
