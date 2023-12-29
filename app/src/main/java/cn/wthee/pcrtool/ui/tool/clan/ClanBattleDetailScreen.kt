package cn.wthee.pcrtool.ui.tool.clan

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.navigation.navigateUp
import cn.wthee.pcrtool.ui.components.IconHorizontalPagerIndicator
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.SelectTypeFab
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.tool.enemy.EnemyDetailScreen
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.getZhNumberText

/**
 * 公会战 BOSS 详情
 * @see [cn.wthee.pcrtool.ui.tool.enemy.EnemyDetailScreen]
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ClanBattleDetailScreen(
    index: Int,
    toSummonDetail: (Int, Int, Int, Int, Int) -> Unit,
    clanBattleDetailViewModel: ClanBattleDetailViewModel = hiltViewModel()
) {
    val pagerState =
        rememberPagerState(initialPage = index) { 5 }

    val uiState by clanBattleDetailViewModel.uiState.collectAsStateWithLifecycle()


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
            //该期公会战数据
            val clanBattleValue = uiState.clanBattleList[0]

            //图标列表
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //日期
                MainTitleText(
                    text = getClanBattleDate(clanBattleValue),
                    modifier = Modifier.padding(vertical = Dimen.mediumPadding)
                )
                //图标
                val urlList = arrayListOf<String>()
                clanBattleValue.unitIds.split("-").subList(0, 5).forEach {
                    urlList.add(
                        ImageRequestHelper.getInstance()
                            .getUrl(ImageRequestHelper.ICON_UNIT, it)
                    )
                }
                IconHorizontalPagerIndicator(pagerState = pagerState, urlList = urlList)
                //BOSS信息
                HorizontalPager(state = pagerState) { pagerIndex ->
                    if (uiState.bossDataList.isNotEmpty()) {
                        val enemy = uiState.bossDataList[pagerIndex]
                        EnemyDetailScreen(
                            enemyId = enemy.enemyId,
                            toSummonDetail = toSummonDetail
                        )
                    }
                }
            }
        }
    }
}


