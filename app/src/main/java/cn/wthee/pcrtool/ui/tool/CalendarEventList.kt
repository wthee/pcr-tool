package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.CalendarEvent
import cn.wthee.pcrtool.data.db.view.CalendarEventData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.*
import cn.wthee.pcrtool.utils.fixJpTime
import cn.wthee.pcrtool.utils.formatTime
import cn.wthee.pcrtool.utils.intArrayList
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
    val coroutineScope = rememberCoroutineScope()
    val dataList =
        eventViewModel.getCalendarEventList().collectAsState(initial = arrayListOf()).value

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
        //回到顶部
        FabCompose(
            iconType = MainIconType.CALENDAR,
            text = stringResource(id = R.string.tool_calendar_event),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
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
                getTypeData(calendar).forEach {
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


/**
 * 获取事项信息
 */
@Composable
private fun getTypeData(data: CalendarEvent): ArrayList<CalendarEventData> {
    val events = arrayListOf<CalendarEventData>()
    when (data.type) {
        "1" -> {
            //露娜塔
            events.add(
                CalendarEventData(
                    stringResource(id = R.string.tower),
                    "",
                    ""
                )
            )
        }
        "-1" -> {
            //特殊地下城
            events.add(
                CalendarEventData(
                    stringResource(id = R.string.sp_dungeon),
                    "",
                    ""
                )
            )
        }
        else -> {
            //正常活动
            val list = data.type.intArrayList
            list.forEach { type ->
                val title = when (type) {
                    31, 41 -> stringResource(id = R.string.normal)
                    32, 42 -> stringResource(id = R.string.hard)
                    39, 49 -> stringResource(id = R.string.very_hard)
                    34 -> stringResource(id = R.string.explore)
                    37 -> stringResource(id = R.string.shrine)
                    38 -> stringResource(id = R.string.temple)
                    45 -> stringResource(id = R.string.dungeon)
                    else -> ""
                }

                val dropMumColor = when (data.getFixedValue()) {
                    1.5f, 2.0f -> colorGold
                    3f -> colorRed
                    4f -> colorGreen
                    else -> MaterialTheme.colorScheme.primary
                }
                val multiple = data.getFixedValue()
                events.add(
                    CalendarEventData(
                        title,
                        stringResource(
                            R.string.multiple,
                            if ((multiple * 10).toInt() % 10 == 0) {
                                multiple.toInt().toString()
                            } else {
                                multiple.toString()
                            }
                        ),
                        stringResource(id = if (type > 40) R.string.mana else R.string.drop),
                        dropMumColor
                    )
                )
            }
        }
    }

    return events
}


@CombinedPreviews
@Composable
private fun CalendarEventItemPreview(){
    PreviewLayout {
        CalendarEventItem(CalendarEvent())
    }
}