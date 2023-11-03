package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.ToolMenuType
import cn.wthee.pcrtool.data.preferences.MainPreferencesKeys
import cn.wthee.pcrtool.navigation.NavActions
import cn.wthee.pcrtool.ui.components.CaptionText
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.components.Subtitle2
import cn.wthee.pcrtool.ui.components.VerticalGrid
import cn.wthee.pcrtool.ui.dataStoreMain
import cn.wthee.pcrtool.ui.home.module.ToolMenu
import cn.wthee.pcrtool.ui.home.module.ToolMenuData
import cn.wthee.pcrtool.ui.home.module.getAction
import cn.wthee.pcrtool.ui.home.module.getToolMenuData
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.ExpandAnimation
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.colorWhite
import cn.wthee.pcrtool.ui.theme.shapeTop
import cn.wthee.pcrtool.utils.editOrder
import cn.wthee.pcrtool.utils.intArrayList
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * 功能分组
 */
private data class ToolMenuGroup(
    val groupTitle: String,
    val toolList: List<ToolMenuData>,
    val groupDesc: String = ""
)

/**
 * 全部工具
 */
@Composable
fun AllToolMenu(initEditMode: Boolean, scrollState: LazyListState, actions: NavActions) {

    val coroutineScope = rememberCoroutineScope()

    //编辑模式
    var isEditMode by remember {
        mutableStateOf(initEditMode)
    }

    val itemGroupList = arrayListOf<ToolMenuGroup>()

    //游戏数据
    val dataList = arrayListOf<ToolMenuData>()
    dataList.addItem(ToolMenuType.CHARACTER)
    dataList.addItem(ToolMenuType.EQUIP)
    dataList.addItem(ToolMenuType.GUILD)
    dataList.addItem(ToolMenuType.CLAN)
    dataList.addItem(ToolMenuType.EXTRA_EQUIP)
    dataList.addItem(ToolMenuType.TRAVEL_AREA)
    dataList.addItem(ToolMenuType.UNIQUE_EQUIP)
    itemGroupList.add(ToolMenuGroup(stringResource(id = R.string.basic_info), dataList))

    //查询
    val searchList = arrayListOf<ToolMenuData>()
    searchList.addItem(ToolMenuType.PVP_SEARCH)
    searchList.addItem(ToolMenuType.NEWS)
    searchList.addItem(ToolMenuType.LEADER)
    searchList.addItem(ToolMenuType.LEADER_TIER)
    searchList.addItem(ToolMenuType.RANDOM_AREA)
    searchList.addItem(ToolMenuType.WEBSITE)
    searchList.addItem(ToolMenuType.TWEET)
    searchList.addItem(ToolMenuType.COMIC)
    itemGroupList.add(
        ToolMenuGroup(
            stringResource(id = R.string.search_api),
            searchList,
            stringResource(id = R.string.search_api_desc)
        )
    )

    //活动信息
    val infoList = arrayListOf<ToolMenuData>()
    infoList.addItem(ToolMenuType.GACHA)
    infoList.addItem(ToolMenuType.STORY_EVENT)
    infoList.addItem(ToolMenuType.FREE_GACHA)
    infoList.addItem(ToolMenuType.BIRTHDAY)
    infoList.addItem(ToolMenuType.CALENDAR_EVENT)
    itemGroupList.add(ToolMenuGroup(stringResource(id = R.string.activity_info), infoList))

    //其它
    val otherList = arrayListOf<ToolMenuData>()
    otherList.addItem(ToolMenuType.MOCK_GACHA)
    if (BuildConfig.DEBUG) {
        otherList.addItem(ToolMenuType.ALL_SKILL)
    }
    itemGroupList.add(ToolMenuGroup(stringResource(id = R.string.other), otherList))

    //测试
    val betaList = arrayListOf<ToolMenuData>()
    betaList.addItem(ToolMenuType.ALL_EQUIP)
    betaList.addItem(ToolMenuType.ALL_QUEST)
    itemGroupList.add(
        ToolMenuGroup(
            stringResource(id = R.string.beta_tool_group),
            betaList,
            stringResource(id = R.string.beta_tool_group_title)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            //预览
            ExpandAnimation(visible = isEditMode) {
                Column(
                    modifier = Modifier
                        .padding(
                            vertical = Dimen.mediumPadding
                        )
                        .fillMaxWidth()
                ) {
                    ToolMenu(actions = actions, isEditMode = isEditMode, isHome = false)
                    //编辑提示
                    Subtitle2(
                        text = stringResource(R.string.tip_click_to_add),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = Dimen.mediumPadding)

                    )
                }
            }

            //全部功能
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .shadow(
                        elevation = if (isEditMode) Dimen.cardElevation else 0.dp,
                        shape = shapeTop()
                    )
                    .background(
                        shape = if (isEditMode) shapeTop() else RoundedCornerShape(0.dp),
                        color = if (isEditMode) MaterialTheme.colorScheme.surface else Color.Transparent
                    ),
                state = scrollState
            ) {
                items(
                    items = itemGroupList,
                    key = {
                        it.groupTitle
                    }
                ) {
                    MenuGroup(
                        actions = actions,
                        group = it,
                        isEditMode = isEditMode
                    )
                }
                item {
                    CommonSpacer()
                }
            }
        }

        //编辑
        MainSmallFab(
            iconType = if (isEditMode) MainIconType.OK else MainIconType.EDIT_TOOL,
            text = stringResource(id = if (isEditMode) R.string.done else R.string.edit),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
        ) {
            coroutineScope.launch {
                isEditMode = !isEditMode
            }
        }
    }


}

/**
 * 菜单组
 */
@Composable
private fun MenuGroup(
    actions: NavActions,
    group: ToolMenuGroup,
    isEditMode: Boolean
) {
    val context = LocalContext.current
    val toolOrderData = remember {
        context.dataStoreMain.data.map {
            it[MainPreferencesKeys.SP_TOOL_ORDER] ?: ""
        }
    }.collectAsState(initial = "").value

    Column(
        modifier = Modifier
            .padding(horizontal = Dimen.mediumPadding)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MainText(
            text = group.groupTitle,
            modifier = Modifier.padding(top = Dimen.largePadding)
        )
        if (group.groupDesc != "") {
            CaptionText(text = group.groupDesc)
        }
        VerticalGrid(
            itemWidth = (Dimen.iconSize * 3),
            modifier = Modifier.padding(top = Dimen.mediumPadding, bottom = Dimen.largePadding)
        ) {
            group.toolList.forEach {
                MenuItem(actions, it, toolOrderData, isEditMode)
            }
        }
    }
}

@Composable
private fun MenuItem(
    actions: NavActions,
    toolMenuData: ToolMenuData,
    orderStr: String,
    isEditMode: Boolean
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val hasAdded = orderStr.intArrayList.contains(toolMenuData.type.id)


    MainCard(
        modifier = Modifier.padding(Dimen.mediumPadding),
        onClick = if (isEditMode) {
            {
                editOrder(
                    context,
                    scope,
                    toolMenuData.type.id,
                    MainPreferencesKeys.SP_TOOL_ORDER
                )
            }
        } else {
            getAction(actions, toolMenuData)
        },
        containerColor = if (hasAdded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .defaultMinSize(minWidth = Dimen.menuItemSize)
                .padding(horizontal = Dimen.smallPadding, vertical = Dimen.mediumPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MainIcon(
                modifier = Modifier.padding(start = Dimen.mediumPadding),
                data = toolMenuData.iconType,
                size = Dimen.mediumIconSize,
                tint = if (hasAdded) colorWhite else MaterialTheme.colorScheme.primary
            )
            Subtitle2(
                text = stringResource(id = toolMenuData.titleId),
                modifier = Modifier.padding(start = Dimen.largePadding),
                color = if (hasAdded) colorWhite else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * 列表扩展函数
 */
private fun ArrayList<ToolMenuData>.addItem(toolMenuType: ToolMenuType) {
    this.add(getToolMenuData(toolMenuType = toolMenuType))
}


@CombinedPreviews
@Composable
private fun MenuGroupPreview() {
    val menu = ToolMenuData(
        R.string.tool_mock_gacha,
        MainIconType.MOCK_GACHA,
        ToolMenuType.MOCK_GACHA
    )

    PreviewLayout {
        MenuGroup(
            actions = NavActions(rememberNavController()),
            group = ToolMenuGroup(
                stringResource(id = R.string.debug_short_text),
                arrayListOf(menu, menu, menu),
                stringResource(id = R.string.debug_short_text),
            ),
            isEditMode = true
        )
    }
}