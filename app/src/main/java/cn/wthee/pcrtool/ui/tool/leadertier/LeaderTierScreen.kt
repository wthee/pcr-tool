package cn.wthee.pcrtool.ui.tool.leadertier

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.CharacterInfo
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.LeaderTierGroup
import cn.wthee.pcrtool.data.model.LeaderTierItem
import cn.wthee.pcrtool.navigation.navigateUp
import cn.wthee.pcrtool.ui.LoadingState
import cn.wthee.pcrtool.ui.components.CaptionText
import cn.wthee.pcrtool.ui.components.CharacterTagRow
import cn.wthee.pcrtool.ui.components.CircularProgressCompose
import cn.wthee.pcrtool.ui.components.CommonGroupTitle
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainContentText
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.SelectTypeFab
import cn.wthee.pcrtool.ui.components.StateBox
import cn.wthee.pcrtool.ui.components.VerticalGrid
import cn.wthee.pcrtool.ui.components.placeholder
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.ExpandAnimation
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.colorGray
import cn.wthee.pcrtool.ui.tool.leaderboard.LeaderCharacterIcon
import cn.wthee.pcrtool.ui.tool.leaderboard.getLeaderUnknownTip
import cn.wthee.pcrtool.utils.BrowserUtil
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.VibrateUtil
import kotlinx.coroutines.launch

/**
 * 角色排行
 */
@Composable
fun LeaderTierScreen(
    toCharacterDetail: (Int) -> Unit,
    leaderTierViewModel: LeaderTierViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()
    val uiState by leaderTierViewModel.uiState.collectAsStateWithLifecycle()

    val tabs = arrayListOf(
        stringResource(id = R.string.leader_tier_0),
        stringResource(id = R.string.leader_tier_1),
        stringResource(id = R.string.leader_tier_2),
        stringResource(id = R.string.clan),
    )


    MainScaffold(
        fab = {
            //回到顶部
            MainSmallFab(
                iconType = MainIconType.LEADER_TIER,
                text = uiState.size.toString(),
                extraContent = if (uiState.loadingState == LoadingState.Loading) {
                    //加载提示
                    {
                        CircularProgressCompose()
                    }
                } else {
                    null
                }
            ) {
                scope.launch {
                    try {
                        scrollState.scrollToItem(0)
                    } catch (_: Exception) {
                    }
                }
            }

        },
        secondLineFab = {
            if (uiState.loadingState == LoadingState.Success) {
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
        mainFabIcon = if (uiState.openDialog) MainIconType.CLOSE else MainIconType.BACK,
        onMainFabClick = {
            if (uiState.openDialog) {
                leaderTierViewModel.changeDialog(false)
            } else {
                navigateUp()
            }
        },
        enableClickClose = uiState.openDialog,
        onCloseClick = {
            leaderTierViewModel.changeDialog(false)
        }
    ) {
        Column {

            LeaderTierHeader(scrollState, uiState.date)

            StateBox(
                stateType = uiState.loadingState,
                loadingContent = {
                    VerticalGrid(
                        itemWidth = Dimen.iconSize * 4,
                        contentPadding = Dimen.largePadding,
                    ) {
                        for (i in 0..10) {
                            LeaderItem(LeaderTierItem(), null, toCharacterDetail)
                        }
                    }
                }
            ) {
                LeaderTierContent(
                    groupList = uiState.groupList,
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
        groupList.forEach {
            stickyHeader {
                //分组标题
                CommonGroupTitle(
                    titleStart = stringResource(
                        id = R.string.leader_tier_d,
                        it.tier
                    ),
                    titleCenter = it.desc,
                    titleEnd = it.leaderList.size.toString(),
                    modifier = Modifier.padding(
                        start = Dimen.mediumPadding,
                        end = Dimen.mediumPadding,
                        bottom = Dimen.mediumPadding,
                    )
                )
            }
            item {
                //分组内容
                VerticalGrid(
                    itemWidth = Dimen.iconSize * 4,
                    contentPadding = Dimen.largePadding,
                ) {
                    it.leaderList.forEach { leader ->
                        //获取角色名
                        val flow = remember(leader.unitId) {
                            leaderTierViewModel.getCharacterBasicInfo(
                                leader.unitId ?: 0
                            )
                        }
                        val basicInfo by flow.collectAsState(initial = null)

                        LeaderItem(leader, basicInfo, toCharacterDetail)
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
 * 头部
 */
@Composable
private fun LeaderTierHeader(
    scrollState: LazyListState,
    date: String,
) {
    val context = LocalContext.current
    val url = stringResource(id = R.string.leader_source_url)
    val showTitle by remember { derivedStateOf { scrollState.firstVisibleItemIndex == 0 } }


    Column {
        //标题
        ExpandAnimation(visible = showTitle) {
            Row(
                modifier = Modifier.padding(
                    horizontal = Dimen.largePadding,
                    vertical = Dimen.mediumPadding
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MainTitleText(
                    text = stringResource(id = R.string.leader_source),
                    modifier = Modifier
                        .clickable {
                            VibrateUtil(context).single()
                            BrowserUtil.open(url)
                        }
                )

                CaptionText(
                    text = stringResource(id = R.string.only_jp),
                    modifier = Modifier.padding(start = Dimen.smallPadding)
                )
                CaptionText(
                    text = date,
                    modifier = Modifier.fillMaxWidth()
                )
            }
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
            .padding(
                bottom = Dimen.largePadding,
                start = Dimen.mediumPadding,
                end = Dimen.mediumPadding,
            )
    ) {
        //图标
        LeaderCharacterIcon(
            hasUnitId,
            placeholder,
            leader.unitId!!,
            leader.url,
            unknown,
            tipText,
            toCharacterDetail
        )

        MainCard(
            modifier = Modifier
                .padding(start = Dimen.mediumPadding)
                .placeholder(placeholder)
                .heightIn(min = Dimen.cardHeight),
            onClick = {
                if (!unknown) {
                    toCharacterDetail(leader.unitId)
                } else {
                    ToastUtil.short(tipText)
                }
            }
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = Dimen.mediumPadding,
                    vertical = Dimen.smallPadding
                )
            ) {
                //名称
                MainContentText(
                    text = if (hasUnitId && !unknown) {
                        basicInfo!!.name
                    } else {
                        leader.name
                    },
                    textAlign = TextAlign.Start,
                    color = textColor,
                    maxLines = 1
                )

            }
            CharacterTagRow(
                modifier = Modifier
                    .padding(top = Dimen.mediumPadding, bottom = Dimen.smallPadding),
                unknown = unknown,
                basicInfo = basicInfo,
                tipText = tipText
            )
        }
    }
}


@CombinedPreviews
@Composable
private fun LeaderItemPreview() {
    PreviewLayout {
        LeaderItem(
            LeaderTierItem(),
            null
        ) {}
    }
}