package cn.wthee.pcrtool.ui.home.event

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.BirthdayData
import cn.wthee.pcrtool.data.db.view.CalendarEvent
import cn.wthee.pcrtool.data.db.view.ClanBattleEvent
import cn.wthee.pcrtool.data.db.view.ClanBattleInfo
import cn.wthee.pcrtool.data.db.view.FreeGachaInfo
import cn.wthee.pcrtool.data.db.view.GachaInfo
import cn.wthee.pcrtool.data.db.view.StoryEventData
import cn.wthee.pcrtool.data.enums.CalendarEventType
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
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.ExpandAnimation
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.tool.birthday.BirthdayItem
import cn.wthee.pcrtool.ui.tool.clan.ClanBattleOverviewItemContent
import cn.wthee.pcrtool.ui.tool.event.CalendarEventItem
import cn.wthee.pcrtool.ui.tool.freegacha.FreeGachaItem
import cn.wthee.pcrtool.ui.tool.gacha.GachaItem
import cn.wthee.pcrtool.ui.tool.storyevent.StoryEventItemContent

/**
 * 日程通用布局
 */
@Composable
fun CalendarEventLayout(
    isEditMode: Boolean,
    calendarType: EventType,
    eventExpandState: Int,
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
    updateEventLayoutState: (Int) -> Unit,
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
            rightIconType = if (eventExpandState == calendarType.type) MainIconType.UP else MainIconType.DOWN,
            isEditMode = isEditMode,
            orderStr = orderStr,
            onClick = {
                if (isEditMode) {
                    updateOrderData(id)
                } else {
                    //弹窗确认
                    if (eventExpandState == calendarType.type) {
                        updateEventLayoutState(0)
                    } else {
                        updateEventLayoutState(calendarType.type)
                    }
                }
            }
        ) {
            ExpandAnimation(visible = eventExpandState == calendarType.type) {
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
                        toAllStoryEventPics = actions.toAllStoryEventPics
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
 * 日程相关跳转
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


@CombinedPreviews
@Composable
private fun CalendarEventLayoutPreview() {
    PreviewLayout {
        val text = stringResource(id = R.string.debug_long_text)
        CalendarEventLayout(
            isEditMode = false,
            calendarType = EventType.COMING_SOON,
            eventExpandState = 2,
            actions = NavActions(NavHostController(LocalContext.current)),
            orderStr = "${OverviewType.COMING_SOON_EVENT.id}",
            eventList = arrayListOf(
                CalendarEvent(
                    type = CalendarEventType.H_DROP.type.toString(),
                    value = 3000,
                    startTime = "2030-01-01 00:00:00",
                    endTime = "2031-01-01 00:00:00"
                )
            ),
            storyEventList = arrayListOf(
                StoryEventData(
                    title = text
                )
            ),
            gachaList = arrayListOf(GachaInfo()),
            freeGachaList = arrayListOf(FreeGachaInfo()),
            birthdayList = arrayListOf(BirthdayData(unitIds = "1-2-3", unitNames = "1-2-3")),
            clanBattleList = arrayListOf(
                ClanBattleEvent(
                    clanBattleInfo = ClanBattleInfo(1)
                )
            ),
            fesUnitIdList = arrayListOf(1),
            updateOrderData = {},
            updateEventLayoutState = {}
        )
    }
}