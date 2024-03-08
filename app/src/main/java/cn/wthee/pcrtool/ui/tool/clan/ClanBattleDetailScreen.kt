package cn.wthee.pcrtool.ui.tool.clan

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.ClanBattleInfo
import cn.wthee.pcrtool.data.db.view.EnemyParameterPro
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.navigation.navigateUp
import cn.wthee.pcrtool.ui.components.IconHorizontalPagerIndicator
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.SelectTypeFab
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.tool.enemy.EnemyDetailScreen
import cn.wthee.pcrtool.ui.tool.enemy.EnemyWeaknessContent
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.getZhNumberText

/**
 * 公会战 BOSS 详情
 */
@Composable
fun ClanBattleDetailScreen(
    toSummonDetail: (String) -> Unit,
    clanBattleDetailViewModel: ClanBattleDetailViewModel = hiltViewModel()
) {
    val uiState by clanBattleDetailViewModel.uiState.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(initialPage = uiState.bossIndex) { 5 }


    MainScaffold(
        fabWithCustomPadding = {
            //阶段选择
            val tabs = arrayListOf<String>()
            for (i in uiState.minPhase..uiState.maxPhase) {
                tabs.add(stringResource(id = R.string.phase, getZhNumberText(i)))
            }
            val sectionColor = getSectionTextColor(section = uiState.phaseIndex + uiState.minPhase)
            SelectTypeFab(
                icon = MainIconType.CLAN_SECTION,
                tabs = tabs,
                selectedIndex = uiState.phaseIndex,
                openDialog = uiState.openDialog,
                changeDialog = clanBattleDetailViewModel::changeDialog,
                selectedColor = sectionColor,
                changeSelect = clanBattleDetailViewModel::changeSelect
            )
        },
        mainFabIcon = if (uiState.openDialog) MainIconType.CLOSE else MainIconType.BACK,
        onMainFabClick = {
            if (uiState.openDialog) {
                clanBattleDetailViewModel.changeDialog(false)
            } else {
                navigateUp()
            }
        },
        enableClickClose = uiState.openDialog,
        onCloseClick = {
            clanBattleDetailViewModel.changeDialog(false)
        }
    ) {
        if (uiState.clanBattleList.isNotEmpty()) {
            ClanBattleDetailContent(
                clanBattleInfo = uiState.clanBattleList[0],
                bossDataList = uiState.bossDataList,
                pagerState = pagerState,
                toSummonDetail = toSummonDetail
            )
        }
    }
}

@Composable
private fun ClanBattleDetailContent(
    clanBattleInfo: ClanBattleInfo,
    bossDataList: List<EnemyParameterPro>,
    pagerState: PagerState,
    toSummonDetail: (String) -> Unit
) {

    //图标列表
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        //日期
        MainTitleText(
            text = getClanBattleDate(clanBattleInfo),
            modifier = Modifier.padding(vertical = Dimen.mediumPadding)
        )
        //图标
        val urlList = arrayListOf<String>()
        clanBattleInfo.unitIds.split("-").subList(0, 5).forEach {
            urlList.add(
                ImageRequestHelper.getInstance()
                    .getUrl(ImageRequestHelper.ICON_UNIT, it)
            )
        }

        //BOSS信息
        if (bossDataList.isNotEmpty()) {
            //图标指示器
            IconHorizontalPagerIndicator(pagerState = pagerState, urlList = urlList) { index ->
                clanBattleInfo.getWeakness(index)?.let {
                    Row(modifier = Modifier.padding(bottom = Dimen.smallPadding)) {
                        EnemyWeaknessContent(
                            weaknessData = it,
                            showText = false
                        )
                    }
                }
            }

            HorizontalPager(state = pagerState) {
                /**
                 * fixme 页面滑动问题
                 * PagerScope#page 滑动时不准确，导致数据加载异常
                 * 暂时替换为 pagerState.currentPage
                 */
                val enemy = bossDataList[pagerState.currentPage]
                if (!LocalInspectionMode.current) {
                    EnemyDetailScreen(
                        enemyId = enemy.enemyId,
                        toSummonDetail = toSummonDetail
                    )
                }
            }
        }
    }
}


/**
 * @see [EnemyDetailScreen] 属性布局预览
 */
@CombinedPreviews
@Composable
private fun ClanBattleDetailContentPreview() {
    PreviewLayout {
        ClanBattleDetailContent(
            clanBattleInfo = ClanBattleInfo(),
            bossDataList = arrayListOf(
                EnemyParameterPro(),
                EnemyParameterPro(),
                EnemyParameterPro(),
                EnemyParameterPro(),
                EnemyParameterPro(),
            ),
            pagerState = rememberPagerState {
                5
            },
            toSummonDetail = {}
        )
    }
}
