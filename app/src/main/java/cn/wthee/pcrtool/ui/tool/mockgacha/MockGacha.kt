package cn.wthee.pcrtool.ui.tool.mockgacha

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.GachaUnitInfo
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.UnitsInGacha
import cn.wthee.pcrtool.data.model.getIds
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.ExpandAnimation
import cn.wthee.pcrtool.ui.theme.FadeAnimation
import cn.wthee.pcrtool.ui.theme.defaultSpring
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.utils.Constants.MOCK_GACHA_MAX_UP_COUNT
import cn.wthee.pcrtool.viewmodel.MockGachaViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import java.util.*

/**
 * 模拟卡池类型
 */
enum class MockGachaType(val type: Int) {
    PICK_UP(0),
    FES(1),
    PICK_UP_SINGLE(2);

    companion object {
        fun getByValue(value: Int) = MockGachaType.values()
            .find { it.type == value } ?: MockGachaType.PICK_UP
    }
}

/**
 * 模拟卡池
 */
@OptIn(ExperimentalPagerApi::class, ExperimentalLayoutApi::class)
@Composable
fun MockGacha(
    pagerState: PagerState = rememberPagerState(),
    mockGachaViewModel: MockGachaViewModel = hiltViewModel()
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
    val allUnits = mockGachaViewModel.getGachaUnits().collectAsState(initial = null).value

    //页面
    val showResult = navViewModel.showMockGachaResult.observeAsState().value ?: false
    //卡池信息
    val gachaId = navViewModel.gachaId.observeAsState().value ?: ""
    val pickUpList = navViewModel.pickUpList.observeAsState().value ?: arrayListOf()
    // 类型
    val mockGachaType = remember {
        mutableStateOf(navViewModel.mockGachaType.value?.type ?: MockGachaType.PICK_UP.type)
    }
    mockGachaType.value =
        navViewModel.mockGachaType.observeAsState().value?.type ?: MockGachaType.PICK_UP.type

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
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                FadeAnimation(pickUpList.isEmpty()) {
                    MainText(
                        text = when (mockGachaType.value) {
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
                                IconCompose(
                                    data = ImageResourceHelper.getInstance()
                                        .getUnitIconUrl(gachaUnitInfo.unitId, 3),
                                    modifier = Modifier.padding(horizontal = Dimen.mediumPadding)
                                ) {
                                    //更新选中
                                    if (!showResult) {
                                        updatePickUpList(gachaUnitInfo)
                                    }
                                }
                                if (mockGachaType.value == MockGachaType.PICK_UP_SINGLE.type && index == pickUpList.size - 1) {
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
                    MockGachaType.getByValue(mockGachaType.value)
                )
            } else {
                MainTabRow(
                    pagerState = pagerState,
                    tabs = pageTabs,
                    modifier = Modifier
                        .fillMaxWidth(0.618f)
                        .align(Alignment.CenterHorizontally)
                )

                HorizontalPager(
                    count = 2,
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
                                    MockGachaType.getByValue(mockGachaType.value),
                                    it
                                )
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
            val mockGachaHelper = MockGachaHelper(
                pickUpType = MockGachaType.getByValue(mockGachaType.value),
                pickUpList = pickUpList,
                allUnits!!
            )

            val tipSingleError = stringResource(id = R.string.tip_to_mock_single)
            FabCompose(
                iconType = MainIconType.MOCK_GACHA_PAY,
                text = if (showResult) "-1500" else stringResource(id = R.string.go_to_mock)
            ) {
                if (pickUpList.isNotEmpty()) {
                    if (!showResult) {
                        //自选单up，至少选择两名
                        if (mockGachaType.value == MockGachaType.PICK_UP_SINGLE.type && pickUpList.size < 2) {
                            ToastUtil.short(tipSingleError)
                            return@FabCompose
                        }
                        navViewModel.showMockGachaResult.postValue(true)
                        //创建卡池，若存在相同卡池，则不重新创建
                        scope.launch {
                            var id = UUID.randomUUID().toString()
                            val oldGacha = mockGachaViewModel.getGachaByPickUp(
                                MockGachaType.getByValue(mockGachaType.value),
                                pickUpList
                            )
                            if (oldGacha != null) {
                                id = oldGacha.gachaId
                                mockGachaType.value = oldGacha.gachaType
                            }
                            navViewModel.gachaId.postValue(id)
                            mockGachaViewModel.createMockGacha(
                                id,
                                MockGachaType.getByValue(mockGachaType.value),
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
            SelectTypeCompose(
                icon = MainIconType.CHANGE_FILTER_TYPE,
                tabs = tabs,
                type = mockGachaType,
                width = Dimen.mockGachaTypeChangeWidth,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
            ) {
                //切换时清空
                navViewModel.pickUpList.postValue(arrayListOf())
                navViewModel.mockGachaType.postValue(MockGachaType.getByValue(mockGachaType.value))
            }
        } else {
            FabCompose(
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
    mockGachaType: MockGachaType,
    allUnits: UnitsInGacha
) {
    Column(
        modifier = Modifier
            .padding(Dimen.mediumPadding)
            .verticalScroll(rememberScrollState())
    ) {
        if (mockGachaType == MockGachaType.FES) {
            // Fes
            ToSelectMockGachaUnitListItem(allUnits.fesLimit, stringResource(R.string.gacha_fes))
        } else {
            // 限定
            ToSelectMockGachaUnitListItem(allUnits.limit, stringResource(R.string.gacha_limit))
            // 常驻
            ToSelectMockGachaUnitListItem(allUnits.normal3, stringResource(R.string.gacha_normal))
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
        updatePickUpList(it)
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
        spanCount = (Dimen.iconSize + Dimen.mediumPadding * 2).spanCount
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
                IconCompose(
                    data = ImageResourceHelper.getInstance()
                        .getUrl(ImageResourceHelper.ICON_UNIT, iconId)
                ) {
                    onClickItem(gachaUnitInfo)
                }
            }
        }
    }
}

/**
 * 更新选中列表
 * 最大可选角色数 6
 */
private fun updatePickUpList(data: GachaUnitInfo) {
    val pickUpList = navViewModel.pickUpList.value ?: arrayListOf()
    val gachaType = navViewModel.mockGachaType.value ?: 0
    val maxPick = if (gachaType == MockGachaType.FES) 1 else MOCK_GACHA_MAX_UP_COUNT

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
    navViewModel.pickUpList.postValue(newList)
}
