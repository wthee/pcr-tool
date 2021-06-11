package cn.wthee.pcrtool.ui.home

import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.NavActions
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shapes
import cn.wthee.pcrtool.ui.tool.CalendarItem
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.viewmodel.OverviewViewModel

/**
 * 首页纵览
 *
 * fixme 数据未加载时，显示占位
 */
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun Overview(
    actions: NavActions,
    overviewViewModel: OverviewViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    overviewViewModel.getCharacterList()
    overviewViewModel.getEquipList()
    overviewViewModel.getCalendarEventList()
    val characterList =
        overviewViewModel.characterList.observeAsState().value
    val equipList = overviewViewModel.equipList.observeAsState().value
    val eventList = overviewViewModel.eventList.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        TopBarCompose(actions)
        //角色
        val cardWidth = ScreenUtil.getWidth().px2dp.dp * 0.618f
        SectionHead(R.string.character) {
            actions.toCharacterList()
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(defaultSpring())
                .horizontalScroll(rememberScrollState())
        ) {
            characterList?.forEach {
                MainCard(
                    modifier = Modifier
                        .padding(start = Dimen.largePadding, end = Dimen.largePadding)
                        .width(cardWidth),
                    onClick = {
                        VibrateUtil(context).single()
                        actions.toCharacterDetail(it.id)
                    }
                ) {
                    CharacterCard(CharacterIdUtil.getMaxCardUrl(it.id))
                }
            }
        }
        //更多功能
        val state = remember {
            mutableStateOf(ToolMenuState.FLOD)
        }
        val title = stringResource(
            id = if (state.value == ToolMenuState.FLOD) {
                R.string.expand
            } else {
                R.string.flod
            }
        )
        Row(
            modifier = Modifier
                .padding(Dimen.largePadding)
                .clip(Shapes.large)
                .clickable(onClick = {
                    VibrateUtil(context).single()
                    state.value = if (state.value == ToolMenuState.FLOD)
                        ToolMenuState.EXPAND
                    else
                        ToolMenuState.FLOD
                })
        ) {
            MainText(
                text = title,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start,
                color = MaterialTheme.colors.onSurface
            )
            IconCompose(
                data = if (state.value == ToolMenuState.FLOD) MainIconType.MAIN.icon else MainIconType.DOWN.icon,
                size = Dimen.fabIconSize
            )
        }
        ToolMenu(actions = actions, state)
        //装备
        SectionHead(R.string.tool_equip) {
            actions.toEquipList()
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(defaultSpring())
                .horizontalScroll(rememberScrollState())
        ) {
            equipList?.let { list ->
                list.forEach {
                    Box(
                        modifier = Modifier.padding(
                            start = Dimen.largePadding,
                            end = Dimen.largePadding,
                            bottom = Dimen.largePadding
                        )
                    ) {
                        IconCompose(data = getEquipIconUrl(it.equipmentId)) {
                            actions.toEquipDetail(it.equipmentId)
                        }
                    }
                }
            }
        }
        //日历
        SectionHead(titleId = R.string.tool_calendar) {
            actions.toCalendar()
        }
        Column(modifier = Modifier.padding(start = Dimen.largePadding, end = Dimen.largePadding)) {
            eventList.value?.let { list ->
                list.forEach {
                    CalendarItem(it)
                }
            }
        }
        //
        CommonSpacer()
    }
}

/**
 * 标题
 */
@Composable
private fun SectionHead(
    @StringRes titleId: Int,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Row(modifier = Modifier.padding(Dimen.largePadding)) {
        MainText(
            text = stringResource(id = titleId),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start,
            color = MaterialTheme.colors.onSurface
        )
        Subtitle1(
            text = stringResource(id = R.string.more),
            modifier = Modifier
                .padding(Dimen.smallPadding)
                .clip(Shapes.large)
                .clickable(onClick = onClick.vibrate {
                    VibrateUtil(context).single()
                }),
            color = MaterialTheme.colors.primary
        )
    }
}
