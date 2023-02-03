package cn.wthee.pcrtool.ui.tool.pvp

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.PvpCharacterData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import cn.wthee.pcrtool.viewmodel.PvpViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import kotlinx.coroutines.launch
import kotlin.math.max


/**
 * 竞技场查询
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun PvpSearchCompose(
    floatWindow: Boolean,
    initSpanCount: Int = 0,
    pagerState: PagerState,
    selectListState: LazyGridState,
    usedListState: LazyGridState,
    resultListState: LazyGridState,
    favoritesListState: LazyGridState,
    historyListState: LazyGridState,
    toCharacter: (Int) -> Unit,
    characterViewModel: CharacterViewModel = hiltViewModel(),
    pvpViewModel: PvpViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val mediumPadding = if (floatWindow) Dimen.smallPadding else Dimen.mediumPadding
    val tip = stringResource(id = R.string.tip_select_5)

    //获取数据
    val characterDataList =
        characterViewModel.getAllCharacter().collectAsState(initial = arrayListOf()).value
    //显示类型
    val showResult = navViewModel.showResult.observeAsState().value ?: false
    //已选择的id
    val selectedIds = navViewModel.selectedPvpData.observeAsState().value ?: arrayListOf()

    val close = navViewModel.fabCloseClick.observeAsState().value ?: false

    if (showResult) {
        navViewModel.fabMainIcon.postValue(MainIconType.CLOSE)
    } else {
        navViewModel.fabMainIcon.postValue(MainIconType.BACK)
    }

    //返回选择
    if (close) {
        navViewModel.showResult.postValue(false)
        navViewModel.fabCloseClick.postValue(false)
        pvpViewModel.requesting = false
    }

    val tabs = arrayListOf(
        stringResource(id = R.string.character),
        stringResource(id = R.string.title_recently_used),
        stringResource(id = R.string.title_love),
        stringResource(id = R.string.title_history),
    )
    val pageCount = tabs.size
    //动态调整 spanCount
    val normalSize = (Dimen.iconSize + Dimen.largePadding * 2)
    val spanCount = if (initSpanCount == 0) normalSize.spanCount else initSpanCount



    Box {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            //标题
            if (!floatWindow) {
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
                        text = stringResource(id = R.string.pvp_upload)
                    ) {
                        //打开网页
                        BrowserUtil.open(addUrl)
                    }
                }

            }
            //已选择列表
            Row(
                modifier = Modifier
                    .padding(mediumPadding)
                    .widthIn(max = getItemWidth() * 1.3f)
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

            //展示查询结果页面或选择角色页面
            if (showResult) {
                if (!selectedIds.contains(PvpCharacterData()) && selectedIds.size == 5) {
                    PvpSearchResult(resultListState, selectedIds, floatWindow, pvpViewModel)
                } else {
                    ToastUtil.short(tip)
                    navViewModel.showResult.postValue(false)
                }
            } else {
                //查询、收藏、历史
                MainTabRow(
                    pagerState = pagerState,
                    tabs = tabs,
                    modifier = Modifier
                        .padding(horizontal = mediumPadding)
                        .fillMaxWidth(1f)
                        .align(Alignment.CenterHorizontally)
                )

                HorizontalPager(
                    count = pageCount,
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
                            characterDataList = characterDataList,
                            pvpViewModel = pvpViewModel
                        )
                        2 -> {
                            PvpFavorites(
                                favoritesListState = favoritesListState,
                                toCharacter = toCharacter,
                                pvpViewModel = pvpViewModel,
                                floatWindow = floatWindow
                            )
                        }
                        else -> {
                            PvpSearchHistory(
                                historyListState = historyListState,
                                toCharacter = toCharacter,
                                pvpViewModel = pvpViewModel,
                                floatWindow = floatWindow
                            )
                        }
                    }
                }
            }
        }
        //底部悬浮按钮
        if (!showResult && !floatWindow) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        end = Dimen.fabMarginEnd,
                        bottom = Dimen.fabMargin
                    )
            ) {
                //悬浮窗
                FabCompose(
                    iconType = MainIconType.FLOAT
                ) {
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
                //查询
                FabCompose(
                    iconType = MainIconType.PVP_SEARCH,
                    text = stringResource(id = R.string.pvp_search)
                ) {
                    //查询
                    try {
                        scope.launch {
                            resultListState.scrollToItem(0)
                        }

                    } catch (_: Exception) {

                    }
                    pvpViewModel.pvpResult.postValue(null)
                    navViewModel.showResult.postValue(true)
                }
            }
        }
    }

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
        it.position in 0..299
    })
    val character1 = arrayListOf(PvpCharacterData(unitId = 1, type = 1))
    character1.addAll(characterDataList.filter {
        it.position in 300..599
    })
    val character2 = arrayListOf(PvpCharacterData(unitId = 2, type = 2))
    character2.addAll(characterDataList.filter {
        it.position in 600..9999
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
                        IconCompose(
                            data = iconId,
                            size = Dimen.smallIconSize
                        )
                    }

                } else {
                    PvpIconItem(selectedIds, it, floatWindow)
                }
            }
            items(5) {
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
                IconCompose(
                    data = it,
                    size = Dimen.fabIconSize,
                    modifier = Modifier.padding(Dimen.smallPadding)
                ) {
                    scope.launch {
                        selectListState.scrollToItem(positions[index])
                    }
                }
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
    val icon = if (pvpCharacterData.unitId == 0) {
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
        IconCompose(
            data = icon
        ) {
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
                val unSelected = newList.find { it.position == 999 }
                if (unSelected != null) {
                    //可以选择
                    newList[0] = pvpCharacterData
                }
            }
            newList.sortWith(comparePvpCharacterData())
            navViewModel.selectedPvpData.postValue(newList)
        }

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
    ids: List<Int>,
    floatWindow: Boolean,
    toCharacter: (Int) -> Unit
) {
    val mediumPadding = if (floatWindow) Dimen.smallPadding else Dimen.mediumPadding

    Row(
        modifier = Modifier
            .padding(mediumPadding)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        ids.forEach {
            Box(
                modifier = Modifier
                    .padding(horizontal = Dimen.smallPadding)
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                IconCompose(
                    data = ImageRequestHelper.getInstance().getMaxIconUrl(it),
                    wrapSize = true
                ) {
                    if (!floatWindow) {
                        toCharacter(it)
                    }
                }
            }
        }
    }
//    VerticalGrid(spanCount = 5) {
//        ids.forEach {
//            Box(
//                contentAlignment = Alignment.Center,
//                modifier = Modifier.weight(1f)
//            ) {
//                IconCompose(
//                    data = ImageResourceHelper.getInstance().getMaxIconUrl(it),
//                    wrapSize = true
//                ) {
//                    if (!floatWindow) {
//                        toCharacter(it)
//                    }
//                }
//            }
//
//        }

//    }
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
        getPositionColor(position)
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