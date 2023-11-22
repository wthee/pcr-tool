package cn.wthee.pcrtool.ui.tool.storyevent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.StoryEventData
import cn.wthee.pcrtool.data.enums.AllPicsType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.RegionType
import cn.wthee.pcrtool.navigation.navigateUp
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.components.CaptionText
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.DateRangePickerCompose
import cn.wthee.pcrtool.ui.components.EventTitleCountdown
import cn.wthee.pcrtool.ui.components.IconTextButton
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainImage
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.RATIO_BANNER
import cn.wthee.pcrtool.ui.components.RATIO_TEASER
import cn.wthee.pcrtool.ui.components.StateBox
import cn.wthee.pcrtool.ui.components.Subtitle1
import cn.wthee.pcrtool.ui.components.getDatePickerYearRange
import cn.wthee.pcrtool.ui.components.getItemWidth
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.colorGold
import cn.wthee.pcrtool.ui.theme.colorGreen
import cn.wthee.pcrtool.ui.theme.colorPurple
import cn.wthee.pcrtool.ui.theme.colorRed
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.ImageRequestHelper.Companion.EVENT_BANNER
import cn.wthee.pcrtool.utils.ImageRequestHelper.Companion.EVENT_TEASER
import cn.wthee.pcrtool.utils.days
import cn.wthee.pcrtool.utils.fixJpTime
import cn.wthee.pcrtool.utils.formatTime
import cn.wthee.pcrtool.utils.getToday
import cn.wthee.pcrtool.utils.second
import kotlinx.coroutines.launch

/**
 * 剧情活动
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryEventListScreen(
    toCharacterDetail: (Int) -> Unit,
    toEventEnemyDetail: (Int) -> Unit,
    toAllPics: (Int, Int) -> Unit,
    storyEventListViewModel: StoryEventListViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberLazyStaggeredGridState()
    val uiState by storyEventListViewModel.uiState.collectAsStateWithLifecycle()
    val dateRangePickerState = rememberDateRangePickerState(yearRange = getDatePickerYearRange())


    MainScaffold(
        enableClickClose = uiState.openDialog,
        onCloseClick = {
            storyEventListViewModel.changeDialog(false)
        },
        secondLineFab = {
            //日期选择
            DateRangePickerCompose(
                dateRangePickerState = dateRangePickerState,
                dateRange = uiState.dateRange,
                openDialog = uiState.openDialog,
                changeRange = storyEventListViewModel::changeRange,
                changeDialog = storyEventListViewModel::changeDialog
            )
        },
        fab = {
            //重置
            if (uiState.dateRange.hasFilter()) {
                MainSmallFab(iconType = MainIconType.RESET) {
                    storyEventListViewModel.reset()
                    dateRangePickerState.setSelection(null, null)
                }
            }

            //回到顶部
            MainSmallFab(
                iconType = MainIconType.EVENT,
                text = stringResource(id = R.string.tool_event),
            ) {
                coroutineScope.launch {
                    try {
                        scrollState.scrollToItem(0)
                    } catch (_: Exception) {
                    }
                }
            }
        },
        mainFabIcon = if (uiState.openDialog) MainIconType.CLOSE else MainIconType.BACK,
        onMainFabClick = {
            if (uiState.openDialog) {
                storyEventListViewModel.changeDialog(false)
            } else {
                navigateUp()
            }
        }
    ) {
        StateBox(stateType = uiState.loadingState) {
            StoryEventListContent(
                scrollState = scrollState,
                storyList = uiState.storyList!!,
                toCharacterDetail = toCharacterDetail,
                toEventEnemyDetail = toEventEnemyDetail,
                toAllPics = toAllPics
            )
        }

    }
}

@Composable
private fun StoryEventListContent(
    scrollState: LazyStaggeredGridState,
    storyList: List<StoryEventData>,
    toCharacterDetail: (Int) -> Unit,
    toEventEnemyDetail: (Int) -> Unit,
    toAllPics: (Int, Int) -> Unit
) {
    LazyVerticalStaggeredGrid(
        state = scrollState,
        columns = StaggeredGridCells.Adaptive(getItemWidth())
    ) {
        items(
            items = storyList,
            key = {
                it.eventId
            }
        ) {
            StoryEventItemContent(
                event = it,
                toCharacterDetail = toCharacterDetail,
                toEventEnemyDetail = toEventEnemyDetail,
                toAllPics = toAllPics
            )
        }
        items(2) {
            CommonSpacer()
        }
    }
}

/**
 * 剧情活动item
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StoryEventItemContent(
    event: StoryEventData,
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
    val days = ed.days(sd, showDay = false)
    if (days == "0") {
        showDays = false
    }

    //支线
    val isSub = event.eventId / 10000 == 2
    //类型判断
    when {
        //支线
        isSub -> {
            type = stringResource(id = R.string.story_event_sub)
            typeColor = colorGreen
            showDays = false
        }
        //复刻
        event.eventId / 10000 == 1 && event.eventId != event.originalEventId -> {
            type = stringResource(id = R.string.story_event_re)
            typeColor = colorGold
        }
        //预告
        sd.second(today) > 0 || previewEvent -> {
            type = stringResource(id = R.string.story_event_preview)
            typeColor = colorPurple
        }
        //正常
        else -> {
            type = stringResource(id = R.string.story_event_new)
            typeColor = colorRed
        }
    }

    val inProgress =
        today.second(sd) > 0 && ed.second(today) > 0 && event.eventId / 10000 != 2
    val comingSoon = today.second(sd) < 0


    Column(
        modifier = Modifier
            .padding(
                horizontal = Dimen.largePadding,
                vertical = Dimen.mediumPadding
            )
    ) {
        //标题
        FlowRow(
            modifier = Modifier.padding(bottom = Dimen.mediumPadding),
            verticalArrangement = Arrangement.Center
        ) {
            MainTitleText(
                text = type,
                backgroundColor = typeColor,
                modifier = Modifier.padding(end = Dimen.smallPadding),
            )
            if (!previewEvent) {
                MainTitleText(
                    text = sd.substring(0, 10),
                    modifier = Modifier.padding(end = Dimen.smallPadding),
                )
            }
            if (showDays) {
                MainTitleText(
                    text = stringResource(R.string.day, days.toInt()),
                    modifier = Modifier.padding(end = Dimen.smallPadding)
                )
            }
            //计时
            EventTitleCountdown(today, sd, ed, inProgress, comingSoon && (!previewEvent))
        }

        MainCard {
            Column(modifier = Modifier.padding(bottom = Dimen.smallPadding)) {
                //banner 图片
                if (inProgress || isSub || !hasTeaser(event.eventId)) {
                    MainImage(
                        data = ImageRequestHelper.getInstance()
                            .getUrl(EVENT_BANNER, event.originalEventId, forceJpType = false),
                        contentScale = ContentScale.FillBounds,
                        ratio = RATIO_BANNER
                    )
                } else {
                    MainImage(
                        data = ImageRequestHelper.getInstance()
                            .getUrl(EVENT_TEASER, event.eventId, forceJpType = false),
                        ratio = RATIO_TEASER,
                    )
                }

                //标题
                Subtitle1(
                    text = event.getEventTitle(),
                    modifier = Modifier.padding(Dimen.mediumPadding),
                    textAlign = TextAlign.Start,
                    selectable = true
                )

                //boss、掉落角色图标
                if (event.getUnitIdList().isNotEmpty()) {
                    Row {
                        //sp boss 图标，处理id 311403 -> 311400
                        if (!isSub && event.bossUnitId != 0) {
                            MainIcon(
                                data = ImageRequestHelper.getInstance()
                                    .getUrl(
                                        ImageRequestHelper.ICON_UNIT,
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
                            MainIcon(
                                data = ImageRequestHelper.getInstance().getMaxIconUrl(unitId),
                                modifier = Modifier.padding(horizontal = Dimen.mediumPadding)
                            ) {
                                toCharacterDetail(unitId)
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(
                            start = Dimen.smallPadding,
                            end = Dimen.mediumPadding,
                            top = Dimen.smallPadding
                        )
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
    RegionType.CN -> eventId >= 10002
    RegionType.TW -> eventId >= 10052 || eventId == 10038 || eventId == 10040 || eventId == 10048 || eventId == 10050
    RegionType.JP -> eventId >= 10052 && eventId != 10055
} && eventId / 10000 == 1


@CombinedPreviews
@Composable
private fun StoryEventListContentPreview() {
    PreviewLayout {
        StoryEventListContent(
            scrollState = rememberLazyStaggeredGridState(),
            storyList = arrayListOf(
                StoryEventData(
                    eventId = 1,
                    title = stringResource(id = R.string.debug_long_text),
                    unitIds = "100101-100101-100102",
                    bossEnemyId = 1
                ),
                StoryEventData(
                    eventId = 10000,
                    title = stringResource(id = R.string.debug_long_text),
                    unitIds = "100101-100101-100102",
                    bossEnemyId = 1
                ),
                StoryEventData(
                    eventId = 20000,
                    title = stringResource(id = R.string.debug_short_text),
                    unitIds = "100101-100101-100102",
                    bossEnemyId = 1
                ),
            ),
            toCharacterDetail = { },
            toEventEnemyDetail = { },
            toAllPics = { _, _ -> }
        )
    }
}