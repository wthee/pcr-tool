package cn.wthee.pcrtool.ui.home.module

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.edit
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.OverviewType
import cn.wthee.pcrtool.data.enums.ToolMenuType
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.NavActions
import cn.wthee.pcrtool.ui.common.CaptionText
import cn.wthee.pcrtool.ui.common.IconCompose
import cn.wthee.pcrtool.ui.common.IconTextButton
import cn.wthee.pcrtool.ui.common.VerticalGrid
import cn.wthee.pcrtool.ui.home.Section
import cn.wthee.pcrtool.ui.home.editOverviewMenuOrder
import cn.wthee.pcrtool.ui.mainSP
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.defaultSpring
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.VibrateUtil
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
    actions: NavActions,
    isEditMode: Boolean,
) {
    val id = OverviewType.TOOL.id
    Section(
        id = id,
        titleId = R.string.function,
        iconType = MainIconType.FUNCTION,
        isEditMode = isEditMode,
        onClick = {
            if (isEditMode)
                editOverviewMenuOrder(id)
            else
                actions.toToolMore(false)
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
fun ToolMenu(actions: NavActions, isEditMode: Boolean = false, isHome: Boolean = true) {

    val context = LocalContext.current
    val sp = mainSP()

    //自定义显示
    var localData = sp.getString(Constants.SP_TOOL_ORDER, "") ?: ""
    //修复自定义错乱问题：3.4.0更新 id 后，清空旧的 id
    localData.intArrayList.forEach {
        if (it < 200) {
            sp.edit {
                putString(Constants.SP_TOOL_ORDER, "")
                //更新
                localData = ""
                navViewModel.toolOrderData.postValue("")
            }
            return@forEach
        }
    }

    var toolOrderData = navViewModel.toolOrderData.observeAsState().value
    if (toolOrderData == null || toolOrderData.isEmpty()) {
        toolOrderData = localData
        navViewModel.toolOrderData.postValue(toolOrderData)
    }

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
        maxColumnWidth = Dimen.toolMenuWidth,
        modifier = Modifier.animateContentSize(defaultSpring())
    ) {
        toolList.forEach {
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
                MenuItem(context, actions, it, isEditMode)
            }
        }
    }
}

@Composable
private fun MenuItem(
    context: Context,
    actions: NavActions,
    toolMenuData: ToolMenuData,
    isEditMode: Boolean
) {
    Column(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable {
                VibrateUtil(context).single()
                if (isEditMode) {
                    // 点击移除
                    editToolMenuOrder(toolMenuData.type.id)
                } else {
                    getAction(actions, toolMenuData)()
                }
            }
            .defaultMinSize(minWidth = Dimen.menuItemSize)
            .padding(Dimen.smallPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconCompose(data = toolMenuData.iconType, size = Dimen.menuIconSize)
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
            ToolMenuType.EVENT -> actions.toEvent()
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
        }
    }

}

/**
 * 获取菜单数据
 */
@Composable
fun getToolMenuData(toolMenuType: ToolMenuType): ToolMenuData {
    val tool = when (toolMenuType) {
        ToolMenuType.CHARACTER -> ToolMenuData(R.string.character, MainIconType.CHARACTER)
        ToolMenuType.EQUIP -> ToolMenuData(R.string.tool_equip, MainIconType.EQUIP)
        ToolMenuType.GUILD -> ToolMenuData(R.string.tool_guild, MainIconType.GUILD)
        ToolMenuType.CLAN -> ToolMenuData(R.string.tool_clan, MainIconType.CLAN)
        ToolMenuType.RANDOM_AREA -> ToolMenuData(R.string.random_area, MainIconType.RANDOM_AREA)
        ToolMenuType.GACHA -> ToolMenuData(R.string.tool_gacha, MainIconType.GACHA)
        ToolMenuType.EVENT -> ToolMenuData(R.string.tool_event, MainIconType.EVENT)
        ToolMenuType.NEWS -> ToolMenuData(R.string.tool_news, MainIconType.NEWS)
        ToolMenuType.FREE_GACHA -> ToolMenuData(R.string.tool_free_gacha, MainIconType.FREE_GACHA)
        ToolMenuType.PVP_SEARCH -> ToolMenuData(R.string.tool_pvp, MainIconType.PVP_SEARCH)
        ToolMenuType.LEADER -> ToolMenuData(R.string.tool_leader, MainIconType.LEADER)
        ToolMenuType.TWEET -> ToolMenuData(R.string.tweet, MainIconType.TWEET)
        ToolMenuType.COMIC -> ToolMenuData(R.string.comic, MainIconType.COMIC)
        ToolMenuType.ALL_SKILL -> ToolMenuData(R.string.skill, MainIconType.SKILL_LOOP)
        ToolMenuType.ALL_EQUIP -> ToolMenuData(R.string.tool_equip, MainIconType.EQUIP_CALC)
        ToolMenuType.MOCK_GACHA -> ToolMenuData(R.string.tool_mock_gacha, MainIconType.MOCK_GACHA)
        ToolMenuType.BIRTHDAY -> ToolMenuData(R.string.tool_birthday, MainIconType.BIRTHDAY)
        ToolMenuType.CALENDAR_EVENT -> ToolMenuData(R.string.tool_calendar_event, MainIconType.CALENDAR)
        ToolMenuType.EXTRA_EQUIP -> ToolMenuData(
            R.string.tool_extra_equip,
            MainIconType.EXTRA_EQUIP
        )
        ToolMenuType.TRAVEL_AREA -> ToolMenuData(
            R.string.tool_travel,
            MainIconType.EXTRA_EQUIP_DROP
        )

    }
    //设置模块类别
    tool.type = toolMenuType
    return tool
}

/**
 * 编辑模块排序
 */
fun editToolMenuOrder(id: Int) {
    val sp = mainSP()
    val orderStr = sp.getString(Constants.SP_TOOL_ORDER, "") ?: ""
    val idStr = "$id-"
    val hasAdded = orderStr.intArrayList.contains(id)

    //新增或移除
    val edited = if (!hasAdded) {
        orderStr + idStr
    } else {
        orderStr.replace(idStr, "")
    }
    sp.edit {
        putString(Constants.SP_TOOL_ORDER, edited)
        //更新
        navViewModel.toolOrderData.postValue(edited)
    }
}
