package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.CalendarEvent
import cn.wthee.pcrtool.data.db.view.CalendarEventData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.*
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.viewmodel.EventViewModel
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.launch


/**
 * 日程记录
 */
@Composable
fun CalendarEventList(
    scrollState: LazyListState,
    eventViewModel: EventViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val dataList =
        eventViewModel.getCalendarEventList().collectAsState(initial = arrayListOf()).value

    //日程列表
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(state = scrollState) {
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
                } catch (e: Exception) {
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
    val today = getToday()
    val sd = calendar.startTime.formatTime.fixJpTime
    val ed = calendar.endTime.formatTime.fixJpTime
    val inProgress = isInProgress(today, calendar.startTime, calendar.endTime)
    val comingSoon = isComingSoon(today, calendar.startTime)

    val color = when {
        inProgress -> {
            MaterialTheme.colorScheme.primary
        }
        comingSoon -> {
            colorPurple
        }
        else -> {
            MaterialTheme.colorScheme.outline
        }
    }

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
            //开始日期
            MainTitleText(
                text = sd.substring(0, 10),
                backgroundColor = color
            )
            //天数
            MainTitleText(
                text = ed.days(sd),
                modifier = Modifier.padding(start = Dimen.smallPadding), backgroundColor = color
            )
            //计时
            Row(
                modifier = Modifier.padding(start = Dimen.smallPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (inProgress) {
                    IconCompose(
                        data = MainIconType.TIME_LEFT,
                        size = Dimen.smallIconSize,
                        tint = color
                    )
                    MainContentText(
                        text = stringResource(R.string.progressing, ed.dates(today)),
                        modifier = Modifier.padding(start = Dimen.smallPadding),
                        textAlign = TextAlign.Start,
                        color = color
                    )
                }
                if (comingSoon) {
                    IconCompose(
                        data = MainIconType.COUNTDOWN,
                        size = Dimen.smallIconSize,
                        tint = color
                    )
                    MainContentText(
                        text = stringResource(R.string.coming_soon, sd.dates(today)),
                        modifier = Modifier.padding(start = Dimen.smallPadding),
                        textAlign = TextAlign.Start,
                        color = color
                    )
                }
            }
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
                CaptionText(text = ed, modifier = Modifier.fillMaxWidth())
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
    if (data.type != "1") {
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
                    (if ((multiple * 10).toInt() % 10 == 0) {
                        multiple.toInt().toString()
                    } else {
                        multiple.toString()
                    }) + "倍",
                    stringResource(id = if (type > 40) R.string.mana else R.string.drop),
                    dropMumColor
                )
            )
        }
    } else {
        //露娜塔
        events.add(
            CalendarEventData(
                stringResource(id = R.string.tower),
                "",
                ""
            )
        )
    }

    return events
}
