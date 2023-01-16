package cn.wthee.pcrtool.ui.tool

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.LeaderTierGroup
import cn.wthee.pcrtool.data.model.LeaderTierItem
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.character.*
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.*
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import cn.wthee.pcrtool.viewmodel.LeaderViewModel
import kotlinx.coroutines.launch

/**
 * 角色排行
 */
@Composable
fun LeaderTier(
    scrollState: LazyListState,
    toCharacterDetail: (Int) -> Unit,
    leaderViewModel: LeaderViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val type = remember {
        mutableStateOf(navViewModel.leaderTierType.value ?: 0)
    }
    type.value = navViewModel.leaderTierType.observeAsState().value ?: 0
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
    val spanCount = (Dimen.iconSize * 3).spanCount

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        //更新
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

        CommonResponseBox(
            responseData = leaderData,
            fabContent = { data ->
                //切换类型
                SelectTypeCompose(
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
                    navViewModel.leaderTierType.postValue(type.value)
                }

                //回到顶部
                FabCompose(
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
                        it.tier == leaderItem.tier.toString()
                    }
                    group =
                        LeaderTierGroup(leaderItem.tier, arrayListOf(), descInfo?.desc ?: "")
                    groupList.add(group)
                }
                group.leaderList.add(leaderItem)
            }

            if (groupList.isNotEmpty()) {
                LazyColumn(state = scrollState) {
                    items(
                        items = groupList,
                        key = {
                            it.tier
                        }
                    ) { groupList ->
                        LeaderGroup(
                            groupList,
                            spanCount,
                            toCharacterDetail
                        )
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
 * 角色评价信息
 */
@Composable
private fun LeaderGroup(
    groupData: LeaderTierGroup,
    spanCount: Int,
    toCharacterDetail: (Int) -> Unit,
    characterViewModel: CharacterViewModel? = hiltViewModel()
) {

    Column {
        //分组标题
        CommonGroupTitle(
            titleStart = stringResource(
                id = R.string.leader_tier_d,
                groupData.tier
            ),
            titleCenter = groupData.desc,
            titleEnd = groupData.leaderList.size.toString(),
//            backgroundColor = getTierColor(groupData.tier),
            modifier = Modifier.padding(
                horizontal = Dimen.mediumPadding,
                vertical = Dimen.largePadding
            )
        )

        //分组内容
        VerticalGrid(
            spanCount = spanCount,
            modifier = Modifier
                .padding(
                    start = Dimen.commonItemPadding,
                    end = Dimen.commonItemPadding
                )
                .animateContentSize(defaultSpring())
        ) {
            groupData.leaderList.forEach { leader ->
                LeaderItem(leader, toCharacterDetail, characterViewModel)
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
    val tipText = getLeaderUnknownTip()


    Row(
        modifier = Modifier
            .padding(
                start = Dimen.mediumPadding,
                end = Dimen.mediumPadding,
                top = Dimen.smallPadding,
                bottom = Dimen.mediumPadding
            )
            .fillMaxWidth()
    ) {
        Box {
            IconCompose(
                data = if (hasUnitId) {
                    ImageResourceHelper.getInstance()
                        .getMaxIconUrl(leader.unitId!!)
                } else {
                    leader.icon
                }
            ) {
                if (!unknown) {
                    toCharacterDetail(leader.unitId!!)
                } else {
                    ToastUtil.short(tipText)
                }
            }
            if (!unknown) {
                PositionIcon(
                    position = basicInfo!!.position,
                    modifier = Modifier
                        .padding(
                            end = Dimen.divLineHeight,
                            bottom = Dimen.divLineHeight
                        )
                        .align(Alignment.BottomEnd),
                    size = Dimen.exSmallIconSize
                )
            }
        }

        MainCard(
            modifier = Modifier
                .padding(start = Dimen.smallPadding)
                .heightIn(min = Dimen.cardHeight),
            onClick = {
                BrowserUtil.open(leader.url)
            }
        ) {
            Column {
                Row(
                    modifier = Modifier.padding(
                        horizontal = Dimen.mediumPadding,
                        vertical = Dimen.smallPadding
                    )
                ) {
                    //名称
                    MainContentText(
                        text = if (hasUnitId && !unknown) {
                            basicInfo!!.getNameF()
                        } else {
                            leader.name
                        },
                        textAlign = TextAlign.Start,
                        color = textColor,
                        maxLines = 1
                    )

                }
                Row(modifier = Modifier.padding(vertical = Dimen.mediumPadding)) {
                    if (!unknown) {
                        //获取方式
                        CharacterTag(
                            modifier = Modifier.padding(horizontal = Dimen.smallPadding),
                            text = getLimitTypeText(limitType = basicInfo!!.limitType),
                            backgroundColor = getLimitTypeColor(limitType = basicInfo.limitType),
                        )
                        //攻击
                        CharacterTag(
                            text = getAtkText(atkType = basicInfo.atkType),
                            backgroundColor = getAtkColor(atkType = basicInfo.atkType),
                        )
                    } else {
                        //获取方式
                        CharacterTag(
                            modifier = Modifier.padding(horizontal = Dimen.smallPadding),
                            text = if (!hasUnitId) {
                                stringResource(id = R.string.leader_need_sync)
                            } else {
                                tipText
                            },
                            backgroundColor = colorGray
                        )
                    }
                }
            }
        }

    }
}


@CombinedPreviews
@Composable
private fun LeaderGroupPreview() {
    val text = stringResource(id = R.string.debug_short_text)
    PreviewLayout {
        LeaderGroup(
            LeaderTierGroup(
                0,
                arrayListOf(
                    LeaderTierItem(name = text)
                ),
                text
            ),
            2,
            {},
            null
        )
    }
}