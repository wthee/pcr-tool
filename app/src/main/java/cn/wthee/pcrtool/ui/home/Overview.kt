package cn.wthee.pcrtool.ui.home

import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.NewsTable
import cn.wthee.pcrtool.data.db.view.CalendarEvent
import cn.wthee.pcrtool.data.db.view.CalendarEventData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.database.getRegion
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.NavActions
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.settingSP
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shape
import cn.wthee.pcrtool.ui.theme.SlideAnimation
import cn.wthee.pcrtool.ui.theme.defaultSpring
import cn.wthee.pcrtool.ui.tool.NewsItem
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.viewmodel.OverviewViewModel
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 首页纵览
 */
@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun Overview(
    actions: NavActions,
    overviewViewModel: OverviewViewModel = hiltViewModel()
) {
    SideEffect {
        overviewViewModel.getR6Ids()
    }
    val context = LocalContext.current
    val region = getRegion()
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val openDialog = MainActivity.navViewModel.openChangeDataDialog.observeAsState().value ?: false
    val downloadState = MainActivity.navViewModel.downloadProgress.observeAsState().value ?: -1
    val close = MainActivity.navViewModel.fabCloseClick.observeAsState().value ?: false
    val mainIcon = MainActivity.navViewModel.fabMainIcon.observeAsState().value ?: MainIconType.MAIN
    //切换数据关闭监听
    if (close) {
        MainActivity.navViewModel.openChangeDataDialog.postValue(false)
        MainActivity.navViewModel.fabMainIcon.postValue(MainIconType.MAIN)
        MainActivity.navViewModel.fabCloseClick.postValue(false)
    }
    if (mainIcon == MainIconType.MAIN) {
        MainActivity.navViewModel.openChangeDataDialog.postValue(false)
    }

    val characterList =
        overviewViewModel.getCharacterList().collectAsState(initial = arrayListOf()).value
    val equipList = overviewViewModel.getEquipList().collectAsState(initial = arrayListOf()).value
    val inProgressEventList =
        overviewViewModel.getCalendarEventList(0).collectAsState(initial = arrayListOf()).value
    val comingSoonEventList =
        overviewViewModel.getCalendarEventList(1).collectAsState(initial = arrayListOf()).value
    val newsList =
        overviewViewModel.getNewsOverview(region).collectAsState(initial = arrayListOf()).value

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.verticalScroll(scrollState)) {
            TopBarCompose(actions)
            //角色
            Section(
                titleId = R.string.character,
                iconType = MainIconType.CHARACTER,
                hintText = characterList.size.toString(),
                visible = characterList.isNotEmpty(),
                onClick = {
                    actions.toCharacterList()
                }
            ) {
                if (characterList.isNotEmpty()) {
                    HorizontalPager(
                        count = characterList.size,
                        state = rememberPagerState(),
                        modifier = Modifier.fillMaxWidth(),
                    ) { index ->
                        val id = if (characterList.isEmpty()) 0 else characterList[index].id
                        Card(
                            modifier = Modifier
                                .padding(
                                    top = Dimen.mediumPadding,
                                    bottom = Dimen.mediumPadding,
                                    start = Dimen.largePadding,
                                    end = Dimen.largePadding
                                )
                                .heightIn(
                                    min = Dimen.characterCardMinHeight
                                ),
                            onClick = {
                                VibrateUtil(context).single()
                                actions.toCharacterDetail(id)
                            },
                            elevation = 0.dp,
                        ) {
                            ImageCompose(CharacterIdUtil.getMaxCardUrl(id), ratio = RATIO)
                        }
                    }
                }
            }

            //装备
            Section(
                titleId = R.string.tool_equip,
                iconType = MainIconType.EQUIP,
                hintText = equipList.size.toString(),
                visible = equipList.isNotEmpty(),
                onClick = {
                    actions.toEquipList()
                }
            ) {
                VerticalGrid(maxColumnWidth = Dimen.iconSize * 2) {
                    if (equipList.size > 0) {
                        equipList.subList(0, 10).forEach {
                            Box(
                                modifier = Modifier
                                    .padding(Dimen.mediumPadding)
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                IconCompose(data = getEquipIconUrl(it.equipmentId)) {
                                    actions.toEquipDetail(it.equipmentId)
                                }
                            }
                        }
                    }
                }
            }

            //更多功能
            Section(
                titleId = R.string.function,
                iconType = MainIconType.FUNCTION
            ) {
                ToolMenu(actions = actions)
            }

            //新闻
            Section(
                titleId = R.string.tool_news,
                iconType = MainIconType.NEWS,
                onClick = {
                    actions.toNews()
                }
            ) {
                Column(
                    modifier = Modifier
                        .padding(
                            top = Dimen.mediumPadding,
                            start = Dimen.largePadding,
                            end = Dimen.largePadding
                        )
                ) {
                    if (newsList.isNotEmpty()) {
                        newsList.forEach {
                            NewsItem(
                                news = it,
                                toDetail = actions.toNewsDetail
                            )
                        }
                    } else {
                        for (i in 0..2) {
                            NewsItem(
                                news = NewsTable(),
                                toDetail = actions.toNewsDetail
                            )
                        }
                    }
                }
            }
            //日历
            if (inProgressEventList.isNotEmpty()) {
                Section(
                    titleId = R.string.tool_calendar,
                    iconType = MainIconType.CALENDAR_TODAY
                ) {
                    Column(
                        modifier = Modifier
                            .padding(
                                top = Dimen.mediumPadding,
                                start = Dimen.largePadding,
                                end = Dimen.largePadding
                            )
                            .fillMaxWidth()
                    ) {
                        inProgressEventList.forEach {
                            CalendarItem(it)
                        }
                    }
                }
            }
            if (comingSoonEventList.isNotEmpty()) {
                Section(
                    titleId = R.string.tool_calendar_comming,
                    iconType = MainIconType.CALENDAR
                ) {
                    Column(
                        modifier = Modifier
                            .padding(
                                top = Dimen.mediumPadding,
                                start = Dimen.largePadding,
                                end = Dimen.largePadding
                            )
                            .fillMaxWidth()
                    ) {
                        comingSoonEventList.forEach {
                            CalendarItem(it)
                        }
                    }
                }
            }
            CommonSpacer()
        }

        //数据切换功能
        ChangeDbCompose(
            openDialog,
            downloadState,
            coroutineScope,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
        )
    }
}

//数据切换选择弹窗
@Composable
private fun ChangeDbCompose(
    openDialog: Boolean,
    downloadState: Int,
    coroutineScope: CoroutineScope,
    modifier: Modifier
) {
    val context = LocalContext.current
    val region = getRegion()
    val menuTexts = arrayListOf(
        stringResource(id = R.string.db_cn),
        stringResource(id = R.string.db_tw),
        stringResource(id = R.string.db_jp),
    )

    //数据切换
    FloatingActionButton(
        modifier = modifier
            .animateContentSize(defaultSpring())
            .padding(
                end = Dimen.fabMarginEnd,
                start = Dimen.fabMargin,
                top = Dimen.fabMargin,
                bottom = Dimen.fabMargin,
            )
            .defaultMinSize(
                minWidth = Dimen.fabSize,
                minHeight = Dimen.fabSize
            ),
        onClick = {
            VibrateUtil(context).single()
            if (!openDialog) {
                MainActivity.navViewModel.fabMainIcon.postValue(MainIconType.CLOSE)
                MainActivity.navViewModel.openChangeDataDialog.postValue(true)
            } else {
                MainActivity.navViewModel.fabCloseClick.postValue(true)
            }
        },
        shape = if (openDialog) Shape.medium else CircleShape,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = Dimen.fabElevation),
        backgroundColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.primary,
    ) {
        if (openDialog) {
            Column(
                modifier = Modifier.width(Dimen.dataChangeWidth),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //版本
                for (i in 0..2) {
                    val mModifier = if (region == i + 2) {
                        Modifier.fillMaxWidth()
                    } else {
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                VibrateUtil(context).single()
                                MainActivity.navViewModel.openChangeDataDialog.postValue(false)
                                if (region != i + 2) {
                                    coroutineScope.launch {
                                        DatabaseUpdater.changeRegion(i + 2)
                                    }
                                }
                            }
                    }
                    SelectText(
                        selected = region == i + 2,
                        text = menuTexts[i],
                        textStyle = MaterialTheme.typography.titleLarge,
                        modifier = mModifier.padding(Dimen.mediumPadding)
                    )
                }
            }
        } else {
            if (downloadState == -2) {
                IconCompose(
                    data = MainIconType.CHANGE_DATA.icon,
                    tint = MaterialTheme.colorScheme.primary,
                    size = Dimen.menuIconSize
                )
            } else {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(Dimen.menuIconSize)
                            .padding(Dimen.smallPadding),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp
                    )
                    //显示下载进度
                    if (downloadState in 1..99) {
                        Text(
                            text = downloadState.toString(),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

/**
 * 标题
 */
@Composable
private fun Section(
    @StringRes titleId: Int,
    iconType: MainIconType,
    hintText: String = "",
    visible: Boolean = true,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val modifier = (if (onClick == null) {
        Modifier
    } else {
        Modifier.clickable(onClick = {
            VibrateUtil(context).single()
            if (visible) {
                onClick.invoke()
            }
        })
    })

    Column(
        modifier = if (MainActivity.animOn) {
            Modifier
                .padding(top = Dimen.largePadding)
                .animateContentSize(defaultSpring())
        } else {
            Modifier
                .padding(top = Dimen.largePadding)
        }
    ) {
        Row(
            modifier = modifier.padding(
                start = Dimen.largePadding,
                end = Dimen.largePadding,
                top = Dimen.mediumPadding,
                bottom = Dimen.mediumPadding
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconCompose(
                data = iconType.icon,
                size = Dimen.fabIconSize,
                tint = MaterialTheme.colorScheme.onSurface
            )
            MainText(
                text = stringResource(id = titleId),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = Dimen.mediumPadding),
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onSurface
            )
            //点击跳转
            if (onClick != null) {
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .clip(Shape.small)
                        .clickable {
                            VibrateUtil(context).single()
                            onClick.invoke()
                        }
                        .padding(start = Dimen.smallPadding, end = Dimen.smallPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (hintText != "") {
                        Text(text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    baselineShift = BaselineShift(+0.2f),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            ) {
                                append(hintText)
                            }
                        })
                    }
                    IconCompose(
                        data = MainIconType.MORE.icon,
                        size = Dimen.fabIconSize,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

            }
        }

        SlideAnimation(visible = visible) {
            content.invoke()
        }
    }

}


/**
 * 日历信息
 */
@ExperimentalMaterialApi
@Composable
private fun CalendarItem(calendar: CalendarEvent) {
    val today = getToday(settingSP(LocalContext.current).getInt(Constants.SP_DATABASE_TYPE, 2))
    val sd = calendar.startTime.formatTime
    val ed = calendar.endTime.formatTime
    val inProgress = today.second(sd) > 0 && ed.second(today) > 0
    val comingSoon = today.second(sd) < 0

    val color = when {
        inProgress -> {
            MaterialTheme.colorScheme.primary
        }
        comingSoon -> {
            colorResource(id = R.color.news_system)
        }
        else -> {
            colorResource(id = R.color.color_rank_4_6)
        }
    }

    FlowRow(
        modifier = Modifier.padding(bottom = Dimen.mediumPadding),
        crossAxisAlignment = FlowCrossAxisAlignment.Center
    ) {
        //开始日期
        MainTitleText(
            text = sd.substring(0, 10),
            backgroundColor = color
        )
        //天数
        MainTitleText(
            text = ed.days(sd),
            modifier = Modifier.padding(start = Dimen.smallPadding), backgroundColor = color
        )
        //计时
        Row(
            modifier = Modifier.padding(start = Dimen.smallPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (inProgress) {
                IconCompose(
                    data = MainIconType.TIME_LEFT.icon,
                    size = Dimen.smallIconSize,
                    tint = color
                )
                MainContentText(
                    text = stringResource(R.string.progressing, ed.dates(today)),
                    modifier = Modifier.padding(start = Dimen.smallPadding),
                    textAlign = TextAlign.Start,
                    color = color
                )
            }
            if (comingSoon) {
                IconCompose(
                    data = MainIconType.COUNTDOWN.icon,
                    size = Dimen.smallIconSize,
                    tint = color
                )
                MainContentText(
                    text = stringResource(R.string.coming_soon, sd.dates(today)),
                    modifier = Modifier.padding(start = Dimen.smallPadding),
                    textAlign = TextAlign.Start,
                    color = color
                )
            }
        }
    }

    MainCard(modifier = Modifier.padding(bottom = Dimen.largePadding)) {
        Column(modifier = Modifier.padding(Dimen.mediumPadding)) {
            //内容
            getTypeData(calendar).forEach {
                Subtitle1(text = it.title + it.info)
            }
            //结束日期
            CaptionText(text = ed, modifier = Modifier.fillMaxWidth())
        }
    }
}


/**
 * 获取事项信息
 */
@Composable
private fun getTypeData(data: CalendarEvent): ArrayList<CalendarEventData> {
    val events = arrayListOf<CalendarEventData>()
    if (data.type != "1") {
        //正常活动
        val list = data.type.split("-")
        list.forEach { s ->
            var colorId = R.color.black
            val title = when (s.toInt()) {
                31 -> {
                    colorId = R.color.color_map_n
                    stringResource(id = R.string.normal)
                }
                32 -> {
                    colorId = R.color.color_map_h
                    stringResource(id = R.string.hard)
                }
                39 -> {
                    colorId = R.color.color_map_vh
                    stringResource(id = R.string.very_hard)
                }
                34 -> {
                    colorId = R.color.color_rank_21
                    stringResource(id = R.string.explore)
                }
                37 -> {
                    colorId = R.color.news_update
                    stringResource(id = R.string.shrine)
                }
                38 -> {
                    colorId = R.color.news_update
                    stringResource(id = R.string.temple)
                }
                45 -> {
                    colorId = R.color.color_rank_2_3
                    stringResource(id = R.string.dungeon)
                }
                else -> ""
            }
            events.add(
                CalendarEventData(
                    title,
                    stringResource(id = R.string.drop_x, data.getFixedValue()),
                    colorId
                )
            )
        }
    } else {
        //露娜塔
        events.add(
            CalendarEventData(
                stringResource(id = R.string.tower),
                "",
            )
        )
    }

    return events
}
