package cn.wthee.pcrtool.ui.tool

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.ToolMenuType
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.NavActions
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.home.*
import cn.wthee.pcrtool.ui.theme.*
import cn.wthee.pcrtool.utils.intArrayList
import kotlinx.coroutines.launch

data class ToolMenuGroup(
    val title: String,
    val list: List<ToolMenuData>
)

/**
 * 全部工具
 */
@Composable
fun AllToolMenu(scrollState: LazyListState, actions: NavActions) {

    val coroutineScope = rememberCoroutineScope()

    //编辑模式
    var isEditMode by remember {
        mutableStateOf(false)
    }

    val itemsList = arrayListOf<ToolMenuGroup>()

    //游戏数据
    val dataList = arrayListOf<ToolMenuData>()
    dataList.add(getToolMenuData(toolMenuType = ToolMenuType.CHARACTER))
    dataList.add(getToolMenuData(toolMenuType = ToolMenuType.EQUIP))
    dataList.add(getToolMenuData(toolMenuType = ToolMenuType.GUILD))
    dataList.add(getToolMenuData(toolMenuType = ToolMenuType.CLAN))
    dataList.add(getToolMenuData(toolMenuType = ToolMenuType.RANDOM_AREA))
    itemsList.add(ToolMenuGroup(stringResource(id = R.string.basic_info), dataList))

    //游戏信息
    val infoList = arrayListOf<ToolMenuData>()
    infoList.add(getToolMenuData(toolMenuType = ToolMenuType.GACHA))
    infoList.add(getToolMenuData(toolMenuType = ToolMenuType.EVENT))
    infoList.add(getToolMenuData(toolMenuType = ToolMenuType.NEWS))
    infoList.add(getToolMenuData(toolMenuType = ToolMenuType.FREE_GACHA))
    infoList.add(getToolMenuData(toolMenuType = ToolMenuType.BIRTHDAY))
    itemsList.add(ToolMenuGroup(stringResource(id = R.string.activity_info), infoList))

    //查询
    val searchList = arrayListOf<ToolMenuData>()
    searchList.add(getToolMenuData(toolMenuType = ToolMenuType.PVP_SEARCH))
    searchList.add(getToolMenuData(toolMenuType = ToolMenuType.LEADER))
    itemsList.add(ToolMenuGroup(stringResource(id = R.string.pvp_search), searchList))

    //其它
    val otherList = arrayListOf<ToolMenuData>()
    otherList.add(getToolMenuData(toolMenuType = ToolMenuType.TWEET))
    otherList.add(getToolMenuData(toolMenuType = ToolMenuType.COMIC))
    otherList.add(getToolMenuData(toolMenuType = ToolMenuType.MOCK_GACHA))
    if (BuildConfig.DEBUG) {
        otherList.add(getToolMenuData(toolMenuType = ToolMenuType.ALL_SKILL))
        otherList.add(getToolMenuData(toolMenuType = ToolMenuType.ALL_EQUIP))
    }
    itemsList.add(ToolMenuGroup(stringResource(id = R.string.other), otherList))

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            //预览
            ExpandAnimation(visible = isEditMode) {
                ToolMenu(actions = actions, isEditMode = isEditMode, isHome = false)
            }
            //编辑提示
            if (isEditMode) {
                Subtitle2(
                    text = stringResource(R.string.tip_click_to_add),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = Dimen.mediumPadding)
                )
            }
            //全部功能
            MainCard(
                shape = if (isEditMode) CardTopShape else Shape.none,
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .padding(horizontal = Dimen.mediumPadding)
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.surface), state = scrollState
                ) {
                    items(
                        items = itemsList,
                        key = {
                            it.title
                        }
                    ) {
                        MenuGroup(
                            actions = actions,
                            title = it.title,
                            items = it.list,
                            isEditMode = isEditMode
                        )
                    }
                    item {
                        CommonSpacer()
                    }
                }
            }
        }

        //编辑
        FabCompose(
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
    title: String,
    items: List<ToolMenuData>,
    isEditMode: Boolean
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
            items.forEach {
                MenuItem(actions, it, isEditMode)
            }
        }
    }
}

@Composable
private fun MenuItem(
    actions: NavActions,
    toolMenuData: ToolMenuData,
    isEditMode: Boolean
) {
    val orderStr = navViewModel.toolOrderData.observeAsState().value ?: ""
    val hasAdded = orderStr.intArrayList.contains(toolMenuData.type.id)


    MainCard(
        modifier = Modifier.padding(Dimen.mediumPadding),
        onClick = if (isEditMode) {
            {
                editToolMenuOrder(toolMenuData.type.id)
            }
        } else {
            getAction(actions, toolMenuData)
        },
        backgroundColor = if (hasAdded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier
                .defaultMinSize(minWidth = Dimen.menuItemSize)
                .padding(horizontal = Dimen.smallPadding, vertical = Dimen.mediumPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconCompose(
                modifier = Modifier.padding(start = Dimen.mediumPadding),
                data = toolMenuData.iconType,
                size = Dimen.mediumIconSize,
                tint = if (hasAdded) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.primary
            )
            Subtitle2(
                text = stringResource(id = toolMenuData.titleId),
                modifier = Modifier.padding(start = Dimen.largePadding)
            )
        }
    }
}
