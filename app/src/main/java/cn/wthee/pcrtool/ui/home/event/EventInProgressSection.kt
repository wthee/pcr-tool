package cn.wthee.pcrtool.ui.home.event

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.data.enums.EventType
import cn.wthee.pcrtool.navigation.NavActions


/**
 * 进行中活动
 */
@Composable
fun EventInProgressSection(
    eventExpandState: Int,
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
        eventExpandState = eventExpandState,
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