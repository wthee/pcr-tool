package cn.wthee.pcrtool.ui.tool.event

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.CalendarEvent
import cn.wthee.pcrtool.data.enums.CalendarEventType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.navigation.navigateUp
import cn.wthee.pcrtool.ui.components.CaptionText
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.DateRangePickerCompose
import cn.wthee.pcrtool.ui.components.EventTitle
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.StateBox
import cn.wthee.pcrtool.ui.components.Subtitle1
import cn.wthee.pcrtool.ui.components.getDatePickerYearRange
import cn.wthee.pcrtool.ui.components.getItemWidth
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.fixJpTime
import cn.wthee.pcrtool.utils.formatTime
import kotlinx.coroutines.launch


/**
 * 日程记录
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarEventListScreen(
    calendarEventListViewModel: CalendarEventListViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberLazyStaggeredGridState()
    val uiState by calendarEventListViewModel.uiState.collectAsStateWithLifecycle()
    val dateRangePickerState = rememberDateRangePickerState(yearRange = getDatePickerYearRange())


    MainScaffold(
        enableClickClose = uiState.openDialog,
        onCloseClick = {
            calendarEventListViewModel.changeDialog(false)
        },
        secondLineFab = {
            //日期选择
            DateRangePickerCompose(
                dateRangePickerState = dateRangePickerState,
                dateRange = uiState.dateRange,
                openDialog = uiState.openDialog,
                changeRange = calendarEventListViewModel::changeRange,
                changeDialog = calendarEventListViewModel::changeDialog
            )
        },
        fab = {
            //重置
            if (uiState.dateRange.hasFilter()) {
                MainSmallFab(
                    iconType = MainIconType.RESET,
                    onClick = {
                        calendarEventListViewModel.reset()
                        dateRangePickerState.setSelection(null, null)
                    }
                )
            }

            //回到顶部
            MainSmallFab(
                iconType = MainIconType.CALENDAR,
                text = stringResource(id = R.string.tool_event),
                onClick = {
                    coroutineScope.launch {
                        try {
                            scrollState.scrollToItem(0)
                        } catch (_: Exception) {
                        }
                    }
                }
            )
        },
        mainFabIcon = if (uiState.openDialog) MainIconType.CLOSE else MainIconType.BACK,
        onMainFabClick = {
            if (uiState.openDialog) {
                calendarEventListViewModel.changeDialog(false)
            } else {
                navigateUp()
            }
        }
    ) {
        StateBox(stateType = uiState.loadState) {
            CalendarEventListContent(
                scrollState = scrollState,
                calendarEventList = uiState.calendarEventList!!
            )
        }
    }

}


@Composable
private fun CalendarEventListContent(
    scrollState: LazyStaggeredGridState,
    calendarEventList: List<CalendarEvent>
) {
    LazyVerticalStaggeredGrid(
        state = scrollState,
        columns = StaggeredGridCells.Adaptive(getItemWidth())
    ) {
        items(calendarEventList) {
            CalendarEventItem(it)
        }
        items(2) {
            CommonSpacer()
        }
    }
}


/**
 * 日历信息
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CalendarEventItem(calendar: CalendarEvent) {

    Column(
        modifier = Modifier.padding(
            horizontal = Dimen.largePadding,
            vertical = Dimen.mediumPadding
        )
    ) {
        FlowRow(
            modifier = Modifier.padding(bottom = Dimen.mediumPadding),
            verticalArrangement = Arrangement.Center
        ) {
            EventTitle(
                startTime = calendar.startTime,
                endTime = calendar.endTime,
                showOverdueColor = true
            )
        }

        MainCard {
            Column(modifier = Modifier.padding(Dimen.mediumPadding)) {
                //内容
                calendar.getEventList().forEach {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Subtitle1(text = it.title + it.info)
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
                CaptionText(
                    text = calendar.endTime.formatTime.fixJpTime,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

}

@CombinedPreviews
@Composable
private fun CalendarEventItemPreview() {
    PreviewLayout {
        CalendarEventItem(
            CalendarEvent(
                type = CalendarEventType.H_DROP.type.toString(),
                value = 3000,
                startTime = "2030-01-01 00:00:00",
                endTime = "2031-01-01 00:00:00"
            )
        )
    }
}