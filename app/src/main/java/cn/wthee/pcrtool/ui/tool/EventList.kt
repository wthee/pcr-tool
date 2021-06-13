package cn.wthee.pcrtool.ui.tool

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.EventData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.viewmodel.EventViewModel
import kotlinx.coroutines.launch

/**
 * 剧情活动
 */
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun EventList(
    scrollState: LazyListState,
    toCharacterDetail: (Int) -> Unit,
    eventViewModel: EventViewModel = hiltViewModel()
) {
    eventViewModel.getEventHistory()
    val events = eventViewModel.events.observeAsState()
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        SlideAnimation(visible = events.value != null) {
            events.value?.let { data ->
                LazyColumn(
                    state = scrollState,
                    contentPadding = PaddingValues(Dimen.largePadding)
                ) {
                    items(data) {
                        EventItem(it, toCharacterDetail)
                    }
                    item {
                        CommonSpacer()
                    }
                }
            }
        }
        //回到顶部
        FabCompose(
            iconType = MainIconType.EVENT,
            text = stringResource(id = R.string.tool_event),
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
 * 剧情活动
 */
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
private fun EventItem(event: EventData, toCharacterDetail: (Int) -> Unit) {

    val type: String
    val typeColor: Color
    var showDays = true
    val today = getToday()
    val startDate = event.startTime.formatTime().substring(0, 10)
    val endDate = event.endTime.formatTime().substring(0, 10)
    val preEvent = startDate == "2030/12/30"
    val days = endDate.days(startDate)
    if (days == "0" || days == "0天") {
        showDays = false
    }

    when {
        //支线
        event.eventId / 10000 == 2 -> {
            type = "支线"
            typeColor = colorResource(id = R.color.color_rank_21)
            showDays = false
        }
        //复刻
        event.eventId / 10000 == 1 && event.storyId % 1000 != event.eventId % 1000 -> {
            type = "复刻"
            typeColor = colorResource(id = R.color.color_rank_7_10)
        }
        //预告
        event.startTime.hourInt(today) > 0 || preEvent -> {
            type = "预告"
            typeColor = colorResource(id = R.color.news_system)
        }
        //正常
        else -> {
            type = "活动"
            typeColor = colorResource(id = R.color.news_update)
        }
    }
    val inProgress =
        today.hourInt(event.startTime) > 0 && event.endTime.hourInt(today) > 0 && event.eventId / 10000 != 2
    val comingSoon = today.hourInt(event.startTime) < 0 && (!preEvent)

    //标题
    Row(
        modifier = Modifier.padding(bottom = Dimen.mediuPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MainTitleText(
            text = type,
            backgroundColor = typeColor
        )
        if (!preEvent) {
            MainTitleText(
                text = startDate,
                modifier = Modifier.padding(start = Dimen.smallPadding),
            )
        }
        if (showDays) {
            MainTitleText(
                text = days,
                modifier = Modifier.padding(start = Dimen.smallPadding)
            )
        }
        //计时
        Row(
            modifier = Modifier.padding(start = Dimen.smallPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (inProgress) {
                IconCompose(
                    data = MainIconType.TIME_LEFT.icon,
                    size = Dimen.smallIconSize,
                )
                MainContentText(
                    text = stringResource(R.string.in_progress, event.endTime.dates(today)),
                    modifier = Modifier.padding(start = Dimen.smallPadding),
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colors.primary
                )
            }
            if (comingSoon) {
                IconCompose(
                    data = MainIconType.COUNTDOWN.icon,
                    size = Dimen.smallIconSize,
                    tint = typeColor
                )
                MainContentText(
                    text = stringResource(R.string.coming_soon, event.startTime.dates(today)),
                    modifier = Modifier.padding(start = Dimen.smallPadding),
                    textAlign = TextAlign.Start,
                    color = typeColor
                )
            }
        }
    }
    MainCard(modifier = Modifier.padding(bottom = Dimen.largePadding)) {
        Column {
            //内容
            MainContentText(
                text = event.getEventTitle(),
                modifier = Modifier.padding(Dimen.mediuPadding),
                textAlign = TextAlign.Start
            )
            //图标
            IconListCompose(
                icons = event.unitIds.intArrayList(),
                toCharacterDetail
            )
            //结束日期
            if (event.eventId / 10000 != 2) {
                CaptionText(
                    text = event.endTime,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(end = Dimen.mediuPadding, bottom = Dimen.mediuPadding)
                )
            }

        }
    }
}

