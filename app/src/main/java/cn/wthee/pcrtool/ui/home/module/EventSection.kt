package cn.wthee.pcrtool.ui.home.module

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.*
import cn.wthee.pcrtool.data.enums.EventType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.OverviewType
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.NavActions
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.home.Section
import cn.wthee.pcrtool.ui.home.editOverviewMenuOrder
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.ExpandAnimation
import cn.wthee.pcrtool.ui.tool.*
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.viewmodel.GachaViewModel
import cn.wthee.pcrtool.viewmodel.OverviewViewModel
import com.google.accompanist.flowlayout.FlowColumn


/**
 * 进行中活动
 */
@Composable
fun InProgressEventSection(
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
fun ComingSoonEventSection(
    confirmState: MutableState<Int>,
    actions: NavActions,
    isEditMode: Boolean,
    overviewViewModel: OverviewViewModel = hiltViewModel(),
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
    birthdayList: List<BirthdayData>,
    gachaViewModel: GachaViewModel = hiltViewModel(),
) {
    //fes 角色id
    val fesUnitIds =
        gachaViewModel.getGachaFesUnitList().collectAsState(initial = arrayListOf()).value

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
                    GachaItem(
                        gachaInfo = it,
                        fesUnitIds = fesUnitIds,
                        toCharacterDetail = actions.toCharacterDetail,
                        toMockGacha = actions.toMockGacha
                    )
                }
                storyEventList.forEach {
                    StoryEventItem(
                        event = it,
                        toCharacterDetail = actions.toCharacterDetail,
                        toEventEnemyDetail = actions.toEventEnemyDetail,
                        toAllPics = actions.toAllPics
                    )
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
                        checkPermissions(context, cn.wthee.pcrtool.ui.home.permissions, false) {
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
                            allText += "▶ 掉落活动\n$eventText\n"
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
                            allText += "▶ 卡池信息\n$gachaText\n\n"
                        }

                        //免费十连
                        var freeGachaText = ""
                        freeGachaList.forEach {
                            val date = getCalendarEventDateText(it.startTime, it.endTime)
                            freeGachaText += "• $date\n${it.getDesc()}"

                        }
                        if (freeGachaText != "") {
                            allText += "▶ 免费十连\n$freeGachaText\n\n"
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
            list.forEachIndexed { index, type ->
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
                }) + "倍"
                if (index != list.size - 1) {
                    eventTitle += "\n"
                }
            }
        }
    }

    return eventTitle
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