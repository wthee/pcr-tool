package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.view.EventData
import cn.wthee.pcrtool.ui.compose.ExtendedFabCompose
import cn.wthee.pcrtool.ui.compose.MainContentText
import cn.wthee.pcrtool.ui.compose.MainTitleText
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shapes
import cn.wthee.pcrtool.utils.days
import cn.wthee.pcrtool.utils.intArrayList
import cn.wthee.pcrtool.viewmodel.EventViewModel
import kotlinx.coroutines.launch

/**
 * 剧情活动
 */
@Composable
fun EventList(
    toCharacterDetail: (Int, Int) -> Unit,
    eventViewModel: EventViewModel = hiltNavGraphViewModel()
) {
    eventViewModel.getEventHistory()
    val events = eventViewModel.events.observeAsState()
    val state = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        events.value?.let { data ->
            LazyColumn(state = state) {
                items(data) {
                    EventItem(it, toCharacterDetail)
                }
            }
        }
        //回到顶部
        ExtendedFabCompose(
            icon = painterResource(id = R.drawable.ic_event),
            text = stringResource(id = R.string.tool_event),
            textWidth = Dimen.getWordWidth(4f),
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
private fun EventItem(event: EventData, toCharacterDetail: (Int, Int) -> Unit) {
    var title = ""
    var type = ""
    var typeColor = R.color.colorPrimary
    var showDays = true
    val startDate = event.startTime.substring(0, 10)
    val endDate = event.endTime.substring(0, 10)
    if (startDate == "2030/12/30") {
        title = "活动预告"
    } else {
        title = "$startDate ~ $endDate"
    }
    val days = endDate.days(startDate)
    if (days == "00") {
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
            typeColor = R.color.news_system
        }
        //正常
        else -> {
            type = "活动"
            typeColor = R.color.news_update
        }
    }

    Card(
        modifier = Modifier
            .padding(Dimen.mediuPadding)
            .fillMaxWidth()
            .shadow(elevation = Dimen.cardElevation, shape = Shapes.large, clip = true)
    ) {
        Column(modifier = Modifier.padding(Dimen.mediuPadding)) {
            //标题
            Row {
                MainTitleText(
                    text = type,
                    backgroundColor = colorResource(id = typeColor)
                )
                MainTitleText(
                    text = title,
                    modifier = Modifier.padding(start = Dimen.smallPadding),
                )
                if (showDays) {
                    MainTitleText(
                        text = days,
                        modifier = Modifier.padding(start = Dimen.smallPadding)
                    )
                }
            }
            //内容
            MainContentText(
                text = event.title,
                modifier = Modifier.padding(top = Dimen.smallPadding, bottom = Dimen.mediuPadding),
                textAlign = TextAlign.Start
            )
            //图标/描述
            IconListCompose(event.unitIds.intArrayList(), toCharacterDetail)
        }
    }
}

