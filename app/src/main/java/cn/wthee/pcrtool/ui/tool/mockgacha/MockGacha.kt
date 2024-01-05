package cn.wthee.pcrtool.ui.tool.mockgacha

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.GachaUnitInfo
import cn.wthee.pcrtool.data.enums.IconResourceType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.MockGachaType
import cn.wthee.pcrtool.data.model.UnitsInGacha
import cn.wthee.pcrtool.data.model.getIds
import cn.wthee.pcrtool.navigation.navigateUp
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.GridIconList
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.MainTabRow
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.SelectText
import cn.wthee.pcrtool.ui.components.SelectTypeFab
import cn.wthee.pcrtool.ui.components.TabData
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.ExpandAnimation
import cn.wthee.pcrtool.ui.theme.FadeAnimation
import cn.wthee.pcrtool.ui.theme.RATIO_GOLDEN
import cn.wthee.pcrtool.ui.theme.defaultSpring
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.MockGachaHelper
import cn.wthee.pcrtool.utils.ToastUtil
import kotlinx.coroutines.launch
import java.util.UUID


/**
 * 模拟卡池
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MockGacha(
    pagerState: PagerState = rememberPagerState { 2 },
    mockGachaViewModel: MockGachaViewModel = hiltViewModel(),
) {
    val uiState by mockGachaViewModel.uiState.collectAsStateWithLifecycle()

    //类型
    val tabs = arrayListOf(
        stringResource(id = R.string.gacha_pick_up_normal),
        stringResource(id = R.string.gacha_pick_up_fes),
        stringResource(id = R.string.gacha_pick_up_single),
    )
    //类型
    val pageTabs = arrayListOf(
        TabData(tab = stringResource(id = R.string.character)),
        TabData(tab = stringResource(id = R.string.title_history))
    )

    //卡池信息
    val showResult = uiState.showResult
    // 类型
    val mockGachaType = uiState.mockGachaType

    //滚动状态
    val unitScrollState = rememberScrollState()
    val historyScrollState = rememberLazyGridState()

    //返回拦截
    BackHandler(uiState.showResult) {
        mockGachaViewModel.changeShowResult(false)
    }

    MainScaffold(
        secondLineFab = {
            //抽取
            MockGachaFabContent(uiState, showResult, mockGachaType, mockGachaViewModel)
        },
        fabWithCustomPadding = {
            //卡池类型选择
            if (!showResult) {
                SelectTypeFab(
                    icon = MainIconType.CHANGE_FILTER_TYPE,
                    tabs = tabs,
                    selectedIndex = mockGachaType.type,
                    openDialog = uiState.openDialog,
                    changeDialog = mockGachaViewModel::changeDialog,
                    changeSelect = mockGachaViewModel::changeSelect,
                )
            } else {
                MainSmallFab(
                    iconType = MainIconType.RESET,
                    text = stringResource(id = R.string.reset_record),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
                ) {
                    mockGachaViewModel.deleteGachaResultByGachaId(uiState.gachaId)
                }
            }
        },
        enableClickClose = uiState.openDialog,
        onCloseClick = {
            mockGachaViewModel.changeDialog(false)
        },
        mainFabIcon = when {
            uiState.openDialog -> MainIconType.CLOSE
            uiState.showResult -> MainIconType.CLOSE
            else -> MainIconType.BACK
        },
        onMainFabClick = {
            when {
                uiState.openDialog -> mockGachaViewModel.changeDialog(false)
                uiState.showResult -> mockGachaViewModel.changeShowResult(false)
                else -> navigateUp()
            }
        },
    ) {
        Column(
            modifier = Modifier
                .padding(top = Dimen.largePadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //模拟抽卡顶部
            MockGachaHeader(uiState, mockGachaType, showResult, mockGachaViewModel)

            if (showResult) {
                //抽卡结果
                MockGachaResult(
                    uiState.gachaId,
                    uiState.pickUpList.getIds(),
                    mockGachaType
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
                            uiState.unitsInGacha?.let {
                                ToSelectMockGachaUnitList(
                                    unitScrollState,
                                    mockGachaType,
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

    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun MockGachaHeader(
    uiState: MockGachaUiState,
    mockGachaType: MockGachaType,
    showResult: Boolean,
    mockGachaViewModel: MockGachaViewModel
) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        FadeAnimation(uiState.pickUpList.isEmpty()) {
            MainText(
                text = when (mockGachaType) {
                    MockGachaType.PICK_UP -> stringResource(id = R.string.tip_to_pick_up_normal)
                    MockGachaType.PICK_UP_SINGLE -> stringResource(id = R.string.tip_to_pick_up_single)
                    else -> stringResource(id = R.string.tip_to_pick_up)
                }
            )
        }
        ExpandAnimation(uiState.pickUpList.isNotEmpty()) {
            //选中的角色
            FlowRow(
                modifier = Modifier.animateContentSize(defaultSpring())
            ) {
                uiState.pickUpList.forEachIndexed { index, gachaUnitInfo ->
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
                                mockGachaViewModel.updatePickUpList(gachaUnitInfo)
                            }
                        }
                        if (mockGachaType == MockGachaType.PICK_UP_SINGLE && index == uiState.pickUpList.size - 1) {
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
}

/**
 * 模拟抽卡按钮
 */
@Composable
private fun MockGachaFabContent(
    uiState: MockGachaUiState,
    showResult: Boolean,
    mockGachaType: MockGachaType,
    mockGachaViewModel: MockGachaViewModel
) {
    val scope = rememberCoroutineScope()

    FadeAnimation(
        modifier = Modifier
            .padding(
                end = Dimen.fabMargin,
                bottom = Dimen.fabMargin * 2 + Dimen.fabSize
            ),
        visible = uiState.pickUpList.isNotEmpty() && uiState.unitsInGacha != null
    ) {
        val tipSingleError = stringResource(id = R.string.tip_to_mock_single)
        MainSmallFab(
            iconType = MainIconType.MOCK_GACHA_PAY,
            text = if (showResult) "-1500" else stringResource(id = R.string.go_to_mock)
        ) {
            if (uiState.pickUpList.isNotEmpty()) {
                val mockGachaHelper = MockGachaHelper(
                    pickUpType = mockGachaType,
                    pickUpList = uiState.pickUpList,
                    uiState.unitsInGacha!!
                )

                if (!showResult) {
                    //自选单up，至少选择两名
                    if (mockGachaType == MockGachaType.PICK_UP_SINGLE && uiState.pickUpList.size < 2) {
                        ToastUtil.short(tipSingleError)
                        return@MainSmallFab
                    }
                    mockGachaViewModel.changeShowResult(true)
                    //创建卡池，若存在相同卡池，则不重新创建
                    scope.launch {
                        var id = UUID.randomUUID().toString()
                        val oldGacha = mockGachaViewModel.getGachaByPickUp(
                            mockGachaType,
                            uiState.pickUpList
                        )
                        if (oldGacha != null) {
                            id = oldGacha.gachaId
                            mockGachaViewModel.changeSelect(oldGacha.gachaType)
                        }
                        mockGachaViewModel.changeGachaId(id)
                        mockGachaViewModel.createMockGacha(
                            id,
                            mockGachaType,
                            uiState.pickUpList,
                        )
                    }
                } else if (uiState.gachaId != "") {
                    val result = mockGachaHelper.giveMe1500Gems()
                    //保存记录
                    mockGachaViewModel.addMockResult(uiState.gachaId, result)
                }
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
            .padding(vertical = Dimen.mediumPadding)
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
    gachaUnitList: List<GachaUnitInfo>,
    title: String,
    mockGachaViewModel: MockGachaViewModel = hiltViewModel()
) {
    val idList = gachaUnitList.map {
        it.unitId + (if (it.rarity == 1) 10 else 30)
    }

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
        MainText(text = gachaUnitList.size.toString())
    }

    GridIconList(
        idList = idList,
        iconResourceType = IconResourceType.CHARACTER
    ) { unitId ->
        gachaUnitList.find { it.unitId / 100 == unitId / 100 }?.let {
            mockGachaViewModel.updatePickUpList(it)
        }
    }
}