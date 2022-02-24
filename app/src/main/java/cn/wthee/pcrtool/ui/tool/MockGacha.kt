package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.MockGachaData
import cn.wthee.pcrtool.data.db.entity.MockGachaResultRecord
import cn.wthee.pcrtool.data.db.view.GachaUnitInfo
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.UnitsInGacha
import cn.wthee.pcrtool.data.model.getIds
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.FadeAnimation
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.viewmodel.MockGachaViewModel
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch
import java.util.*

/**
 * 模拟卡池
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun MockGacha(
    pagerState: PagerState = rememberPagerState(),
    mockGachaViewModel: MockGachaViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    //类型
    val tabs = arrayListOf(
        stringResource(id = R.string.gacha_pick_up_normal),
        stringResource(id = R.string.gacha_pick_up_fes)
    )
    //类型
    val pageTabs = arrayListOf(
        stringResource(id = R.string.character),
        stringResource(id = R.string.title_history)
    )

    //角色列表
    val allUnits = mockGachaViewModel.getGachaUnits().collectAsState(initial = null).value

    //页面
    val showResult = navViewModel.showMockGachaResult.observeAsState().value ?: false
    //卡池信息
    val gachaId = navViewModel.gachaId.observeAsState().value ?: ""
    val pickUpList = navViewModel.pickUpList.observeAsState().value ?: arrayListOf()
    // 0：自选角色 1：fes角色
    val gachaType = remember {
        mutableStateOf(navViewModel.gachaType.value ?: 0)
    }
    //关闭
    val close = navViewModel.fabCloseClick.observeAsState().value ?: false

    if (showResult) {
        navViewModel.fabMainIcon.postValue(MainIconType.CLOSE)
    } else {
        navViewModel.fabMainIcon.postValue(MainIconType.BACK)
    }
    //返回选择
    if (close) {
        navViewModel.showMockGachaResult.postValue(false)
        navViewModel.fabCloseClick.postValue(false)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(top = Dimen.largePadding)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = Dimen.iconSize),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (pickUpList.isEmpty()) {
                    MainText(text = "请选择 UP 角色")
                } else {
                    pickUpList.forEach {
                        IconCompose(
                            data = ImageResourceHelper.getInstance().getMaxIconUrl(it.unitId),
                            modifier = Modifier.padding(horizontal = Dimen.mediumPadding)
                        ) {
                            //更新选中
                            if (!showResult) {
                                updatePickUpList(it)
                            }
                        }
                    }
                }
            }
            if (showResult) {
                //抽卡结果
                MockGachaResultRecordList(gachaId, pickUpList)
            } else {
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                        )
                    },
                    backgroundColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth(0.618f)
                        .align(Alignment.CenterHorizontally)
                ) {
                    pageTabs.forEachIndexed { index, s ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                scope.launch {
                                    VibrateUtil(context).single()
                                    pagerState.scrollToPage(index)
                                }
                            }) {
                            Subtitle1(
                                text = s,
                                modifier = Modifier.padding(Dimen.smallPadding),
                                color = if (pagerState.currentPage == index) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    Color.Unspecified
                                }
                            )
                        }
                    }
                }

                HorizontalPager(
                    count = 2,
                    state = pagerState,
                    modifier = Modifier.padding(top = Dimen.mediumPadding)
                ) { pageIndex ->
                    when (pageIndex) {
                        0 -> //角色选择
                            allUnits?.let {
                                ToSelectMockGachaUnitList(gachaType.value, it)
                            }
                        else -> {
                            //历史记录展示
                            MockGachaHistory()
                        }
                    }
                }

            }
        }

        //抽取
        FadeAnimation(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = Dimen.fabMargin,
                    bottom = Dimen.fabMargin * 2 + Dimen.fabSize
                ),
            visible = pickUpList.isNotEmpty() && allUnits != null
        ) {
            FabCompose(
                iconType = MainIconType.MOCK_GACHA,
                text = if (showResult) "+19" else "加载卡池"
            ) {
                if (pickUpList.isNotEmpty()) {
                    if (!showResult) {
                        navViewModel.showMockGachaResult.postValue(true)
                        //创建卡池，若存在相同卡池，则不重新创建
                        scope.launch {
                            var id = UUID.randomUUID().toString()
                            val oldGacha = mockGachaViewModel.getGachaByPickUp(pickUpList)
                            if (oldGacha != null) {
                                id = oldGacha.gachaId
                            }
                            navViewModel.gachaId.postValue(id)
                            mockGachaViewModel.createMockGacha(
                                id,
                                gachaType.value,
                                pickUpList
                            )
                        }
                    } else if (gachaId != "") {
                        val result = MockGachaHelper(
                            pickUpType = gachaType.value,
                            pickUpList = pickUpList,
                            allUnits!!
                        ).giveMe1500Gems()
                        //保存记录
                        mockGachaViewModel.addMockResult(gachaId, result)
                    }
                }
            }
        }
        //卡池类型选择
        if (!showResult) {
            SelectTypeCompose(
                icon = MainIconType.MOCK_GACHA_TYPE,
                tabs = tabs,
                type = gachaType,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
            ) {
                //切换时清空
                navViewModel.pickUpList.postValue(arrayListOf())
                navViewModel.gachaType.postValue(gachaType.value)
            }
        }
    }
}

/**
 * 历史卡池
 */
@Composable
private fun MockGachaHistory(mockGachaViewModel: MockGachaViewModel = hiltViewModel()) {
    //历史记录
    mockGachaViewModel.getHistory()
    val historyData = mockGachaViewModel.historyList.observeAsState().value ?: arrayListOf()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(historyData) {
            MockGachaHistoryItem(it)
        }
        item {
            CommonSpacer()
        }
        item {
            CommonSpacer()
        }
    }
}

/**
 * 卡池历史记录 item
 */
@Composable
private fun MockGachaHistoryItem(
    gachaData: MockGachaData,
    mockGachaViewModel: MockGachaViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.padding(
            horizontal = Dimen.largePadding,
            vertical = Dimen.mediumPadding
        )
    ) {
        //标题
        Row(
            modifier = Modifier.padding(bottom = Dimen.mediumPadding)
        ) {
            MainTitleText(
                text = gachaData.createTime.formatTime.substring(0, 10)
            )
            Spacer(modifier = Modifier.weight(1f))
            IconCompose(
                data = MainIconType.MOCK_GACHA.icon,
                size = Dimen.fabIconSize
            ) {
                scope.launch {
                    navViewModel.gachaId.postValue(gachaData.gachaId)
                    //卡池详情
                    val gacha = mockGachaViewModel.getGacha(gachaData.gachaId)
                    val newPickUpList = arrayListOf<GachaUnitInfo>()
                    gacha.pickUpIds.intArrayList.forEach {
                        newPickUpList.add(
                            GachaUnitInfo(
                                it,
                                "",
                                -1,
                                3
                            )
                        )
                    }
                    navViewModel.pickUpList.postValue(newPickUpList)
                    //显示卡池结果
                    navViewModel.showMockGachaResult.postValue(true)
                }
            }
        }

        MainCard {
            Column(modifier = Modifier.padding(Dimen.mediumPadding)) {
                //up 角色
                Row {
                    gachaData.pickUpIds.intArrayList.forEach { unitId ->
                        IconCompose(
                            data = ImageResourceHelper.getInstance().getUrl(
                                ImageResourceHelper.ICON_UNIT,
                                unitId + 30
                            ),
                            modifier = Modifier.padding(Dimen.mediumPadding)
                        )
                    }
                }

                //日期
                CaptionText(
                    text = "最近抽取 ${gachaData.lastUpdateTime}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = Dimen.mediumPadding)

                )
            }
        }
    }
}

/**
 * 抽取结果详情
 */
@Composable
private fun MockGachaResultRecordList(
    gachaId: String,
    pickUpList: List<GachaUnitInfo>,
    mockGachaViewModel: MockGachaViewModel = hiltViewModel()
) {
    mockGachaViewModel.getResult(gachaId = gachaId)
    val resultRecordList =
        mockGachaViewModel.resultRecordList.observeAsState().value ?: arrayListOf()
    //TODO 显示抽中 up 角色的相关信息，总抽数、第n次抽中哪个角色、累计消耗钻石数、抽中的三星角色
    // 放到底部，点击展示详情，动态更新数据

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        itemsIndexed(resultRecordList) { index, resultRecord ->
            MockGachaResultRecordItem(resultRecordList.size - index, pickUpList, resultRecord)
        }
        item {
            CommonSpacer()
        }
        item {
            CommonSpacer()
        }
    }

}

/**
 * 卡池抽取记录列表项
 */
@Composable
private fun MockGachaResultRecordItem(
    order: Int,
    pickUpList: List<GachaUnitInfo>,
    recordData: MockGachaResultRecord
) {
    val formatResult = arrayListOf<GachaUnitInfo>()
    //pickUp 标记
    val pickUpIndexList = arrayListOf<Int>()
    val rarity3List = arrayListOf<Int>()

    recordData.unitIds.intArrayList.forEachIndexed { index, unitId ->
        val rarity = recordData.unitRaritys.intArrayList[index]
        if (pickUpList.getIds().contains(unitId)) {
            pickUpIndexList.add(index)
        }
        if (rarity == 3) {
            rarity3List.add(index)
        }
        formatResult.add(GachaUnitInfo(unitId, "", -1, rarity))
    }

    Column(
        modifier = Modifier.padding(
            horizontal = Dimen.largePadding,
            vertical = Dimen.mediumPadding
        )
    ) {
        //标题
        Row(
            modifier = Modifier.padding(bottom = Dimen.mediumPadding)
        ) {
            MainTitleText(
                text = "第 $order 次"
            )
            //TODO 展示结果统计
            //抽中 pickUp 角色，添加标注
            if (pickUpIndexList.isNotEmpty()) {
                MainTitleText(
                    text = "PICK UP",
                    modifier = Modifier.padding(start = Dimen.smallPadding),
                    backgroundColor = colorResource(id = R.color.color_rank_18_20)
                )
            } else if (rarity3List.isNotEmpty()) {
                MainTitleText(
                    text = "★3",
                    modifier = Modifier.padding(start = Dimen.smallPadding),
                    backgroundColor = colorResource(id = R.color.color_rank_7_10)
                )
            }
        }

        MainCard {
            Column(modifier = Modifier.padding(bottom = Dimen.mediumPadding)) {
                MockGachaUnitIconListCompose(true, formatResult, pickUpIndexList) {}
                //日期
                CaptionText(
                    text = recordData.createTime,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = Dimen.mediumPadding)

                )
            }
        }
    }
}

/**
 * 全部角色列表
 */
@Composable
private fun ToSelectMockGachaUnitList(
    gachaType: Int,
    allUnits: UnitsInGacha
) {
    Column(
        modifier = Modifier
            .padding(Dimen.mediumPadding)
            .verticalScroll(rememberScrollState())
    ) {
        if (gachaType == 1) {
            // FES
            ToSelectMockGachaUnitListItem(allUnits.fesLimit, "Fes限定★3")
        } else {
            // 限定
            ToSelectMockGachaUnitListItem(allUnits.limit, "限定★3")
            // 常驻
            ToSelectMockGachaUnitListItem(allUnits.normal3, "常驻★3")
        }
        CommonSpacer()

    }
}

/**
 * 角色列表
 */
@Composable
private fun ToSelectMockGachaUnitListItem(
    data: List<GachaUnitInfo>,
    title: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = Dimen.mediumPadding)
    ) {
        MainTitleText(text = title)
        Spacer(modifier = Modifier.weight(1f))
        MainText(text = data.size.toString())
    }
    MockGachaUnitIconListCompose(icons = data, onClickItem = {
        updatePickUpList(it)
    })
}

/**
 * 更新选中列表
 */
private fun updatePickUpList(data: GachaUnitInfo) {
    val pickUpList = navViewModel.pickUpList.value ?: arrayListOf()
    val newList = arrayListOf<GachaUnitInfo>()
    newList.addAll(pickUpList)
    if (newList.contains(data)) {
        newList.remove(data)
    } else {
        if (pickUpList.size >= 4) {
            ToastUtil.short("最多可选 4 名角色")
            return
        } else {
            newList.add(data)
        }
    }
    navViewModel.pickUpList.postValue(newList)
}


/**
 * 角色图标列表
 */
@Composable
private fun MockGachaUnitIconListCompose(
    isResultPage: Boolean = false,
    icons: List<GachaUnitInfo>,
    pickUpIndex: ArrayList<Int> = arrayListOf(),
    onClickItem: (GachaUnitInfo) -> Unit
) {
    VerticalGrid(
        modifier = Modifier.padding(
            top = Dimen.mediumPadding,
            start = Dimen.mediumPadding,
            end = Dimen.mediumPadding
        ),
        maxColumnWidth = Dimen.iconSize + Dimen.mediumPadding * 2,
    ) {
        icons.forEachIndexed { index, gachaUnitInfo ->
            val iconId = gachaUnitInfo.unitId + (if (gachaUnitInfo.rarity == 1) 10 else 30)
            Column(
                modifier = Modifier
                    .padding(
                        bottom = Dimen.mediumPadding
                    )
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconCompose(
                    data = ImageResourceHelper.getInstance()
                        .getUrl(ImageResourceHelper.ICON_UNIT, iconId)
                ) {
                    if (!isResultPage) {
                        onClickItem(gachaUnitInfo)
                    }
                }
                if (isResultPage) {
                    val textColor = when {
                        pickUpIndex.contains(index) -> {
                            colorResource(id = R.color.color_rank_18_20)
                        }
                        gachaUnitInfo.rarity == 3 -> {
                            colorResource(id = R.color.color_rank_7_10)
                        }
                        else -> {
                            Color.Unspecified
                        }
                    }
                    MainText(text = "★${gachaUnitInfo.rarity}", color = textColor)
                }
            }
        }
    }
}