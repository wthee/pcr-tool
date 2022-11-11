package cn.wthee.pcrtool.ui.home

import android.Manifest
import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.edit
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.NewsTable
import cn.wthee.pcrtool.data.db.view.*
import cn.wthee.pcrtool.data.enums.EventType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.MainActivity.Companion.animOnFlag
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.NavActions
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.mainSP
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.ExpandAnimation
import cn.wthee.pcrtool.ui.theme.colorWhite
import cn.wthee.pcrtool.ui.theme.defaultSpring
import cn.wthee.pcrtool.ui.tool.*
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.viewmodel.NoticeViewModel
import cn.wthee.pcrtool.viewmodel.OverviewViewModel
import com.google.accompanist.flowlayout.FlowColumn
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

//首页
private enum class OverviewType(val id: Int) {
    CHARACTER(0),
    EQUIP(1),
    TOOL(2),
    NEWS(3),
    IN_PROGRESS_EVENT(4),
    COMING_SOON_EVENT(5);

    companion object {
        fun getByValue(value: Int) = values()
            .find { it.id == value } ?: CHARACTER
    }
}

/**
 * 首页纵览
 */
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

    //编辑模式
    val isEditMode = remember {
        mutableStateOf(false)
    }

    val sp = mainSP()

    //自定义显示
    val localData = sp.getString(Constants.SP_OVERVIEW_ORDER, "0-1-2-3-4-5") ?: ""
    var overviewOrderData = navViewModel.overviewOrderData.observeAsState().value
    if (overviewOrderData == null || overviewOrderData.isEmpty()) {
        overviewOrderData = localData
        navViewModel.overviewOrderData.postValue(overviewOrderData)
    }


    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(state = scrollState) {
            item {
                TopBarCompose(isEditMode, noticeViewModel)
            }
            if (!isEditMode.value) {
                overviewOrderData.intArrayList.forEach {
                    item {
                        when (OverviewType.getByValue(it)) {
                            OverviewType.CHARACTER -> CharacterSection(
                                actions = actions,
                                isEditMode = false
                            )
                            OverviewType.EQUIP -> EquipSection(
                                actions = actions,
                                isEditMode = false
                            )
                            OverviewType.TOOL -> ToolSection(
                                actions = actions,
                                isEditMode = false
                            )
                            OverviewType.NEWS -> NewsSection(
                                actions = actions,
                                isEditMode = false
                            )
                            OverviewType.IN_PROGRESS_EVENT -> InProgressEventSection(
                                confirmState,
                                actions = actions, isEditMode = false
                            )
                            OverviewType.COMING_SOON_EVENT -> ComingSoonEventSection(
                                confirmState,
                                actions = actions, isEditMode = false
                            )
                        }
                    }
                }
            } else {
                // 编辑模式显示全部
                item {
                    Subtitle2(
                        text = stringResource(R.string.tip_click_to_add),
                        modifier = Modifier
                            .padding(vertical = Dimen.mediumPadding)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                //角色
                item {
                    CharacterSection(actions, isEditMode = true)
                }

                //装备
                item {
                    EquipSection(actions, isEditMode = true)
                }

                //更多功能
                item {
                    ToolSection(actions, isEditMode = true)
                }

                //新闻
                item {
                    NewsSection(actions, isEditMode = true)
                }

                //进行中
                item {
                    InProgressEventSection(confirmState, actions, isEditMode = true)
                }

                //活动预告
                item {
                    ComingSoonEventSection(confirmState, actions, isEditMode = true)
                }
            }

            item {
                CommonSpacer()
            }
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
 * 功能模块
 */
@Composable
private fun ToolSection(
    actions: NavActions,
    isEditMode: Boolean,
) {
    val id = OverviewType.TOOL.id
    Section(
        id = id,
        titleId = R.string.function,
        iconType = MainIconType.FUNCTION,
        isEditMode = isEditMode,
        onClick = {
            if (isEditMode)
                editOverviewMenuOrder(id)
            else
                actions.toToolMore(false)
        }
    ) {
        ToolMenu(actions = actions)
    }
}

/**
 * 公告
 */
@Composable
private fun NewsSection(
    actions: NavActions,
    isEditMode: Boolean,
    overviewViewModel: OverviewViewModel = hiltViewModel()
) {
    val id = OverviewType.NEWS.id
    //公告列表
    val newsList =
        overviewViewModel.getNewsOverview().collectAsState(initial = null).value

    Section(
        id = id,
        titleId = R.string.tool_news,
        iconType = MainIconType.NEWS,
        isEditMode = isEditMode,
        onClick = {
            if (isEditMode)
                editOverviewMenuOrder(id)
            else
                actions.toNews()
        }
    ) {
        Column {
            if (newsList == null) {
                for (i in 0 until 3) {
                    NewsItem(
                        news = NewsTable(),
                        toNewsDetail = actions.toNewsDetail
                    )
                }
            } else if (newsList.isNotEmpty()) {
                newsList.forEach {
                    NewsItem(
                        news = it,
                        toNewsDetail = actions.toNewsDetail
                    )
                }
            } else {
                CenterTipText(stringResource(id = R.string.no_data))
            }
        }
    }
}

/**
 * 进行中活动
 */
@Composable
private fun InProgressEventSection(
    confirmState: MutableState<Int>,
    actions: NavActions,
    isEditMode: Boolean,
    overviewViewModel: OverviewViewModel = hiltViewModel()
) {

    //进行中掉落活动
    val inProgressEventList =
        overviewViewModel.getCalendarEventList(EventType.IN_PROGRESS)
            .collectAsState(initial = arrayListOf()).value
    //进行中剧情活动
    val inProgressStoryEventList =
        overviewViewModel.getStoryEventList(EventType.IN_PROGRESS)
            .collectAsState(initial = arrayListOf()).value
    //进行中卡池
    val inProgressGachaList =
        overviewViewModel.getGachaList(EventType.IN_PROGRESS)
            .collectAsState(initial = arrayListOf()).value
    //进行中免费十连
    val inProgressFreeGachaList =
        overviewViewModel.getFreeGachaList(EventType.IN_PROGRESS)
            .collectAsState(initial = arrayListOf()).value
    //进行中生日日程
    val inProgressBirthdayList =
        overviewViewModel.getBirthdayList(EventType.IN_PROGRESS)
            .collectAsState(initial = arrayListOf()).value

    CalendarEventLayout(
        isEditMode,
        EventType.IN_PROGRESS,
        confirmState,
        actions,
        inProgressEventList,
        inProgressStoryEventList,
        inProgressGachaList,
        inProgressFreeGachaList,
        inProgressBirthdayList
    )
}

/**
 * 活动预告
 */
@Composable
private fun ComingSoonEventSection(
    confirmState: MutableState<Int>,
    actions: NavActions,
    isEditMode: Boolean,
    overviewViewModel: OverviewViewModel = hiltViewModel()
) {
    //预告掉落活动
    val comingSoonEventList =
        overviewViewModel.getCalendarEventList(EventType.COMING_SOON)
            .collectAsState(initial = arrayListOf()).value
    //预告剧情活动
    val comingSoonStoryEventList =
        overviewViewModel.getStoryEventList(EventType.COMING_SOON)
            .collectAsState(initial = arrayListOf()).value
    //预告卡池
    val comingSoonGachaList =
        overviewViewModel.getGachaList(EventType.COMING_SOON)
            .collectAsState(initial = arrayListOf()).value
    //预告免费十连
    val comingSoonFreeGachaList =
        overviewViewModel.getFreeGachaList(EventType.COMING_SOON)
            .collectAsState(initial = arrayListOf()).value
    //生日
    val comingSoonBirthdayList =
        overviewViewModel.getBirthdayList(EventType.COMING_SOON)
            .collectAsState(initial = arrayListOf()).value

    CalendarEventLayout(
        isEditMode,
        EventType.COMING_SOON,
        confirmState,
        actions,
        comingSoonEventList,
        comingSoonStoryEventList,
        comingSoonGachaList,
        comingSoonFreeGachaList,
        comingSoonBirthdayList
    )
}


/**
 * 角色
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
private fun CharacterSection(
    actions: NavActions,
    isEditMode: Boolean,
    overviewViewModel: OverviewViewModel = hiltViewModel()
) {
    val id = OverviewType.CHARACTER.id

    //角色总数
    val characterCount =
        overviewViewModel.getCharacterCount().collectAsState(initial = 0).value
    //角色列表
    val characterList =
        overviewViewModel.getCharacterInfoList().collectAsState(initial = arrayListOf()).value
    Section(
        id = id,
        titleId = R.string.character,
        iconType = MainIconType.CHARACTER,
        hintText = characterCount.toString(),
        contentVisible = characterList.isNotEmpty(),
        isEditMode = isEditMode,
        onClick = {
            if (isEditMode)
                editOverviewMenuOrder(id)
            else
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
                val unitId = if (characterList.isEmpty()) 0 else characterList[index].id
                MainCard(
                    modifier = Modifier.width(getItemWidth()),
                    onClick = {
                        actions.toCharacterDetail(unitId)
                    }
//                    elevation = CardDefaults.cardElevation(0.dp),
//                    shape = MaterialTheme.shapes.medium,
//                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
                ) {
                    ImageCompose(
                        data = ImageResourceHelper.getInstance().getMaxCardUrl(unitId),
                        ratio = RATIO
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
    isEditMode: Boolean,
    overviewViewModel: OverviewViewModel = hiltViewModel()
) {
    val id = OverviewType.EQUIP.id
    val equipSpanCount =
        ScreenUtil.getWidth() / (Dimen.iconSize + Dimen.largePadding * 2).value.dp2px
    //装备总数
    val equipCount = overviewViewModel.getEquipCount().collectAsState(initial = 0).value
    //装备列表
    val equipList = overviewViewModel.getEquipList(maxOf(1, equipSpanCount) * 2)
        .collectAsState(initial = arrayListOf()).value

    Section(
        id = id,
        titleId = R.string.tool_equip,
        iconType = MainIconType.EQUIP,
        hintText = equipCount.toString(),
        contentVisible = equipList.isNotEmpty(),
        isEditMode = isEditMode,
        onClick = {
            if (isEditMode)
                editOverviewMenuOrder(id)
            else
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
@Composable
private fun CalendarEventLayout(
    isEditMode: Boolean,
    calendarType: EventType,
    confirmState: MutableState<Int>,
    actions: NavActions,
    eventList: List<CalendarEvent>,
    storyEventList: List<EventData>,
    gachaList: List<GachaInfo>,
    freeGachaList: List<FreeGachaInfo>,
    birthdayList: List<BirthdayData>
) {
    val id = if (calendarType == EventType.IN_PROGRESS) {
        OverviewType.IN_PROGRESS_EVENT.id
    } else {
        OverviewType.COMING_SOON_EVENT.id
    }
    val spanCount = ScreenUtil.getWidth() / getItemWidth().value.dp2px
    val isNotEmpty =
        eventList.isNotEmpty() || storyEventList.isNotEmpty() || gachaList.isNotEmpty() || freeGachaList.isNotEmpty() || birthdayList.isNotEmpty()
    val titleId = if (calendarType == EventType.IN_PROGRESS) {
        R.string.tool_calendar_inprogress
    } else {
        R.string.tool_calendar_comming
    }


    if (isEditMode || isNotEmpty) {
        Section(
            id = id,
            titleId = titleId,
            iconType = if (calendarType == EventType.IN_PROGRESS) MainIconType.CALENDAR_TODAY else MainIconType.CALENDAR,
            rightIconType = if (confirmState.value == calendarType.type) MainIconType.CLOSE else MainIconType.MAIN,
            isEditMode = isEditMode,
            onClick = {
                if (isEditMode) {
                    editOverviewMenuOrder(id)
                } else {
                    //弹窗确认
                    if (confirmState.value == calendarType.type) {
                        confirmState.value = 0
                    } else {
                        confirmState.value = calendarType.type
                    }
                }
            }
        ) {
            ExpandAnimation(visible = confirmState.value == calendarType.type) {
                CalendarEventOperation(
                    confirmState,
                    eventList,
                    storyEventList,
                    gachaList,
                    freeGachaList,
                    birthdayList
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
                birthdayList.forEach {
                    BirthdayItem(it, actions.toCharacterDetail)
                }
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
    freeGachaList: List<FreeGachaInfo>,
    birthdayList: List<BirthdayData>,
) {
    val context = LocalContext.current
    val regionName = getRegionName(MainActivity.regionType)

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
                    .clip(MaterialTheme.shapes.medium)
                    .clickable {
                        VibrateUtil(context).single()
                        checkPermissions(context, permissions, false) {
                            val allEvents = arrayListOf<SystemCalendarEventData>()
                            //掉落活动
                            eventList.forEach {
                                allEvents.add(
                                    SystemCalendarEventData(
                                        it.startTime,
                                        it.endTime,
                                        getTypeDataToString(it)
                                    )
                                )
                            }
                            //剧情活动
                            storyEventList.forEach {
                                allEvents.add(
                                    SystemCalendarEventData(
                                        it.startTime,
                                        it.endTime,
                                        it.getEventTitle()
                                    )
                                )
                            }
                            //卡池
                            gachaList.forEach {
                                allEvents.add(
                                    SystemCalendarEventData(
                                        it.startTime,
                                        it.endTime,
                                        it.getDesc()
                                    )
                                )
                            }
                            //免费十连
                            freeGachaList.forEach {
                                allEvents.add(
                                    SystemCalendarEventData(
                                        it.startTime,
                                        it.endTime,
                                        it.getDesc()
                                    )
                                )
                            }
                            //免费十连
                            freeGachaList.forEach {
                                allEvents.add(
                                    SystemCalendarEventData(
                                        it.startTime,
                                        it.endTime,
                                        it.getDesc()
                                    )
                                )
                            }
                            //生日日程
                            birthdayList.forEach {
                                allEvents.add(
                                    SystemCalendarEventData(
                                        it.getStartTime(),
                                        it.getEndTime(),
                                        it.getDesc()
                                    )
                                )
                            }
                            //添加至系统日历
                            SystemCalendarHelper().insertEvents(allEvents)

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
                    .clip(MaterialTheme.shapes.medium)
                    .clickable {
                        VibrateUtil(context).single()
                        var allText = ""
                        //掉落活动
                        var eventText = ""
                        eventList.forEach {
                            val date = getCalendarEventDateText(it.startTime, it.endTime)
                            eventText += "• $date\n${getTypeDataToString(it)}\n"
                        }
                        if (eventText != "") {
                            allText += "▶ 掉落活动\n$eventText\n\n"
                        }

                        //剧情活动
                        var storyText = ""
                        storyEventList.forEach {
                            val date = getCalendarEventDateText(it.startTime, it.endTime)
                            storyText += "• $date\n${it.getEventTitle()}"
                        }
                        if (storyText != "") {
                            allText += "▶ 剧情活动\n$storyText\n\n"
                        }

                        //卡池
                        var gachaText = ""
                        gachaList.forEach {
                            val date = getCalendarEventDateText(it.startTime, it.endTime)
                            gachaText += "• $date\n${it.getDesc()}"

                        }
                        if (gachaText != "") {
                            allText += "▶ 卡池信息\n$gachaText\n"
                        }

                        //免费十连
                        var freeGachaText = ""
                        freeGachaList.forEach {
                            val date = getCalendarEventDateText(it.startTime, it.endTime)
                            freeGachaText += "• $date\n${it.getDesc()}"

                        }
                        if (freeGachaText != "") {
                            allText += "▶ 免费十连\n$freeGachaText\n"
                        }

                        //免费十连
                        var birthdayText = ""
                        birthdayList.forEach {
                            val date = getCalendarEventDateText(it.getStartTime(), it.getEndTime())
                            birthdayText += "• $date\n${it.getDesc()}"

                        }
                        if (birthdayText != "") {
                            allText += "▶ 角色生日\n$birthdayText\n"
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

/**
 * 数据切换选择弹窗
 */
@Composable
private fun ChangeDbCompose(
    openDialog: Boolean,
    downloadState: Int,
    coroutineScope: CoroutineScope,
    modifier: Modifier
) {
    val context = LocalContext.current
    val region = MainActivity.regionType
    val menuTexts = arrayListOf(
        stringResource(id = R.string.db_cn),
        stringResource(id = R.string.db_tw),
        stringResource(id = R.string.db_jp),
    )

    //展开边距修正
    val mFabModifier = if (openDialog) {
        modifier.padding(start = Dimen.textfabMargin, end = Dimen.textfabMargin)

    } else {
        modifier
    }

    //数据切换
    SmallFloatingActionButton(
        modifier = mFabModifier
            .animateContentSize(defaultSpring())
            .padding(
                end = Dimen.fabMarginEnd,
                start = Dimen.fabMargin,
                top = Dimen.fabMargin,
                bottom = Dimen.fabMargin,
            ),
        shape = if (openDialog) MaterialTheme.shapes.medium else CircleShape,
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
                                coroutineScope.launch {
                                    DatabaseUpdater.changeDatabase(i + 2)
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
 * 标题、内容
 */
@Composable
private fun Section(
    id: Int,
    @StringRes titleId: Int,
    iconType: MainIconType,
    hintText: String = "",
    contentVisible: Boolean = true,
    isEditMode: Boolean,
    rightIconType: MainIconType? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val orderStr = navViewModel.overviewOrderData.observeAsState().value ?: ""
    //是否已显示到首页
    val hasAdded = orderStr.intArrayList.contains(id) && isEditMode
    //首页排序
    val index = orderStr.intArrayList.indexOf(id)

    val modifier = if (onClick == null) {
        Modifier
    } else {
        Modifier
            .padding(horizontal = Dimen.mediumPadding)
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = {
                VibrateUtil(context).single()
                if (contentVisible) {
                    onClick()
                }
            })
            .background(
                color = if (hasAdded) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = MaterialTheme.shapes.medium
            )
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
            modifier = modifier
                .padding(Dimen.mediumPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            //首页序号，编辑时显示
            if (isEditMode && index != -1) {
                MainText(
                    text = "${index + 1}",
                    modifier = Modifier
                        .padding(horizontal = Dimen.mediumPadding),
                    textAlign = TextAlign.Start,
                    color = if (hasAdded) colorWhite else MaterialTheme.colorScheme.onSurface
                )
            }
            IconCompose(
                data = iconType,
                size = Dimen.fabIconSize,
                tint = if (hasAdded) colorWhite else MaterialTheme.colorScheme.onSurface
            )
            MainText(
                text = stringResource(id = titleId),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = Dimen.mediumPadding),
                textAlign = TextAlign.Start,
                color = if (hasAdded) colorWhite else MaterialTheme.colorScheme.onSurface
            )
            //更多信息，编辑时隐藏
            if (!isEditMode) {
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(start = Dimen.smallPadding, end = Dimen.smallPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (onClick != null) {
                        if (hintText != "") {
                            Subtitle2(
                                text = hintText,
                                modifier = Modifier.align(Alignment.CenterVertically),
                                fontWeight = FontWeight.Medium
                            )
                        }
                        IconCompose(
                            data = rightIconType ?: MainIconType.MORE,
                            size = Dimen.fabIconSize,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                }
            }
        }

        if (contentVisible && !isEditMode) {
            Column {
                content()
            }
        }
    }

}

/**
 * 获取事项信息
 */
private fun getTypeDataToString(data: CalendarEvent): String {
    var eventTitle = ""
    when (data.type) {
        "1" -> {
            //露娜塔
            eventTitle = "露娜塔"
        }
        "-1" -> {
            //特殊地下城
            eventTitle = "特殊地下城"
        }
        else -> {
            //正常活动
            val list = data.type.intArrayList
            list.forEach { type ->
                val title = when (type) {
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
                eventTitle += title + (if (type > 40) "玛那掉落量" else "掉落量") + (if ((multiple * 10).toInt() % 10 == 0) {
                    multiple.toInt().toString()
                } else {
                    multiple.toString()
                }) + "倍\n"
            }
        }
    }

    return eventTitle
}


//编辑排序
private fun editOverviewMenuOrder(id: Int) {
    val sp = mainSP()
    val orderStr = sp.getString(Constants.SP_OVERVIEW_ORDER, "") ?: ""
    val idStr = "$id-"
    val hasAdded = orderStr.intArrayList.contains(id)

    //新增或移除
    val edited = if (!hasAdded) {
        orderStr + idStr
    } else {
        orderStr.replace(idStr, "")
    }
    sp.edit {
        putString(Constants.SP_OVERVIEW_ORDER, edited)
        //更新
        navViewModel.overviewOrderData.postValue(edited)
    }
}

/**
 * 日历日程时间范围文本
 */
private fun getCalendarEventDateText(
    startTime: String,
    endTime: String
) = startTime.formatTime.fixJpTime.substring(
    0,
    10
) + " ~ " + endTime.formatTime.fixJpTime.substring(0, 10)
