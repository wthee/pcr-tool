package cn.wthee.pcrtool.ui.tool.mockgacha

import androidx.activity.ComponentActivity
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.GachaUnitInfo
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.MockGachaType
import cn.wthee.pcrtool.data.model.UnitsInGacha
import cn.wthee.pcrtool.data.model.getIds
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.MainTabRow
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.SelectText
import cn.wthee.pcrtool.ui.components.SelectTypeFab
import cn.wthee.pcrtool.ui.components.VerticalGrid
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.ExpandAnimation
import cn.wthee.pcrtool.ui.theme.FadeAnimation
import cn.wthee.pcrtool.ui.theme.RATIO_GOLDEN
import cn.wthee.pcrtool.ui.theme.defaultSpring
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.MockGachaHelper
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.viewmodel.MockGachaViewModel
import kotlinx.coroutines.launch
import java.util.UUID

//模拟抽卡最大up数
private const val MOCK_GACHA_MAX_UP_COUNT = 12

//模拟抽卡fes最大up数
private const val MOCK_GACHA_FES_MAX_UP_COUNT = 2

/**
 * 模拟卡池
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun MockGacha(
    pagerState: PagerState = rememberPagerState { 2 },
    mockGachaViewModel: MockGachaViewModel = hiltViewModel(LocalContext.current as ComponentActivity),
) {
    val scope = rememberCoroutineScope()

    //类型
    val tabs = arrayListOf(
        stringResource(id = R.string.gacha_pick_up_normal),
        stringResource(id = R.string.gacha_pick_up_fes),
        stringResource(id = R.string.gacha_pick_up_single),
    )
    //类型
    val pageTabs = arrayListOf(
        stringResource(id = R.string.character),
        stringResource(id = R.string.title_history)
    )

    //角色列表
    val allUnitsFlow = remember {
        mockGachaViewModel.getGachaUnits()
    }
    val allUnits by allUnitsFlow.collectAsState(initial = null)

    //页面
    val showResult = mockGachaViewModel.showMockGachaResult.observeAsState().value ?: false
    //卡池信息
    val gachaId = mockGachaViewModel.gachaId.observeAsState().value ?: ""
    val pickUpList = mockGachaViewModel.pickUpList.observeAsState().value ?: arrayListOf()
    // 类型
    val mockGachaType = remember {
        mutableIntStateOf(
            mockGachaViewModel.mockGachaType.value?.type ?: MockGachaType.PICK_UP.type
        )
    }
    mockGachaType.intValue =
        mockGachaViewModel.mockGachaType.observeAsState().value?.type ?: MockGachaType.PICK_UP.type

    //关闭
    val close = navViewModel.fabCloseClick.observeAsState().value ?: false
    if (showResult) {
        navViewModel.fabMainIcon.postValue(MainIconType.CLOSE)
    } else {
        navViewModel.fabMainIcon.postValue(MainIconType.BACK)
    }
    //返回选择
    if (close) {
        mockGachaViewModel.showMockGachaResult.postValue(false)
        navViewModel.fabCloseClick.postValue(false)
    }

    //滚动状态
    val unitScrollState = rememberScrollState()
    val historyScrollState = rememberLazyGridState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .padding(top = Dimen.largePadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                FadeAnimation(pickUpList.isEmpty()) {
                    MainText(
                        text = when (mockGachaType.intValue) {
                            MockGachaType.PICK_UP.type -> stringResource(id = R.string.tip_to_pick_up_normal)
                            MockGachaType.PICK_UP_SINGLE.type -> stringResource(id = R.string.tip_to_pick_up_single)
                            else -> stringResource(id = R.string.tip_to_pick_up)
                        }
                    )
                }
                ExpandAnimation(pickUpList.isNotEmpty()) {
                    //选中的角色
                    FlowRow(
                        modifier = Modifier.animateContentSize(defaultSpring())
                    ) {
                        pickUpList.forEachIndexed { index, gachaUnitInfo ->
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(top = Dimen.smallPadding)
                            ) {
                                MainIcon(
                                    data = ImageRequestHelper.getInstance()
                                        .getUnitIconUrl(gachaUnitInfo.unitId, 3),
                                    modifier = Modifier.padding(horizontal = Dimen.mediumPadding)
                                ) {
                                    //更新选中
                                    if (!showResult) {
                                        updatePickUpList(gachaUnitInfo, mockGachaViewModel)
                                    }
                                }
                                if (mockGachaType.intValue == MockGachaType.PICK_UP_SINGLE.type && index == pickUpList.size - 1) {
                                    SelectText(
                                        selected = true,
                                        text = stringResource(id = R.string.selected_mark)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            if (showResult) {
                //抽卡结果
                MockGachaResult(
                    gachaId,
                    pickUpList.getIds(),
                    MockGachaType.getByValue(mockGachaType.intValue)
                )
            } else {
                MainTabRow(
                    pagerState = pagerState,
                    tabs = pageTabs,
                    modifier = Modifier
                        .fillMaxWidth(RATIO_GOLDEN)
                        .align(Alignment.CenterHorizontally)
                ) {
                    if (pagerState.currentPage == 0) {
                        unitScrollState.scrollTo(0)
                    } else {
                        historyScrollState.scrollToItem(0)
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .padding(top = Dimen.mediumPadding)
                        .fillMaxSize(),
                    verticalAlignment = Alignment.Top
                ) { pageIndex ->
                    when (pageIndex) {
                        0 -> //角色选择
                            allUnits?.let {
                                ToSelectMockGachaUnitList(
                                    unitScrollState,
                                    MockGachaType.getByValue(mockGachaType.intValue),
                                    it
                                )
                            }

                        else -> {
                            //历史记录展示
                            MockGachaHistory(historyScrollState)
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
            val tipSingleError = stringResource(id = R.string.tip_to_mock_single)
            MainSmallFab(
                iconType = MainIconType.MOCK_GACHA_PAY,
                text = if (showResult) "-1500" else stringResource(id = R.string.go_to_mock)
            ) {
                if (pickUpList.isNotEmpty()) {
                    val mockGachaHelper = MockGachaHelper(
                        pickUpType = MockGachaType.getByValue(mockGachaType.intValue),
                        pickUpList = pickUpList,
                        allUnits!!
                    )

                    if (!showResult) {
                        //自选单up，至少选择两名
                        if (mockGachaType.intValue == MockGachaType.PICK_UP_SINGLE.type && pickUpList.size < 2) {
                            ToastUtil.short(tipSingleError)
                            return@MainSmallFab
                        }
                        mockGachaViewModel.showMockGachaResult.postValue(true)
                        //创建卡池，若存在相同卡池，则不重新创建
                        scope.launch {
                            var id = UUID.randomUUID().toString()
                            val oldGacha = mockGachaViewModel.getGachaByPickUp(
                                MockGachaType.getByValue(mockGachaType.intValue),
                                pickUpList
                            )
                            if (oldGacha != null) {
                                id = oldGacha.gachaId
                                mockGachaType.intValue = oldGacha.gachaType
                            }
                            mockGachaViewModel.gachaId.postValue(id)
                            mockGachaViewModel.createMockGacha(
                                id,
                                MockGachaType.getByValue(mockGachaType.intValue),
                                pickUpList,
                            )
                        }
                    } else if (gachaId != "") {
                        val result = mockGachaHelper.giveMe1500Gems()
                        //保存记录
                        mockGachaViewModel.addMockResult(gachaId, result)
                    }
                }
            }
        }
        //卡池类型选择
        if (!showResult) {
            SelectTypeFab(
                icon = MainIconType.CHANGE_FILTER_TYPE,
                tabs = tabs,
                type = mockGachaType,
                width = Dimen.mockGachaTypeChangeWidth,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
            ) {
                //切换时清空
                mockGachaViewModel.pickUpList.postValue(arrayListOf())
                mockGachaViewModel.mockGachaType.postValue(MockGachaType.getByValue(mockGachaType.intValue))
            }
        } else {
            MainSmallFab(
                iconType = MainIconType.RESET,
                text = stringResource(id = R.string.reset_record),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
            ) {
                mockGachaViewModel.deleteGachaResultByGachaId(gachaId)
            }
        }
    }
}


/**
 * 全部角色列表
 */
@Composable
private fun ToSelectMockGachaUnitList(
    scrollState: ScrollState,
    mockGachaType: MockGachaType,
    allUnits: UnitsInGacha
) {
    Column(
        modifier = Modifier
            .padding(Dimen.mediumPadding)
            .verticalScroll(scrollState)
    ) {
        if (mockGachaType == MockGachaType.FES) {
            // Fes
            ToSelectMockGachaUnitGroup(allUnits.fesLimit, stringResource(R.string.gacha_fes))
        } else {
            // 限定
            ToSelectMockGachaUnitGroup(allUnits.limit, stringResource(R.string.gacha_limit))
            // 常驻
            ToSelectMockGachaUnitGroup(allUnits.normal3, stringResource(R.string.gacha_normal))
        }
        CommonSpacer()
        CommonSpacer()
    }
}

/**
 * 角色列表分组
 */
@Composable
private fun ToSelectMockGachaUnitGroup(
    data: List<GachaUnitInfo>,
    title: String,
    mockGachaViewModel: MockGachaViewModel = hiltViewModel(LocalContext.current as ComponentActivity)
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(
            start = Dimen.mediumPadding,
            end = Dimen.mediumPadding,
            top = Dimen.mediumPadding
        )
    ) {
        MainTitleText(text = title)
        Spacer(modifier = Modifier.weight(1f))
        MainText(text = data.size.toString())
    }
    MockGachaUnitIconListCompose(icons = data, onClickItem = {
        updatePickUpList(it, mockGachaViewModel)
    })
}

/**
 * 角色图标列表
 */
@Composable
private fun MockGachaUnitIconListCompose(
    icons: List<GachaUnitInfo>,
    onClickItem: (GachaUnitInfo) -> Unit
) {
    VerticalGrid(
        modifier = Modifier.padding(
            top = Dimen.mediumPadding
        ),
        itemWidth = Dimen.iconSize,
        contentPadding = Dimen.mediumPadding
    ) {
        icons.forEach { gachaUnitInfo ->
            val iconId = gachaUnitInfo.unitId + (if (gachaUnitInfo.rarity == 1) 10 else 30)
            Column(
                modifier = Modifier
                    .padding(
                        bottom = Dimen.mediumPadding
                    )
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MainIcon(
                    data = ImageRequestHelper.getInstance()
                        .getUrl(ImageRequestHelper.ICON_UNIT, iconId)
                ) {
                    onClickItem(gachaUnitInfo)
                }
            }
        }
    }
}

/**
 * 更新选中列表
 */
private fun updatePickUpList(data: GachaUnitInfo, mockGachaViewModel: MockGachaViewModel) {
    val pickUpList = mockGachaViewModel.pickUpList.value ?: arrayListOf()
    val gachaType = mockGachaViewModel.mockGachaType.value ?: 0
    val maxPick =
        if (gachaType == MockGachaType.FES) MOCK_GACHA_FES_MAX_UP_COUNT else MOCK_GACHA_MAX_UP_COUNT

    val newList = arrayListOf<GachaUnitInfo>()
    newList.addAll(pickUpList)
    if (newList.contains(data)) {
        newList.remove(data)
    } else {
        if (pickUpList.size >= maxPick) {
            ToastUtil.short(getString(R.string.gacha_max_select_count, maxPick))
            return
        } else {
            newList.add(data)
        }
    }
    mockGachaViewModel.pickUpList.postValue(newList)
}
