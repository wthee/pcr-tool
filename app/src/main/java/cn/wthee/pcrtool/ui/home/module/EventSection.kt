package cn.wthee.pcrtool.ui.home.module

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.BirthdayData
import cn.wthee.pcrtool.data.db.view.CalendarEvent
import cn.wthee.pcrtool.data.db.view.ClanBattleEvent
import cn.wthee.pcrtool.data.db.view.EventData
import cn.wthee.pcrtool.data.db.view.FreeGachaInfo
import cn.wthee.pcrtool.data.db.view.GachaInfo
import cn.wthee.pcrtool.data.enums.EventType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.OverviewType
import cn.wthee.pcrtool.navigation.NavActions
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.components.VerticalGrid
import cn.wthee.pcrtool.ui.components.getItemWidth
import cn.wthee.pcrtool.ui.home.Section
import cn.wthee.pcrtool.ui.home.editOverviewMenuOrder
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.tool.BirthdayItem
import cn.wthee.pcrtool.ui.tool.CalendarEventItem
import cn.wthee.pcrtool.ui.tool.FreeGachaItem
import cn.wthee.pcrtool.ui.tool.GachaItem
import cn.wthee.pcrtool.ui.tool.clan.ClanBattleOverview
import cn.wthee.pcrtool.ui.tool.storyevent.StoryEventItem
import cn.wthee.pcrtool.viewmodel.GachaViewModel
import cn.wthee.pcrtool.viewmodel.OverviewViewModel


/**
 * 进行中活动
 */
@Composable
fun InProgressEventSection(
    actions: NavActions,
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
        actions,
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
    actions: NavActions,
    isEditMode: Boolean,
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
        actions,
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
private fun CalendarEventLayout(
    isEditMode: Boolean,
    calendarType: EventType,
    actions: NavActions,
    eventList: List<CalendarEvent>,
    storyEventList: List<EventData>,
    gachaList: List<GachaInfo>,
    freeGachaList: List<FreeGachaInfo>,
    birthdayList: List<BirthdayData>,
    clanBattleList: List<ClanBattleEvent>,
    gachaViewModel: GachaViewModel = hiltViewModel(),
) {
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
            isEditMode = isEditMode,
            orderStr = MainActivity.navViewModel.overviewOrderData.observeAsState().value ?: "",
            onClick = {
                if (isEditMode) {
                    editOverviewMenuOrder(id)
                } else {
                    actions.toCalendarEventList()
                }
            }
        ) {

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