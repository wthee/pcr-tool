package cn.wthee.pcrtool.ui.home

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.NavActions
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.VibrateUtil
import coil.annotation.ExperimentalCoilApi
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
@ExperimentalCoilApi
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
        ToolMenuData(R.string.comic, MainIconType.COMIC),
    )
    if (BuildConfig.debug) {
        list.add(ToolMenuData(R.string.redownload_db, MainIconType.DB_DOWNLOAD))
        list.add(ToolMenuData(R.string.skill, MainIconType.SKILL_LOOP))
    }

    VerticalGrid(
        maxColumnWidth = Dimen.toolMenuWidth,
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
                        FadeAnimation(downloadState == -2) {
                            MenuItem(coroutineScope, context, actions, it)
                        }
                    }
                    MainIconType.SKILL_LOOP -> {
                        if (BuildConfig.DEBUG) {
                            MenuItem(coroutineScope, context, actions, it)
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

@ExperimentalCoilApi
@Composable
private fun MenuItem(
    coroutineScope: CoroutineScope,
    context: Context,
    actions: NavActions,
    it: ToolMenuData
) {
    Column(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = getAction(coroutineScope, context, actions, it))
            .defaultMinSize(minWidth = Dimen.menuItemSize)
            .padding(Dimen.smallPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconCompose(data = it.iconType.icon, size = Dimen.menuIconSize)
        CaptionText(
            text = stringResource(id = it.titleId),
            modifier = Modifier.padding(top = Dimen.mediumPadding)
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
            MainIconType.LEADER -> actions.toLeader()
            MainIconType.EQUIP -> actions.toEquipList()
            MainIconType.TWEET -> actions.toTweetList()
            MainIconType.CHANGE_DATA -> {
                MainActivity.navViewModel.openChangeDataDialog.postValue(true)
            }
            MainIconType.COMIC -> actions.toComicList()
            MainIconType.DB_DOWNLOAD -> {
                coroutineScope.launch {
                    DatabaseUpdater.checkDBVersion(0)
                }
            }
            MainIconType.SKILL_LOOP -> {
                actions.toAllSkillList()
            }
            else -> {
            }
        }
    }

}