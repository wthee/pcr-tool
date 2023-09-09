package cn.wthee.pcrtool.ui.home.module

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
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
    //进行中公会战
    val inProgressClanBattleList =
        overviewViewModel.getClanBattleEvent(EventType.IN_PROGRESS)
            .collectAsState(initial = arrayListOf()).value

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
    //公会战
    val comingSoonClanBattleList =
        overviewViewModel.getClanBattleEvent(EventType.COMING_SOON)
            .collectAsState(initial = arrayListOf()).value

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
    val fesUnitIds =
        gachaViewModel.getGachaFesUnitList().collectAsState(initial = arrayListOf()).value

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