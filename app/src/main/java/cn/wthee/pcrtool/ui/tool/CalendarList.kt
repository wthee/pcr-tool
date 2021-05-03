package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.CalendarEventData
import cn.wthee.pcrtool.data.db.view.DropEvent
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.compose.ExtendedFabCompose
import cn.wthee.pcrtool.ui.compose.MainContentText
import cn.wthee.pcrtool.ui.compose.MainTitleText
import cn.wthee.pcrtool.ui.compose.StaggeredVerticalGrid
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shapes
import cn.wthee.pcrtool.utils.days
import cn.wthee.pcrtool.utils.daysInt
import cn.wthee.pcrtool.viewmodel.CalendarViewModel
import kotlinx.coroutines.launch
import java.util.*

@ExperimentalFoundationApi
@Composable
fun CalendarCompose(calendarViewModel: CalendarViewModel = hiltNavGraphViewModel()) {
    calendarViewModel.getDropEvent()
    val calendarData = calendarViewModel.dropEvents.observeAsState()

    val state = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    MainActivity.navViewModel.loading.postValue(true)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.bg_gray))
    ) {
        Column(modifier = Modifier.verticalScroll(state)) {
            calendarData.value?.let { data ->
                MainActivity.navViewModel.loading.postValue(false)
                StaggeredVerticalGrid(
                    maxColumnWidth = 220.dp,
                    modifier = Modifier.padding(Dimen.mediuPadding)
                ) {
                    data.forEach {
                        CalendarItem(it)
                    }
                    Spacer(modifier = Modifier.height(Dimen.sheetMarginBottom))
                }
            }
        }

        //回到顶部
        ExtendedFabCompose(
            iconType = MainIconType.CALENDAR,
            text = stringResource(id = R.string.tool_calendar),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
        ) {
            coroutineScope.launch {
                state.scrollTo(0)
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
    val sd = calendar.getFixedStartTime()
    val ed = calendar.getFixedEndTime()
    val inProgress = today.daysInt(sd) >= 0 && ed.daysInt(today) >= 0
    val comingSoon = today.daysInt(sd) < 0
    Column(
        modifier = Modifier
            .padding(Dimen.mediuPadding)
            .fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(bottom = Dimen.mediuPadding)) {
            MainTitleText(text = calendar.getFixedStartTime())
            MainTitleText(
                text = calendar.getFixedEndTime().days(calendar.getFixedStartTime()),
                modifier = Modifier.padding(start = Dimen.mediuPadding)
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = Dimen.cardElevation, shape = Shapes.large, clip = true)
        ) {
            Column(
                modifier = Modifier.padding(Dimen.mediuPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (inProgress) {
                    MainTitleText(
                        text = stringResource(R.string.in_progress, ed.days(today)),
                        modifier = Modifier.padding(Dimen.smallPadding),
                        backgroundColor = colorResource(id = R.color.news_update)
                    )
                }
                if (comingSoon) {
                    MainTitleText(
                        text = stringResource(R.string.coming_soon, sd.days(today)),
                        modifier = Modifier.padding(Dimen.smallPadding),
                        backgroundColor = colorResource(id = R.color.news_system)
                    )

                }
                //内容
                getTypeData(calendar).forEach {
                    Row() {
                        MainContentText(
                            text = it.title,
                            color = colorResource(id = it.colorId),
                            modifier = Modifier.padding(bottom = Dimen.smallPadding),
                            textAlign = TextAlign.Start
                        )
                        MainContentText(
                            text = it.info,
                            textAlign = TextAlign.Start
                        )
                    }
                }
                if (inProgress) {
                    Text(
                        text = stringResource(id = R.string.dead_line, calendar.getFixedEndTime()),
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(top = Dimen.mediuPadding)
                    )
                }
            }
        }
    }
}


/**
 * 获取当天时间
 */
fun getToday(): String {
    val cal = Calendar.getInstance()
    cal.time = Date(System.currentTimeMillis())
    val year = cal.get(Calendar.YEAR)
    val month = cal.get(Calendar.MONTH) + 1
    val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
    val today = "$year/$month/$dayOfMonth"
    return today
}

/**
 * 获取事项信息
 */
@Composable
private fun getTypeData(data: DropEvent): ArrayList<CalendarEventData> {
    val events = arrayListOf<CalendarEventData>()
    val list = data.type.split("-")
    list.forEachIndexed { index, s ->
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
                colorId = R.color.cool_apk
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
