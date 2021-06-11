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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.NavActions
import cn.wthee.pcrtool.ui.compose.CaptionText
import cn.wthee.pcrtool.ui.compose.IconCompose
import cn.wthee.pcrtool.ui.compose.VerticalGrid
import cn.wthee.pcrtool.ui.compose.defaultSpring
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shapes
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.vibrate
import java.util.*

data class ToolMenuData(
    @StringRes val titleId: Int,
    val iconType: MainIconType,
    val regionForNews: Int = 0
)

enum class ToolMenuState {
    FLOD, EXPAND
}

/**
 * 菜单
 * fixme 公告优化
 */
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun ToolMenu(
    actions: NavActions, state: MutableState<ToolMenuState> = remember {
        mutableStateOf(ToolMenuState.FLOD)
    }
) {
    val context = LocalContext.current
    val updateApp = MainActivity.noticeViewModel.updateApp.observeAsState().value ?: -1

    val list = arrayListOf(
        ToolMenuData(R.string.character, MainIconType.CHARACTER),
        ToolMenuData(R.string.tool_equip, MainIconType.EQUIP),
        ToolMenuData(R.string.tool_pvp, MainIconType.PVP_SEARCH),
        ToolMenuData(R.string.tool_calendar, MainIconType.CALENDAR),
        ToolMenuData(R.string.tool_news_cn, MainIconType.NEWS, 2),
        ToolMenuData(R.string.tool_news_tw, MainIconType.NEWS, 3),
        ToolMenuData(R.string.tool_news_jp, MainIconType.NEWS, 4),
        ToolMenuData(R.string.app_notice, MainIconType.NOTICE),
        ToolMenuData(R.string.tool_gacha, MainIconType.GACHA),
        ToolMenuData(R.string.tool_clan, MainIconType.CLAN),
        ToolMenuData(R.string.tool_event, MainIconType.EVENT),
        ToolMenuData(R.string.tool_guild, MainIconType.GUILD),
        ToolMenuData(R.string.tool_leader, MainIconType.LEADER),
    )
    val newlist: ArrayList<ToolMenuData>
    if (state.value == ToolMenuState.FLOD) {
        newlist = arrayListOf()
        newlist.addAll(list.subList(0, 8))
    } else {
        newlist = list
    }
    VerticalGrid(
        maxColumnWidth = Dimen.toolMenuWidth,
        modifier = Modifier.animateContentSize(defaultSpring())
    ) {
        newlist.forEach {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = Dimen.mediuPadding,
                        start = Dimen.mediuPadding,
                        end = Dimen.mediuPadding
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (it.iconType != MainIconType.NOTICE) {
                    Column(
                        modifier = Modifier
                            .clip(Shapes.large)
                            .clickable(onClick = getAction(context, actions, it))
                            .padding(Dimen.smallPadding)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconCompose(data = it.iconType.icon, size = Dimen.menuIconSize)
                        CaptionText(
                            text = stringResource(id = it.titleId),
                            modifier = Modifier.padding(top = Dimen.mediuPadding)
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .clip(Shapes.large)
                            .clickable {
                                VibrateUtil(context).single()
                                actions.toNotice()
                            }
                            .padding(Dimen.smallPadding)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (updateApp == -1) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(Dimen.menuIconSize)
                                    .padding(Dimen.smallPadding),
                                color = MaterialTheme.colors.primary,
                                strokeWidth = Dimen.lineHeight
                            )
                        } else {
                            IconCompose(
                                data = if (updateApp == 1) MainIconType.APP_UPDATE.icon else MainIconType.NOTICE.icon,
                                tint = if (updateApp == 1) colorResource(id = R.color.color_rank_21) else MaterialTheme.colors.primary,
                                size = Dimen.menuIconSize
                            )
                        }
                        CaptionText(
                            text = stringResource(
                                id = when (updateApp) {
                                    0 -> R.string.app_notice
                                    1 -> R.string.to_update
                                    else -> R.string.checking
                                }
                            ),
                            modifier = Modifier.padding(top = Dimen.mediuPadding)
                        )
                    }
                }

            }

        }
    }
}

private fun getAction(context: Context, actions: NavActions, tool: ToolMenuData): () -> Unit {
    return {}.vibrate {
        VibrateUtil(context).single()
        when (tool.iconType) {
            MainIconType.CHARACTER -> actions.toCharacterList()
            MainIconType.GACHA -> actions.toGacha()
            MainIconType.CLAN -> actions.toClan()
            MainIconType.EVENT -> actions.toEvent()
            MainIconType.GUILD -> actions.toGuild()
            MainIconType.NEWS -> actions.toNews(tool.regionForNews)
            MainIconType.PVP_SEARCH -> actions.toPvp()
            MainIconType.CALENDAR -> actions.toCalendar()
            MainIconType.LEADER -> actions.toLeader()
            MainIconType.EQUIP -> actions.toEquipList()
            else -> {
            }
        }
    }

}