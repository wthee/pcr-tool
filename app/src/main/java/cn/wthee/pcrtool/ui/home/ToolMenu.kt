package cn.wthee.pcrtool.ui.home

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.NavActions
import cn.wthee.pcrtool.ui.compose.CaptionText
import cn.wthee.pcrtool.ui.compose.IconCompose
import cn.wthee.pcrtool.ui.compose.VerticalGrid
import cn.wthee.pcrtool.ui.compose.defaultSpring
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shapes
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
@ExperimentalAnimationApi
@Composable
fun ToolMenu(actions: NavActions) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val downloadState = MainActivity.navViewModel.downloadProgress.observeAsState().value ?: -1

    val list = arrayListOf(
        ToolMenuData(R.string.tool_pvp, MainIconType.PVP_SEARCH),
        ToolMenuData(R.string.tool_clan, MainIconType.CLAN),
        ToolMenuData(R.string.tool_leader, MainIconType.LEADER),
        ToolMenuData(R.string.tool_gacha, MainIconType.GACHA),
        ToolMenuData(R.string.tool_event, MainIconType.EVENT),
        ToolMenuData(R.string.tool_guild, MainIconType.GUILD),
        ToolMenuData(R.string.tweet, MainIconType.TWEET),
        ToolMenuData(R.string.change_db, MainIconType.CHANGE_DATA),
        ToolMenuData(R.string.comic, MainIconType.COMIC),
    )

    VerticalGrid(
        maxColumnWidth = Dimen.toolMenuWidth,
        modifier = Modifier.animateContentSize(defaultSpring())
    ) {

        list.forEach {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = Dimen.mediuPadding,
                        start = Dimen.mediuPadding,
                        end = Dimen.mediuPadding,
                        bottom = Dimen.largePadding
                    ),
                contentAlignment = Alignment.Center
            ) {
                when (it.iconType) {
                    MainIconType.CHANGE_DATA -> {
                        val modifier = if (downloadState == -2) {
                            Modifier
                                .clip(Shapes.large)
                                .clickable(
                                    onClick = getAction(
                                        coroutineScope,
                                        context,
                                        actions,
                                        it
                                    )
                                )
                                .fillMaxWidth()
                        } else {
                            Modifier
                                .clip(Shapes.large)
                                .fillMaxWidth()
                        }
                        Column(
                            modifier = modifier,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (downloadState == -2) {
                                IconCompose(
                                    data = MainIconType.CHANGE_DATA.icon,
                                    tint = MaterialTheme.colors.primary,
                                    size = Dimen.menuIconSize
                                )
                            } else {
                                Box(contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .size(Dimen.menuIconSize)
                                            .padding(Dimen.smallPadding),
                                        color = MaterialTheme.colors.primary,
                                        strokeWidth = Dimen.lineHeight
                                    )
                                    //显示下载进度
                                    if (downloadState in 1..99) {
                                        Text(
                                            text = downloadState.toString(),
                                            color = MaterialTheme.colors.primary,
                                            style = MaterialTheme.typography.overline
                                        )
                                    }
                                }
                            }
                            CaptionText(
                                text = stringResource(
                                    id = when (downloadState) {
                                        -2 -> R.string.change_db
                                        else -> R.string.checking
                                    }
                                ),
                                modifier = Modifier.padding(top = Dimen.mediuPadding)
                            )
                        }
                    }
                    else -> {
                        MenuItem(coroutineScope, context, actions, it)
                    }
                }

            }

        }
    }
}

@Composable
private fun MenuItem(
    coroutineScope: CoroutineScope,
    context: Context,
    actions: NavActions,
    it: ToolMenuData
) {
    Column(
        modifier = Modifier
            .clip(Shapes.large)
            .clickable(onClick = getAction(coroutineScope, context, actions, it))
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconCompose(data = it.iconType.icon, size = Dimen.menuIconSize)
        CaptionText(
            text = stringResource(id = it.titleId),
            modifier = Modifier.padding(top = Dimen.mediuPadding)
        )
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
            MainIconType.CALENDAR -> actions.toCalendar()
            MainIconType.LEADER -> actions.toLeader()
            MainIconType.EQUIP -> actions.toEquipList()
            MainIconType.TWEET -> actions.toTweetList()
            MainIconType.CHANGE_DATA -> {
                coroutineScope.launch {
                    DatabaseUpdater.changeType()
                }
            }
            MainIconType.COMIC -> actions.toComicList()
            MainIconType.DB_DOWNLOAD -> {
                coroutineScope.launch {
                    DatabaseUpdater.checkDBVersion(0)
                }
            }
            else -> {
            }
        }
    }

}