package cn.wthee.pcrtool.ui.tool.clan

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.components.IconHorizontalPagerIndicator
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.SelectTypeFab
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.tool.enemy.EnemyAllInfo
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.getZhNumberText
import cn.wthee.pcrtool.viewmodel.ClanBattleViewModel
import cn.wthee.pcrtool.viewmodel.EnemyViewModel


/**
 * 公会战 BOSS 详情
 * @see [cn.wthee.pcrtool.ui.tool.enemy.EnemyDetail]
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ClanBattleDetail(
    clanBattleId: Int,
    index: Int,
    maxPhase: Int,
    toSummonDetail: (Int, Int, Int, Int, Int) -> Unit,
    clanBattleViewModel: ClanBattleViewModel = hiltViewModel(),
    enemyViewModel: EnemyViewModel = hiltViewModel(),
) {
    //阶段选择状态
    val phaseIndex = remember {
        mutableStateOf(maxPhase - 1)
    }
    val clanBattleInfo =
        clanBattleViewModel.getAllClanBattleData(clanBattleId, phaseIndex.value + 1)
            .collectAsState(initial = null).value
    val pagerState =
        rememberPagerState(initialPage = index)


    //页面
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        clanBattleInfo?.let { clanBattleList ->
            //该期公会战数据
            val clanBattleValue = clanBattleList[0]

            //五个Boss信息
            val bossDataList =
                enemyViewModel.getAllBossAttr(clanBattleValue.enemyIdList).collectAsState(
                    initial = arrayListOf()
                ).value
            //所有部位信息
            val partEnemyMap = enemyViewModel.getMultiEnemyAttr(clanBattleValue.targetCountDataList)
                .collectAsState(initial = hashMapOf()).value
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
                val urls = arrayListOf<String>()
                clanBattleValue.unitIds.split("-").subList(0, 5).forEach {
                    urls.add(
                        ImageRequestHelper.getInstance()
                            .getUrl(ImageRequestHelper.ICON_UNIT, it)
                    )
                }
                IconHorizontalPagerIndicator(pagerState = pagerState, urls = urls)
                //BOSS信息
                HorizontalPager(
                    pageCount = 5,
                    state = pagerState,
                ) { pagerIndex ->
                    if (bossDataList.isNotEmpty()) {
                        EnemyAllInfo(
                            bossDataList[pagerIndex],
                            clanBattleValue.getMultiCount(pagerIndex) > 0,
                            partEnemyMap[bossDataList[pagerIndex].enemyId],
                            toSummonDetail
                        )
                    }
                }
            }

            //阶段选择
            val tabs = arrayListOf<String>()
            for (i in 1..maxPhase) {
                tabs.add(stringResource(id = R.string.phase, getZhNumberText(i)))
            }
            val sectionColor = getSectionTextColor(section = phaseIndex.value + 1)
            SelectTypeFab(
                icon = MainIconType.CLAN_SECTION,
                tabs = tabs,
                type = phaseIndex,
                selectedColor = sectionColor,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
            )
        }
    }
}


