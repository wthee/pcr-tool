package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.EventData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.days
import cn.wthee.pcrtool.utils.getToday
import cn.wthee.pcrtool.utils.hourInt
import cn.wthee.pcrtool.utils.intArrayList
import cn.wthee.pcrtool.viewmodel.EventViewModel
import kotlinx.coroutines.launch

/**
 * 剧情活动
 */
@Composable
fun EventList(
    toCharacterDetail: (Int) -> Unit,
    eventViewModel: EventViewModel = hiltNavGraphViewModel()
) {
    eventViewModel.getEventHistory()
    val events = eventViewModel.events.observeAsState()
    val state = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.bg_gray))
    ) {
        events.value?.let { data ->
            LazyColumn(state = state) {
                items(data) {
                    EventItem(it, toCharacterDetail)
                }
                item {
                    CommonSpacer()
                }
            }
        }
        //回到顶部
        ExtendedFabCompose(
            iconType = MainIconType.EVENT,
            text = stringResource(id = R.string.tool_event),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
        ) {
            coroutineScope.launch {
                state.scrollToItem(0)
            }
        }
    }


}

/**
 * 剧情活动
 */
@Composable
private fun EventItem(event: EventData, toCharacterDetail: (Int) -> Unit) {
    var title: String
    val type: String
    val typeColor: Int
    var showDays = true
    val today = getToday()
    val startDate = event.startTime.substring(0, 10)
    val endDate = event.endTime.substring(0, 10)
    title = if (startDate == "2030/12/30") {
        "预告"
    } else {
        "$startDate ~ $endDate"
    }
    val days = endDate.days(startDate)
    if (days == "0" || days == "0天") {
        showDays = false
    }
    when {
        //支线
        event.eventId / 10000 == 2 -> {
            type = "支线"
            typeColor = R.color.cool_apk
            title = startDate
            showDays = false
        }
        //复刻
        event.eventId / 10000 == 1 && event.storyId % 1000 != event.eventId % 1000 -> {
            type = "复刻"
            typeColor = R.color.color_rank_7_10
        }
        event.startTime.hourInt(today) > 0 -> {
            type = "预告"
            typeColor = R.color.news_system
        }
        //正常
        else -> {
            type = "活动"
            typeColor = R.color.news_update
        }
    }
    val inProgress =
        today.hourInt(event.startTime) > 0 && event.endTime.hourInt(today) > 0 && event.eventId / 10000 != 2

    Column(
        modifier = Modifier
            .padding(Dimen.mediuPadding)
            .fillMaxWidth()
    ) {
        //标题
        Row(modifier = Modifier.padding(bottom = Dimen.mediuPadding)) {
            MainTitleText(
                text = type,
                backgroundColor = colorResource(id = typeColor)
            )
            MainTitleText(
                text = title,
                modifier = Modifier.padding(start = Dimen.smallPadding),
            )
            if (inProgress) {
                MainTitleText(
                    text = stringResource(R.string.in_progress, endDate.days(today)),
                    modifier = Modifier.padding(start = Dimen.smallPadding),
                    backgroundColor = colorResource(id = R.color.news_update)
                )
            } else if (showDays) {
                MainTitleText(
                    text = days,
                    modifier = Modifier.padding(start = Dimen.smallPadding)
                )
            }
        }
        MainCard {
            Column(modifier = Modifier.padding(Dimen.mediuPadding)) {
                //内容
                MainContentText(
                    text = event.getEventTitle(),
                    modifier = Modifier.padding(bottom = Dimen.smallPadding),
                    textAlign = TextAlign.Start
                )
                //图标
                IconListCompose(event.unitIds.intArrayList(), toCharacterDetail)
            }
        }
    }
}

