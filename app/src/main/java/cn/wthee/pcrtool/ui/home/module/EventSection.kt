package cn.wthee.pcrtool.ui.home.module

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.BirthdayData
import cn.wthee.pcrtool.data.db.view.CalendarEvent
import cn.wthee.pcrtool.data.db.view.ClanBattleEvent
import cn.wthee.pcrtool.data.db.view.FreeGachaInfo
import cn.wthee.pcrtool.data.db.view.GachaInfo
import cn.wthee.pcrtool.data.db.view.StoryEventData
import cn.wthee.pcrtool.data.enums.EventType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.OverviewType
import cn.wthee.pcrtool.data.enums.ToolMenuType
import cn.wthee.pcrtool.data.preferences.MainPreferencesKeys
import cn.wthee.pcrtool.navigation.NavActions
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.VerticalGrid
import cn.wthee.pcrtool.ui.components.getItemWidth
import cn.wthee.pcrtool.ui.home.Section
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.ExpandAnimation
import cn.wthee.pcrtool.ui.theme.defaultSpring
import cn.wthee.pcrtool.ui.tool.BirthdayItem
import cn.wthee.pcrtool.ui.tool.CalendarEventItem
import cn.wthee.pcrtool.ui.tool.FreeGachaItem
import cn.wthee.pcrtool.ui.tool.GachaItem
import cn.wthee.pcrtool.ui.tool.clan.ClanBattleOverview
import cn.wthee.pcrtool.ui.tool.storyevent.StoryEventItem
import cn.wthee.pcrtool.utils.editOrder
import cn.wthee.pcrtool.viewmodel.GachaViewModel
import cn.wthee.pcrtool.viewmodel.OverviewViewModel


/**
 * 进行中活动
 */
@Composable
fun InProgressEventSection(
    confirmState: MutableState<Int>,
    actions: NavActions,
    orderStr: String,
    isEditMode: Boolean,
    overviewViewModel: OverviewViewModel = hiltViewModel()
) {

    //进行中掉落活动
    val inProgressEventListFlow = remember {
        overviewViewModel.getCalendarEventList(EventType.IN_PROGRESS)
    }
    val inProgressEventList by inProgressEventListFlow.collectAsState(initial = arrayListOf())
    //进行中剧情活动
    val inProgressStoryEventListFlow = remember {
        overviewViewModel.getStoryEventList(EventType.IN_PROGRESS)
    }
    val inProgressStoryEventList by inProgressStoryEventListFlow.collectAsState(initial = arrayListOf())
    //进行中卡池
    val inProgressGachaListFlow = remember {
        overviewViewModel.getGachaList(EventType.IN_PROGRESS)
    }
    val inProgressGachaList by inProgressGachaListFlow.collectAsState(initial = arrayListOf())
    //进行中免费十连
    val inProgressFreeGachaListFlow = remember {
        overviewViewModel.getFreeGachaList(EventType.IN_PROGRESS)
    }
    val inProgressFreeGachaList by inProgressFreeGachaListFlow.collectAsState(initial = arrayListOf())
    //进行中生日日程
    val inProgressBirthdayListFlow = remember {
        overviewViewModel.getBirthdayList(EventType.IN_PROGRESS)
    }
    val inProgressBirthdayList by inProgressBirthdayListFlow.collectAsState(initial = arrayListOf())
    //进行中公会战
    val inProgressClanBattleListFlow = remember {
        overviewViewModel.getClanBattleEvent(EventType.IN_PROGRESS)
    }
    val inProgressClanBattleList by inProgressClanBattleListFlow.collectAsState(initial = arrayListOf())

    CalendarEventLayout(
        isEditMode,
        EventType.IN_PROGRESS,
        confirmState,
        actions,
        orderStr,
        inProgressEventList,
        inProgressStoryEventList,
        inProgressGachaList,
        inProgressFreeGachaList,
        inProgressBirthdayList,
        inProgressClanBattleList
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
    orderStr: String,
    overviewViewModel: OverviewViewModel = hiltViewModel(),
) {
    //预告掉落活动
    val comingSoonEventListFlow = remember {
        overviewViewModel.getCalendarEventList(EventType.COMING_SOON)
    }
    val comingSoonEventList by comingSoonEventListFlow.collectAsState(initial = arrayListOf())
    //预告剧情活动
    val comingSoonStoryEventListFlow = remember {
        overviewViewModel.getStoryEventList(EventType.COMING_SOON)
    }
    val comingSoonStoryEventList by comingSoonStoryEventListFlow.collectAsState(initial = arrayListOf())
    //预告卡池
    val comingSoonGachaListFlow = remember {
        overviewViewModel.getGachaList(EventType.COMING_SOON)
    }
    val comingSoonGachaList by comingSoonGachaListFlow.collectAsState(initial = arrayListOf())
    //预告免费十连
    val comingSoonFreeGachaFlow = remember {
        overviewViewModel.getFreeGachaList(EventType.COMING_SOON)
    }
    val comingSoonFreeGachaList by comingSoonFreeGachaFlow.collectAsState(initial = arrayListOf())
    //生日
    val comingSoonBirthdayListFlow = remember {
        overviewViewModel.getBirthdayList(EventType.COMING_SOON)
    }
    val comingSoonBirthdayList by comingSoonBirthdayListFlow.collectAsState(initial = arrayListOf())
    //公会战
    val comingSoonClanBattleListFlow = remember {
        overviewViewModel.getClanBattleEvent(EventType.COMING_SOON)
    }
    val comingSoonClanBattleList by comingSoonClanBattleListFlow.collectAsState(initial = arrayListOf())

    CalendarEventLayout(
        isEditMode,
        EventType.COMING_SOON,
        confirmState,
        actions,
        orderStr,
        comingSoonEventList,
        comingSoonStoryEventList,
        comingSoonGachaList,
        comingSoonFreeGachaList,
        comingSoonBirthdayList,
        comingSoonClanBattleList
    )
}


/**
 * 活动
 */
@Composable
fun CalendarEventLayout(
    isEditMode: Boolean,
    calendarType: EventType,
    confirmState: MutableState<Int>,
    actions: NavActions,
    orderStr: String,
    eventList: List<CalendarEvent>,
    storyEventList: List<StoryEventData>,
    gachaList: List<GachaInfo>,
    freeGachaList: List<FreeGachaInfo>,
    birthdayList: List<BirthdayData>,
    clanBattleList: List<ClanBattleEvent>,
    gachaViewModel: GachaViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    //fes 角色id
    val fesUnitIdsFlow = remember {
        gachaViewModel.getGachaFesUnitList()
    }
    val fesUnitIds by fesUnitIdsFlow.collectAsState(initial = arrayListOf())

    val id = if (calendarType == EventType.IN_PROGRESS) {
        OverviewType.IN_PROGRESS_EVENT.id
    } else {
        OverviewType.COMING_SOON_EVENT.id
    }
    val isNotEmpty =
        eventList.isNotEmpty() || storyEventList.isNotEmpty() || gachaList.isNotEmpty()
                || freeGachaList.isNotEmpty() || birthdayList.isNotEmpty()
                || clanBattleList.isNotEmpty()
    val titleId = if (calendarType == EventType.IN_PROGRESS) {
        R.string.tool_calendar_inprogress
    } else {
        R.string.tool_calendar_coming
    }


    if (isEditMode || isNotEmpty) {
        Section(
            id = id,
            titleId = titleId,
            iconType = if (calendarType == EventType.IN_PROGRESS) MainIconType.CALENDAR_TODAY else MainIconType.CALENDAR,
            rightIconType = if (confirmState.value == calendarType.type) MainIconType.UP else MainIconType.DOWN,
            isEditMode = isEditMode,
            orderStr = orderStr,
            onClick = {
                if (isEditMode) {
                    editOrder(
                        context,
                        scope,
                        id,
                        MainPreferencesKeys.SP_OVERVIEW_ORDER
                    )
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
                CalendarEventOperation(actions)
            }
            VerticalGrid(
                itemWidth = getItemWidth(),
                modifier = Modifier.padding(top = Dimen.mediumPadding)
            ) {
                clanBattleList.forEach {
                    ClanBattleOverview(
                        clanBattleEvent = it,
                        toClanBossInfo = actions.toClanBossInfo
                    )
                }
                gachaList.forEach {
                    GachaItem(
                        gachaInfo = it,
                        fesUnitIds = fesUnitIds,
                        toCharacterDetail = actions.toCharacterDetail,
                        toMockGacha = actions.toMockGacha
                    )
                }
                freeGachaList.forEach {
                    FreeGachaItem(it)
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
    actions: NavActions
) {
    val context = LocalContext.current
    //日程相关工具
    val toolList = arrayListOf<ToolMenuData>()
    toolList.add(getToolMenuData(toolMenuType = ToolMenuType.CLAN))
    toolList.add(getToolMenuData(toolMenuType = ToolMenuType.GACHA))
    toolList.add(getToolMenuData(toolMenuType = ToolMenuType.FREE_GACHA))
    toolList.add(getToolMenuData(toolMenuType = ToolMenuType.STORY_EVENT))
    toolList.add(getToolMenuData(toolMenuType = ToolMenuType.CALENDAR_EVENT))
    toolList.add(getToolMenuData(toolMenuType = ToolMenuType.BIRTHDAY))

    MainCard(
        modifier = Modifier.padding(
            horizontal = Dimen.largePadding,
            vertical = Dimen.mediumPadding
        )
    ) {
        VerticalGrid(
            itemWidth = Dimen.menuItemSize,
            contentPadding = Dimen.largePadding + Dimen.mediumPadding,
            modifier = Modifier.animateContentSize(defaultSpring())
        ) {
            toolList.forEach {
                Box(
                    modifier = Modifier
                        .padding(
                            top = Dimen.mediumPadding,
                            bottom = Dimen.largePadding
                        )
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    MenuItem(context, actions, it, false)
                }
            }
        }
    }
}