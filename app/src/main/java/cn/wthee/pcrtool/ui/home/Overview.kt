package cn.wthee.pcrtool.ui.home

import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ScaleFactor
import androidx.compose.ui.layout.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.NewsTable
import cn.wthee.pcrtool.data.db.entity.getRegion
import cn.wthee.pcrtool.data.db.view.CalendarEvent
import cn.wthee.pcrtool.data.db.view.CalendarEventData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.NavActions
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.viewmodel.OverviewViewModel
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

/**
 * 首页纵览
 */
@ExperimentalCoilApi
@ExperimentalPagerApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun Overview(
    actions: NavActions,
    overviewViewModel: OverviewViewModel = hiltViewModel()
) {
    SideEffect {
        overviewViewModel.getR6Ids()
    }
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val characterSize = 10
    val characterList =
        overviewViewModel.getCharacterList(characterSize)
            .collectAsState(initial = arrayListOf()).value
    val equipList = overviewViewModel.getEquipList().collectAsState(initial = arrayListOf()).value
    val inProgressEventList =
        overviewViewModel.getCalendarEventList(0).collectAsState(initial = arrayListOf()).value
    val comingSoonEventList =
        overviewViewModel.getCalendarEventList(1).collectAsState(initial = arrayListOf()).value
    val newsList =
        overviewViewModel.getNewsOverview().collectAsState(initial = arrayListOf()).value

    val pagerCount = 6
    val pagerState =
        rememberPagerState(
            pageCount = pagerCount,
            initialOffscreenLimit = pagerCount - 1,
            infiniteLoop = true
        )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        TopBarCompose(actions)
        //角色
        Section(
            titleId = R.string.character,
            iconType = MainIconType.CHARACTER,
            visible = characterList.isNotEmpty(),
            onClick = {
                actions.toCharacterList()
            }
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) { index ->
                val id = characterList[index].id
                val infiniteLoopIndex =
                    if (index == pagerState.pageCount - 1 && pagerState.currentPage == 0) {
                        //从首个滚动到最后一个
                        pagerState.currentPage - 1
                    } else if (index == 0 && pagerState.currentPage == pagerState.pageCount - 1) {
                        //从最后一个滚动的首个
                        pagerState.pageCount
                    } else {
                        index
                    }
                Card(
                    modifier = Modifier
                        .padding(top = Dimen.mediuPadding, bottom = Dimen.mediuPadding)
                        .fillMaxWidth(0.8f)
                        .graphicsLayer {
                            val pageOffset =
                                calculateCurrentOffsetForPage(infiniteLoopIndex).absoluteValue
                            lerp(
                                start = ScaleFactor(0.9f, 0.9f),
                                stop = ScaleFactor(1f, 1f),
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            ).also { scale ->
                                scaleX = scale.scaleY
                                scaleY = scale.scaleY
                            }
                        },
                    onClick = {
                        VibrateUtil(context).single()
                        if (index == pagerState.currentPage) {
                            actions.toCharacterDetail(id)
                        } else {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(infiniteLoopIndex)
                            }
                        }
                    },
                    elevation = 0.dp,
                ) {
                    ImageCompose(CharacterIdUtil.getMaxCardUrl(id), ratio = RATIO)
                }

            }
        }

        //装备
        Section(
            titleId = R.string.tool_equip,
            iconType = MainIconType.EQUIP,
            visible = equipList.isNotEmpty(),
            onClick = {
                actions.toEquipList()
            }
        ) {
            VerticalGrid(maxColumnWidth = Dimen.iconSize * 2) {
                equipList.forEach {
                    Box(
                        modifier = Modifier
                            .padding(Dimen.mediuPadding)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        IconCompose(data = getEquipIconUrl(it.equipmentId)) {
                            actions.toEquipDetail(it.equipmentId)
                        }
                    }
                }
            }
        }

        //更多功能
        Section(
            titleId = R.string.function,
            iconType = MainIconType.FUNCTION
        ) {
            ToolMenu(actions = actions)
        }

        //新闻
        Section(
            titleId = R.string.tool_news,
            iconType = MainIconType.NEWS,
            onClick = {
                actions.toNews()
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(
                        top = Dimen.mediuPadding,
                        start = Dimen.largePadding,
                        end = Dimen.largePadding
                    )
            ) {
                if (newsList.isNotEmpty()) {
                    newsList.forEach {
                        NewsItem(
                            region = it.url.getRegion(),
                            news = it,
                            toDetail = actions.toNewsDetail
                        )
                    }
                } else {
                    for (i in 0..2) {
                        NewsItem(
                            region = 2,
                            news = NewsTable(),
                            toDetail = actions.toNewsDetail
                        )
                    }
                }
            }
        }
        //日历
        if (inProgressEventList.isNotEmpty()) {
            Section(
                titleId = R.string.tool_calendar,
                iconType = MainIconType.CALENDAR_TODAY
            ) {
                Column(
                    modifier = Modifier
                        .padding(
                            top = Dimen.mediuPadding,
                            start = Dimen.largePadding,
                            end = Dimen.largePadding
                        )
                        .fillMaxWidth()
                ) {
                    inProgressEventList.forEach {
                        CalendarItem(it)
                    }
                }
            }
        }
        if (comingSoonEventList.isNotEmpty()) {
            Section(
                titleId = R.string.tool_calendar_comming,
                iconType = MainIconType.CALENDAR
            ) {
                Column(
                    modifier = Modifier
                        .padding(
                            top = Dimen.mediuPadding,
                            start = Dimen.largePadding,
                            end = Dimen.largePadding
                        )
                        .fillMaxWidth()
                ) {
                    comingSoonEventList.forEach {
                        CalendarItem(it)
                    }
                }
            }
        }
        CommonSpacer()
    }
}

/**
 * 标题
 */
@ExperimentalAnimationApi
@ExperimentalCoilApi
@Composable
private fun Section(
    @StringRes titleId: Int,
    iconType: MainIconType,
    visible: Boolean = true,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val modifier = (if (onClick == null) {
        Modifier
    } else {
        Modifier.clickable(onClick = {
            VibrateUtil(context).single()
            if (visible) {
                onClick.invoke()
            }
        })
    })

    Column(
        modifier = Modifier
            .padding(top = Dimen.largePadding)
            .animateContentSize(defaultSpring())
    ) {
        Row(
            modifier = modifier.padding(
                start = Dimen.largePadding,
                end = Dimen.largePadding,
                top = Dimen.mediuPadding,
                bottom = Dimen.mediuPadding
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconCompose(
                data = iconType.icon,
                size = Dimen.fabIconSize,
                tint = MaterialTheme.colors.onSurface
            )
            MainText(
                text = stringResource(id = titleId),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = Dimen.mediuPadding),
                textAlign = TextAlign.Start,
                color = MaterialTheme.colors.onSurface
            )
            if (onClick != null) {
                IconCompose(
                    data = MainIconType.MORE.icon,
                    size = Dimen.fabIconSize,
                    onClick = onClick,
                    tint = MaterialTheme.colors.onSurface
                )
            }
        }

        FadeAnimation(visible = visible) {
            content.invoke()
        }
    }

}

@ExperimentalMaterialApi
@Composable
private fun NewsItem(
    region: Int,
    news: NewsTable,
    toDetail: (String) -> Unit,
) {
    val placeholder = news.title == ""
    val tag = when (region) {
        2 -> R.string.db_cn
        3 -> R.string.db_tw
        else -> R.string.db_jp
    }
    val colorId = when (region) {
        2 -> R.color.news_update
        3 -> R.color.news_system
        else -> R.color.colorPrimary
    }
    //标题
    Row(
        modifier = Modifier
            .padding(bottom = Dimen.mediuPadding)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MainTitleText(
            text = stringResource(id = tag),
            backgroundColor = colorResource(id = colorId),
            modifier = Modifier.placeholder(
                visible = placeholder,
                highlight = PlaceholderHighlight.shimmer()
            )
        )
        MainTitleText(
            text = news.date,
            modifier = Modifier
                .padding(start = Dimen.smallPadding)
                .placeholder(
                    visible = placeholder,
                    highlight = PlaceholderHighlight.shimmer()
                ),
        )
    }
    MainCard(modifier = Modifier
        .padding(bottom = Dimen.largePadding)
        .placeholder(
            visible = placeholder,
            highlight = PlaceholderHighlight.shimmer()
        ),
        onClick = {
            if (!placeholder) {
                toDetail(news.id)
            }
        }
    ) {
        //内容
        Subtitle1(
            text = news.title,
            modifier = Modifier.padding(Dimen.mediuPadding),
            selectable = true
        )
    }
}


/**
 * 日历信息
 */
@ExperimentalCoilApi
@ExperimentalMaterialApi
@Composable
private fun CalendarItem(calendar: CalendarEvent) {
    val today = getToday()
    val sd = calendar.startTime.formatTime
    val ed = calendar.endTime.formatTime
    val inProgress = today.second(sd) > 0 && ed.second(today) > 0
    val comingSoon = today.second(sd) < 0

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

    FlowRow(
        modifier = Modifier.padding(bottom = Dimen.mediuPadding),
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
                    data = MainIconType.TIME_LEFT.icon,
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

    MainCard(modifier = Modifier.padding(bottom = Dimen.largePadding)) {
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
private fun getTypeData(data: CalendarEvent): ArrayList<CalendarEventData> {
    val events = arrayListOf<CalendarEventData>()
    if (data.type != "1") {
        //正常活动
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
    } else {
        //露娜塔
        events.add(
            CalendarEventData(
                stringResource(id = R.string.tower),
                "",
            )
        )
    }

    return events
}

