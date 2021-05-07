package cn.wthee.pcrtool.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.ui.compose.ExtendedFabCompose
import cn.wthee.pcrtool.ui.compose.FabCompose
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shapes
import kotlinx.coroutines.launch

/**
 * 菜单
 */
@Composable
fun MenuContent(viewModel: NavViewModel, actions: NavActions) {
    val fabMainIcon = viewModel.fabMainIcon.observeAsState().value ?: MainIconType.OK
    val coroutineScope = rememberCoroutineScope()
    val updateApp = viewModel.updateApp.observeAsState().value ?: false

    if (fabMainIcon == MainIconType.DOWN) {
        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.alpha_black))
                .clickable {
                    viewModel.fabMainIcon.postValue(MainIconType.MAIN)
                }
        ) {
            Row(modifier = Modifier.height(Dimen.largeMenuHeight)) {
                Column(modifier = Modifier.weight(0.45f)) {
                    MenuItem(
                        text = stringResource(id = R.string.tool_event),
                        iconType = MainIconType.EVENT,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.5f)
                    ) {
                        actions.toEventStory()
                    }
                    MenuItem(
                        text = stringResource(id = R.string.tool_guild),
                        iconType = MainIconType.GUILD,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.5f)
                    ) {
                        actions.toGuildList()
                    }
                }
                MenuItem(
                    text = stringResource(id = R.string.tool_gacha),
                    iconType = MainIconType.GACHA,
                    modifier = Modifier
                        .weight(0.2f)
                        .fillMaxHeight()
                ) {
                    actions.toGacha()
                }
                Column(
                    modifier = Modifier
                        .weight(0.35f)
                        .height(Dimen.largeMenuHeight)
                ) {
                    MenuItem(
                        text = stringResource(id = R.string.tool_clan),
                        iconType = MainIconType.CLAN,
                        modifier = Modifier
                            .weight(0.5f)
                            .fillMaxWidth()
                            .height(Dimen.smallMenuHeight)
                    ) {
                        actions.toClanBattleList()
                    }
                    MenuItem(
                        text = stringResource(id = R.string.tool_leader),
                        iconType = MainIconType.LEADER,
                        modifier = Modifier
                            .weight(0.5f)
                            .fillMaxWidth()
                            .height(Dimen.smallMenuHeight)
                    ) {
                        actions.toLeaderboard()
                    }
                }
            }

            Row {
                MenuItem(
                    text = stringResource(id = R.string.tool_calendar),
                    iconType = MainIconType.CALENDAR,
                    modifier = Modifier
                        .weight(0.25f)
                        .height(Dimen.largeMenuHeight)
                ) {
                    actions.toCalendar()
                }
                Column(modifier = Modifier
                    .weight(0.3f)
                    .height(Dimen.largeMenuHeight)) {
                    MenuItem(
                        text = stringResource(id = R.string.db_cn),
                        iconType = MainIconType.NEWS,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        //官网公告
                        actions.toNews(2)
                    }
                    MenuItem(
                        text = stringResource(id = R.string.db_tw),
                        iconType = MainIconType.NEWS,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        //官网公告
                        actions.toNews(3)
                    }
                    MenuItem(
                        text = stringResource(id = R.string.db_jp),
                        iconType = MainIconType.NEWS,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        //官网公告
                        actions.toNews(4)
                    }
                }

                MenuItem(
                    text = stringResource(id = R.string.tool_pvp),
                    iconType = MainIconType.PVP_SEARCH,
                    modifier = Modifier
                        .weight(0.45f)
                        .height(Dimen.largeMenuHeight)
                ) {
                    actions.toPvpSearch()
                }
            }
            Row {
                MenuItem(
                    text = stringResource(id = R.string.tool_equip),
                    iconType = MainIconType.EQUIP,
                    modifier = Modifier
                        .weight(0.5f)
                        .height(Dimen.smallMenuHeight)
                ) {
                    actions.toEquipList()
                }
                MenuItem(
                    text = stringResource(id = R.string.setting),
                    iconType = MainIconType.SETTING,
                    modifier = Modifier
                        .weight(0.5f)
                        .height(Dimen.smallMenuHeight)
                ) {
                    actions.toSettings()
                }
            }
            Row(
                modifier = Modifier
                    .padding(
                        top = Dimen.mediuPadding,
                        bottom = Dimen.fabMargin,
                        end = Dimen.fabMarginEnd
                    )
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                //更新通知
                if (updateApp == -1) {
                    //加载中
                    FabCompose(
                        content = {
                            CircularProgressIndicator(
                                modifier = Modifier.size(Dimen.fabIconSize),
                                strokeWidth = Dimen.lineHeight
                            )
                        },
                        modifier = Modifier.padding(end = Dimen.fabSmallMarginEnd)
                    ) {
                        actions.toNotice
                    }
                } else {
                    val icon = if (updateApp == 1) MainIconType.APP_UPDATE else MainIconType.NOTICE
                    FabCompose(
                        iconType = icon,
                        modifier = Modifier.padding(end = Dimen.fabSmallMarginEnd)
                    ) {
                        actions.toNotice()
                    }
                }


                //数据版本切换
                ExtendedFabCompose(
                    iconType = MainIconType.CHANGE_DATA,
                    text = stringResource(id = R.string.change_db)
                ) {
                    coroutineScope.launch {
                        viewModel.fabMainIcon.postValue(MainIconType.MAIN)
                        DatabaseUpdater.changeType()
                    }
                }
            }
        }
    }

}

/**
 * 菜单项
 */
@Composable
fun MenuItem(text: String, iconType: MainIconType, modifier: Modifier, action: () -> Unit) {
    Card(
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = MaterialTheme.colors.onSurface,
        modifier = modifier
            .padding(Dimen.mediuPadding)
            .shadow(elevation = Dimen.cardElevation, shape = Shapes.large, clip = true)
            .clickable(onClick = action)
    ) {
        Box() {
            Text(
                text = text,
                color = MaterialTheme.colors.onPrimary,
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(Dimen.mediuPadding)
                    .align(Alignment.TopStart)
            )
            Icon(
                iconType.icon,
                contentDescription = null,
                tint = MaterialTheme.colors.onPrimary,
                modifier = Modifier
                    .size(Dimen.iconSize)
                    .padding(Dimen.mediuPadding)
                    .align(Alignment.BottomEnd)
            )
        }
    }
}
