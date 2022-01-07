package cn.wthee.pcrtool.ui.tool.pvp

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.PvpCharacterData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.MainActivity.Companion.r6Ids
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import cn.wthee.pcrtool.viewmodel.PvpViewModel
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch
import java.util.*


/**
 * 竞技场查询
 */
@ExperimentalPagerApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun PvpSearchCompose(
    floatWindow: Boolean = false,
    initSpanCount: Int = 0,
    pagerState: PagerState = rememberPagerState(),
    selectListState: LazyListState = rememberLazyListState(),
    resultListState: LazyListState = rememberLazyListState(),
    favoritesListState: LazyListState = rememberLazyListState(),
    historyListState: LazyListState = rememberLazyListState(),
    toCharacter: (Int) -> Unit,
    characterViewModel: CharacterViewModel = hiltViewModel(),
    pvpViewModel: PvpViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val mediumPadding = if (floatWindow) Dimen.smallPadding else Dimen.mediumPadding
    val tip = stringResource(id = R.string.tip_select_5)

    //获取数据
    val data = characterViewModel.getAllCharacter().collectAsState(initial = arrayListOf()).value
    //显示类型
    val showResult = navViewModel.showResult.observeAsState().value ?: false
    //已选择的id
    val selectedIds = navViewModel.selectedPvpData.observeAsState().value ?: arrayListOf()
    //重新查询
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

    //动态调整 spanCount
    val normalSize = (Dimen.iconSize + Dimen.largePadding * 2).value.dp2px
    val spanCount = if (initSpanCount == 0) ScreenUtil.getWidth() / normalSize else initSpanCount


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
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
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .padding(mediumPadding)
                        .width(getItemWidth())
                        .align(Alignment.Center),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    selectedIds.forEach {
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            PvpIconItem(
                                selectedIds = selectedIds,
                                it = it,
                                floatWindow = floatWindow
                            )
                        }
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
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth(0.618f)
                        .align(Alignment.CenterHorizontally)
                ) {
                    tabs.forEachIndexed { index, s ->
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
                    count = 3,
                    state = pagerState,
                    modifier = Modifier.padding(top = mediumPadding)
                ) { pageIndex ->
                    when (pageIndex) {
                        0 -> PvpCharacterSelectPage(
                            spanCount = spanCount,
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
                    iconType = MainIconType.FLOAT
                ) {
                    val homeIntent = Intent(Intent.ACTION_MAIN)
                    homeIntent.addCategory(Intent.CATEGORY_HOME)
                    if (Settings.canDrawOverlays(context)) {
                        //启动悬浮服务
                        val serviceIntent = Intent(context, PvpFloatService::class.java)
                        navViewModel.floatServiceRun.postValue(true)
                        serviceIntent.putExtra("spanCount", spanCount)
                        context.stopService(serviceIntent)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(serviceIntent)
                        } else {
                            context.startService(serviceIntent)
                        }
                        context.startActivity(homeIntent)
                    } else {
                        val intent = Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:${MyApplication.context.packageName}")
                        )
                        context.startActivity(intent)
                    }
                }
                //跳转
                FabCompose(
                    iconType = MainIconType.FRIEND_LINK
                ) {
                    //打开网页
                    openWebView(context, url, urlTip)
                }
                //添加信息
                val addUrl = stringResource(id = R.string.pcrdfans_upload_url)
                val addTip = stringResource(id = R.string.pvp_info_add_tip)
                FabCompose(
                    iconType = MainIconType.PVP_ADD
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
@ExperimentalPagerApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
private fun PvpCharacterSelectPage(
    spanCount: Int,
    selectListState: LazyListState,
    selectedIds: ArrayList<PvpCharacterData>,
    floatWindow: Boolean,
    data: List<PvpCharacterData>
) {
    val scope = rememberCoroutineScope()
    //选择页面
    val character0 = arrayListOf(PvpCharacterData(type = 0))
    character0.addAll(data.filter {
        it.position in 0..299
    })
    val character1 = arrayListOf(PvpCharacterData(type = 1))
    character1.addAll(data.filter {
        it.position in 300..599
    })
    val character2 = arrayListOf(PvpCharacterData(type = 2))
    character2.addAll(data.filter {
        it.position in 600..9999
    })

    //站位图标在列表中的位置
    val positions = arrayListOf(0, 0, 0)
    //中卫以上填充数
    positions[1] = getLine(character0, spanCount)
    //后卫以上填充数
    positions[0] = getLine(character0 + character1, spanCount)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        //角色图标列表
        LazyVerticalGrid(cells = GridCells.Fixed(spanCount), state = selectListState) {
            items(character0 + character1 + character2) {
                PvpIconItem(selectedIds, it, floatWindow)
            }
            items(spanCount) {
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
 * 获取行数
 */
private fun getLine(
    list: List<PvpCharacterData>,
    spanCount: Int
) = list.size / spanCount


/**
 * 角色图标
 */
@ExperimentalMaterialApi
@Composable
fun PvpIconItem(
    selectedIds: ArrayList<PvpCharacterData>,
    it: PvpCharacterData,
    floatWindow: Boolean
) {
    val textTopPadding = if (floatWindow) Dimen.divLineHeight else Dimen.smallPadding
    val iconSize = if (floatWindow) Dimen.mediumIconSize else Dimen.iconSize

    if (it.type != -1) {
        val iconId: Int
        val textColorId: Int
        val textId: Int

        when (it.type) {
            0 -> {
                iconId = R.drawable.ic_position_0
                textColorId = R.color.color_rank_18_20
                textId = R.string.position_0
            }
            1 -> {
                iconId = R.drawable.ic_position_1
                textColorId = R.color.color_rank_7_10
                textId = R.string.position_1
            }
            2 -> {
                iconId = R.drawable.ic_position_2
                textColorId = R.color.colorPrimary
                textId = R.string.position_2
            }
            else -> {
                iconId = R.drawable.unknown_gray
                textColorId = R.color.black
                textId = R.string.all
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(Dimen.smallPadding)
                .fillMaxWidth()
        ) {
            Box(modifier = Modifier.size(iconSize), contentAlignment = Alignment.Center) {
                IconCompose(
                    data = iconId,
                    size = if (floatWindow) Dimen.smallIconSize else Dimen.fabIconSize
                )
            }

            Text(
                text = stringResource(id = textId),
                color = colorResource(id = textColorId),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = textTopPadding)
            )
        }

    } else {
        val tipSelectLimit = stringResource(id = R.string.tip_select_limit)
        val selected = selectedIds.contains(it)
        val newList = arrayListOf<PvpCharacterData>()
        selectedIds.forEach {
            newList.add(it)
        }
        val icon = if (it.unitId == 0) {
            R.drawable.unknown_gray
        } else {
            ImageResourceHelper.getInstance().getMaxIconUrl(
                it.unitId,
                r6Ids.contains(it.unitId)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(Dimen.smallPadding)
                .fillMaxWidth()
        ) {
            //图标
            IconCompose(
                data = icon,
                size = iconSize
            ) {
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
            val position = if (it != PvpCharacterData()) it.position else 0
            CharacterPositionText(
                showColor = selected,
                position = position,
                modifier = Modifier.padding(top = textTopPadding),
            )
        }
    }
}


