package cn.wthee.pcrtool.ui.tool.leadertier

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.CharacterInfo
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.TalentType
import cn.wthee.pcrtool.data.model.LeaderTierGroup
import cn.wthee.pcrtool.data.model.LeaderTierItem
import cn.wthee.pcrtool.navigation.navigateUp
import cn.wthee.pcrtool.ui.LoadState
import cn.wthee.pcrtool.ui.components.CharacterTagRow
import cn.wthee.pcrtool.ui.components.CommonGroupTitle
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.ExpandableHeader
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainContentText
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.components.SelectTypeFab
import cn.wthee.pcrtool.ui.components.StateBox
import cn.wthee.pcrtool.ui.components.VerticalGridList
import cn.wthee.pcrtool.ui.components.placeholder
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.colorGray
import cn.wthee.pcrtool.ui.theme.defaultTween
import cn.wthee.pcrtool.ui.tool.leaderboard.LeaderCharacterIcon
import cn.wthee.pcrtool.ui.tool.leaderboard.getLeaderUnknownTip
import cn.wthee.pcrtool.utils.ToastUtil
import kotlinx.coroutines.launch

/**
 * 角色评级
 */
@Composable
fun LeaderTierScreen(
    toCharacterDetail: (Int) -> Unit,
    leaderTierViewModel: LeaderTierViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()
    val uiState by leaderTierViewModel.uiState.collectAsStateWithLifecycle()
    val hasTalent = (uiState.talentUnitMap[1] ?: arrayListOf()).isNotEmpty()

    //评级类型
    val tabs = arrayListOf(
        stringResource(id = R.string.leader_tier_0),
        stringResource(id = R.string.leader_tier_1),
        stringResource(id = R.string.leader_tier_2),
        stringResource(id = R.string.clan),
    )
    //天赋类型
    val talentTabs = arrayListOf<String>()
    TalentType.entries.forEachIndexed { _, talentType ->
        talentTabs.add(
            stringResource(id = talentType.typeNameId)
        )
    }


    MainScaffold(
        fab = {
            if (hasTalent && uiState.loadState == LoadState.Success) {
                SelectTypeFab(
                    icon = MainIconType.TALENT,
                    tabs = talentTabs,
                    selectedIndex = uiState.talentType.type,
                    openDialog = uiState.openTalentDialog,
                    changeDialog = leaderTierViewModel::changeTalentDialog,
                    changeSelect = leaderTierViewModel::changeTalentSelect,
                    noPadding = true
                )
            }
            //回到顶部
            MainSmallFab(
                iconType = MainIconType.LEADER_TIER,
                text = uiState.count.toString(),
                loading = uiState.loadState == LoadState.Loading,
                onClick = {
                    scope.launch {
                        try {
                            scrollState.scrollToItem(0)
                        } catch (_: Exception) {
                        }
                    }
                }
            )
        },
        secondLineFab = {
            if (uiState.loadState == LoadState.Success) {
                //切换类型
                SelectTypeFab(
                    icon = MainIconType.CLAN_SECTION,
                    tabs = tabs,
                    selectedIndex = uiState.leaderTierType.type,
                    openDialog = uiState.openDialog,
                    changeDialog = leaderTierViewModel::changeDialog,
                    changeSelect = leaderTierViewModel::changeSelect,
                    isSecondLineFab = true
                )
            }
        },
        mainFabIcon = if (uiState.openDialog || uiState.openTalentDialog) {
            MainIconType.CLOSE
        } else {
            MainIconType.BACK
        },
        onMainFabClick = {
            if (uiState.openDialog) {
                leaderTierViewModel.changeDialog(false)
            } else if (uiState.openTalentDialog) {
                leaderTierViewModel.changeTalentDialog(false)
            } else {
                navigateUp()
            }
        },
        enableClickClose = uiState.openDialog || uiState.openTalentDialog,
        onCloseClick = {
            leaderTierViewModel.changeDialog(false)
            leaderTierViewModel.changeTalentDialog(false)
        }
    ) {
        Column {
            //标题
            ExpandableHeader(
                scrollState = scrollState,
                title = stringResource(id = R.string.leader_source),
                startText = stringResource(id = R.string.only_jp),
                endText = uiState.date,
                url = stringResource(id = R.string.leader_source_url)
            )

            StateBox(
                stateType = uiState.loadState,
                loadingContent = {
                    Column {
                        for (i in 0..1) {
                            CommonGroupTitle(
                                titleStart = "",
                                titleCenter = "",
                                titleEnd = "",
                                modifier = Modifier
                                    .padding(
                                        start = Dimen.mediumPadding,
                                        end = Dimen.mediumPadding,
                                        bottom = Dimen.mediumPadding,
                                    )
                                    .placeholder(true)
                            )

                            VerticalGridList(
                                itemCount = 6 * (i + 1),
                                itemWidth = Dimen.iconSize * 4,
                                contentPadding = Dimen.mediumPadding,
                            ) {
                                LeaderItem(LeaderTierItem(), null, toCharacterDetail)
                            }
                        }
                    }
                }
            ) {
                LeaderTierContent(
                    groupList = uiState.currentGroupList,
                    scrollState = scrollState,
                    leaderTierViewModel = leaderTierViewModel,
                    toCharacterDetail = toCharacterDetail
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun LeaderTierContent(
    groupList: List<LeaderTierGroup>,
    scrollState: LazyListState,
    leaderTierViewModel: LeaderTierViewModel,
    toCharacterDetail: (Int) -> Unit
) {
    LazyColumn(
        state = scrollState
    ) {
        groupList.forEach { group ->
            stickyHeader {
                //分组标题
                CommonGroupTitle(
                    titleStart = stringResource(
                        id = R.string.leader_tier_d,
                        group.tier
                    ),
                    titleCenter = group.desc,
                    titleEnd = group.leaderList.size.toString(),
                    modifier = Modifier.padding(
                        start = Dimen.mediumPadding,
                        end = Dimen.mediumPadding,
                        bottom = Dimen.mediumPadding,
                    )
                )
            }
            item {
                if (group.leaderList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = Dimen.mediumPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        MainText(text = stringResource(id = R.string.no_data))
                    }
                } else {
                    //分组内容
                    VerticalGridList(
                        itemCount = group.leaderList.size,
                        itemWidth = Dimen.iconSize * 4,
                        contentPadding = Dimen.mediumPadding,
                        minRowHeight = false,
                        verticalAlignment = Alignment.Top
                    ) {
                        val leader = group.leaderList[it]
                        //获取角色名
                        val flow = remember(leader.unitId) {
                            leaderTierViewModel.getCharacterBasicInfo(
                                leader.unitId ?: 0
                            )
                        }
                        val basicInfo by flow.collectAsState(initial = null)

                        LeaderItem(
                            leader = leader,
                            basicInfo = basicInfo,
                            toCharacterDetail = toCharacterDetail
                        )
                    }
                }
            }
        }
        items(count = 2) {
            CommonSpacer()
        }
    }
}

/**
 * 角色
 */
@Composable
private fun LeaderItem(
    leader: LeaderTierItem,
    basicInfo: CharacterInfo?,
    toCharacterDetail: (Int) -> Unit,
) {
    val placeholder = leader.name == ""
    val hasUnitId = leader.unitId != null && leader.unitId != 0
    //是否登场角色
    val unknown = basicInfo == null || basicInfo.position == 0

    val textColor = if (!hasUnitId || unknown) {
        colorGray
    } else {
        Color.Unspecified
    }
    val tipText = getLeaderUnknownTip(hasUnitId)


    Row(
        modifier = Modifier
            .fillMaxHeight()
            .animateContentSize(defaultTween())
    ) {
        //图标
        LeaderCharacterIcon(
            hasUnitId = hasUnitId,
            placeholder = placeholder,
            unitId = leader.unitId,
            url = leader.url,
            unknown = unknown,
            tipText = tipText,
            toCharacterDetail = toCharacterDetail
        )

        MainCard(
            modifier = Modifier
                .padding(start = Dimen.mediumPadding)
                .placeholder(placeholder),
            onClick = {
                if (!unknown) {
                    leader.unitId?.let { toCharacterDetail(it) }
                } else {
                    ToastUtil.short(tipText)
                }
            },
        ) {
            //名称
            MainContentText(
                modifier = Modifier.padding(
                    horizontal = Dimen.mediumPadding,
                    vertical = Dimen.smallPadding
                ),
                text = if (hasUnitId && !unknown) {
                    basicInfo!!.name
                } else {
                    leader.name
                },
                textAlign = TextAlign.Start,
                color = textColor,
                maxLines = 1
            )
            //标签
            CharacterTagRow(
                modifier = Modifier
                    .padding(top = Dimen.mediumPadding, bottom = Dimen.smallPadding),
                unknown = unknown,
                basicInfo = basicInfo,
                tipText = tipText,
                showUniqueEquipType = false
            )
        }
    }
}


@CombinedPreviews
@Composable
private fun LeaderItemPreview() {
    PreviewLayout {
        VerticalGridList(
            itemCount = 2,
            itemWidth = Dimen.iconSize * 4,
            fixColumns = 2,
            contentPadding = Dimen.mediumPadding,
            minRowHeight = false,
            verticalAlignment = Alignment.Top
        ) {
            if (it == 0) {
                LeaderItem(
                    leader = LeaderTierItem(
                        name = stringResource(id = R.string.debug_name),
                    ),
                    basicInfo = CharacterInfo(
                        id = 1,
                        name = stringResource(id = R.string.debug_name),
                        position = 100,
                        uniqueEquipType = 2
                    )
                ) {}
            } else {
                LeaderItem(
                    leader = LeaderTierItem(
                        name = stringResource(id = R.string.debug_name),
                    ),
                    basicInfo = CharacterInfo(
                        name = stringResource(id = R.string.debug_name)
                    )
                ) {}
            }
        }

    }
}