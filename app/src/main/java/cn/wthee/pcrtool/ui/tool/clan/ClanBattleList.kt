package cn.wthee.pcrtool.ui.tool.clan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.ClanBattleEvent
import cn.wthee.pcrtool.data.db.view.ClanBattleInfo
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.components.CaptionText
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.EventTitle
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.Subtitle2
import cn.wthee.pcrtool.ui.components.commonPlaceholder
import cn.wthee.pcrtool.ui.components.getItemWidth
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.colorCopper
import cn.wthee.pcrtool.ui.theme.colorGold
import cn.wthee.pcrtool.ui.theme.colorOrange
import cn.wthee.pcrtool.ui.theme.colorPurple
import cn.wthee.pcrtool.ui.theme.colorRed
import cn.wthee.pcrtool.ui.theme.colorSilver
import cn.wthee.pcrtool.ui.theme.colorWhite
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.ImageRequestHelper.Companion.ICON_UNIT
import cn.wthee.pcrtool.utils.fillZero
import cn.wthee.pcrtool.utils.fixJpTime
import cn.wthee.pcrtool.utils.formatTime
import cn.wthee.pcrtool.utils.getZhNumberText
import cn.wthee.pcrtool.utils.intArrayList
import cn.wthee.pcrtool.viewmodel.ClanBattleViewModel
import kotlinx.coroutines.launch

/**
 * 每月 BOSS 信息列表
 */
@Composable
fun ClanBattleList(
    scrollState: LazyGridState,
    toClanBossInfo: (Int, Int, Int, Int) -> Unit,
    clanBattleViewModel: ClanBattleViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    //会战列表
    val clanListFlow = remember {
        clanBattleViewModel.getAllClanBattleData()
    }
    val clanList by clanListFlow.collectAsState(initial = arrayListOf())


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        val visible = clanList.isNotEmpty()
        if (visible) {
            LazyVerticalGrid(
                state = scrollState,
                columns = GridCells.Adaptive(getItemWidth())
            ) {
                items(
                    items = clanList,
                    key = {
                        it.clanBattleId
                    }
                ) {
                    ClanBattleItem(clanBattleInfo = it, toClanBossInfo = toClanBossInfo)
                }
                item {
                    CommonSpacer()
                }
            }
        } else {
            LazyVerticalGrid(
                state = rememberLazyGridState(),
                columns = GridCells.Adaptive(getItemWidth())
            ) {
                items(10) {
                    ClanBattleItem(
                        clanBattleInfo = ClanBattleInfo(),
                        toClanBossInfo = toClanBossInfo
                    )
                }
                item {
                    CommonSpacer()
                }
            }

        }
        //回到顶部
        MainSmallFab(
            iconType = MainIconType.CLAN,
            text = stringResource(id = R.string.tool_clan),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
        ) {
            coroutineScope.launch {
                try {
                    scrollState.scrollToItem(0)
                } catch (_: Exception) {
                }
            }
        }
    }

}

/**
 * 首页预览用
 */
@Composable
fun ClanBattleOverview(
    clanBattleEvent: ClanBattleEvent,
    toClanBossInfo: (Int, Int, Int, Int) -> Unit,
    clanBattleViewModel: ClanBattleViewModel = hiltViewModel()
) {
    //会战列表
    val clanListFlow = remember {
        clanBattleViewModel.getAllClanBattleData(clanBattleEvent.id)
    }
    val clanList by clanListFlow.collectAsState(initial = arrayListOf())


    if (clanList.isNotEmpty()) {
        ClanBattleItem(clanBattleEvent, clanList[0], toClanBossInfo)
    } else {
        Column(
            modifier = Modifier.padding(
                horizontal = Dimen.largePadding,
                vertical = Dimen.mediumPadding
            )
        ) {
            //标题
            Row(
                modifier = Modifier.padding(bottom = Dimen.mediumPadding)
            ) {
                //标题
                MainTitleText(
                    text = stringResource(id = R.string.tool_clan),
                    modifier = Modifier.padding(end = Dimen.smallPadding),
                    backgroundColor = colorOrange
                )
                //显示倒计时
                EventTitle(
                    clanBattleEvent.startTime.formatTime,
                    clanBattleEvent.getFixedEndTime(),
                    showDays = false
                )
            }

            MainCard {
                Column(Modifier.padding(bottom = Dimen.mediumPadding)) {
                    Row(
                        modifier = Modifier
                            .padding(Dimen.mediumPadding)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Subtitle2(text = stringResource(id = R.string.tool_clan_no_boss))
                    }

                    //结束日期
                    CaptionText(
                        text = clanBattleEvent.getFixedEndTime().fixJpTime,
                        modifier = Modifier
                            .padding(end = Dimen.mediumPadding)
                            .fillMaxWidth()
                    )
                }

            }
        }
    }
}

/**
 * 图标列表
 * @param clanBattleEvent 不为空时，首页日程展示用
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ClanBattleItem(
    clanBattleEvent: ClanBattleEvent? = null,
    clanBattleInfo: ClanBattleInfo,
    toClanBossInfo: (Int, Int, Int, Int) -> Unit
) {
    val placeholder = clanBattleInfo.clanBattleId == -1
    val bossUnitIdList = try {
        clanBattleInfo.unitIds.intArrayList.subList(0, 5)
    } catch (_: Exception) {
        arrayListOf()
    }


    Column(
        modifier = Modifier.padding(
            horizontal = Dimen.largePadding,
            vertical = Dimen.mediumPadding
        )
    ) {
        //标题
        FlowRow(
            modifier = Modifier.padding(bottom = Dimen.mediumPadding)
        ) {
            if (clanBattleEvent == null) {
                //日期
                MainTitleText(
                    text = getClanBattleDate(clanBattleInfo),
                    modifier = Modifier.commonPlaceholder(visible = placeholder)
                )
                //阶段数
                MainTitleText(
                    text = stringResource(
                        id = R.string.phase,
                        getZhNumberText(clanBattleInfo.maxPhase)
                    ),
                    backgroundColor = getSectionTextColor(clanBattleInfo.maxPhase),
                    modifier = Modifier
                        .padding(start = Dimen.smallPadding)
                        .commonPlaceholder(visible = placeholder)
                )
            } else {
                //标题
                MainTitleText(
                    text = stringResource(id = R.string.tool_clan),
                    modifier = Modifier.padding(end = Dimen.smallPadding),
                    backgroundColor = colorOrange
                )
                //首页显示倒计时
                EventTitle(
                    clanBattleEvent.startTime.formatTime,
                    clanBattleEvent.getFixedEndTime(),
                    showDays = false
                )
            }

        }

        MainCard(
            modifier = Modifier.commonPlaceholder(visible = placeholder)
        ) {
            Column(Modifier.padding(bottom = Dimen.mediumPadding)) {
                //图标
                Row(
                    modifier = Modifier
                        .padding(Dimen.mediumPadding)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    bossUnitIdList.forEachIndexed { index, it ->
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            MainIcon(
                                data = ImageRequestHelper.getInstance().getUrl(ICON_UNIT, it)
                            ) {
                                if (!placeholder) {
                                    toClanBossInfo(
                                        clanBattleInfo.clanBattleId,
                                        index,
                                        clanBattleInfo.minPhase,
                                        clanBattleInfo.maxPhase,
                                    )
                                }
                            }
                            //多目标提示
                            val targetCount = clanBattleInfo.getMultiCount(index)
                            if (targetCount > 0) {
                                //阴影
                                MainText(
                                    text = targetCount.toString(),
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(
                                        start = Dimen.textElevation,
                                        top = Dimen.textElevation
                                    )
                                )
                                MainText(
                                    text = targetCount.toString(),
                                    color = colorWhite,
                                )
                            }
                        }
                    }
                    if (placeholder) {
                        MainIcon(
                            data = R.drawable.unknown_gray
                        )
                    }
                }

                //结束时间
                if (clanBattleEvent != null) {
                    //结束日期
                    CaptionText(
                        text = clanBattleEvent.getFixedEndTime().fixJpTime,
                        modifier = Modifier
                            .padding(end = Dimen.mediumPadding)
                            .fillMaxWidth()
                    )
                }
            }

        }
    }

}


/**
 * 获取公会战阶段字体颜色
 */
@Composable
fun getSectionTextColor(section: Int) = when (section) {
    1 -> colorCopper
    2 -> colorSilver
    3 -> colorGold
    4 -> colorPurple
    else -> colorRed
}

/**
 * 获取公会战年月
 */
@Composable
fun getClanBattleDate(clanBattleInfo: ClanBattleInfo): String {
    return stringResource(
        R.string.clan_battle_y_m,
        clanBattleInfo.startTime.substring(0, 4),
        clanBattleInfo.releaseMonth.toString().fillZero()
    )
}

@CombinedPreviews
@Composable
private fun ClanBattleItemPreview() {
    PreviewLayout {
        ClanBattleItem(
            clanBattleInfo = ClanBattleInfo(1001),
            toClanBossInfo = { _, _, _, _ -> })
    }
}