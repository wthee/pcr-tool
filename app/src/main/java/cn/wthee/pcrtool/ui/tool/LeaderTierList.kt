package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.LeaderTierType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.LeaderTierGroup
import cn.wthee.pcrtool.data.model.LeaderTierItem
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.components.CaptionText
import cn.wthee.pcrtool.ui.components.CenterTipText
import cn.wthee.pcrtool.ui.components.CharacterTagRow
import cn.wthee.pcrtool.ui.components.CommonGroupTitle
import cn.wthee.pcrtool.ui.components.CommonResponseBox
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainContentText
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.SelectTypeFab
import cn.wthee.pcrtool.ui.components.VerticalGrid
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.ExpandAnimation
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.colorGray
import cn.wthee.pcrtool.utils.BrowserUtil
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.fixedLeaderDate
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import cn.wthee.pcrtool.viewmodel.LeaderViewModel
import kotlinx.coroutines.launch

/**
 * 角色排行
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LeaderTier(
    scrollState: LazyListState,
    toCharacterDetail: (Int) -> Unit,
    leaderViewModel: LeaderViewModel = hiltViewModel(),
    characterViewModel: CharacterViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val type = remember {
        mutableStateOf(navViewModel.leaderTierType.value?.type ?: 0)
    }
    type.value = navViewModel.leaderTierType.observeAsState().value?.type ?: 0
    val tabs = arrayListOf(
        stringResource(id = R.string.leader_tier_0),
        stringResource(id = R.string.leader_tier_1),
        stringResource(id = R.string.leader_tier_2),
        stringResource(id = R.string.clan),
    )

    val flow = remember(type.value) {
        leaderViewModel.getLeaderTier(type.value)
    }
    val leaderData = flow.collectAsState(initial = null).value

    val url = stringResource(id = R.string.leader_source_url)

    val scrollChange = remember { derivedStateOf { scrollState.firstVisibleItemIndex } }


    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        //更新
        ExpandAnimation(visible = scrollChange.value == 0) {
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
                    text = leaderData?.data?.desc?.fixedLeaderDate ?: "",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }


        CommonResponseBox(
            responseData = leaderData,
            fabContent = { data ->
                //切换类型
                SelectTypeFab(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    icon = MainIconType.CHANGE_FILTER_TYPE,
                    tabs = tabs,
                    type = type,
                    width = Dimen.dataChangeWidth + Dimen.fabSize,
                    paddingValues = PaddingValues(
                        end = Dimen.fabMargin,
                        bottom = Dimen.fabMargin * 2 + Dimen.fabSize
                    )
                ) {
                    navViewModel.leaderTierType.postValue(LeaderTierType.getByValue(type.value))
                }

                //回到顶部
                MainSmallFab(
                    iconType = MainIconType.LEADER_TIER,
                    text = data.leader.size.toString(),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
                ) {
                    scope.launch {
                        scope.launch {
                            try {
                                scrollState.scrollToItem(0)
                            } catch (_: Exception) {
                            }
                        }
                    }
                }
            }
        ) { data ->
            //分组
            val groupList = arrayListOf<LeaderTierGroup>()
            data.leader.forEach { leaderItem ->
                var group = groupList.find {
                    it.tier == leaderItem.tier
                }
                if (group == null) {
                    val descInfo = data.tierSummary.find {
                        it.tier == leaderItem.tier
                    }
                    group =
                        LeaderTierGroup(leaderItem.tier, arrayListOf(), descInfo?.desc ?: "")
                    groupList.add(group)
                }
                group.leaderList.add(leaderItem)
            }

            if (groupList.isNotEmpty()) {
                LazyColumn(state = scrollState) {
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
                                    LeaderItem(leader, toCharacterDetail, characterViewModel)
                                }
                            }
                        }
                    }
                    items(count = 2) {
                        CommonSpacer()
                    }
                }
            } else {
                CenterTipText(text = stringResource(id = R.string.no_data))
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
    toCharacterDetail: (Int) -> Unit,
    characterViewModel: CharacterViewModel?
) {
    //获取角色名
    val flow = remember(leader.unitId) {
        characterViewModel?.getCharacterBasicInfo(leader.unitId ?: 0)
    }
    val basicInfo = flow?.collectAsState(initial = null)?.value
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
            leader.unitId!!,
            leader.url,
            unknown,
            tipText,
            toCharacterDetail
        )

        MainCard(
            modifier = Modifier
                .padding(start = Dimen.mediumPadding)
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
    val text = stringResource(id = R.string.debug_short_text)
    PreviewLayout {
        LeaderItem(
            LeaderTierItem(),
            {},
            null
        )
    }
}