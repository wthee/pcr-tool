package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.ToolMenuType
import cn.wthee.pcrtool.data.preferences.MainPreferencesKeys
import cn.wthee.pcrtool.navigation.NavActions
import cn.wthee.pcrtool.ui.LoadState
import cn.wthee.pcrtool.ui.components.CaptionText
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.LifecycleEffect
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.components.Subtitle2
import cn.wthee.pcrtool.ui.components.VerticalGridList
import cn.wthee.pcrtool.ui.dataStoreMain
import cn.wthee.pcrtool.ui.home.tool.ToolMenu
import cn.wthee.pcrtool.ui.home.tool.ToolSectionViewModel
import cn.wthee.pcrtool.ui.home.tool.getAction
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.ExpandAnimation
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.colorWhite
import cn.wthee.pcrtool.ui.theme.noShape
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
    val toolList: List<ToolMenuType>,
    val groupDesc: String = ""
)

/**
 * 全部工具
 *
 * 新增工具步骤：
 * 0、完成工具页面功能
 * 1、[ToolMenuType]中添加枚举
 * 2、[AllToolMenuScreen]设置分组
 * 3、[NavActions]添加action、[NavGraph]添加页面
 * 4、[ToolSection]添加跳转
 */
@Composable
fun AllToolMenuScreen(
    initEditMode: Boolean,
    actions: NavActions,
    toolSectionViewModel: ToolSectionViewModel = hiltViewModel()
) {

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()

    //编辑模式
    var isEditMode by remember {
        mutableStateOf(initEditMode)
    }

    val itemGroupList = arrayListOf<ToolMenuGroup>()

    //游戏数据
    val dataList = arrayListOf<ToolMenuType>()
    dataList.add(ToolMenuType.CHARACTER)
    dataList.add(ToolMenuType.EQUIP)
    dataList.add(ToolMenuType.GUILD)
    dataList.add(ToolMenuType.CLAN)
    dataList.add(ToolMenuType.EXTRA_EQUIP)
    dataList.add(ToolMenuType.TRAVEL_AREA)
    dataList.add(ToolMenuType.UNIQUE_EQUIP)
    dataList.add(ToolMenuType.TALENT_LIST)
    dataList.add(ToolMenuType.TALENT_QUEST)
    dataList.add(ToolMenuType.ROLE)
    itemGroupList.add(ToolMenuGroup(stringResource(id = R.string.basic_info), dataList))

    //查询
    val searchList = arrayListOf<ToolMenuType>()
    searchList.add(ToolMenuType.PVP_SEARCH)
    searchList.add(ToolMenuType.NEWS)
    searchList.add(ToolMenuType.LEADER)
    searchList.add(ToolMenuType.LEADER_TIER)
    searchList.add(ToolMenuType.RANDOM_AREA)
    searchList.add(ToolMenuType.WEBSITE)
    searchList.add(ToolMenuType.TWEET)
    searchList.add(ToolMenuType.COMIC)
    searchList.add(ToolMenuType.LOAD_COMIC)
    itemGroupList.add(
        ToolMenuGroup(
            stringResource(id = R.string.search_api),
            searchList,
            stringResource(id = R.string.search_api_desc)
        )
    )

    //活动信息
    val infoList = arrayListOf<ToolMenuType>()
    infoList.add(ToolMenuType.GACHA)
    infoList.add(ToolMenuType.STORY_EVENT)
    infoList.add(ToolMenuType.FREE_GACHA)
    infoList.add(ToolMenuType.BIRTHDAY)
    infoList.add(ToolMenuType.CALENDAR_EVENT)
    itemGroupList.add(ToolMenuGroup(stringResource(id = R.string.activity_info), infoList))

    //其它
    val otherList = arrayListOf<ToolMenuType>()
    otherList.add(ToolMenuType.MOCK_GACHA)
    otherList.add(ToolMenuType.ALL_QUEST)
    otherList.add(ToolMenuType.ALL_EQUIP)
    itemGroupList.add(ToolMenuGroup(stringResource(id = R.string.other), otherList))

    //测试
    if (BuildConfig.DEBUG) {
        val betaList = arrayListOf<ToolMenuType>()
        betaList.add(ToolMenuType.UNKNOWN_SKILL_LIST)
        itemGroupList.add(
            ToolMenuGroup(
                stringResource(id = R.string.beta_tool_group),
                betaList
            )
        )
    }


    val uiState by toolSectionViewModel.uiState.collectAsStateWithLifecycle()
    LifecycleEffect(Lifecycle.Event.ON_CREATE) {
        toolSectionViewModel.getToolOrderData()
    }

    MainScaffold(
        fab = {
            //编辑
            MainSmallFab(
                iconType = if (isEditMode) MainIconType.OK else MainIconType.EDIT_TOOL,
                text = stringResource(id = if (isEditMode) R.string.done else R.string.edit),
                onClick = {
                    coroutineScope.launch {
                        isEditMode = !isEditMode
                    }
                }
            )
        }
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
                    ToolMenu(
                        toolOrderData = uiState.toolOrderData,
                        loadState = LoadState.Success,
                        actions = actions,
                        isEditMode = isEditMode,
                        isHome = false,
                        updateToolOrderData = toolSectionViewModel::updateToolOrderData
                    )
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
                        shape = if (isEditMode) shapeTop() else noShape(),
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
                        isEditMode = isEditMode,
                        updateOrderData = toolSectionViewModel::updateToolOrderData
                    )
                }
                item {
                    CommonSpacer()
                }
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
    isEditMode: Boolean,
    updateOrderData: (String) -> Unit
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
        VerticalGridList(
            modifier = Modifier.padding(top = Dimen.mediumPadding, bottom = Dimen.largePadding),
            itemCount = group.toolList.size,
            itemWidth = Dimen.iconSize * 3,
            contentPadding = Dimen.mediumPadding
        ) {
            MenuItem(
                actions = actions,
                toolMenuType = group.toolList[it],
                orderStr = toolOrderData,
                isEditMode = isEditMode,
                updateOrderData = updateOrderData
            )
        }
    }
}

@Composable
private fun MenuItem(
    actions: NavActions,
    toolMenuType: ToolMenuType,
    orderStr: String,
    isEditMode: Boolean,
    updateOrderData: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val hasAdded = orderStr.intArrayList.contains(toolMenuType.id)


    MainCard(
        onClick = if (isEditMode) {
            {
                editOrder(
                    context,
                    scope,
                    toolMenuType.id,
                    MainPreferencesKeys.SP_TOOL_ORDER
                ) {
                    updateOrderData(it)
                }
            }
        } else {
            getAction(actions, toolMenuType)
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
                data = toolMenuType.iconType,
                size = Dimen.mediumIconSize,
                tint = if (hasAdded) colorWhite else MaterialTheme.colorScheme.primary
            )
            Subtitle2(
                text = stringResource(id = toolMenuType.titleId),
                modifier = Modifier.padding(start = Dimen.largePadding),
                color = if (hasAdded) colorWhite else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}


@CombinedPreviews
@Composable
private fun MenuGroupPreview() {


    PreviewLayout {
        MenuGroup(
            actions = NavActions(rememberNavController()),
            group = ToolMenuGroup(
                stringResource(id = R.string.debug_short_text),
                arrayListOf(ToolMenuType.MOCK_GACHA),
                stringResource(id = R.string.debug_short_text),
            ),
            isEditMode = true
        ) {

        }
    }
}