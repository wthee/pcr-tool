package cn.wthee.pcrtool.ui.tool.clan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.ClanBattleEvent
import cn.wthee.pcrtool.data.db.view.ClanBattleInfo
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.PreviewBox
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.*
import cn.wthee.pcrtool.utils.ImageResourceHelper
import cn.wthee.pcrtool.utils.ImageResourceHelper.Companion.ICON_UNIT
import cn.wthee.pcrtool.utils.fixJpTime
import cn.wthee.pcrtool.utils.getZhNumberText
import cn.wthee.pcrtool.utils.intArrayList
import cn.wthee.pcrtool.viewmodel.ClanBattleViewModel
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.launch

/**
 * 每月 BOSS 信息列表
 */
@Composable
fun ClanBattleList(
    scrollState: LazyGridState,
    toClanBossInfo: (Int, Int, Int) -> Unit,
    clanBattleViewModel: ClanBattleViewModel = hiltViewModel()
) {

    val clanList =
        clanBattleViewModel.getAllClanBattleData().collectAsState(initial = arrayListOf()).value
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        val visible = clanList.isNotEmpty()
        LazyVerticalGrid(
            state = scrollState,
            columns = GridCells.Adaptive(getItemWidth())
        ) {
            if (visible) {
                items(
                    items = clanList,
                    key = {
                        it.clanBattleId
                    }
                ) {
                    ClanBattleItem(clanBattleInfo = it, toClanBossInfo = toClanBossInfo)
                }
            } else {
                items(20) {
                    ClanBattleItem(
                        clanBattleInfo = ClanBattleInfo(),
                        toClanBossInfo = toClanBossInfo
                    )
                }
            }

            item {
                CommonSpacer()
            }
        }
        //回到顶部
        FabCompose(
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
 * 图标列表
 * @param clanBattleEvent 不为空时，首页日程展示用
 */
@Composable
fun ClanBattleItem(
    clanBattleEvent: ClanBattleEvent? = null,
    clanBattleInfo: ClanBattleInfo? = null,
    toClanBossInfo: (Int, Int, Int) -> Unit,
    clanBattleViewModel: ClanBattleViewModel = hiltViewModel()
) {
    val mClanBattleInfo = clanBattleInfo
        ?: clanBattleViewModel.getAllClanBattleData(clanBattleEvent?.getClanBattleId() ?: -1)
            .collectAsState(initial = arrayListOf(ClanBattleInfo())).value[0]
    val placeholder = mClanBattleInfo.clanBattleId == -1
    val bossUnitIdList = mClanBattleInfo.unitIds.intArrayList.subList(0, 5)


    Column(
        modifier = Modifier.padding(
            horizontal = Dimen.largePadding,
            vertical = Dimen.mediumPadding
        )
    ) {
        //标题
        FlowRow(
            modifier = Modifier.padding(bottom = Dimen.mediumPadding),
            crossAxisAlignment = FlowCrossAxisAlignment.Center
        ) {
            if (clanBattleEvent == null) {
                //日期
                MainTitleText(
                    text = mClanBattleInfo.getDate(),
                    modifier = Modifier.commonPlaceholder(visible = placeholder)
                )
                //阶段数
                MainTitleText(
                    text = stringResource(
                        id = R.string.phase,
                        getZhNumberText(mClanBattleInfo.phase)
                    ),
                    backgroundColor = getSectionTextColor(mClanBattleInfo.phase),
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
                    clanBattleEvent.startTime,
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
                    modifier = Modifier.padding(
                        top = Dimen.mediumPadding,
                        start = Dimen.mediumPadding,
                        end = Dimen.mediumPadding
                    )
                ) {
                    bossUnitIdList.forEachIndexed { index, it ->
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            IconCompose(
                                data = ImageResourceHelper.getInstance().getUrl(ICON_UNIT, it)
                            ) {
                                if (!placeholder) {
                                    toClanBossInfo(
                                        mClanBattleInfo.clanBattleId,
                                        index,
                                        mClanBattleInfo.phase
                                    )
                                }
                            }
                            //多目标提示
                            val targetCount = mClanBattleInfo.getMultiCount(index)
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
                }

                //结束时间
                if (clanBattleEvent != null) {
                    //结束日期
                    CaptionText(
                        text = clanBattleEvent.getFixedEndTime().fixJpTime,
                        modifier = Modifier
                            .padding(top = Dimen.mediumPadding, end = Dimen.mediumPadding)
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


@Preview
@Composable
private fun ClanBattleItemPreview() {
    PreviewBox {
        Column {
            ClanBattleItem(
                clanBattleInfo = ClanBattleInfo(1001),
                toClanBossInfo = { _, _, _ -> })
        }
    }
}