package cn.wthee.pcrtool.ui.tool.mockgacha

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import cn.wthee.pcrtool.ui.theme.FadeAnimation
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.viewmodel.MockGachaViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
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
    gachaType.value = navViewModel.gachaType.observeAsState().value ?: 0

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

    val tipToSelect = stringResource(id = R.string.tip_to_pick_up)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(top = Dimen.largePadding)
                .fillMaxSize()
        ) {
            //选中的角色
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = Dimen.iconSize),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (pickUpList.isEmpty()) {
                    MainText(text = tipToSelect)
                } else {
                    pickUpList.forEach {
                        IconCompose(
                            data = ImageResourceHelper.getInstance().getUnitIconUrl(it.unitId, 3),
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
                MockGachaResult(gachaId, pickUpList.getIds())
            } else {
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    containerColor = Color.Transparent,
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
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                    }
                }

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
            val mockGachaHelper = MockGachaHelper(
                pickUpType = gachaType.value,
                pickUpList = pickUpList,
                allUnits!!
            )

            FabCompose(
                iconType = if (showResult) MainIconType.MOCK_GACHA_PAY else MainIconType.MOCK_GACHA,
                text = if (showResult) "-1500" else stringResource(id = R.string.go_to_mock)
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
                                gachaType.value = oldGacha.gachaType
                            }
                            navViewModel.gachaId.postValue(id)
                            mockGachaViewModel.createMockGacha(
                                id,
                                gachaType.value,
                                pickUpList
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
                type = gachaType,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
            ) {
                //切换时清空
                navViewModel.pickUpList.postValue(arrayListOf())
                navViewModel.gachaType.postValue(gachaType.value)
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
    gachaType: Int,
    allUnits: UnitsInGacha
) {
    Column(
        modifier = Modifier
            .padding(Dimen.mediumPadding)
            .verticalScroll(rememberScrollState())
    ) {
        if (gachaType == 1) {
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
            top = Dimen.mediumPadding,
            start = Dimen.mediumPadding,
            end = Dimen.mediumPadding
        ),
        maxColumnWidth = Dimen.iconSize + Dimen.mediumPadding * 2,
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
 */
private fun updatePickUpList(data: GachaUnitInfo) {
    val pickUpList = navViewModel.pickUpList.value ?: arrayListOf()
    val gachaType = navViewModel.gachaType.value ?: 0
    val maxPick = if (gachaType == 0) 4 else 1

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
