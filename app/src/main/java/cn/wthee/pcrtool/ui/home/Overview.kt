package cn.wthee.pcrtool.ui.home

import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.NewsTable
import cn.wthee.pcrtool.data.db.entity.fix
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.NavActions
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shapes
import cn.wthee.pcrtool.ui.tool.CalendarItem
import cn.wthee.pcrtool.utils.CharacterIdUtil
import cn.wthee.pcrtool.utils.ScreenUtil
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.px2dp
import cn.wthee.pcrtool.viewmodel.OverviewViewModel

/**
 * 首页纵览
 */
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun Overview(
    actions: NavActions,
    overviewViewModel: OverviewViewModel = hiltViewModel()
) {
    LaunchedEffect({ }) {
        val r6Ids = overviewViewModel.getR6Ids()
        MainActivity.navViewModel.r6Ids.postValue(r6Ids)
    }
    val scrollState = rememberScrollState()

    overviewViewModel.getCharacterList()
    overviewViewModel.getEquipList()
    overviewViewModel.getCalendarEventList()
    overviewViewModel.getNewsOverview()

    val characterList =
        overviewViewModel.characterList.observeAsState().value
    val equipList = overviewViewModel.equipList.observeAsState().value
    val inProgressEventList = overviewViewModel.inProgressEventList.observeAsState().value
    val comingSoonEventList = overviewViewModel.comingSoonEventList.observeAsState().value
    val newsList = overviewViewModel.newsList.observeAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        TopBarCompose(actions)
        //角色
        val cardWidth = ScreenUtil.getWidth().px2dp.dp * 0.618f
        Section(
            titleId = R.string.character,
            iconType = MainIconType.CHARACTER,
            loadState = characterList == null || characterList.isEmpty(),
            onClick = {
                actions.toCharacterList()
            }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                characterList?.forEach {
                    MainCard(
                        modifier = Modifier
                            .padding(
                                start = Dimen.largePadding,
                                end = Dimen.largePadding,
                                top = Dimen.mediuPadding,
                                bottom = Dimen.mediuPadding
                            )
                            .width(cardWidth),
                        onClick = {
                            actions.toCharacterDetail(it.id)
                        }
                    ) {
                        CharacterCard(CharacterIdUtil.getMaxCardUrl(it.id))
                    }
                }
            }
        }

        //装备
        Section(
            titleId = R.string.tool_equip,
            iconType = MainIconType.EQUIP,
            loadState = equipList == null || equipList.isEmpty(),
            onClick = {
                actions.toEquipList()
            }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                equipList?.let { list ->
                    list.forEach {
                        Box(
                            modifier = Modifier.padding(
                                start = Dimen.largePadding,
                                end = Dimen.largePadding,
                                top = Dimen.mediuPadding,
                                bottom = Dimen.mediuPadding
                            )
                        ) {
                            IconCompose(data = getEquipIconUrl(it.equipmentId)) {
                                actions.toEquipDetail(it.equipmentId)
                            }
                        }
                    }
                }
            }
        }

        //更多功能
        Section(
            titleId = R.string.function,
            iconType = MainIconType.FUNCTION,
            loadState = false
        ) {
            ToolMenu(actions = actions)
        }

        //新闻
        Section(
            titleId = R.string.tool_news,
            iconType = MainIconType.NEWS,
            loadState = newsList?.data == null,
            onClick = {
                actions.toNews()
            }
        ) {
            Column(
                modifier = Modifier.padding(
                    top = Dimen.mediuPadding,
                    start = Dimen.largePadding,
                    end = Dimen.largePadding
                )
            ) {
                newsList?.data?.let { list ->
                    list.forEach {
                        NewsItem(
                            region = it.getRegion(),
                            news = it,
                            actions.toNewsDetail
                        )
                    }
                }
            }
        }
        //日历
        if (inProgressEventList != null && inProgressEventList.isNotEmpty()) {
            Section(
                titleId = R.string.tool_calendar,
                iconType = MainIconType.CALENDAR_TODAY,
                loadState = false
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
        if (comingSoonEventList != null && comingSoonEventList.isNotEmpty()) {
            Section(
                titleId = R.string.tool_calendar_comming,
                iconType = MainIconType.CALENDAR,
                loadState = false
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
@Composable
private fun Section(
    @StringRes titleId: Int,
    iconType: MainIconType,
    loadState: Boolean,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val modifier = (if (onClick == null) {
        Modifier.clip(Shapes.small)
    } else {
        Modifier
            .clip(Shapes.small)
            .clickable(onClick = {
                VibrateUtil(context).single()
                onClick.invoke()
            })
    })

    Column(
        modifier = Modifier
            .padding(top = Dimen.mediuPadding)
            .animateContentSize(defaultSpring())
    ) {
        Row(
            modifier = modifier.padding(
                start = Dimen.largePadding,
                end = Dimen.largePadding,
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
        if (loadState) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(Dimen.largePadding)
                    .size(Dimen.fabIconSize)
                    .padding(Dimen.lineHeight)
                    .align(Alignment.CenterHorizontally),
                color = MaterialTheme.colors.primary,
                strokeWidth = Dimen.lineHeight
            )
        } else {
            content.invoke()
        }
    }

}

@ExperimentalMaterialApi
@Composable
private fun NewsItem(
    region: Int,
    news: NewsTable,
    toDetail: (String, String, Int, String) -> Unit,
) {

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
            backgroundColor = colorResource(id = colorId)
        )
        MainTitleText(
            text = news.date,
            modifier = Modifier.padding(start = Dimen.smallPadding),
        )
    }
    MainCard(modifier = Modifier.padding(bottom = Dimen.largePadding), onClick = {
        toDetail(news.title.fix(), news.url.fix(), region, news.date)
    }) {
        //内容
        Subtitle1(
            text = news.title,
            modifier = Modifier.padding(Dimen.mediuPadding),
            selectable = true
        )
    }
}
