package cn.wthee.pcrtool.ui.home

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.NavActions
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shape
import cn.wthee.pcrtool.ui.theme.defaultSpring
import cn.wthee.pcrtool.utils.VibrateUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class ToolMenuData(
    @StringRes val titleId: Int,
    val iconType: MainIconType,
    val regionForNews: Int = 0
)

/**
 * 菜单
 */
@ExperimentalMaterialApi
@Composable
fun ToolMenu(actions: NavActions, all: Boolean) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val downloadState = navViewModel.downloadProgress.observeAsState().value ?: -2

    val list = arrayListOf(
        ToolMenuData(R.string.tool_pvp, MainIconType.PVP_SEARCH),
        ToolMenuData(R.string.tool_clan, MainIconType.CLAN),
        ToolMenuData(R.string.tool_leader, MainIconType.LEADER),
        ToolMenuData(R.string.tool_gacha, MainIconType.GACHA),
        ToolMenuData(R.string.tool_event, MainIconType.EVENT),
        ToolMenuData(R.string.tool_guild, MainIconType.GUILD),
        ToolMenuData(R.string.random_area, MainIconType.RANDOM_AREA)
    )
    if (all) {
        list.add(ToolMenuData(R.string.tweet, MainIconType.TWEET))
        list.add(ToolMenuData(R.string.comic, MainIconType.COMIC))
        if (BuildConfig.DEBUG) {
            list.add(ToolMenuData(R.string.skill, MainIconType.SKILL_LOOP))
            list.add(ToolMenuData(R.string.tool_equip, MainIconType.EQUIP_CALC))
        }
        list.add(ToolMenuData(R.string.redownload_db, MainIconType.DB_DOWNLOAD))

    } else {
        list.add(ToolMenuData(R.string.tool_more, MainIconType.TOOL_MORE))
    }


    VerticalGrid(
        maxColumnWidth = if (all) {
            (getItemWidth() + Dimen.mediumPadding * 2) / 2
        } else {
            Dimen.toolMenuWidth
        },
        modifier = Modifier.animateContentSize(defaultSpring())
    ) {
        list.forEach {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = Dimen.mediumPadding,
                        start = Dimen.mediumPadding,
                        end = Dimen.mediumPadding,
                        bottom = Dimen.largePadding
                    ),
                contentAlignment = Alignment.Center
            ) {
                when (it.iconType) {
                    MainIconType.DB_DOWNLOAD -> {
                        if (downloadState > -2) {
                            MainCard {
                                Row(
                                    modifier = Modifier
                                        .clip(Shape.medium)
                                        .clickable(
                                            onClick = getAction(
                                                coroutineScope,
                                                context,
                                                actions,
                                                it
                                            )
                                        )
                                        .defaultMinSize(minWidth = Dimen.menuItemSize)
                                        .padding(Dimen.smallPadding),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator(
                                            modifier = Modifier
                                                .size(Dimen.iconSize)
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
                            MenuItem(coroutineScope, context, actions, it, all)
                        }
                    }
                    else -> MenuItem(coroutineScope, context, actions, it, all)
                }

            }

        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun MenuItem(
    coroutineScope: CoroutineScope,
    context: Context,
    actions: NavActions,
    it: ToolMenuData,
    all: Boolean
) {
    if (all) {
        MainCard {
            Row(
                modifier = Modifier
                    .clip(Shape.medium)
                    .clickable(onClick = getAction(coroutineScope, context, actions, it))
                    .defaultMinSize(minWidth = Dimen.menuItemSize)
                    .padding(Dimen.smallPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconCompose(data = it.iconType.icon, size = Dimen.iconSize)
                Subtitle2(
                    text = stringResource(id = it.titleId),
                    modifier = Modifier.padding(start = Dimen.mediumPadding),
                )
            }
        }
    } else {
        Column(
            modifier = Modifier
                .clip(Shape.medium)
                .clickable(onClick = getAction(coroutineScope, context, actions, it))
                .defaultMinSize(minWidth = Dimen.menuItemSize)
                .padding(Dimen.smallPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconCompose(data = it.iconType.icon, size = Dimen.menuIconSize)
            CaptionText(
                text = stringResource(id = it.titleId),
                modifier = Modifier.padding(top = Dimen.mediumPadding),
                textAlign = TextAlign.Start
            )
        }
    }
}

private fun getAction(
    coroutineScope: CoroutineScope,
    context: Context,
    actions: NavActions,
    tool: ToolMenuData
): () -> Unit {

    return {
        VibrateUtil(context).single()
        when (tool.iconType) {
            MainIconType.CHARACTER -> actions.toCharacterList()
            MainIconType.GACHA -> actions.toGacha()
            MainIconType.CLAN -> actions.toClan()
            MainIconType.EVENT -> actions.toEvent()
            MainIconType.GUILD -> actions.toGuild()
            MainIconType.PVP_SEARCH -> actions.toPvp()
            MainIconType.LEADER -> actions.toLeader()
            MainIconType.EQUIP -> actions.toEquipList()
            MainIconType.TWEET -> actions.toTweetList()
            MainIconType.CHANGE_DATA -> navViewModel.openChangeDataDialog.postValue(true)
            MainIconType.COMIC -> actions.toComicList()
            MainIconType.DB_DOWNLOAD -> {
                coroutineScope.launch {
                    DatabaseUpdater.checkDBVersion(0)
                }
            }
            MainIconType.SKILL_LOOP -> actions.toAllSkillList()
            MainIconType.EQUIP_CALC -> actions.toAllEquipList()
            MainIconType.RANDOM_AREA -> actions.toRandomEquipArea(0)
            MainIconType.TOOL_MORE -> actions.toToolMore()
            else -> {
            }
        }
    }

}