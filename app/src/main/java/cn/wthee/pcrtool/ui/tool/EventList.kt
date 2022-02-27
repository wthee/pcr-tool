package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyGridState
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.EventData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.database.getRegion
import cn.wthee.pcrtool.ui.PreviewBox
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.utils.ImageResourceHelper.Companion.EVENT_BANNER
import cn.wthee.pcrtool.viewmodel.EventViewModel
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.launch

/**
 * 剧情活动
 */
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun EventList(
    scrollState: LazyGridState,
    toCharacterDetail: (Int) -> Unit,
    toAllPics: (Int, Int) -> Unit,
    eventViewModel: EventViewModel = hiltViewModel()
) {
    val events = eventViewModel.getEventHistory().collectAsState(initial = arrayListOf()).value
    val coroutineScope = rememberCoroutineScope()


    Box(modifier = Modifier.fillMaxSize()) {
        if (events.isNotEmpty()) {
            LazyVerticalGrid(
                state = scrollState,
                cells = GridCells.Adaptive(getItemWidth())
            ) {
                items(events) {
                    EventItem(it, toCharacterDetail, toAllPics)
                }
                item {
                    CommonSpacer()
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
                try {
                    scrollState.scrollToItem(0)
                } catch (e: Exception) {
                }
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
fun EventItem(
    event: EventData,
    toCharacterDetail: (Int) -> Unit,
    toAllPics: (Int, Int) -> Unit
) {
    val context = LocalContext.current
    val type: String
    val typeColor: Color
    var showDays = true
    val regionType = getRegion()
    val today = getToday()
    val sd = fixJpTime(event.startTime.formatTime, regionType)
    val ed = fixJpTime(event.endTime.formatTime, regionType)
    val preEvent = sd.substring(0, 10) == "2030/12/30"
    val days = ed.days(sd)
    if (days == "0" || days == "0天") {
        showDays = false
    }


    when {
        //支线
        event.eventId / 10000 == 2 -> {
            type = "支线"
            typeColor = colorResource(id = R.color.color_rank_21_23)
            showDays = false
        }
        //复刻
        event.eventId / 10000 == 1 && event.storyId % 1000 != event.eventId % 1000 -> {
            type = "复刻"
            typeColor = colorResource(id = R.color.color_rank_7_10)
        }
        //预告
        sd.second(today) > 0 || preEvent -> {
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
        today.second(sd) > 0 && ed.second(today) > 0 && event.eventId / 10000 != 2
    val comingSoon = today.second(sd) < 0 && (!preEvent)
    val id = 10000 + event.storyId % 1000


    Column(
        modifier = Modifier.padding(
            horizontal = Dimen.largePadding,
            vertical = Dimen.mediumPadding
        )
    ) {
        //标题
        FlowRow(
            modifier = Modifier.padding(bottom = Dimen.mediumPadding),
            crossAxisAlignment = FlowCrossAxisAlignment.Center
        ) {
            MainTitleText(
                text = type,
                backgroundColor = typeColor
            )
            if (!preEvent) {
                MainTitleText(
                    text = sd.substring(0, 10),
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
                        data = MainIconType.TIME_LEFT,
                        size = Dimen.smallIconSize,
                    )
                    MainContentText(
                        text = stringResource(R.string.progressing, ed.dates(today)),
                        modifier = Modifier.padding(start = Dimen.smallPadding),
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                if (comingSoon) {
                    IconCompose(
                        data = MainIconType.COUNTDOWN,
                        size = Dimen.smallIconSize,
                        tint = colorResource(id = R.color.news_system)
                    )
                    MainContentText(
                        text = stringResource(R.string.coming_soon, sd.dates(today)),
                        modifier = Modifier.padding(start = Dimen.smallPadding),
                        textAlign = TextAlign.Start,
                        color = colorResource(id = R.color.news_system)
                    )
                }
            }
        }
        MainCard {
            Column(modifier = Modifier.padding(bottom = Dimen.mediumPadding)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    //banner 图片
                    Box(modifier = Modifier.weight(3f)) {
                        ImageCompose(
                            data = ImageResourceHelper.getInstance().getUrl(EVENT_BANNER, id),
                            ratio = RATIO_BANNER,
                            loadingId = R.drawable.load,
                            errorId = R.drawable.error,
                        )
                    }
                    //图标
                    if (event.getUnitIdList().isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            event.getUnitIdList().forEach {
                                UnitIcon(
                                    id = it,
                                    onClickItem = toCharacterDetail
                                )
                            }
                        }
                    }
                }
                //内容
                MainContentText(
                    text = event.getEventTitle(),
                    modifier = Modifier.padding(Dimen.mediumPadding),
                    textAlign = TextAlign.Start,
                    selectable = true
                )
                Row(
                    modifier = Modifier
                        .padding(horizontal = Dimen.mediumPadding)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Subtitle2(
                        text = stringResource(R.string.story_pic),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            VibrateUtil(context).single()
                            toAllPics(event.storyId, 1)
                        })
                    //结束日期
                    CaptionText(
                        text = if (event.eventId / 10000 != 2) {
                            ed
                        } else {
                            stringResource(R.string.no_limit)
                        }
                    )
                }

            }
        }
    }

}

@Preview
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
private fun EventItemPreview() {
    PreviewBox {
        Column {
            EventItem(event = EventData(), toCharacterDetail = {}, toAllPics = { _, _ -> })
        }
    }
}