package cn.wthee.pcrtool.ui.tool.talent

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
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
import cn.wthee.pcrtool.data.enums.IconResourceType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.PositionType
import cn.wthee.pcrtool.data.enums.RoleType
import cn.wthee.pcrtool.data.enums.TalentRoleListType
import cn.wthee.pcrtool.data.enums.TalentType
import cn.wthee.pcrtool.ui.components.CenterTipText
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.Dot
import cn.wthee.pcrtool.ui.components.IconItem
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.MainTabRow
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.StateBox
import cn.wthee.pcrtool.ui.components.TabData
import cn.wthee.pcrtool.ui.components.Tag
import cn.wthee.pcrtool.ui.components.VerticalGridList
import cn.wthee.pcrtool.ui.theme.Dimen
import kotlinx.coroutines.launch

/**
 * 角色天赋列表
 */
@Composable
fun UnitTalentListScreen(
    toCharacterDetail: (Int) -> Unit,
    unitTalentListViewModel: UnitTalentViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val uiState by unitTalentListViewModel.uiState.collectAsStateWithLifecycle()
    //页面状态
    val pagerState = rememberPagerState {
        5
    }
    //列表状态
    val scrollStateList = arrayListOf(
        rememberScrollState(),
        rememberScrollState(),
        rememberScrollState(),
        rememberScrollState(),
        rememberScrollState(),
    )
    //筛选的天赋
    val talentType = TalentType.getByType(uiState.talentType)

    MainScaffold(
        fab = {
            //回到顶部
            if (uiState.showAllType) {
                MainSmallFab(
                    iconType = MainIconType.TALENT,
                    text = stringResource(id = R.string.unit_talent),
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
                    iconType = MainIconType.TALENT,
                    text = stringResource(id = talentType.typeNameId) + " ${uiState.unitTalentList?.size ?: 0}",
                    tintColor = talentType.color,
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
                UnitTalentPagerContent(
                    unitTalentList = uiState.unitTalentList,
                    pagerState = pagerState,
                    scrollStateList = scrollStateList,
                    toCharacterDetail = toCharacterDetail
                )
            } else {
                UnitTalentListItemContent(
                    scrollState = scrollStateList[0],
                    list = uiState.unitTalentList ?: arrayListOf(),
                    selectedUnitId = uiState.selectedUnitId,
                    toCharacterDetail = toCharacterDetail
                )
            }
        }
    }

}

@Composable
private fun UnitTalentPagerContent(
    unitTalentList: List<CharacterTalentRoleInfo>?,
    pagerState: PagerState,
    scrollStateList: List<ScrollState>,
    toCharacterDetail: (Int) -> Unit
) {

    //类型
    val pageTabs = arrayListOf<TabData>()
    TalentType.entries.forEachIndexed { index, talentType ->
        if (index != 0) {
            pageTabs.add(
                TabData(
                    tab = stringResource(id = talentType.typeNameId),
                    color = talentType.color,
                    count = unitTalentList?.count { talentData ->
                        talentData.talentId == talentType.type
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
            val list = unitTalentList?.filter { it.talentId == pagerState.currentPage + 1 }
                ?: arrayListOf()

            UnitTalentListItemContent(
                scrollState = scrollStateList[pagerState.currentPage],
                list = list,
                toCharacterDetail = toCharacterDetail
            )
        }
    }

}

@Composable
private fun UnitTalentListItemContent(
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
            listType = TalentRoleListType.TALENT,
            selectedUnitId = selectedUnitId,
            toCharacterDetail = toCharacterDetail
        )
        //魔法
        UnitAtkTypeList(
            list = list,
            atkType = AtkType.MAGIC,
            listType = TalentRoleListType.TALENT,
            selectedUnitId = selectedUnitId,
            toCharacterDetail = toCharacterDetail
        )
        CommonSpacer()
    }
}


/**
 * （物理或魔法）角色列表
 * 1
 */
@Composable
fun UnitAtkTypeList(
    list: List<CharacterTalentRoleInfo>,
    atkType: AtkType,
    selectedUnitId: Int? = null,
    listType: TalentRoleListType,
    toCharacterDetail: (Int) -> Unit
) {
    val atkTypeList =
        list.filter { it.atkType == atkType.type }
    val character0 = arrayListOf(CharacterTalentRoleInfo(unitId = 0))
    character0.addAll(atkTypeList.filter {
        PositionType.getPositionType(it.position) == PositionType.POSITION_FRONT
    })
    val character1 = arrayListOf(CharacterTalentRoleInfo(unitId = 1))
    character1.addAll(atkTypeList.filter {
        PositionType.getPositionType(it.position) == PositionType.POSITION_MIDDLE
    })
    val character2 = arrayListOf(CharacterTalentRoleInfo(unitId = 2))
    character2.addAll(atkTypeList.filter {
        PositionType.getPositionType(it.position) == PositionType.POSITION_BACK
    })

    val unitList = arrayListOf<CharacterTalentRoleInfo>()
    if (character0.size > 1) {
        unitList.addAll(
            character0.map { it }
        )
    }
    if (character1.size > 1) {
        unitList.addAll(
            character1.map { it }
        )
    }
    if (character2.size > 1) {
        unitList.addAll(
            character2.map { it }
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(
            start = Dimen.mediumPadding,
            end = Dimen.mediumPadding,
            top = Dimen.mediumPadding
        )
    ) {
        MainTitleText(text = stringResource(id = atkType.typeNameId))
        Spacer(modifier = Modifier.weight(1f))
        MainText(text = atkTypeList.size.toString())
    }

    VerticalGridList(
        itemCount = unitList.size,
        itemWidth = Dimen.iconSize,
        contentPadding = Dimen.exSmallPadding,
        verticalContentPadding = Dimen.smallPadding
    ) {
        val unit = unitList[it]
        val selected = selectedUnitId == unit.unitId

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            IconItem(
                id = unit.unitId,
                selected = selected,
                iconResourceType = IconResourceType.CHARACTER,
                onClickItem = if (selected) null else toCharacterDetail
            )

            if (listType == TalentRoleListType.TALENT) {
                // 职能信息
                if (unit.roleId != 0) {
                    val roleType = RoleType.getByType(unit.roleId)
                    Tag(
                        modifier = Modifier
                            .padding(top = Dimen.exSmallPadding),
                        text = stringResource(id = roleType.typeNameId),
                        backgroundColor = roleType.color,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            } else if (listType == TalentRoleListType.ROLE) {
                //天赋信息
                if (unit.talentId != 0) {
                    Dot(color = TalentType.getByType(unit.talentId).color)
                }

            }

        }
    }
}