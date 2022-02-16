package cn.wthee.pcrtool.ui.tool

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.NavActions
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.home.ToolMenuData
import cn.wthee.pcrtool.ui.home.getAction
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.defaultSpring
import kotlinx.coroutines.CoroutineScope

data class ToolMenuGroup(
    val title: String,
    val list: List<ToolMenuData>
)

/**
 * 全部工具
 * TODO 添加到首页
 */
@ExperimentalMaterialApi
@Composable
fun AllToolMenu(scrollState: LazyListState, actions: NavActions) {

    val downloadState = navViewModel.downloadProgress.observeAsState().value ?: -2
    val coroutineScope = rememberCoroutineScope()

    val list = arrayListOf<ToolMenuGroup>()

    //游戏数据
    val dataList = arrayListOf<ToolMenuData>()
    dataList.add(ToolMenuData(R.string.character, MainIconType.CHARACTER))
    dataList.add(ToolMenuData(R.string.tool_equip, MainIconType.EQUIP))
    dataList.add(ToolMenuData(R.string.tool_guild, MainIconType.GUILD))
    dataList.add(ToolMenuData(R.string.tool_clan, MainIconType.CLAN))
    dataList.add(ToolMenuData(R.string.random_area, MainIconType.RANDOM_AREA))
    list.add(ToolMenuGroup(stringResource(id = R.string.basic_info), dataList))

    //游戏信息
    val infoList = arrayListOf<ToolMenuData>()
    infoList.add(ToolMenuData(R.string.tool_gacha, MainIconType.GACHA))
    infoList.add(ToolMenuData(R.string.tool_event, MainIconType.EVENT))
    infoList.add(ToolMenuData(R.string.tool_news, MainIconType.NEWS))
    infoList.add(ToolMenuData(R.string.tool_free_gacha, MainIconType.FREE_GACHA))
    list.add(ToolMenuGroup(stringResource(id = R.string.activity_info), infoList))

    //查询
    val searchList = arrayListOf<ToolMenuData>()
    searchList.add(ToolMenuData(R.string.tool_pvp, MainIconType.PVP_SEARCH))
    searchList.add(ToolMenuData(R.string.tool_leader, MainIconType.LEADER))
    list.add(ToolMenuGroup(stringResource(id = R.string.pvp_search), searchList))

    //其它
    val otherList = arrayListOf<ToolMenuData>()
    otherList.add(ToolMenuData(R.string.tweet, MainIconType.TWEET))
    otherList.add(ToolMenuData(R.string.comic, MainIconType.COMIC))
    if (BuildConfig.DEBUG) {
        otherList.add(ToolMenuData(R.string.skill, MainIconType.SKILL_LOOP))
        otherList.add(ToolMenuData(R.string.tool_equip, MainIconType.EQUIP_CALC))
    }
    otherList.add(ToolMenuData(R.string.redownload_db, MainIconType.DB_DOWNLOAD))
    list.add(ToolMenuGroup(stringResource(id = R.string.other), otherList))


    LazyColumn(
        modifier = Modifier
            .padding(horizontal = Dimen.mediumPadding)
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface), state = scrollState
    ) {
        items(list) {
            MenuGroup(
                coroutineScope = coroutineScope,
                actions = actions,
                title = it.title,
                list = it.list,
                downloadState = downloadState
            )
        }
        item {
            CommonSpacer()
        }
    }
}

/**
 * 菜单组
 */
@ExperimentalMaterialApi
@Composable
private fun MenuGroup(
    coroutineScope: CoroutineScope,
    actions: NavActions,
    title: String,
    list: List<ToolMenuData>,
    downloadState: Int
) {

    Column(
        modifier = Modifier
            .padding(horizontal = Dimen.mediumPadding)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MainText(
            text = title,
            modifier = Modifier.padding(top = Dimen.largePadding * 2, bottom = Dimen.mediumPadding)
        )
        VerticalGrid(
            maxColumnWidth = (getItemWidth() + Dimen.mediumPadding * 2) / 2,
            modifier = Modifier.animateContentSize(defaultSpring())
        ) {
            list.forEach {
                when (it.iconType) {
                    MainIconType.DB_DOWNLOAD -> {
                        if (downloadState > -2) {
                            MainCard(
                                modifier = Modifier.padding(Dimen.mediumPadding),
                                onClick = getAction(
                                    coroutineScope,
                                    actions,
                                    it
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .defaultMinSize(minWidth = Dimen.menuItemSize)
                                        .padding(Dimen.smallPadding),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator(
                                            modifier = Modifier
                                                .size(Dimen.mediumIconSize)
                                                .padding(Dimen.smallPadding),
                                            color = MaterialTheme.colorScheme.primary,
                                            strokeWidth = 2.dp
                                        )
                                        //显示下载进度
                                        if (downloadState in 1..99) {
                                            CaptionText(
                                                text = downloadState.toString(),
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                    Subtitle2(
                                        text = stringResource(id = if (downloadState == -2) it.titleId else R.string.db_downloading),
                                        modifier = Modifier.padding(start = Dimen.mediumPadding),
                                    )
                                }
                            }
                        } else {
                            MenuItem(coroutineScope, actions, it)
                        }
                    }
                    else -> MenuItem(coroutineScope, actions, it)
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun MenuItem(
    coroutineScope: CoroutineScope,
    actions: NavActions,
    it: ToolMenuData
) {
    MainCard(
        modifier = Modifier.padding(Dimen.mediumPadding),
        onClick = getAction(coroutineScope, actions, it)
    ) {
        Row(
            modifier = Modifier
                .defaultMinSize(minWidth = Dimen.menuItemSize)
                .padding(horizontal = Dimen.smallPadding, vertical = Dimen.mediumPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconCompose(
                modifier = Modifier.padding(start = Dimen.mediumPadding),
                data = it.iconType.icon,
                size = Dimen.mediumIconSize
            )
            Subtitle2(
                text = stringResource(id = it.titleId),
                modifier = Modifier.padding(start = Dimen.largePadding),
            )
        }
    }
}
