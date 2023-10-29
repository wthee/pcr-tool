package cn.wthee.pcrtool.ui.home.module

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.OverviewType
import cn.wthee.pcrtool.data.enums.ToolMenuType
import cn.wthee.pcrtool.data.preferences.MainPreferencesKeys
import cn.wthee.pcrtool.navigation.NavActions
import cn.wthee.pcrtool.ui.components.CaptionText
import cn.wthee.pcrtool.ui.components.IconTextButton
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.VerticalGrid
import cn.wthee.pcrtool.ui.dataStoreMain
import cn.wthee.pcrtool.ui.home.Section
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.defaultSpring
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.editOrder
import cn.wthee.pcrtool.utils.intArrayList
import kotlinx.coroutines.flow.map

data class ToolMenuData(
    @StringRes val titleId: Int,
    val iconType: MainIconType,
    var type: ToolMenuType = ToolMenuType.CHARACTER
)


/**
 * 功能模块
 */
@Composable
fun ToolSection(
    updateOrderData: (Int) -> Unit,
    actions: NavActions,
    isEditMode: Boolean,
    orderStr: String
) {
    val id = OverviewType.TOOL.id

    Section(
        id = id,
        titleId = R.string.function,
        iconType = MainIconType.FUNCTION,
        isEditMode = isEditMode,
        orderStr = orderStr,
        onClick = {
            if (isEditMode) {
                updateOrderData(id)
            } else {
                actions.toToolMore(false)
            }
        }
    ) {
        ToolMenu(actions = actions)
    }
}


/**
 * 菜单
 * @param isEditMode 是否为编辑模式
 */
@Composable
fun ToolMenu(
    actions: NavActions,
    isEditMode: Boolean = false,
    isHome: Boolean = true
) {

    val context = LocalContext.current

    //自定义显示
    val toolOrderData = remember {
        context.dataStoreMain.data.map {
            it[MainPreferencesKeys.SP_TOOL_ORDER] ?: ""
        }
    }.collectAsState(initial = "").value

    val toolList = arrayListOf<ToolMenuData>()
    toolOrderData.intArrayList.forEach {
        ToolMenuType.getByValue(it)?.let { toolMenuType ->
            toolList.add(getToolMenuData(toolMenuType = toolMenuType))
        }
    }

    if (toolList.isEmpty() && !isEditMode && isHome) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            IconTextButton(icon = MainIconType.MAIN, text = stringResource(R.string.to_add_tool)) {
                actions.toToolMore(true)
            }
        }
    }

    VerticalGrid(
        itemWidth = Dimen.menuItemSize,
        contentPadding = Dimen.largePadding + Dimen.mediumPadding,
        modifier = Modifier.animateContentSize(defaultSpring())
    ) {
        toolList.forEach {
            Box(
                modifier = Modifier
                    .padding(
                        top = Dimen.mediumPadding,
                        bottom = Dimen.largePadding
                    )
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                MenuItem(context, actions, it, isEditMode)
            }
        }
    }
}

@Composable
fun MenuItem(
    context: Context,
    actions: NavActions,
    toolMenuData: ToolMenuData,
    isEditMode: Boolean
) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable {
                VibrateUtil(context).single()
                if (isEditMode) {
                    // 点击移除
                    editOrder(
                        context,
                        scope,
                        toolMenuData.type.id,
                        MainPreferencesKeys.SP_TOOL_ORDER
                    )
                } else {
                    getAction(actions, toolMenuData)()
                }
            }
            .defaultMinSize(minWidth = Dimen.menuItemSize)
            .padding(Dimen.smallPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MainIcon(data = toolMenuData.iconType, size = Dimen.menuIconSize)
        CaptionText(
            text = stringResource(id = toolMenuData.titleId),
            modifier = Modifier.padding(top = Dimen.mediumPadding),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * 菜单跳转
 */
fun getAction(
    actions: NavActions,
    tool: ToolMenuData
): () -> Unit {

    return {
        when (tool.type) {
            ToolMenuType.CHARACTER -> actions.toCharacterList()
            ToolMenuType.GACHA -> actions.toGacha()
            ToolMenuType.CLAN -> actions.toClan()
            ToolMenuType.STORY_EVENT -> actions.toStoryEvent()
            ToolMenuType.GUILD -> actions.toGuild()
            ToolMenuType.PVP_SEARCH -> actions.toPvp()
            ToolMenuType.LEADER -> actions.toLeader()
            ToolMenuType.EQUIP -> actions.toEquipList()
            ToolMenuType.TWEET -> actions.toTweetList()
            ToolMenuType.COMIC -> actions.toComicList()
            ToolMenuType.ALL_SKILL -> actions.toAllSkillList()
            ToolMenuType.ALL_EQUIP -> actions.toAllEquipList()
            ToolMenuType.RANDOM_AREA -> actions.toRandomEquipArea(0)
            ToolMenuType.NEWS -> actions.toNews()
            ToolMenuType.FREE_GACHA -> actions.toFreeGacha()
            ToolMenuType.MOCK_GACHA -> actions.toMockGacha()
            ToolMenuType.BIRTHDAY -> actions.toBirthdayList()
            ToolMenuType.CALENDAR_EVENT -> actions.toCalendarEventList()
            ToolMenuType.EXTRA_EQUIP -> actions.toExtraEquipList()
            ToolMenuType.TRAVEL_AREA -> actions.toExtraEquipTravelAreaList()
            ToolMenuType.WEBSITE -> actions.toWebsiteList()
            ToolMenuType.LEADER_TIER -> actions.toLeaderTier()
            ToolMenuType.ALL_QUEST -> actions.toAllQuest()
            ToolMenuType.UNIQUE_EQUIP -> actions.toUniqueEquipList()
        }
    }

}

/**
 * 获取菜单数据
 */
fun getToolMenuData(toolMenuType: ToolMenuType): ToolMenuData {
    val tool = when (toolMenuType) {
        ToolMenuType.CHARACTER -> ToolMenuData(R.string.character, MainIconType.CHARACTER)
        ToolMenuType.EQUIP -> ToolMenuData(R.string.tool_equip, MainIconType.EQUIP)
        ToolMenuType.GUILD -> ToolMenuData(R.string.tool_guild, MainIconType.GUILD)
        ToolMenuType.CLAN -> ToolMenuData(R.string.tool_clan, MainIconType.CLAN)
        ToolMenuType.RANDOM_AREA -> ToolMenuData(R.string.random_area, MainIconType.RANDOM_AREA)
        ToolMenuType.GACHA -> ToolMenuData(R.string.tool_gacha, MainIconType.GACHA)
        ToolMenuType.STORY_EVENT -> ToolMenuData(R.string.tool_event, MainIconType.EVENT)
        ToolMenuType.NEWS -> ToolMenuData(R.string.tool_news, MainIconType.NEWS)
        ToolMenuType.FREE_GACHA -> ToolMenuData(R.string.tool_free_gacha, MainIconType.FREE_GACHA)
        ToolMenuType.PVP_SEARCH -> ToolMenuData(R.string.tool_pvp, MainIconType.PVP_SEARCH)
        ToolMenuType.LEADER -> ToolMenuData(R.string.tool_leader, MainIconType.LEADER)
        ToolMenuType.TWEET -> ToolMenuData(R.string.tweet, MainIconType.TWEET)
        ToolMenuType.COMIC -> ToolMenuData(R.string.comic, MainIconType.COMIC)
        ToolMenuType.ALL_SKILL -> ToolMenuData(R.string.skill, MainIconType.SKILL_LOOP)
        ToolMenuType.ALL_EQUIP -> ToolMenuData(R.string.calc_equip_count, MainIconType.EQUIP_CALC)
        ToolMenuType.MOCK_GACHA -> ToolMenuData(R.string.tool_mock_gacha, MainIconType.MOCK_GACHA)
        ToolMenuType.BIRTHDAY -> ToolMenuData(R.string.tool_birthday, MainIconType.BIRTHDAY)
        ToolMenuType.CALENDAR_EVENT -> ToolMenuData(
            R.string.tool_calendar_event,
            MainIconType.CALENDAR
        )

        ToolMenuType.EXTRA_EQUIP -> ToolMenuData(
            R.string.tool_extra_equip,
            MainIconType.EXTRA_EQUIP
        )

        ToolMenuType.TRAVEL_AREA -> ToolMenuData(
            R.string.tool_travel,
            MainIconType.EXTRA_EQUIP_DROP
        )

        ToolMenuType.WEBSITE -> ToolMenuData(R.string.tool_website, MainIconType.WEBSITE_BOOKMARK)
        ToolMenuType.LEADER_TIER -> ToolMenuData(
            R.string.tool_leader_tier,
            MainIconType.LEADER_TIER
        )

        ToolMenuType.ALL_QUEST -> ToolMenuData(
            R.string.tool_all_quest,
            MainIconType.ALL_QUEST
        )

        ToolMenuType.UNIQUE_EQUIP -> ToolMenuData(
            R.string.tool_unique_equip,
            MainIconType.UNIQUE_EQUIP
        )
    }
    //设置模块类别
    tool.type = toolMenuType
    return tool
}