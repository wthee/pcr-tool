package cn.wthee.pcrtool.ui.home.tool

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.OverviewType
import cn.wthee.pcrtool.data.enums.ToolMenuType
import cn.wthee.pcrtool.data.preferences.MainPreferencesKeys
import cn.wthee.pcrtool.navigation.NavActions
import cn.wthee.pcrtool.ui.LoadingState
import cn.wthee.pcrtool.ui.components.CaptionText
import cn.wthee.pcrtool.ui.components.IconTextButton
import cn.wthee.pcrtool.ui.components.LifecycleEffect
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.StateBox
import cn.wthee.pcrtool.ui.components.VerticalStaggeredGrid
import cn.wthee.pcrtool.ui.home.Section
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.defaultSpring
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.deleteSpace
import cn.wthee.pcrtool.utils.editOrder
import cn.wthee.pcrtool.utils.intArrayList

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
    orderStr: String,
    toolSectionViewModel: ToolSectionViewModel = hiltViewModel()
) {
    val uiState by toolSectionViewModel.uiState.collectAsStateWithLifecycle()
    LifecycleEffect(Lifecycle.Event.ON_CREATE) {
        toolSectionViewModel.getToolOrderData()
    }

    ToolSectionContent(
        uiState = uiState,
        actions = actions,
        isEditMode = isEditMode,
        orderStr = orderStr,
        updateOrderData = updateOrderData,
        updateToolOrderData = toolSectionViewModel::updateToolOrderData,
    )
}

@Composable
private fun ToolSectionContent(
    uiState: ToolSectionUiState,
    actions: NavActions,
    isEditMode: Boolean,
    orderStr: String,
    updateOrderData: (Int) -> Unit,
    updateToolOrderData: (String) -> Unit
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
        ToolMenu(
            toolOrderData = uiState.toolOrderData,
            loadingState = uiState.loadingState,
            actions = actions,
            isEditMode = isEditMode,
            updateToolOrderData = updateToolOrderData
        )
    }
}


/**
 * 菜单
 * @param isEditMode 是否为编辑模式
 */
@Composable
fun ToolMenu(
    toolOrderData: String?,
    loadingState: LoadingState,
    actions: NavActions,
    isEditMode: Boolean = false,
    isHome: Boolean = true,
    updateToolOrderData: (String) -> Unit
) {

    StateBox(
        stateType = loadingState,
        errorContent = {
            if (!isEditMode && isHome) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Dimen.mediumPadding),
                    contentAlignment = Alignment.Center
                ) {
                    IconTextButton(
                        icon = MainIconType.ADD,
                        text = stringResource(R.string.to_add_tool)
                    ) {
                        actions.toToolMore(true)
                    }
                }
            }
        }
    ) {
        val toolList = arrayListOf<ToolMenuData>()
        toolOrderData?.intArrayList?.forEach {
            ToolMenuType.getByValue(it)?.let { toolMenuType ->
                toolList.add(getToolMenuData(toolMenuType = toolMenuType))
            }
        }

        VerticalStaggeredGrid(
            itemWidth = Dimen.menuItemSize,
            contentPadding = Dimen.mediumPadding,
            modifier = Modifier.animateContentSize(defaultSpring())
        ) {
            toolList.forEach {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    MenuItem(
                        actions = actions,
                        toolMenuData = it,
                        isEditMode = isEditMode,
                        updateOrderData = updateToolOrderData
                    )
                }
            }
        }
    }
}

@Composable
fun MenuItem(
    actions: NavActions,
    toolMenuData: ToolMenuData,
    isEditMode: Boolean,
    updateOrderData: ((String) -> Unit)? = null
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

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
                    ) {
                        if (updateOrderData != null) {
                            updateOrderData(it)
                        }
                    }
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
//            ToolMenuType.ALL_SKILL -> actions.toAllSkillList()
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
            ToolMenuType.LOAD_COMIC -> actions.toLoadComicList()
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
        ToolMenuType.COMIC -> ToolMenuData(R.string.comic_4, MainIconType.COMIC)
//        ToolMenuType.ALL_SKILL -> ToolMenuData(R.string.skill, MainIconType.SKILL_LOOP)
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

        ToolMenuType.LOAD_COMIC -> ToolMenuData(
            R.string.tool_load_comic,
            MainIconType.LOAD_COMIC
        )
    }
    //设置模块类别
    tool.type = toolMenuType
    return tool
}


@CombinedPreviews
@Composable
private fun ToolSectionContentPreview() {
    PreviewLayout {
        ToolSectionContent(
            uiState = ToolSectionUiState(
                toolOrderData = """
                    ${ToolMenuType.BIRTHDAY.id}-
                    ${ToolMenuType.PVP_SEARCH.id}-
                    ${ToolMenuType.ALL_EQUIP.id}-
                    ${ToolMenuType.GACHA.id}-
                    ${ToolMenuType.FREE_GACHA.id}-
                    ${ToolMenuType.GUILD.id}-
                """.deleteSpace,
                loadingState = LoadingState.Success
            ),
            actions = NavActions(NavHostController(LocalContext.current)),
            isEditMode = false,
            orderStr = "${OverviewType.TOOL.id}",
            updateOrderData = { },
            updateToolOrderData = {}
        )

        //未添加功能时
        ToolSectionContent(
            uiState = ToolSectionUiState(
                toolOrderData = "",
                loadingState = LoadingState.Error
            ),
            actions = NavActions(NavHostController(LocalContext.current)),
            isEditMode = false,
            orderStr = "${OverviewType.TOOL.id}",
            updateOrderData = { },
            updateToolOrderData = {}
        )
    }
}