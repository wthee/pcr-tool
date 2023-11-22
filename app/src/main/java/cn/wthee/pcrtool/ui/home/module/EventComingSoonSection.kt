package cn.wthee.pcrtool.ui.home.module

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.data.enums.EventType
import cn.wthee.pcrtool.navigation.NavActions


/**
 * 活动预告
 */
@Composable
fun EventComingSoonSection(
    eventLayoutState: Int,
    updateOrderData: (Int) -> Unit,
    updateEventLayoutState : (Int) -> Unit,
    actions: NavActions,
    isEditMode: Boolean,
    orderStr: String,
    eventSectionViewModel: EventSectionViewModel = hiltViewModel(),
) {
    val uiState by eventSectionViewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(EventType.COMING_SOON) {
        eventSectionViewModel.loadData(EventType.COMING_SOON)
    }


    CalendarEventLayout(
        isEditMode = isEditMode,
        calendarType = EventType.COMING_SOON,
        eventLayoutState = eventLayoutState,
        actions = actions,
        orderStr = orderStr,
        eventList = uiState.comingSoonEventList,
        storyEventList = uiState.comingSoonStoryEventList,
        gachaList = uiState.comingSoonGachaList,
        freeGachaList = uiState.comingSoonFreeGachaList,
        birthdayList = uiState.comingSoonBirthdayList,
        clanBattleList = uiState.comingSoonClanBattleList,
        fesUnitIdList = uiState.fesUnitIdList,
        updateOrderData = updateOrderData,
        updateEventLayoutState = updateEventLayoutState
    )
}