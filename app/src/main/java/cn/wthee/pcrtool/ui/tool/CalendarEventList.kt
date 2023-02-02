package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.CalendarEvent
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.*
import cn.wthee.pcrtool.utils.fixJpTime
import cn.wthee.pcrtool.utils.formatTime
import cn.wthee.pcrtool.viewmodel.EventViewModel
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.launch


/**
 * 日程记录
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarEventList(
    scrollState: LazyStaggeredGridState,
    eventViewModel: EventViewModel = hiltViewModel()
) {
    val dateRange = remember {
        mutableStateOf(DateRange())
    }
    val coroutineScope = rememberCoroutineScope()
    val dataList =
        eventViewModel.getCalendarEventList(dateRange.value).collectAsState(initial = arrayListOf()).value

    //日程列表
    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalStaggeredGrid(
            state = scrollState,
            columns = StaggeredGridCells.Adaptive(getItemWidth())
        ) {
            items(dataList) {
                CalendarEventItem(it)
            }
            item {
                CommonSpacer()
            }
        }

        //日期选择
        DateRangePickerCompose(dateRange = dateRange)

        //回到顶部
        FabCompose(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = Dimen.fabMarginEnd,
                    bottom = Dimen.fabMargin
                ),
            iconType = MainIconType.CALENDAR,
            text = stringResource(id = R.string.tool_calendar_event)
        ) {
            coroutineScope.launch {
                try {
                    scrollState.scrollToItem(0)
                } catch (_: Exception) {
                }
            }
        }

    }

}

/**
 * 日历信息
 */
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
            crossAxisAlignment = FlowCrossAxisAlignment.Center
        ) {
            EventTitle(calendar.startTime, calendar.endTime, showOverdueColor = true)
        }

        MainCard {
            Column(modifier = Modifier.padding(Dimen.mediumPadding)) {
                //内容
                calendar.getEventList().forEach {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Subtitle2(text = it.title + it.info)
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
        CalendarEventItem(CalendarEvent())
    }
}