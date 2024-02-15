package cn.wthee.pcrtool.ui.tool.talent

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.TalentData
import cn.wthee.pcrtool.data.enums.AtkType
import cn.wthee.pcrtool.data.enums.IconResourceType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.TalentType
import cn.wthee.pcrtool.ui.components.CenterTipText
import cn.wthee.pcrtool.ui.components.GridIconList
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.MainTabRow
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.StateBox
import cn.wthee.pcrtool.ui.components.TabData
import cn.wthee.pcrtool.ui.theme.Dimen
import kotlinx.coroutines.launch

/**
 * 角色天赋列表
 */
@OptIn(ExperimentalFoundationApi::class)
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

    MainScaffold(
        fab = {
            //回到顶部
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
        }
    ) {
        StateBox(
            stateType = uiState.loadState,
            errorContent = {
                CenterTipText(text = stringResource(R.string.not_installed))
            }
        ) {
            UnitTalentContent(
                unitTalentList = uiState.unitTalentList,
                pagerState = pagerState,
                scrollStateList = scrollStateList,
                toCharacterDetail = toCharacterDetail
            )
        }

    }

}


@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun UnitTalentContent(
    unitTalentList: List<TalentData>?,
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

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollStateList[pagerState.currentPage])
            ) {
                val atkTypeList1 =
                    list.filter { it.atkType == AtkType.PHYSICAL.type }.map { it.unitId }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(
                        start = Dimen.mediumPadding,
                        end = Dimen.mediumPadding,
                        top = Dimen.mediumPadding
                    )
                ) {
                    MainTitleText(text = stringResource(id = R.string.physical))
                    Spacer(modifier = Modifier.weight(1f))
                    MainText(text = atkTypeList1.size.toString())
                }

                GridIconList(
                    idList = atkTypeList1,
                    iconResourceType = IconResourceType.CHARACTER,
                    onClickItem = toCharacterDetail
                )

                val atkTypeList2 =
                    list.filter { it.atkType == AtkType.MAGIC.type }.map { it.unitId }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(
                        start = Dimen.mediumPadding,
                        end = Dimen.mediumPadding,
                        top = Dimen.mediumPadding
                    )
                ) {
                    MainTitleText(text = stringResource(id = R.string.magic))
                    Spacer(modifier = Modifier.weight(1f))
                    MainText(text = atkTypeList2.size.toString())
                }

                GridIconList(
                    idList = atkTypeList2,
                    iconResourceType = IconResourceType.CHARACTER,
                    onClickItem = toCharacterDetail
                )
            }
        }
    }

}