package cn.wthee.pcrtool.ui.tool.pvp

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.PvpFavoriteData
import cn.wthee.pcrtool.data.db.entity.PvpHistoryData
import cn.wthee.pcrtool.data.db.view.PvpCharacterData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.PositionType
import cn.wthee.pcrtool.data.model.PvpResultData
import cn.wthee.pcrtool.data.model.ResponseData
import cn.wthee.pcrtool.navigation.navigateUp
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.IconTextButton
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.MainTabRow
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.TabData
import cn.wthee.pcrtool.ui.components.VerticalGridList
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.BrowserUtil
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.spanCount
import kotlinx.coroutines.launch
import kotlin.math.max


/**
 * 竞技场查询
 * listState 用于记录滚动位置（悬浮窗）
 * @param floatWindow 是否为悬浮窗
 * @param initSpanCount 列数
 * @param selectListState 角色列表滚动状态
 * @param usedListState 常用列表滚动状态
 * @param resultListState 查询结果列表滚动状态
 * @param favoritesListState 收藏列表滚动状态
 * @param historyListState 历史查询列表滚动状态
 */
@Composable
fun PvpSearchScreen(
    floatWindow: Boolean,
    initSpanCount: Int = 0,
    pagerState: PagerState,
    selectListState: LazyGridState,
    usedListState: LazyGridState,
    resultListState: LazyGridState,
    favoritesListState: LazyGridState,
    historyListState: LazyGridState,
    toCharacter: (Int) -> Unit,
    pvpViewModel: PvpViewModel = hiltViewModel()
) {
    val uiState by pvpViewModel.uiState.collectAsStateWithLifecycle()

    //显示类型
    val showResult = navViewModel.showResult.observeAsState().value ?: false
    //已选择的id
    val selectedIds = navViewModel.selectedPvpData.observeAsState().value ?: arrayListOf()


    //返回拦截
    BackHandler(showResult) {
        navViewModel.showResult.postValue(false)
        pvpViewModel.changeRequesting(false)
    }

    //动态调整 spanCount
    val itemWidth = (Dimen.iconSize + Dimen.largePadding * 2)
    val appWidth = LocalView.current.width
    var size by remember { mutableStateOf(IntSize(width = appWidth, height = 0)) }
    val spanCount = if (initSpanCount == 0) {
        spanCount(size.width, itemWidth)
    } else {
        initSpanCount
    }

    //重置查询结果
    LaunchedEffect(selectedIds) {
        if (selectedIds.contains(PvpCharacterData())) {
            navViewModel.showResult.postValue(false)
        }
    }

    MainScaffold(
        modifier = Modifier.onSizeChanged {
            size = it
        },
        hideMainFab = floatWindow,
        mainFabIcon = if (showResult) MainIconType.CLOSE else MainIconType.BACK,
        onMainFabClick = {
            if (showResult) {
                navViewModel.showResult.postValue(false)
                pvpViewModel.changeRequesting(false)
            } else {
                navigateUp()
            }
        },
        fab = {
            //底部悬浮按钮
            if (!showResult && !floatWindow) {
                PvpSearchFabContent(
                    resultListState = resultListState,
                    searchByCharacterList = pvpViewModel::searchByCharacterList
                )
            }
        }
    ) {
        PvpSearchContent(
            floatWindow = floatWindow,
            showResult = showResult,
            spanCount = spanCount,
            characterDataList = uiState.allUnitList,
            selectedIds = selectedIds,
            pagerState = pagerState,
            selectListState = selectListState,
            usedListState = usedListState,
            resultListState = resultListState,
            favoritesListState = favoritesListState,
            historyListState = historyListState,
            result = uiState.pvpResult,
            favoritesList = uiState.favoritesList,
            allFavoritesList = uiState.allFavoritesList,
            recentlyUsedUnitList = uiState.recentlyUsedUnitList,
            historyList = uiState.historyList,
            research = pvpViewModel::research,
            delete = pvpViewModel::delete,
            insert = pvpViewModel::insert,
            toCharacter = toCharacter,
            searchByDefs = pvpViewModel::searchByDefs
        )
    }

}

@Composable
private fun PvpSearchContent(
    floatWindow: Boolean,
    showResult: Boolean,
    spanCount: Int,
    characterDataList: List<PvpCharacterData>,
    selectedIds: ArrayList<PvpCharacterData>,
    pagerState: PagerState,
    selectListState: LazyGridState,
    usedListState: LazyGridState,
    resultListState: LazyGridState,
    favoritesListState: LazyGridState,
    historyListState: LazyGridState,
    result: ResponseData<List<PvpResultData>>?,
    favoritesList: List<PvpFavoriteData>,
    allFavoritesList: List<PvpFavoriteData>,
    recentlyUsedUnitList: List<PvpCharacterData>,
    historyList: List<PvpHistoryData>,
    research: () -> Unit,
    delete: (String, String) -> Unit,
    insert: (PvpFavoriteData) -> Unit,
    toCharacter: (Int) -> Unit,
    searchByDefs: (List<Int>) -> Unit,
) {
    val mediumPadding = if (floatWindow) Dimen.smallPadding else Dimen.mediumPadding

    val tabs = arrayListOf(
        TabData(tab = stringResource(id = R.string.character)),
        TabData(tab = stringResource(id = R.string.title_recently_used)),
        TabData(tab = stringResource(id = R.string.title_favorite)),
        TabData(tab = stringResource(id = R.string.title_history)),
    )


    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        //标题
        if (!floatWindow) {
            PvpSearchHeader()
        }
        //已选择列表
        PvpSearchSelectedContent(mediumPadding, selectedIds, floatWindow)

        //展示查询结果页面或选择角色页面
        if (showResult) {
            PvpSearchResult(
                result = result,
                favoritesList = favoritesList,
                resultListState = resultListState,
                floatWindow = floatWindow,
                research = research,
                delete = delete,
                insert = insert
            )
        } else {
            //查询、收藏、历史
            MainTabRow(
                pagerState = pagerState,
                tabs = tabs,
                modifier = Modifier
                    .padding(horizontal = mediumPadding)
                    .fillMaxWidth(1f)
                    .align(Alignment.CenterHorizontally)
            ) {
                when (pagerState.currentPage) {
                    0 -> selectListState.scrollToItem(0)
                    1 -> usedListState.scrollToItem(0)
                    2 -> favoritesListState.scrollToItem(0)
                    3 -> historyListState.scrollToItem(0)
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.padding(top = mediumPadding)
            ) { pageIndex ->
                when (pageIndex) {
                    0 -> PvpToSelectList(
                        spanCount = spanCount,
                        selectListState = selectListState,
                        selectedIds = selectedIds,
                        floatWindow = floatWindow,
                        characterDataList = characterDataList
                    )

                    1 -> PvpRecentlyUsedList(
                        spanCount = spanCount,
                        usedListState = usedListState,
                        selectedIds = selectedIds,
                        floatWindow = floatWindow,
                        recentlyUsedUnitList = recentlyUsedUnitList
                    )

                    2 -> {
                        PvpFavorites(
                            allFavoritesList = allFavoritesList,
                            favoritesListState = favoritesListState,
                            toCharacter = toCharacter,
                            floatWindow = floatWindow,
                            delete = delete,
                            searchByDefs = searchByDefs
                        )
                    }

                    else -> {
                        PvpSearchHistory(
                            historyList = historyList,
                            historyListState = historyListState,
                            floatWindow = floatWindow,
                            toCharacter = toCharacter,
                            searchByDefs = searchByDefs
                        )
                    }
                }
            }
        }
    }
}

/**
 * 已选择的角色列表
 */
@Composable
private fun PvpSearchSelectedContent(
    mediumPadding: Dp,
    selectedIds: java.util.ArrayList<PvpCharacterData>,
    floatWindow: Boolean
) {
    Row(
        modifier = Modifier
            .padding(mediumPadding)
            .widthIn(max = Dimen.itemMaxWidth)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        selectedIds.forEach {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                PvpIconItem(
                    selectedIds = selectedIds,
                    pvpCharacterData = it,
                    floatWindow = floatWindow
                )
            }
        }
    }
}

/**
 * 顶部内容（标题、上传）
 */
@Composable
private fun PvpSearchHeader() {
    val context = LocalContext.current
    val url = stringResource(id = R.string.pcrdfans_url)
    val addUrl = stringResource(id = R.string.pcrdfans_upload_url)

    Row(
        modifier = Modifier.padding(
            horizontal = Dimen.largePadding,
            vertical = Dimen.mediumPadding
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MainTitleText(
            text = stringResource(id = R.string.pcrdfans),
            modifier = Modifier
                .clickable {
                    VibrateUtil(context).single()
                    BrowserUtil.open(url)
                }
        )
        Spacer(modifier = Modifier.weight(1f))
        //添加信息
        IconTextButton(
            icon = MainIconType.PVP_ADD,
            text = stringResource(id = R.string.pvp_upload),
            onClick = {
                //打开网页
                BrowserUtil.open(addUrl)
            }
        )
    }
}

/**
 * 底部按钮
 */
@Composable
private fun PvpSearchFabContent(
    resultListState: LazyGridState,
    searchByCharacterList: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    //悬浮窗
    MainSmallFab(
        iconType = MainIconType.FLOAT,
        onClick = {
            val homeIntent = Intent(Intent.ACTION_MAIN)
            homeIntent.addCategory(Intent.CATEGORY_HOME)
            if (Settings.canDrawOverlays(context)) {
                //启动悬浮服务
                val serviceIntent = Intent(context, PvpFloatService::class.java)
                navViewModel.floatServiceRun.postValue(true)
                context.stopService(serviceIntent)
                context.startActivity(homeIntent)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
            } else {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${MyApplication.context.packageName}")
                )
                context.startActivity(intent)
            }
        }
    )
    //查询
    MainSmallFab(
        iconType = MainIconType.PVP_SEARCH,
        text = stringResource(id = R.string.pvp_search),
        onClick = {
            //查询
            try {
                scope.launch {
                    resultListState.scrollToItem(0)
                }
            } catch (_: Exception) {

            }
            searchByCharacterList()
        }
    )
}

/**
 * 角色选择
 */
@Composable
private fun PvpToSelectList(
    spanCount: Int,
    selectListState: LazyGridState,
    selectedIds: ArrayList<PvpCharacterData>,
    floatWindow: Boolean,
    characterDataList: List<PvpCharacterData>,
) {
    val scope = rememberCoroutineScope()
    //选择页面
    val character0 = arrayListOf(PvpCharacterData(unitId = 0, type = 0))
    character0.addAll(characterDataList.filter {
        PositionType.getPositionType(it.position) == PositionType.POSITION_FRONT
    })
    val character1 = arrayListOf(PvpCharacterData(unitId = 1, type = 1))
    character1.addAll(characterDataList.filter {
        PositionType.getPositionType(it.position) == PositionType.POSITION_MIDDLE
    })
    val character2 = arrayListOf(PvpCharacterData(unitId = 2, type = 2))
    character2.addAll(characterDataList.filter {
        PositionType.getPositionType(it.position) == PositionType.POSITION_BACK
    })
    //站位图标在列表中的位置
    val positions = arrayListOf(0, 0, 0)
    //中卫在列表中的位置
    positions[1] = character0.size
    //后卫在列表中的位置
    positions[0] = (character0 + character1).size

    //全部角色
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        //角色图标列表
        LazyVerticalGrid(
            columns = GridCells.Fixed(max(5, spanCount)),
            state = selectListState,
            verticalArrangement = Arrangement.Center
        ) {
            items(
                items = character0 + character1 + character2,
                key = {
                    it.unitId
                }
            ) {
                if (it.type != -1) {
                    //位置图标
                    val iconId: Int = when (it.type) {
                        0 -> {
                            R.drawable.ic_position_0
                        }

                        1 -> {
                            R.drawable.ic_position_1
                        }

                        else -> {
                            R.drawable.ic_position_2
                        }
                    }

                    Box(
                        modifier = Modifier.size(Dimen.iconSize),
                        contentAlignment = Alignment.Center
                    ) {
                        MainIcon(
                            data = iconId,
                            size = Dimen.positionIconSize
                        )
                    }

                } else {
                    PvpIconItem(selectedIds, it, floatWindow)
                }
            }
            items(if (floatWindow) 5 else 10) {
                CommonSpacer()
            }
        }

        //指示器
        val modifier = if (floatWindow) {
            Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(bottom = Dimen.fabMargin, end = Dimen.smallPadding)
        } else {
            Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(
                    bottom = Dimen.fabSize + Dimen.fabMargin + Dimen.mediumPadding,
                    start = Dimen.mediumPadding,
                    end = Dimen.fabMargin
                )
        }
        Row(modifier = modifier) {
            val icons = arrayListOf(
                R.drawable.ic_position_2,
                R.drawable.ic_position_1,
                R.drawable.ic_position_0,
            )
            icons.forEachIndexed { index, it ->
                MainIcon(
                    data = it,
                    size = Dimen.fabIconSize,
                    modifier = Modifier.padding(Dimen.smallPadding),
                    onClick = {
                        scope.launch {
                            selectListState.scrollToItem(positions[index])
                        }
                    }
                )
            }
        }
    }

}

/**
 * 角色图标
 */
@Composable
fun PvpIconItem(
    selectedIds: ArrayList<PvpCharacterData>,
    pvpCharacterData: PvpCharacterData,
    floatWindow: Boolean
) {
    //角色图标
    val icon = if (pvpCharacterData.unitId == -1) {
        R.drawable.unknown_gray
    } else {
        ImageRequestHelper.getInstance().getMaxIconUrl(pvpCharacterData.unitId)
    }
    //选中判断
    val selected = selectedIds.find { it.unitId == pvpCharacterData.unitId } != null

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(Dimen.smallPadding)
    ) {
        //图标
        MainIcon(
            data = icon,
            onClick = {
                val newList = arrayListOf<PvpCharacterData>()
                selectedIds.forEach {
                    newList.add(it)
                }

                //点击选择或取消选择
                if (selected) {
                    var cancelSelectIndex = 0
                    newList.forEachIndexed { index, sel ->
                        if (pvpCharacterData.unitId == sel.unitId) {
                            cancelSelectIndex = index
                        }
                    }
                    newList[cancelSelectIndex] = PvpCharacterData()
                } else {

                    if (!newList.contains(PvpCharacterData())) {
                        ToastUtil.short(getString(R.string.tip_selected_5))
                    }

                    val unSelected = newList.find { it.position == 999 }
                    if (unSelected != null) {
                        //可以选择
                        newList[0] = pvpCharacterData
                    }
                }
                newList.sortWith(comparePvpCharacterData())
                navViewModel.selectedPvpData.postValue(newList)
            }
        )

        //位置
        val position =
            if (pvpCharacterData != PvpCharacterData()) pvpCharacterData.position else 0
        val textTopPadding = if (floatWindow) Dimen.divLineHeight else Dimen.smallPadding

        CharacterPositionText(
            showColor = selected,
            position = position,
            modifier = Modifier.padding(top = textTopPadding),
        )
    }
}

/**
 * 角色图标列表（5个/行）
 */
@Composable
fun PvpUnitIconLine(
    modifier: Modifier = Modifier,
    ids: List<Int>,
    floatWindow: Boolean,
    toCharacter: (Int) -> Unit
) {

    VerticalGridList(
        modifier = modifier,
        itemCount = ids.size,
        itemWidth = 0.dp,
        fixColumns = 5,
        contentPadding = if (floatWindow) {
            Dimen.smallPadding
        } else {
            Dimen.largePadding
        },
        verticalContentPadding = 0.dp
    ) {
        MainIcon(
            data = ImageRequestHelper.getInstance().getMaxIconUrl(ids[it]),
            wrapSize = true,
            onClick = {
                if (!floatWindow) {
                    toCharacter(ids[it])
                }
            }
        )
    }
}


/**
 * 角色站位文本样式
 */
@Composable
private fun CharacterPositionText(
    modifier: Modifier = Modifier,
    showColor: Boolean = true,
    position: Int,
    textAlign: TextAlign = TextAlign.Center,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium
) {
    val color = if (showColor) {
        PositionType.getPositionType(position).color
    } else {
        Color.Unspecified
    }

    Text(
        text = position.toString(),
        color = color,
        style = textStyle,
        maxLines = 1,
        textAlign = textAlign,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}

/**
 * 竞技场角色排序
 */
fun comparePvpCharacterData() = Comparator<PvpCharacterData> { o1, o2 ->
    val p = o2.position.compareTo(o1.position)
    if (p == 0) {
        o2.unitId.compareTo(o1.unitId)
    } else {
        p
    }
}


@CombinedPreviews
@Composable
private fun PvpSearchScreenContentPreview() {
    PreviewLayout {
        PvpSearchContent(
            floatWindow = false,
            showResult = false,
            spanCount = 0,
            characterDataList = arrayListOf(
                PvpCharacterData(unitId = 11, position = 100),
                PvpCharacterData(unitId = 22, position = 400),
                PvpCharacterData(unitId = 33, position = 700),
            ),
            selectedIds = arrayListOf(
                PvpCharacterData(),
                PvpCharacterData(),
                PvpCharacterData(),
                PvpCharacterData(),
                PvpCharacterData(),
            ),
            pagerState = rememberPagerState {
                4
            },
            selectListState = rememberLazyGridState(),
            usedListState = rememberLazyGridState(),
            resultListState = rememberLazyGridState(),
            favoritesListState = rememberLazyGridState(),
            historyListState = rememberLazyGridState(),
            result = null,
            favoritesList = arrayListOf(),
            allFavoritesList = arrayListOf(),
            recentlyUsedUnitList = arrayListOf(),
            historyList = arrayListOf(),
            research = {},
            delete = { _, _ -> },
            insert = {},
            toCharacter = {},
            searchByDefs = {}
        )
    }
}