package cn.wthee.pcrtool.ui.tool

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.CalendarEventData
import cn.wthee.pcrtool.data.db.view.DropEvent
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.database.getDatabaseType
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.viewmodel.CalendarViewModel
import kotlinx.coroutines.launch
import java.util.*

/**
 * 日历活动
 * fixme 进入时卡顿
 */
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun CalendarCompose(
    scrollState: LazyListState,
    calendarViewModel: CalendarViewModel = hiltViewModel()
) {
    calendarViewModel.getDropEvent()
    val calendarData = calendarViewModel.dropEvents.observeAsState()

    val coroutineScope = rememberCoroutineScope()
    val title = when (getDatabaseType()) {
        1 -> stringResource(id = R.string.db_cn)
        else -> stringResource(id = R.string.db_jp)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = if (MaterialTheme.colors.isLight) R.color.bg_gray else R.color.bg_gray_dark))
    ) {
        SlideAnimation(visible = calendarData.value != null) {
            calendarData.value?.let { data ->
                LazyColumn(
                    state = scrollState,
                    contentPadding = PaddingValues(Dimen.mediuPadding)
                ) {
                    items(data) {
                        CalendarItem(it)
                    }
                    item {
                        CommonSpacer()
                    }
                }
            }
        }


        //回到顶部
        FabCompose(
            iconType = MainIconType.CALENDAR,
            text = title + stringResource(id = R.string.calendar),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
        ) {
            coroutineScope.launch {
                scrollState.scrollToItem(0)
            }
        }
    }
}


/**
 * 日历信息
 */
@Composable
private fun CalendarItem(calendar: DropEvent) {
    val today = getToday()
    val sd = calendar.startTime.formatTime()
    val ed = calendar.endTime.formatTime()
    val inProgress = today.hourInt(sd) > 0 && ed.hourInt(today) > 0
    val comingSoon = today.hourInt(sd) < 0

    val color = when {
        inProgress -> {
            MaterialTheme.colors.primary
        }
        comingSoon -> {
            colorResource(id = R.color.news_system)
        }
        else -> {
            colorResource(id = R.color.color_rank_4_6)
        }
    }

    Row(modifier = Modifier.padding(bottom = Dimen.mediuPadding)) {
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
                    data = MainIconType.TIME_LEFT.icon,
                    size = Dimen.smallIconSize,
                    tint = color
                )
                MainContentText(
                    text = stringResource(R.string.in_progress, ed.dates(today)),
                    modifier = Modifier.padding(start = Dimen.smallPadding),
                    textAlign = TextAlign.Start,
                    color = color
                )
            }
            if (comingSoon) {
                IconCompose(
                    data = MainIconType.COUNTDOWN.icon,
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

    MainCard(modifier = Modifier.padding(bottom = Dimen.mediuPadding)) {
        Column(modifier = Modifier.padding(Dimen.mediuPadding)) {
            //内容
            getTypeData(calendar).forEach {
                Subtitle1(text = it.title + it.info)
            }
            //结束日期
            CaptionText(text = ed, modifier = Modifier.fillMaxWidth())
        }
    }
}


/**
 * 获取事项信息
 */
@Composable
private fun getTypeData(data: DropEvent): ArrayList<CalendarEventData> {
    val events = arrayListOf<CalendarEventData>()
    val list = data.type.split("-")
    list.forEach { s ->
        var colorId = R.color.black
        val title = when (s.toInt()) {
            31 -> {
                colorId = R.color.color_map_n
                stringResource(id = R.string.normal)
            }
            32 -> {
                colorId = R.color.color_map_h
                stringResource(id = R.string.hard)
            }
            39 -> {
                colorId = R.color.color_map_vh
                stringResource(id = R.string.very_hard)
            }
            34 -> {
                colorId = R.color.color_rank_21
                stringResource(id = R.string.explore)
            }
            37 -> {
                colorId = R.color.news_update
                stringResource(id = R.string.shrine)
            }
            38 -> {
                colorId = R.color.news_update
                stringResource(id = R.string.temple)
            }
            45 -> {
                colorId = R.color.color_rank_2_3
                stringResource(id = R.string.dungeon)
            }
            else -> ""
        }
        events.add(
            CalendarEventData(
                title,
                stringResource(id = R.string.drop_x, data.getFixedValue()),
                colorId
            )
        )
    }
    return events
}
