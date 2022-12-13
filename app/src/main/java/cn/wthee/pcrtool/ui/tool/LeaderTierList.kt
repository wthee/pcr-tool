package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.LeaderTierGroup
import cn.wthee.pcrtool.data.model.LeaderTierItem
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.*
import cn.wthee.pcrtool.utils.BrowserUtil
import cn.wthee.pcrtool.utils.ScreenUtil
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.dp2px
import cn.wthee.pcrtool.viewmodel.LeaderViewModel

/**
 * 角色排行
 */
@Composable
fun LeaderTier(
    scrollState: LazyListState,
    leaderViewModel: LeaderViewModel = hiltViewModel()
) {

    val type = remember {
        mutableStateOf(0)
    }
    val leaderData = leaderViewModel.getLeaderTier(type.value)
        .collectAsState(initial = null).value

    val tabs = arrayListOf(
        stringResource(id = R.string.leader_tier_0),
        stringResource(id = R.string.leader_tier_1),
        stringResource(id = R.string.leader_tier_2),
        stringResource(id = R.string.clan),
    )
    val url = stringResource(id = R.string.leader_source_url)
    val context = LocalContext.current
    val spanCount =
        ScreenUtil.getWidth() / (Dimen.iconSize * 3 + Dimen.largePadding * 2).value.dp2px
    //加载中
    MainActivity.navViewModel.loading.postValue(leaderData == null)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(horizontal = Dimen.largePadding)
                .fillMaxSize()
        ) {
            //更新
            Row(
                modifier = Modifier.padding(vertical = Dimen.mediumPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MainTitleText(
                    text = "GameWith",
                    modifier = Modifier
                        .clickable {
                            VibrateUtil(context).single()
                            BrowserUtil.open(context, url)
                        }
                )
                FadeAnimation(visible = leaderData != null && leaderData.desc != "") {
                    CaptionText(text = leaderData!!.desc, modifier = Modifier.fillMaxWidth())
                }
            }
            FadeAnimation(visible = leaderData?.leader?.isNotEmpty() == true) {
                //分组
                val groupList = arrayListOf<LeaderTierGroup>()
                leaderData?.leader?.forEach { leaderItem ->
                    var group = groupList.find {
                        it.tier == leaderItem.tier
                    }
                    if (group == null) {
                        val descInfo = leaderData.tierSummary.find {
                            it.tier == leaderItem.tier.toString()
                        }
                        group =
                            LeaderTierGroup(leaderItem.tier, arrayListOf(), descInfo?.desc ?: "")
                        groupList.add(group)
                    }
                    group.leaderList.add(leaderItem)
                }

                LazyColumn(state = scrollState) {
                    items(
                        items = groupList,
                        key = {
                            it.tier
                        }
                    ) { groupList ->
                        LeaderGroup(
                            groupList,
                            spanCount
                        )
                    }
                    item {
                        CommonSpacer()
                    }
                }
            }
        }


        SelectTypeCompose(
            icon = MainIconType.LEADER_TIER,
            tabs = tabs,
            type = type,
            width = Dimen.dataChangeWidth + Dimen.fabSize,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
        )
    }
}

/**
 * 角色评价信息
 */
@Composable
private fun LeaderGroup(
    groupData: LeaderTierGroup,
    spanCount: Int
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
            modifier = Modifier.padding(
                bottom = Dimen.largePadding,
                start = Dimen.commonItemPadding,
                end = Dimen.commonItemPadding
            ),
        ) {
            groupData.leaderList.forEach { leader ->
                LeaderItem(leader)
            }
        }
    }
}

/**
 * 角色
 */
@Composable
private fun LeaderItem(
    leader: LeaderTierItem
) {
    val context = LocalContext.current

    var leaderState by remember { mutableStateOf(leader) }
    if (leaderState != leader) {
        leaderState = leader
    }

    val icon: @Composable () -> Unit by remember {
        mutableStateOf(
            {
                IconCompose(
                    data = leaderState.icon
                )
            }
        )
    }
    val name: @Composable () -> Unit by remember {
        mutableStateOf(
            {
                MainContentText(
                    text = leaderState.name,
                    textAlign = TextAlign.Start,
                    maxLines = 2,
                    selectable = true,
                    modifier = Modifier.padding(start = Dimen.smallPadding),
                )
            }
        )
    }

    Row(
        modifier = Modifier
            .padding(
                start = Dimen.smallPadding,
                end = Dimen.smallPadding,
                bottom = Dimen.mediumPadding
            )
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.extraSmall)
            .clickable {
                BrowserUtil.open(context, leaderState.url)
            }
            .padding(Dimen.smallPadding)
    ) {
        icon()
        name()
    }
}

/**
 * 获取评级颜色
 */
private fun getTierColor(tier: Int) = when (tier) {
    0 -> colorRed
    1 -> colorPurple
    2 -> colorGold
    3 -> colorGreen
    else -> colorBlue
}