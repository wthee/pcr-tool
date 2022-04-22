package cn.wthee.pcrtool.ui.home

import android.Manifest
import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
import cn.wthee.pcrtool.data.db.view.*
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.database.getRegion
import cn.wthee.pcrtool.ui.MainActivity.Companion.animOnFlag
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.NavActions
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.*
import cn.wthee.pcrtool.ui.tool.FreeGachaItem
import cn.wthee.pcrtool.ui.tool.GachaItem
import cn.wthee.pcrtool.ui.tool.NewsItem
import cn.wthee.pcrtool.ui.tool.StoryEventItem
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.viewmodel.NoticeViewModel
import cn.wthee.pcrtool.viewmodel.OverviewViewModel
import com.google.accompanist.flowlayout.FlowColumn
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

//权限
val permissions = arrayOf(
    Manifest.permission.READ_CALENDAR,
    Manifest.permission.WRITE_CALENDAR,
)

/**
 * 首页纵览
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Overview(
    actions: NavActions,
    scrollState: LazyListState,
    overviewViewModel: OverviewViewModel = hiltViewModel(),
    noticeViewModel: NoticeViewModel = hiltViewModel()
) {
    SideEffect {
        overviewViewModel.getR6Ids()
    }
    val region = getRegion()
    val coroutineScope = rememberCoroutineScope()
    val openDialog = navViewModel.openChangeDataDialog.observeAsState().value ?: false

    val downloadState = navViewModel.downloadProgress.observeAsState().value ?: -1
    val close = navViewModel.fabCloseClick.observeAsState().value ?: false
    val mainIcon = navViewModel.fabMainIcon.observeAsState().value ?: MainIconType.MAIN
    //切换数据关闭监听
    if (close) {
        navViewModel.openChangeDataDialog.postValue(false)
        navViewModel.fabMainIcon.postValue(MainIconType.MAIN)
        navViewModel.fabCloseClick.postValue(false)
    }
    if (mainIcon == MainIconType.MAIN) {
        navViewModel.openChangeDataDialog.postValue(false)
    }

    //添加日历确认弹窗
    val confirmState = remember {
        mutableStateOf(0)
    }

    val spanCount = ScreenUtil.getWidth() / getItemWidth().value.dp2px

    //公告列表
    val newsList =
        overviewViewModel.getNewsOverview(region).collectAsState(initial = arrayListOf()).value
    //进行中掉落活动
    val inProgressEventList =
        overviewViewModel.getCalendarEventList(0).collectAsState(initial = arrayListOf()).value
    //进行中剧情活动
    val inProgressStoryEventList =
        overviewViewModel.getStoryEventList(0).collectAsState(initial = arrayListOf()).value
    //进行中卡池
    val inProgressGachaList =
        overviewViewModel.getGachaList(0).collectAsState(initial = arrayListOf()).value
    //进行中免费十连
    val inProgressFreeGachaList =
        overviewViewModel.getFreeGachaList(0).collectAsState(initial = arrayListOf()).value
    //预告掉落活动
    val comingSoonEventList =
        overviewViewModel.getCalendarEventList(1).collectAsState(initial = arrayListOf()).value
    //预告剧情活动
    val comingSoonStoryEventList =
        overviewViewModel.getStoryEventList(1).collectAsState(initial = arrayListOf()).value
    //预告卡池
    val comingSoonGachaList =
        overviewViewModel.getGachaList(1).collectAsState(initial = arrayListOf()).value
    //预告免费十连
    val comingSoonFreeGachaList =
        overviewViewModel.getFreeGachaList(1).collectAsState(initial = arrayListOf()).value

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            TopBarCompose(actions, noticeViewModel)
            //角色
            CharacterSection(actions)

            //装备
            EquipSection(actions)

            //更多功能
            Section(
                titleId = R.string.function,
                iconType = MainIconType.FUNCTION,
                onClick = {
                    actions.toToolMore()
                }
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
                Column {
                    if (newsList.isNotEmpty()) {
                        newsList.forEach {
                            NewsItem(
                                news = it,
                                toDetail = actions.toNewsDetail
                            )
                        }
                    } else {
                        for (i in 0 until 3) {
                            NewsItem(
                                news = NewsTable(),
                                toDetail = actions.toNewsDetail
                            )
                        }
                    }
                }
            }

            //进行中
            CalendarEventSection(
                1,
                confirmState,
                spanCount,
                actions,
                inProgressEventList,
                inProgressStoryEventList,
                inProgressGachaList,
                inProgressFreeGachaList
            )

            //活动预告
            CalendarEventSection(
                2,
                confirmState,
                spanCount,
                actions,
                comingSoonEventList,
                comingSoonStoryEventList,
                comingSoonGachaList,
                comingSoonFreeGachaList
            )
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


/**
 * 角色
 */
@OptIn(ExperimentalPagerApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun CharacterSection(
    actions: NavActions,
    overviewViewModel: OverviewViewModel = hiltViewModel()
) {

    val context = LocalContext.current

    //角色总数
    val characterCount =
        overviewViewModel.getCharacterCount().collectAsState(initial = 0).value
    //角色列表
    val characterList =
        overviewViewModel.getCharacterList().collectAsState(initial = arrayListOf()).value

    Section(
        titleId = R.string.character,
        iconType = MainIconType.CHARACTER,
        hintText = characterCount.toString(),
        visible = characterList.isNotEmpty(),
        onClick = {
            actions.toCharacterList()
        }
    ) {
        if (characterList.isNotEmpty()) {
            HorizontalPager(
                count = characterList.size,
                state = rememberPagerState(),
                modifier = Modifier
                    .padding(vertical = Dimen.mediumPadding)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = Dimen.largePadding),
                itemSpacing = Dimen.mediumPadding
            ) { index ->
                val id = if (characterList.isEmpty()) 0 else characterList[index].id
                Card(
                    modifier = Modifier
                        .width(getItemWidth())
                        .clip(Shape.medium)
                        .clickable {
                            VibrateUtil(context).single()
                            actions.toCharacterDetail(id)
                        },
                    elevation = CardDefaults.cardElevation(0.dp),
                    shape = Shape.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
                ) {
                    ImageCompose(
                        data = ImageResourceHelper.getInstance().getMaxCardUrl(id),
                        ratio = RATIO,
                        loadingId = R.drawable.load,
                        errorId = R.drawable.error
                    )
                }
            }
        }
    }
}

/**
 * 装备
 */
@Composable
private fun EquipSection(
    actions: NavActions,
    overviewViewModel: OverviewViewModel = hiltViewModel()
) {
    val equipSpanCount =
        ScreenUtil.getWidth() / (Dimen.iconSize + Dimen.largePadding * 2).value.dp2px
    //装备总数
    val equipCount = overviewViewModel.getEquipCount().collectAsState(initial = 0).value
    //装备列表
    val equipList = overviewViewModel.getEquipList(maxOf(1, equipSpanCount) * 2)
        .collectAsState(initial = arrayListOf()).value

    Section(
        titleId = R.string.tool_equip,
        iconType = MainIconType.EQUIP,
        hintText = equipCount.toString(),
        visible = equipList.isNotEmpty(),
        onClick = {
            actions.toEquipList()
        }
    ) {
        VerticalGrid(
            spanCount = maxOf(1, equipSpanCount),
            modifier = Modifier.padding(horizontal = Dimen.commonItemPadding)
        ) {
            if (equipList.isNotEmpty()) {
                equipList.forEach {
                    Box(
                        modifier = Modifier
                            .padding(Dimen.mediumPadding)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        IconCompose(
                            data = ImageResourceHelper.getInstance()
                                .getEquipPic(it.equipmentId)
                        ) {
                            actions.toEquipDetail(it.equipmentId)
                        }
                    }
                }
            }
        }
    }
}

/**
 * 活动
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
private fun CalendarEventSection(
    calendarType: Int,
    confirmState: MutableState<Int>,
    spanCount: Int,
    actions: NavActions,
    eventList: List<CalendarEvent>,
    storyEventList: List<EventData>,
    gachaList: List<GachaInfo>,
    freeGachaList: List<FreeGachaInfo>,
) {
    Section(
        titleId = if (calendarType == 1) R.string.tool_calendar else R.string.tool_calendar_comming,
        iconType = if (calendarType == 1) MainIconType.CALENDAR_TODAY else MainIconType.CALENDAR,
        rightIconType = if (confirmState.value == calendarType) MainIconType.CLOSE else MainIconType.MAIN,
        onClick = {
            //弹窗确认
            if (confirmState.value == calendarType) {
                confirmState.value = 0
            } else {
                confirmState.value = calendarType
            }
        }
    ) {
        ExpandAnimation(visible = confirmState.value == calendarType) {
            CalendarEventOperation(
                confirmState,
                eventList,
                storyEventList,
                gachaList
            )
        }
        VerticalGrid(
            spanCount = spanCount,
            modifier = Modifier.padding(top = Dimen.mediumPadding)
        ) {
            gachaList.forEach {
                GachaItem(it, actions.toCharacterDetail)
            }
            storyEventList.forEach {
                StoryEventItem(it, actions.toCharacterDetail, actions.toAllPics)
            }
            eventList.forEach {
                CalendarEventItem(it)
            }
            freeGachaList.forEach {
                FreeGachaItem(it)
            }
        }
    }
}

/**
 * 添加日历确认
 */
@Composable
private fun CalendarEventOperation(
    confirmState: MutableState<Int>,
    eventList: List<CalendarEvent>,
    storyEventList: List<EventData>,
    gachaList: List<GachaInfo>,
) {
    val context = LocalContext.current
    val region = getRegion()
    val regionName = when (region) {
        2 -> stringResource(id = R.string.db_cn)
        3 -> stringResource(id = R.string.db_tw)
        4 -> stringResource(id = R.string.db_jp)
        else -> ""
    }

    // 添加日历确认
    MainCard(
        modifier = Modifier.padding(
            horizontal = Dimen.largePadding,
            vertical = Dimen.mediumPadding
        )
    ) {
        FlowColumn {
            //添加日历
            Row(
                modifier = Modifier
                    .padding(horizontal = Dimen.largePadding, vertical = Dimen.mediumPadding)
                    .clip(Shape.small)
                    .clickable {
                        VibrateUtil(context).single()
                        checkPermissions(context, permissions, false) {
                            //掉落活动
                            eventList.forEach {
                                SystemCalendarHelper().insert(
                                    it.startTime,
                                    it.endTime,
                                    getTypeDataToString(it)
                                )
                            }
                            //剧情活动
                            storyEventList.forEach {
                                SystemCalendarHelper().insert(
                                    it.startTime,
                                    it.endTime,
                                    it.getEventTitle()
                                )
                            }
                            //卡池
                            gachaList.forEach {
                                SystemCalendarHelper().insert(
                                    it.startTime,
                                    it.endTime,
                                    it.getDesc()
                                )
                            }
                            confirmState.value = 0
                        }
                    }
                    .padding(Dimen.mediumPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconCompose(data = MainIconType.ADD_CALENDAR, size = Dimen.fabIconSize)
                MainText(
                    text = stringResource(R.string.add_to_calendar),
                    modifier = Modifier.padding(horizontal = Dimen.smallPadding)
                )
            }

            //复制至剪贴板
            Row(
                modifier = Modifier
                    .padding(horizontal = Dimen.largePadding, vertical = Dimen.mediumPadding)
                    .clip(Shape.small)
                    .clickable {
                        VibrateUtil(context).single()
                        var allText = ""
                        //掉落活动
                        var eventText = ""
                        eventList.forEach {
                            val date = fixJpTime(it.startTime.formatTime, region).substring(
                                0,
                                10
                            ) + " ~ " + fixJpTime(it.endTime.formatTime, region).substring(0, 10)
                            eventText += "• $date\n${getTypeDataToString(it)}\n"
                        }
                        if (eventText != "") {
                            allText += "▶ 掉落活动\n$eventText\n\n"
                        }

                        //剧情活动
                        var storyText = ""
                        storyEventList.forEach {
                            val date = fixJpTime(it.startTime.formatTime, region).substring(
                                0,
                                10
                            ) + " ~ " + fixJpTime(it.endTime.formatTime, region).substring(0, 10)
                            storyText += "• $date\n${it.getEventTitle()}"
                        }
                        if (storyText != "") {
                            allText += "▶ 剧情活动\n$storyText\n\n"
                        }

                        //卡池
                        var gachaText = ""
                        gachaList.forEach {
                            val date = fixJpTime(it.startTime.formatTime, region).substring(
                                0,
                                10
                            ) + " ~ " + fixJpTime(it.endTime.formatTime, region).substring(0, 10)
                            gachaText += "• $date\n${it.getDesc()}"

                        }
                        if (gachaText != "") {
                            allText += "▶ 卡池信息\n$gachaText\n"
                        }
                        //复制
                        copyText(context, "——$regionName——\n\n$allText")
                        confirmState.value = 0
                    }
                    .padding(Dimen.mediumPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconCompose(data = MainIconType.COPY, size = Dimen.fabIconSize)
                MainText(
                    text = stringResource(R.string.copy_event),
                    modifier = Modifier.padding(horizontal = Dimen.smallPadding)
                )
            }
        }

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
    SmallFloatingActionButton(
        modifier = modifier
            .animateContentSize(defaultSpring())
            .padding(
                end = Dimen.fabMarginEnd,
                start = Dimen.fabMargin,
                top = Dimen.fabMargin,
                bottom = Dimen.fabMargin,
            ),
        containerColor = MaterialTheme.colorScheme.background,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = Dimen.fabElevation),
        shape = if (openDialog) androidx.compose.material.MaterialTheme.shapes.medium else CircleShape,
        onClick = {
            VibrateUtil(context).single()
            if (!openDialog) {
                navViewModel.fabMainIcon.postValue(MainIconType.CLOSE)
                navViewModel.openChangeDataDialog.postValue(true)
            } else {
                navViewModel.fabCloseClick.postValue(true)
            }
        },
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
                                navViewModel.openChangeDataDialog.postValue(false)
                                navViewModel.fabCloseClick.postValue(true)
                                if (region != i + 2) {
                                    coroutineScope.launch {
                                        DatabaseUpdater.changeDatabase(i + 2)
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
                    data = MainIconType.CHANGE_DATA,
                    tint = MaterialTheme.colorScheme.primary,
                    size = Dimen.fabIconSize
                )
            } else {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressCompose()
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
    rightIconType: MainIconType? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val modifier = if (onClick == null) {
        Modifier
    } else {
        //只有一个按钮有点击事件时
        Modifier
            .clip(Shape.medium)
            .clickable(onClick = {
                VibrateUtil(context).single()
                if (visible) {
                    onClick.invoke()
                }
            })
    }


    Column(
        modifier = if (animOnFlag) {
            Modifier
                .padding(top = Dimen.largePadding)
                .animateContentSize(defaultSpring())
        } else {
            Modifier
                .padding(top = Dimen.largePadding)
        }
    ) {
        Row(
            modifier = modifier.padding(Dimen.mediumPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconCompose(
                data = iconType,
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
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(start = Dimen.smallPadding, end = Dimen.smallPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onClick != null) {
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
                        data = rightIconType ?: MainIconType.MORE,
                        size = Dimen.fabIconSize,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

            }
        }

        SlideAnimation(visible = visible) {
            Column {
                content.invoke()
            }
        }
    }

}


/**
 * 日历信息
 */
@Composable
private fun CalendarEventItem(calendar: CalendarEvent) {
    val regionType = getRegion()
    val today = getToday()
    val sd = fixJpTime(calendar.startTime.formatTime, regionType)
    val ed = fixJpTime(calendar.endTime.formatTime, regionType)
    val inProgress = isInProgress(today, calendar.startTime, calendar.endTime, regionType)
    val comingSoon = isComingSoon(today, calendar.startTime, regionType)

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

    Column(
        modifier = Modifier.padding(
            horizontal = Dimen.largePadding,
            vertical = Dimen.mediumPadding
        )
    ) {
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
                        data = MainIconType.TIME_LEFT,
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
                        data = MainIconType.COUNTDOWN,
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

        MainCard {
            Column(modifier = Modifier.padding(Dimen.mediumPadding)) {
                //内容
                getTypeData(calendar).forEach {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Subtitle2(text = it.title + it.info)
                        if (it.multiple != "") {
                            Subtitle1(
                                text = it.multiple,
                                color = it.color,
                                modifier = Modifier.padding(horizontal = Dimen.smallPadding)
                            )
                        }
                    }
                }
                //结束日期
                CaptionText(text = ed, modifier = Modifier.fillMaxWidth())
            }
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
            val title = when (s.toInt()) {
                31, 41 -> stringResource(id = R.string.normal)
                32, 42 -> stringResource(id = R.string.hard)
                39, 49 -> stringResource(id = R.string.very_hard)
                34 -> stringResource(id = R.string.explore)
                37 -> stringResource(id = R.string.shrine)
                38 -> stringResource(id = R.string.temple)
                45 -> stringResource(id = R.string.dungeon)
                else -> ""
            }

            val dropMumColor = when (data.getFixedValue()) {
                1.5f, 2.0f -> colorResource(id = R.color.color_rank_7_10)
                3f -> colorResource(id = R.color.color_rank_18_20)
                4f -> colorResource(id = R.color.color_rank_21_23)
                else -> MaterialTheme.colorScheme.primary
            }
            val multiple = data.getFixedValue()
            events.add(
                CalendarEventData(
                    title,
                    (if ((multiple * 10).toInt() % 10 == 0) {
                        multiple.toInt().toString()
                    } else {
                        multiple.toString()
                    }) + "倍",
                    stringResource(id = if (s.toInt() > 40) R.string.mana else R.string.drop),
                    dropMumColor
                )
            )
        }
    } else {
        //露娜塔
        events.add(
            CalendarEventData(
                stringResource(id = R.string.tower),
                "",
                ""
            )
        )
    }

    return events
}

/**
 * 获取事项信息
 */
private fun getTypeDataToString(data: CalendarEvent): String {
    var eventTitle = ""
    if (data.type != "1") {
        //正常活动
        val list = data.type.split("-")
        list.forEach { s ->
            val title = when (s.toInt()) {
                31, 41 -> "普通关卡"
                32, 42 -> "困难关卡"
                39, 49 -> "高难关卡"
                34 -> "探索"
                37 -> "圣迹调查"
                38 -> "神殿调查"
                45 -> "地下城"
                else -> ""
            }
            val multiple = data.getFixedValue()
            eventTitle += title + (if (s.toInt() > 40) "玛那掉落量" else "掉落量") + (if ((multiple * 10).toInt() % 10 == 0) {
                multiple.toInt().toString()
            } else {
                multiple.toString()
            }) + "倍\n"

        }
    } else {
        //露娜塔
        eventTitle = "露娜塔"
    }

    return eventTitle
}