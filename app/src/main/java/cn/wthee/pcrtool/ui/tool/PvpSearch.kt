package cn.wthee.pcrtool.ui.tool

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.PvpFavoriteData
import cn.wthee.pcrtool.data.db.view.PvpCharacterData
import cn.wthee.pcrtool.data.db.view.getIdStr
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.PvpResultData
import cn.wthee.pcrtool.data.model.ResponseData
import cn.wthee.pcrtool.database.getRegion
import cn.wthee.pcrtool.service.PvpService
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.theme.CardTopShape
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import cn.wthee.pcrtool.viewmodel.PvpViewModel
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.google.gson.JsonArray
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.round


/**
 * 竞技场查询
 *
 * TODO 调整页面，适配悬浮窗
 */
@ExperimentalAnimationApi
@ExperimentalPagerApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun PvpSearchCompose(
    scrollState: LazyListState,
    toFavorite: () -> Unit,
    floatWindow: Boolean = false,
    viewModel: CharacterViewModel = hiltViewModel(),
    pvpViewModel: PvpViewModel = hiltViewModel()
) {
    //获取数据
    viewModel.getAllCharacter()
    val data = viewModel.allPvpCharacterData.observeAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    //滚动状态
    val positionIndex = remember {
        mutableStateOf(2)
    }
    //显示类型
    val showResult = remember {
        mutableStateOf(false)
    }
    //已选择的id
    val navIds = navViewModel.selectedPvpData.value
    val selectedIds = remember {
        if (navIds != null && navIds.isNotEmpty()) {
            mutableStateListOf(
                navIds[0],
                navIds[1],
                navIds[2],
                navIds[3],
                navIds[4],
            )
        } else {
            mutableStateListOf(
                PvpCharacterData(),
                PvpCharacterData(),
                PvpCharacterData(),
                PvpCharacterData(),
                PvpCharacterData(),
            )
        }
    }
    navViewModel.selectedPvpData.postValue(selectedIds.subList(0, 5))

    val research = remember {
        mutableStateOf(true)
    }
    val ids = navViewModel.selectedIds.observeAsState().value ?: ""
    val close = navViewModel.fabCloseClick.observeAsState().value ?: false

    if (showResult.value) {
        navViewModel.fabMainIcon.postValue(MainIconType.CLOSE)
    } else {
        navViewModel.fabMainIcon.postValue(MainIconType.BACK)
    }

    val url = stringResource(id = R.string.pcrdfans_url)
    val urlTip = stringResource(id = R.string.pcrdfans_com)

    //返回选择
    if (close) {
        showResult.value = false
        research.value = false
        navViewModel.fabCloseClick.postValue(false)
        navViewModel.selectedIds.postValue("")
        pvpViewModel.pvpResult.postValue(null)
        pvpViewModel.requesting.postValue(false)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        //从收藏重新查询时，自动填充数据
        if (ids != "" && research.value) {
            val idList = arrayListOf<Int>()
            for (id in ids.split("-")) {
                if (id.isNotBlank()) {
                    idList.add(id.toInt())
                }
            }
            //获取带角色站位的数据
            scope.launch {
                val selectedData = viewModel.getPvpCharacterByIds(idList)
                if (selectedData.isNotEmpty()) {
                    for (i in 0..4) {
                        selectedIds[i] = selectedData[4 - i]
                    }
                    showResult.value = true
                    research.value = false
                }
            }
        }

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
                        PvpIconItem(selectedIds = selectedIds, it = it, floatWindow)
                    }
                }
            }
            //供选择列表
            SlideAnimation(visible = data.value != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = Dimen.cardElevation,
                    shape = CardTopShape
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        if (showResult.value) {
                            //展示搜索结果
                            val idArray = JsonArray()
                            for (sel in selectedIds.subList(0, 5)) {
                                idArray.add(sel.unitId)
                            }
                            pvpViewModel.getPVPData(idArray)
                            pvpViewModel.requesting.postValue(true)
                            val result = pvpViewModel.pvpResult.observeAsState().value
                            PvpSearchResult(
                                selectedIds.subList(0, 5).getIdStr(),
                                result,
                                floatWindow
                            )
                        } else {
                            //选择页面
                            data.value?.let { dataValue ->
                                val character0 = dataValue.filter {
                                    it.position in 0..299
                                }
                                val character1 = dataValue.filter {
                                    it.position in 300..599
                                }
                                val character2 = dataValue.filter {
                                    it.position in 600..9999
                                }
                                val spanCount = 5
                                val showIcon = listOf(0, 0, 1, 0, 0)
                                //站位图标在列表中的位置
                                val positions = arrayListOf(0, 0, 0)
                                //中卫以上填充数
                                val filledCount1 =
                                    (spanCount - character0.size % spanCount) % spanCount
                                positions[1] =
                                    (spanCount + character0.size + filledCount1) / spanCount
                                //后卫以上填充数
                                val filledCount2 =
                                    (spanCount - character1.size % spanCount) % spanCount
                                positions[0] =
                                    ((positions[1] + 1) * spanCount + character1.size + filledCount2) / spanCount
                                //滚动监听
                                when (scrollState.firstVisibleItemIndex) {
                                    //后
                                    positions[0] -> {
                                        if (positionIndex.value != 0) {
                                            positionIndex.value = 0
                                        }
                                    }
                                    scrollState.layoutInfo.totalItemsCount - scrollState.layoutInfo.visibleItemsInfo.size -> {
                                        if (scrollState.layoutInfo.totalItemsCount != 0) {
                                            positionIndex.value = 0
                                        }
                                    }
                                    //中
                                    positions[1] -> {
                                        if (positionIndex.value != 1) {
                                            positionIndex.value = 1
                                        }
                                    }
                                    //前
                                    positions[2] -> {
                                        if (positionIndex.value != 2) {
                                            positionIndex.value = 2
                                        }
                                    }

                                }
                                Box {
                                    //供选择列表
                                    LazyVerticalGrid(
                                        cells = GridCells.Fixed(spanCount),
                                        state = scrollState
                                    ) {
                                        //前
                                        itemsIndexed(showIcon) { index, _ ->
                                            if (index == 2) {
                                                PvpPositionIcon(R.drawable.ic_position_0)
                                            }
                                        }
                                        items(character0) {
                                            PvpIconItem(selectedIds, it, floatWindow)
                                        }
                                        //中
                                        items(filledCount1) {
                                            CommonSpacer()
                                        }
                                        itemsIndexed(showIcon) { index, _ ->
                                            if (index == 2) {
                                                PvpPositionIcon(R.drawable.ic_position_1)
                                            }
                                        }
                                        items(character1) {
                                            PvpIconItem(selectedIds, it, floatWindow)
                                        }
                                        //后
                                        items(filledCount2 % spanCount) {
                                            CommonSpacer()
                                        }
                                        itemsIndexed(showIcon) { index, _ ->
                                            if (index == 2) {
                                                PvpPositionIcon(R.drawable.ic_position_2)
                                            }
                                        }
                                        items(character2) {
                                            PvpIconItem(selectedIds, it, floatWindow)
                                        }
                                        items(spanCount) {
                                            Spacer(modifier = Modifier.height(Dimen.iconSize))
                                        }
                                    }
                                    //指示器
                                    Row(
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .navigationBarsPadding()
                                            .padding(
                                                bottom = Dimen.fabSize + Dimen.fabMargin + Dimen.mediuPadding,
                                                start = Dimen.mediuPadding,
                                                end = Dimen.fabMargin
                                            )
                                    ) {
                                        val icons = arrayListOf(
                                            R.drawable.ic_position_2,
                                            R.drawable.ic_position_1,
                                            R.drawable.ic_position_0,
                                        )
                                        icons.forEachIndexed { index, it ->
                                            val modifier = if (positionIndex.value == index) {
                                                Modifier
                                                    .padding(Dimen.smallPadding)
                                                    .border(
                                                        Dimen.border,
                                                        MaterialTheme.colors.primary,
                                                        CircleShape
                                                    )
                                            } else {
                                                Modifier.padding(Dimen.smallPadding)
                                            }
                                            Image(painter = painterResource(id = it),
                                                contentDescription = null,
                                                modifier = modifier
                                                    .clip(CircleShape)
                                                    .size(Dimen.fabIconSize)
                                                    .clickable {
                                                        VibrateUtil(context).single()
                                                        positionIndex.value = index
                                                        scope.launch {
                                                            scrollState.scrollToItem(positions[index])
                                                        }
                                                    })
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
        //底部悬浮按钮
        if (!showResult.value) {
            Column(
                modifier = Modifier.align(Alignment.BottomEnd),
                horizontalAlignment = Alignment.End
            ) {
                Row(
                    modifier = Modifier
                        .padding(end = Dimen.fabMargin, bottom = Dimen.fabMargin)
                ) {
                    //悬浮窗
                    FabCompose(
                        iconType = if (floatWindow) MainIconType.FLOAT_CLOSE else MainIconType.FLOAT,
                        defaultPadding = false
                    ) {
                        val serviceIntent = Intent(context, PvpService::class.java)
                        val homeIntent = Intent(Intent.ACTION_MAIN)
                        homeIntent.addCategory(Intent.CATEGORY_HOME)

                        if (floatWindow) {
                            //结束悬浮窗服务
                            context.stopService(serviceIntent)
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (Settings.canDrawOverlays(context)) {
                                    //启动悬浮服务
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
                                context.startService(serviceIntent)
                                context.startActivity(homeIntent)
                            }

                        }

                    }
                }
                Row(
                    modifier = Modifier
                        .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
                ) {
                    //跳转
                    if (!floatWindow) {
                        FabCompose(
                            iconType = MainIconType.FRIEND_LINK,
                            modifier = Modifier.padding(end = Dimen.fabSmallMarginEnd)
                        ) {
                            //打开网页
                            openWebView(context, url, urlTip)
                        }
                    }
                    //收藏
                    FabCompose(
                        iconType = MainIconType.LOVE_FILL,
                        modifier = Modifier.padding(end = Dimen.fabSmallMarginEnd)
                    ) {
                        toFavorite()
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
                    val tip = stringResource(id = R.string.tip_select_5)
                    FabCompose(
                        iconType = MainIconType.PVP_SEARCH,
                        text = stringResource(id = R.string.pvp_search)
                    ) {
                        //查询
                        scope.launch {
                            if (selectedIds.contains(PvpCharacterData())) {
                                ToastUtil.short(tip)
                            } else {
                                navViewModel.fabMainIcon.postValue(MainIconType.CLOSE)
                                showResult.value = true
                            }
                        }
                    }
                }
            }

        }
    }

}


@Composable
private fun PvpPositionIcon(iconId: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimen.iconSize),
        contentAlignment = Alignment.Center
    ) {
        IconCompose(
            data = iconId,
            size = Dimen.fabIconSize
        )
    }
}


@Composable
fun PvpIconItem(
    selectedIds: SnapshotStateList<PvpCharacterData>,
    it: PvpCharacterData,
    floatWindow: Boolean
) {
    val tipSelectLimit = stringResource(id = R.string.tip_select_limit)
    val selected = selectedIds.contains(it)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(Dimen.smallPadding)
    ) {
        val icon = if (it.unitId == 0) {
            R.drawable.unknown_gray
        } else {
            CharacterIdUtil.getMaxIconUrl(
                it.unitId,
                MainActivity.r6Ids.contains(it.unitId)
            )
        }
        //图标
        IconCompose(data = icon) {
            //点击选择或取消选择
            if (selected) {
                var cancelSelectIndex = 0
                selectedIds.forEachIndexed { index, sel ->
                    if (it.position == sel.position) {
                        cancelSelectIndex = index
                    }
                }
                selectedIds[cancelSelectIndex] = PvpCharacterData()
            } else {
                val unSelected = selectedIds.find { it.position == 999 }
                if (unSelected == null) {
                    //选完了
                    ToastUtil.short(tipSelectLimit)
                } else {
                    //可以选择
                    selectedIds[0] = it
                }
            }
            selectedIds.sortByDescending { it.position }
        }
        //位置
        if (!floatWindow) {
            val text =
                if (it != PvpCharacterData()) it.position.toString() else stringResource(id = R.string.unselect)
            Text(
                text = text,
                color = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                style = MaterialTheme.typography.subtitle2,
                modifier = Modifier.padding(bottom = Dimen.smallPadding)
            )
        }
    }
}

/**
 * 查询结果页面
 */
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun PvpSearchResult(
    defIds: String,
    result: ResponseData<List<PvpResultData>>?,
    floatWindow: Boolean,
    pvpViewModel: PvpViewModel = hiltViewModel()
) {
    val placeholder = result == null
    val region = getRegion()
    pvpViewModel.getFavoritesList(defIds, region)
    //结果
    //收藏信息
    val favorites = pvpViewModel.favorites.observeAsState()
    val favoritesList = arrayListOf<String>()

    val context = LocalContext.current
    val vibrated = remember {
        mutableStateOf(false)
    }
    val mediuPadding = if (floatWindow) Dimen.smallPadding else Dimen.mediuPadding

    if (favorites.value != null) {
        SideEffect {
            favorites.value?.let {
                it.forEach { data ->
                    favoritesList.add(data.atks)
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            if (!placeholder) {
                if (result!!.message == "success") {
                    //振动提醒
                    if (!vibrated.value) {
                        vibrated.value = true
                        VibrateUtil(context).done()
                    }
                    val hasData = result.data!!.isNotEmpty()
                    if (hasData) {
                        //查询成功
                        val list = result.data!!.sortedByDescending { it.up }
                        FadeAnimation(visible = hasData) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxSize()
                            ) {
                                //展示查询结果
                                LazyColumn(
                                    contentPadding = PaddingValues(
                                        start = mediuPadding,
                                        end = mediuPadding
                                    )
                                ) {
                                    itemsIndexed(items = list) { index, item ->
                                        PvpAtkTeam(
                                            favoritesList,
                                            index + 1,
                                            item,
                                            region,
                                            floatWindow,
                                            pvpViewModel
                                        )
                                    }
                                    item {
                                        CommonSpacer()
                                    }
                                }
                            }
                        }
                    } else {
                        MainText(
                            text = stringResource(id = R.string.pvp_no_data),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                } else {
                    MainText(
                        text = stringResource(id = R.string.data_get_error),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            } else {
                //占位图
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    //展示查询结果
                    LazyColumn(
                        contentPadding = PaddingValues(
                            start = mediuPadding,
                            end = mediuPadding
                        )
                    ) {
                        items(10) {
                            PvpAtkTeam(
                                favoritesList,
                                0,
                                PvpResultData(),
                                region,
                                floatWindow,
                                pvpViewModel
                            )
                        }
                        item {
                            CommonSpacer()
                        }
                    }
                }
            }

        }
    }

}

/**
 * 查询结果 Item
 */
@ExperimentalMaterialApi
@Composable
private fun PvpAtkTeam(
    favoritesList: List<String>,
    i: Int,
    item: PvpResultData,
    region: Int,
    floatWindow: Boolean,
    viewModel: PvpViewModel
) {
    val placeholder = item.id == ""
    val scope = rememberCoroutineScope()
    val favorites = remember {
        mutableStateOf(favoritesList.contains(item.atk))
    }

    val mediuPadding = if (floatWindow) Dimen.smallPadding else Dimen.mediuPadding

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(mediuPadding)
    ) {
        Row {
            MainTitleText(
                text = stringResource(id = R.string.team_no, i.toString().fillZero()),
                modifier = Modifier
                    .padding(bottom = mediuPadding)
                    .placeholder(visible = placeholder, highlight = PlaceholderHighlight.shimmer())
            )
            Spacer(modifier = Modifier.weight(1f))
            //收藏
            IconCompose(
                data = if (favorites.value) MainIconType.LOVE_FILL.icon else MainIconType.LOVE_LINE.icon,
                size = Dimen.fabIconSize
            ) {
                if (!placeholder) {
                    scope.launch {
                        if (favorites.value) {
                            //已收藏，取消收藏
                            viewModel.delete(item.atk, item.def, region)
                        } else {
                            //未收藏，添加收藏
                            val simpleDateFormat =
                                SimpleDateFormat(
                                    "yyyy/MM/dd HH:mm:ss.SSS",
                                    Locale.CHINESE
                                )
                            val date = Date(System.currentTimeMillis())
                            viewModel.insert(
                                PvpFavoriteData(
                                    item.id,
                                    item.atk,
                                    item.def,
                                    simpleDateFormat.format(date),
                                    region
                                )
                            )
                        }
                        favorites.value = !favorites.value
                    }
                }
            }
        }

        MainCard(
            modifier = Modifier.placeholder(
                visible = placeholder,
                highlight = PlaceholderHighlight.shimmer()
            )
        ) {
            val upRatio = if (item.up == 0) 0 else {
                round(item.up * 1.0 / (item.up + item.down) * 100).toInt()
            }
            Column(
                modifier = Modifier.padding(
                    start = mediuPadding,
                    end = mediuPadding
                )
            ) {
                //点赞信息
                Row(
                    modifier = Modifier.padding(bottom = Dimen.smallPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MainContentText(
                        text = "${upRatio}%",
                        color = MaterialTheme.colors.primary,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.weight(0.3f)
                    )
                    MainContentText(
                        text = item.up.toString(),
                        color = colorResource(id = R.color.color_rank_21),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.weight(0.3f)
                    )
                    MainContentText(
                        text = item.down.toString(),
                        color = colorResource(id = R.color.color_rank_18_20),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.weight(1f)
                    )
                }
                //队伍角色图标
                //进攻
                Row(
                    modifier = Modifier
                        .padding(bottom = if (floatWindow) Dimen.smallPadding else Dimen.largePadding)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    item.getIdList(0).forEachIndexed { index, it ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(Dimen.smallPadding),
                            contentAlignment = Alignment.Center
                        ) {
                            IconCompose(
                                data = CharacterIdUtil.getMaxIconUrl(
                                    it,
                                    MainActivity.r6Ids.contains(it)
                                )
                            )
                        }
                    }
                }
            }
        }

    }
}