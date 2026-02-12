package cn.wthee.pcrtool.ui.tool.role

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.CharacterTalentRoleInfo
import cn.wthee.pcrtool.data.enums.AtkType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.RoleType
import cn.wthee.pcrtool.data.enums.TalentRoleListType
import cn.wthee.pcrtool.ui.components.CenterTipText
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.MainTabRow
import cn.wthee.pcrtool.ui.components.StateBox
import cn.wthee.pcrtool.ui.components.TabData
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.tool.talent.UnitAtkTypeList
import kotlinx.coroutines.launch

/**
 * 角色职能列表
 */
@Composable
fun UnitRoleListScreen(
    toCharacterDetail: (Int) -> Unit,
    unitRoleListViewModel: UnitRoleViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val uiState by unitRoleListViewModel.uiState.collectAsStateWithLifecycle()
    //页面状态
    val pagerState = rememberPagerState {
        8
    }
    //列表状态
    val scrollStateList = arrayListOf(
        rememberScrollState(),
        rememberScrollState(),
        rememberScrollState(),
        rememberScrollState(),
        rememberScrollState(),
        rememberScrollState(),
        rememberScrollState(),
        rememberScrollState(),
    )
    //筛选的职能
    val roleType = RoleType.getByType(uiState.roleType)

    MainScaffold(
        fab = {
            //回到顶部
            if (uiState.showAllType) {
                MainSmallFab(
                    iconType = MainIconType.ROLE,
                    text = stringResource(id = R.string.unit_role),
                    onClick = {
                        coroutineScope.launch {
                            try {
                                scrollStateList[pagerState.currentPage].scrollTo(0)
                            } catch (_: Exception) {
                            }
                        }
                    }
                )
            } else {
                MainSmallFab(
                    iconType = MainIconType.ROLE,
                    text = stringResource(id = roleType.typeNameId) + " ${uiState.unitRoleList?.size ?: 0}",
                    tintColor = roleType.color,
                    onClick = {
                        coroutineScope.launch {
                            try {
                                scrollStateList[0].scrollTo(0)
                            } catch (_: Exception) {
                            }
                        }
                    }
                )
            }
        }
    ) {
        StateBox(
            stateType = uiState.loadState,
            errorContent = {
                CenterTipText(text = stringResource(R.string.not_installed))
            }
        ) {
            if (uiState.showAllType) {
                UnitRolePagerContent(
                    unitRoleList = uiState.unitRoleList,
                    pagerState = pagerState,
                    scrollStateList = scrollStateList,
                    toCharacterDetail = toCharacterDetail
                )
            } else {
                UnitRoleListItemContent(
                    scrollState = scrollStateList[0],
                    list = uiState.unitRoleList ?: arrayListOf(),
                    selectedUnitId = uiState.selectedUnitId,
                    toCharacterDetail = toCharacterDetail
                )
            }
        }
    }

}

@Composable
private fun UnitRolePagerContent(
    unitRoleList: List<CharacterTalentRoleInfo>?,
    pagerState: PagerState,
    scrollStateList: List<ScrollState>,
    toCharacterDetail: (Int) -> Unit
) {

    //类型
    val pageTabs = arrayListOf<TabData>()
    RoleType.entries.forEachIndexed { index, roleType ->
        if (index != 0) {
            pageTabs.add(
                TabData(
                    tab = stringResource(id = roleType.typeNameId),
                    color = roleType.color,
                    count = unitRoleList?.count { roleData ->
                        roleData.roleId == roleType.type
                    }
                )
            )
        }
    }

    Column(
        modifier = Modifier
            .padding(top = Dimen.largePadding)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MainTabRow(
            pagerState = pagerState,
            tabs = pageTabs,
            scrollable = true,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
        ) {
            scrollStateList[it].scrollTo(0)
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .padding(top = Dimen.mediumPadding)
                .fillMaxSize(),
            verticalAlignment = Alignment.Top
        ) {
            val list = unitRoleList?.filter { it.roleId == pagerState.currentPage + 1 }
                ?: arrayListOf()

            UnitRoleListItemContent(
                scrollState = scrollStateList[pagerState.currentPage],
                list = list,
                toCharacterDetail = toCharacterDetail
            )
        }
    }

}

@Composable
private fun UnitRoleListItemContent(
    scrollState: ScrollState,
    list: List<CharacterTalentRoleInfo>,
    selectedUnitId: Int? = null,
    toCharacterDetail: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
    ) {
        //物理
        UnitAtkTypeList(
            list = list,
            atkType = AtkType.PHYSICAL,
            listType = TalentRoleListType.ROLE,
            selectedUnitId = selectedUnitId,
            toCharacterDetail = toCharacterDetail
        )
        //魔法
        UnitAtkTypeList(
            list = list,
            atkType = AtkType.MAGIC,
            listType = TalentRoleListType.ROLE,
            selectedUnitId = selectedUnitId,
            toCharacterDetail = toCharacterDetail
        )
        CommonSpacer()
    }
}