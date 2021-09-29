package cn.wthee.pcrtool.ui.tool

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.PvpCharacterData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.service.PvpService
import cn.wthee.pcrtool.service.getFloatWindowHeight
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.tool.pvp.PvpFavorites
import cn.wthee.pcrtool.ui.tool.pvp.PvpSearchHistory
import cn.wthee.pcrtool.ui.tool.pvp.PvpSearchResult
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import cn.wthee.pcrtool.viewmodel.PvpViewModel
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch
import java.util.*


/**
 * 竞技场查询
 */
@ExperimentalCoilApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun PvpSearchCompose(
    floatWindow: Boolean = false,
    pagerState: PagerState = rememberPagerState(pageCount = 3),
    selectListState: ScrollState = rememberScrollState(),
    resultListState: LazyListState = rememberLazyListState(),
    favoritesListState: LazyListState = rememberLazyListState(),
    historyListState: LazyListState = rememberLazyListState(),
    toCharacter: (Int) -> Unit,
    characterViewModel: CharacterViewModel = hiltViewModel(),
    pvpViewModel: PvpViewModel = hiltViewModel()
) {
    //获取数据
    val data = characterViewModel.getAllCharacter().collectAsState(initial = arrayListOf()).value
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val mediumPadding = if (floatWindow) Dimen.smallPadding else Dimen.mediumPadding
    val serviceIntent = Intent(context, PvpService::class.java)
    val tip = stringResource(id = R.string.tip_select_5)


    //显示类型
    val showResult = navViewModel.showResult.observeAsState().value ?: false

    //已选择的id
    val selectedIds = navViewModel.selectedPvpData.observeAsState().value ?: arrayListOf()

    val research = remember {
        mutableStateOf(true)
    }
    val close = navViewModel.fabCloseClick.observeAsState().value ?: false

    if (showResult) {
        navViewModel.fabMainIcon.postValue(MainIconType.CLOSE)
    } else {
        navViewModel.fabMainIcon.postValue(MainIconType.BACK)
    }

    val url = stringResource(id = R.string.pcrdfans_url)
    val urlTip = stringResource(id = R.string.pcrdfans_com)
    val tabs = arrayListOf(
        stringResource(id = R.string.character),
        stringResource(id = R.string.title_love),
        stringResource(id = R.string.title_history),
    )

    //返回选择
    if (close) {
        navViewModel.showResult.postValue(false)
        research.value = false
        navViewModel.fabCloseClick.postValue(false)
        pvpViewModel.requesting = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        Column {
            //标题
            if (!floatWindow) {
                MainTitleText(
                    text = stringResource(id = R.string.pcrdfans),
                    modifier = Modifier
                        .padding(start = Dimen.largePadding, top = Dimen.largePadding)
                        .clickable {
                            openWebView(context, url, urlTip)
                        }
                )
            }
            //已选择列表
            Row(
                modifier = Modifier
                    .padding(
                        top = if (floatWindow) Dimen.smallPadding else Dimen.largePadding,
                        start = if (floatWindow) Dimen.smallPadding else Dimen.largePadding,
                        end = if (floatWindow) Dimen.smallPadding else Dimen.largePadding,
                        bottom = Dimen.smallPadding
                    )
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                selectedIds.forEach {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        PvpIconItem(
                            selectedIds = selectedIds,
                            it = it,
                            floatWindow = floatWindow,
                            selectedEffect = false
                        )
                    }
                }
            }
            //供选择列表
            if (showResult) {
                if (!research.value && selectedIds.contains(PvpCharacterData())) {
                    ToastUtil.short(tip)
                    navViewModel.showResult.postValue(false)
                } else {
                    PvpSearchResult(resultListState, selectedIds, floatWindow)
                }
            } else {
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                        )
                    },
                    backgroundColor = Color.Transparent,
                    contentColor = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .fillMaxWidth(0.618f)
                        .align(Alignment.CenterHorizontally)
                ) {
                    tabs.forEachIndexed { index, s ->
                        Tab(selected = pagerState.currentPage == index, onClick = {
                            scope.launch {
                                VibrateUtil(context).single()
                                pagerState.scrollToPage(index)
                            }
                        }) {
                            Subtitle2(text = s, modifier = Modifier.padding(Dimen.smallPadding))
                        }
                    }
                }
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.padding(top = mediumPadding)
                ) { pageIndex ->
                    when (pageIndex) {
                        0 -> PvpCharacterSelectPage(
                            selectListState = selectListState,
                            selectedIds = selectedIds,
                            floatWindow = floatWindow,
                            data = data,
                        )
                        1 -> {
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
                        end = if (floatWindow) Dimen.fabMargin else Dimen.fabMarginEnd,
                        bottom = Dimen.fabMargin
                    )
            ) {
                //悬浮窗
                FabCompose(
                    iconType = MainIconType.FLOAT,
                    modifier = Modifier.padding(end = Dimen.fabSmallMarginEnd)
                ) {
                    val homeIntent = Intent(Intent.ACTION_MAIN)
                    homeIntent.addCategory(Intent.CATEGORY_HOME)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (Settings.canDrawOverlays(context)) {
                            //启动悬浮服务
                            navViewModel.floatServiceRun.postValue(true)
                            context.startService(serviceIntent)
                            context.startActivity(homeIntent)
                        } else {
                            val intent = Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:${MyApplication.context.packageName}")
                            )
                            context.startActivity(intent)
                        }
                    } else {
                        navViewModel.floatServiceRun.postValue(true)
                        context.startService(serviceIntent)
                        context.startActivity(homeIntent)
                    }

                }
                //跳转
                FabCompose(
                    iconType = MainIconType.FRIEND_LINK,
                    modifier = Modifier.padding(end = Dimen.fabSmallMarginEnd)
                ) {
                    //打开网页
                    openWebView(context, url, urlTip)
                }
                //添加信息
                val addUrl = stringResource(id = R.string.pcrdfans_upload_url)
                val addTip = stringResource(id = R.string.pvp_info_add_tip)
                FabCompose(
                    iconType = MainIconType.PVP_ADD,
                    modifier = Modifier.padding(end = Dimen.fabSmallMarginEnd)
                ) {
                    //打开网页
                    openWebView(context, addUrl, addTip)
                }
                //查询
                FabCompose(
                    iconType = MainIconType.PVP_SEARCH,
                    text = if (floatWindow) "" else stringResource(id = R.string.pvp_search)
                ) {
                    //查询
                    scope.launch {
                        try {
                            resultListState.scrollToItem(0)
                        } catch (ignore: Exception) {

                        }

                        if (selectedIds.contains(PvpCharacterData())) {
                            ToastUtil.short(tip)
                        } else {
                            pvpViewModel.pvpResult.postValue(null)
                            navViewModel.fabMainIcon.postValue(MainIconType.CLOSE)
                            navViewModel.showResult.postValue(true)
                        }
                    }
                }
            }
        }
    }

}

/**
 * 角色选择
 */
@ExperimentalCoilApi
@ExperimentalPagerApi
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
private fun PvpCharacterSelectPage(
    selectListState: ScrollState,
    selectedIds: ArrayList<PvpCharacterData>,
    floatWindow: Boolean,
    data: List<PvpCharacterData>
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    //选择页面
    val character0 = data.filter {
        it.position in 0..299
    }
    val character1 = data.filter {
        it.position in 300..599
    }
    val character2 = data.filter {
        it.position in 600..9999
    }
    val spanCount = 5
    //站位图标在列表中的位置
    val positions = arrayListOf(0, 0, 0)
    val padding = (Dimen.smallPadding * 2).value.dp2px
    val itemHeight = if (!floatWindow) {
        ScreenUtil.getWidth() / 5
    } else {
        ((getFloatWindowHeight() * 0.618f / 5 - padding) / 0.618f + padding).toInt()
    }
    val lines = arrayListOf(0, 0, 0)
    lines[0] = getLine(character0, spanCount) + 1
    lines[1] = getLine(character1, spanCount) + 1
    lines[2] = getLine(character2, spanCount) + 1
    //中卫以上填充数
    positions[1] = lines[0] * itemHeight
    //后卫以上填充数
    positions[0] = (lines[0] + lines[1]) * itemHeight

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(modifier = Modifier.verticalScroll(selectListState)) {
            PvpPositionIcon(R.drawable.ic_position_0, itemHeight)
            VerticalGrid(spanCount = spanCount) {
                character0.forEach {
                    PvpIconItem(selectedIds, it, floatWindow)
                }
            }
            PvpPositionIcon(R.drawable.ic_position_1, itemHeight)
            VerticalGrid(spanCount = spanCount) {
                character1.forEach {
                    PvpIconItem(selectedIds, it, floatWindow)
                }
            }
            PvpPositionIcon(R.drawable.ic_position_2, itemHeight)
            VerticalGrid(spanCount = spanCount) {
                character2.forEach {
                    PvpIconItem(selectedIds, it, floatWindow)
                }
            }
            CommonSpacer()
            CommonSpacer()
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
                Image(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(Dimen.smallPadding)
                        .clip(CircleShape)
                        .size(Dimen.fabIconSize)
                        .clickable {
                            VibrateUtil(context).single()
                            scope.launch {
                                selectListState.animateScrollTo(positions[index])
                            }
                        })
            }
        }
    }
}

/**
 * 获取行数
 */
private fun getLine(
    list: List<PvpCharacterData>,
    spanCount: Int
) = if (list.size % spanCount == 0) {
    list.size / spanCount
} else {
    list.size / spanCount + 1
}

/**
 * 位置图标
 */
@ExperimentalCoilApi
@Composable
private fun PvpPositionIcon(iconId: Int, height: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height.px2dp.dp),
        contentAlignment = Alignment.Center
    ) {
        IconCompose(
            data = iconId,
            size = Dimen.fabIconSize,
        )
    }
}

/**
 * 角色图标
 */
@ExperimentalCoilApi
@Composable
fun PvpIconItem(
    selectedIds: ArrayList<PvpCharacterData>,
    it: PvpCharacterData,
    floatWindow: Boolean,
    selectedEffect: Boolean = true
) {
    val tipSelectLimit = stringResource(id = R.string.tip_select_limit)
    val selected = selectedIds.contains(it)
    val newList = arrayListOf<PvpCharacterData>()
    selectedIds.forEach {
        newList.add(it)
    }
    val icon = if (it.unitId == 0) {
        R.drawable.unknown_gray
    } else {
        CharacterIdUtil.getMaxIconUrl(
            it.unitId,
            MainActivity.r6Ids.contains(it.unitId)
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(Dimen.smallPadding)
            .fillMaxWidth()
            .aspectRatio(if (floatWindow) 0.618f else 1f)
    ) {
        //图标
        IconCompose(data = icon, wrapSize = floatWindow) {
            //点击选择或取消选择
            if (selected) {
                var cancelSelectIndex = 0
                newList.forEachIndexed { index, sel ->
                    if (it.position == sel.position) {
                        cancelSelectIndex = index
                    }
                }
                newList[cancelSelectIndex] = PvpCharacterData()
            } else {
                val unSelected = newList.find { it.position == 999 }
                if (unSelected == null) {
                    //选完了
                    ToastUtil.short(tipSelectLimit)
                } else {
                    //可以选择
                    newList[0] = it
                }
            }
            newList.sortByDescending { it.position }
            navViewModel.selectedPvpData.postValue(newList)
        }
        //位置
        val text =
            if (it != PvpCharacterData()) it.position.toString() else stringResource(id = R.string.unselect)
        SelectText(
            selected = selected && selectedEffect,
            text = text,
            padding = if (floatWindow) Dimen.divLineHeight else Dimen.smallPadding
        )
    }
}


