package cn.wthee.pcrtool.ui.home.event

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import cn.wthee.pcrtool.navigation.NavActions
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.VerticalStaggeredGrid
import cn.wthee.pcrtool.ui.components.getItemWidth
import cn.wthee.pcrtool.ui.home.Section
import cn.wthee.pcrtool.ui.home.tool.MenuItem
import cn.wthee.pcrtool.ui.home.tool.ToolMenuData
import cn.wthee.pcrtool.ui.home.tool.getToolMenuData
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.ExpandAnimation
import cn.wthee.pcrtool.ui.tool.birthday.BirthdayItem
import cn.wthee.pcrtool.ui.tool.clan.ClanBattleOverviewItemContent
import cn.wthee.pcrtool.ui.tool.event.CalendarEventItem
import cn.wthee.pcrtool.ui.tool.freegacha.FreeGachaItem
import cn.wthee.pcrtool.ui.tool.gacha.GachaItem
import cn.wthee.pcrtool.ui.tool.storyevent.StoryEventItemContent


/**
 * 进行中活动
 */
@Composable
fun EventInProgressSection(
    eventLayoutState: Int,
    updateOrderData: (Int) -> Unit,
    updateEventLayoutState : (Int) -> Unit,
    actions: NavActions,
    orderStr: String,
    isEditMode: Boolean,
    eventSectionViewModel: EventSectionViewModel = hiltViewModel(),
) {
    val uiState by eventSectionViewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(EventType.IN_PROGRESS) {
        eventSectionViewModel.loadData(EventType.IN_PROGRESS)
    }


    CalendarEventLayout(
        isEditMode = isEditMode,
        calendarType = EventType.IN_PROGRESS,
        eventLayoutState = eventLayoutState,
        actions = actions,
        orderStr = orderStr,
        eventList = uiState.inProgressEventList,
        storyEventList = uiState.inProgressStoryEventList,
        gachaList = uiState.inProgressGachaList,
        freeGachaList = uiState.inProgressFreeGachaList,
        birthdayList = uiState.inProgressBirthdayList,
        clanBattleList = uiState.inProgressClanBattleList,
        fesUnitIdList = uiState.fesUnitIdList,
        updateOrderData = updateOrderData,
        updateEventLayoutState = updateEventLayoutState
    )
}


/**
 * 活动
 */
@Composable
fun CalendarEventLayout(
    isEditMode: Boolean,
    calendarType: EventType,
    eventLayoutState: Int,
    actions: NavActions,
    orderStr: String,
    eventList: List<CalendarEvent>,
    storyEventList: List<StoryEventData>,
    gachaList: List<GachaInfo>,
    freeGachaList: List<FreeGachaInfo>,
    birthdayList: List<BirthdayData>,
    clanBattleList: List<ClanBattleEvent>,
    fesUnitIdList: List<Int>,
    updateOrderData: (Int) -> Unit,
    updateEventLayoutState : (Int) -> Unit,
) {

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
            rightIconType = if (eventLayoutState == calendarType.type) MainIconType.UP else MainIconType.DOWN,
            isEditMode = isEditMode,
            orderStr = orderStr,
            onClick = {
                if (isEditMode) {
                    updateOrderData(id)
                } else {
                    //弹窗确认
                    if (eventLayoutState == calendarType.type) {
                        updateEventLayoutState(0)
                    } else {
                        updateEventLayoutState(calendarType.type)
                    }
                }
            }
        ) {
            ExpandAnimation(visible = eventLayoutState == calendarType.type) {
                CalendarEventOperation(actions)
            }
            VerticalStaggeredGrid(
                itemWidth = getItemWidth() + Dimen.largePadding * 2,
                modifier = Modifier.padding(top = Dimen.mediumPadding)
            ) {
                clanBattleList.forEach {
                    ClanBattleOverviewItemContent(
                        clanBattleEvent = it,
                        toClanBossInfo = actions.toClanBossInfo
                    )
                }
                gachaList.forEach {
                    GachaItem(
                        gachaInfo = it,
                        fesUnitIdList = fesUnitIdList,
                        toCharacterDetail = actions.toCharacterDetail,
                        toMockGachaFromList = actions.toMockGachaFromList
                    )
                }
                freeGachaList.forEach {
                    FreeGachaItem(it)
                }
                storyEventList.forEach {
                    StoryEventItemContent(
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
        VerticalStaggeredGrid(
            itemWidth = Dimen.menuItemSize,
            contentPadding = Dimen.mediumPadding,
        ) {
            toolList.forEach {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    MenuItem(actions = actions, toolMenuData = it, isEditMode = false)
                }
            }
        }
    }
}