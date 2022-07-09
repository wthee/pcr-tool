package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.EventData
import cn.wthee.pcrtool.data.enums.AllPicsType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.PreviewBox
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.*
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.utils.ImageResourceHelper.Companion.EVENT_BANNER
import cn.wthee.pcrtool.viewmodel.EventViewModel
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.launch

/**
 * 剧情活动
 */
@Composable
fun StoryEventList(
    scrollState: LazyGridState,
    toCharacterDetail: (Int) -> Unit,
    toAllPics: (Int, Int) -> Unit,
    eventViewModel: EventViewModel = hiltViewModel()
) {
    val events = eventViewModel.getStoryEventHistory().collectAsState(initial = arrayListOf()).value
    val coroutineScope = rememberCoroutineScope()


    Box(modifier = Modifier.fillMaxSize()) {
        if (events.isNotEmpty()) {
            LazyVerticalGrid(
                state = scrollState,
                columns = GridCells.Adaptive(getItemWidth())
            ) {
                items(
                    items = events,
                    key = {
                        it.eventId
                    }
                ) {
                    StoryEventItem(it, toCharacterDetail, toAllPics)
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
                } catch (_: Exception) {
                }
            }
        }
    }


}

/**
 * 剧情活动
 */
@Composable
fun StoryEventItem(
    event: EventData,
    toCharacterDetail: (Int) -> Unit,
    toAllPics: (Int, Int) -> Unit
) {
    val context = LocalContext.current
    val type: String
    val typeColor: Color
    var showDays = true
    val today = getToday()
    val sd = event.startTime.formatTime.fixJpTime
    val ed = event.endTime.formatTime.fixJpTime
    val preEvent = sd.substring(0, 10) == "2030/12/30"
    val days = ed.days(sd)
    if (days == "0" || days == "0天") {
        showDays = false
    }


    when {
        //支线
        event.eventId / 10000 == 2 -> {
            type = "支线"
            typeColor = colorGreen
            showDays = false
        }
        //复刻
        event.eventId / 10000 == 1 && event.storyId % 1000 != event.eventId % 1000 -> {
            type = "复刻"
            typeColor = colorGold
        }
        //预告
        sd.second(today) > 0 || preEvent -> {
            type = "预告"
            typeColor = colorPurple
        }
        //正常
        else -> {
            type = "活动"
            typeColor = colorRed
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
                        tint = colorPurple
                    )
                    MainContentText(
                        text = stringResource(R.string.coming_soon, sd.dates(today)),
                        modifier = Modifier.padding(start = Dimen.smallPadding),
                        textAlign = TextAlign.Start,
                        color = colorPurple
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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    //查看立绘
                    IconTextButton(
                        icon = MainIconType.PREVIEW_IMAGE,
                        text = stringResource(R.string.story_pic)
                    ) {
                        toAllPics(event.storyId, AllPicsType.STORY.type)
                    }
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
@Composable
private fun StoryEventItemPreview() {
    PreviewBox {
        Column {
            StoryEventItem(event = EventData(), toCharacterDetail = {}, toAllPics = { _, _ -> })
        }
    }
}