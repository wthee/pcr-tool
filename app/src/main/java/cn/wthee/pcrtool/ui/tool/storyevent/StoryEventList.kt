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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.EventData
import cn.wthee.pcrtool.data.enums.AllPicsType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.PreviewBox
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.*
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.utils.ImageResourceHelper.Companion.EVENT_BANNER
import cn.wthee.pcrtool.utils.ImageResourceHelper.Companion.EVENT_TEASER
import cn.wthee.pcrtool.viewmodel.EventViewModel
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.launch

/**
 * 剧情活动
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StoryEventList(
    scrollState: LazyStaggeredGridState,
    toCharacterDetail: (Int) -> Unit,
    toEventEnemyDetail: (Int) -> Unit,
    toAllPics: (Int, Int) -> Unit,
    eventViewModel: EventViewModel = hiltViewModel()
) {
    val events = eventViewModel.getStoryEventHistory().collectAsState(initial = arrayListOf()).value
    val coroutineScope = rememberCoroutineScope()


    Box(modifier = Modifier.fillMaxSize()) {
        if (events.isNotEmpty()) {
            LazyVerticalStaggeredGrid(
                state = scrollState,
                columns = StaggeredGridCells.Adaptive(getItemWidth())
            ) {
                items(
                    items = events,
                    key = {
                        it.eventId
                    }
                ) {
                    StoryEventItem(
                        event = it,
                        toCharacterDetail = toCharacterDetail,
                        toEventEnemyDetail = toEventEnemyDetail,
                        toAllPics = toAllPics
                    )
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
    toEventEnemyDetail: (Int) -> Unit,
    toAllPics: (Int, Int) -> Unit
) {
    val type: String
    val typeColor: Color
    var showDays = true
    val today = getToday()
    val sd = event.startTime.formatTime.fixJpTime
    val ed = event.endTime.formatTime.fixJpTime
    val previewEvent = sd.substring(0, 10) == "2030/12/30"
    val days = ed.days(sd)
    if (days == "0" || days == "0天") {
        showDays = false
    }

    //支线
    val isSub = event.eventId / 10000 == 2
    //类型判断
    when {
        //支线
        isSub -> {
            type = "支线"
            typeColor = colorGreen
            showDays = false
        }
        //复刻
        event.eventId / 10000 == 1 && event.eventId != event.originalEventId -> {
            type = "复刻"
            typeColor = colorGold
        }
        //预告
        sd.second(today) > 0 || previewEvent -> {
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
    val comingSoon = today.second(sd) < 0 && (!previewEvent)


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
            if (!previewEvent) {
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
                //banner 图片
                if (inProgress || isSub || !hasTeaser(event.eventId)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        ImageCompose(
                            data = ImageResourceHelper.getInstance()
                                .getUrl(EVENT_BANNER, event.originalEventId),
                            ratio = RATIO_BANNER,
                            modifier = Modifier.clip(MaterialTheme.shapes.medium),
                            placeholder = false
                        )
                    }
                } else {
                    ImageCompose(
                        data = ImageResourceHelper.getInstance()
                            .getUrl(EVENT_TEASER, event.eventId),
                        ratio = RATIO_TEASER,
                        modifier = Modifier.clip(shapeTop())
                    )
                }

                //标题
                Subtitle1(
                    text = event.getEventTitle(),
                    modifier = Modifier.padding(Dimen.mediumPadding),
                    textAlign = TextAlign.Start,
                    selectable = true
                )

                //掉落角色图标
                if (event.getUnitIdList().isNotEmpty()) {
                    Row {
                        //sp boss 图标，处理id 311403 -> 311400
                        if (!isSub && event.bossUnitId != 0) {
                            IconCompose(
                                data = ImageResourceHelper.getInstance()
                                    .getUrl(
                                        ImageResourceHelper.ICON_UNIT,
                                        event.bossUnitId / 10 * 10
                                    ),
                                modifier = Modifier.padding(start = Dimen.mediumPadding)
                            ) {
                                toEventEnemyDetail(event.bossEnemyId)
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        //活动掉落角色图标
                        event.getUnitIdList().forEach { itemId ->
                            val unitId = itemId % 10000 * 100 + 1
                            IconCompose(
                                data = ImageResourceHelper.getInstance().getMaxIconUrl(unitId),
                                modifier = Modifier.padding(end = Dimen.mediumPadding)
                            ) {
                                toCharacterDetail(unitId)
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(horizontal = Dimen.mediumPadding)
                        .fillMaxWidth(),
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
                        text = if (isSub) {
                            stringResource(R.string.no_limit)
                        } else {
                            ed
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

            }
        }
    }

}

/**
 * 设置是否有teaser
 */
private fun hasTeaser(eventId: Int) = when (MainActivity.regionType) {
    2 -> eventId >= 10002
    3 -> eventId >= 10052 || eventId == 10038 || eventId == 10040 || eventId == 10048 || eventId == 10050
    4 -> eventId >= 10052 && eventId != 10055
    else -> false
} && eventId / 10000 == 1

@Preview
@Composable
private fun StoryEventItemPreview() {
    PreviewBox {
        Column {
            StoryEventItem(
                event = EventData(),
                toCharacterDetail = {},
                toEventEnemyDetail = {},
                toAllPics = { _, _ -> })
        }
    }
}