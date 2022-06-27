package cn.wthee.pcrtool.ui.tool.clan

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.EnemyParameterPro
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.UnitType
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.skill.SkillItem
import cn.wthee.pcrtool.ui.skill.SkillLoopList
import cn.wthee.pcrtool.ui.theme.CardTopShape
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.ImageResourceHelper
import cn.wthee.pcrtool.utils.getZhNumberText
import cn.wthee.pcrtool.viewmodel.ClanViewModel
import cn.wthee.pcrtool.viewmodel.SkillViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState


/**
 * 团队战 BOSS 详情
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun ClanBattleDetail(
    clanBattleId: Int,
    index: Int,
    maxPhase: Int,
    toSummonDetail: ((Int, Int) -> Unit)? = null,
    clanViewModel: ClanViewModel = hiltViewModel()
) {
    //阶段选择状态
    val phaseIndex = remember {
        mutableStateOf(maxPhase - 1)
    }
    val clanBattleInfo =
        clanViewModel.getAllClanBattleData(clanBattleId, phaseIndex.value + 1)
            .collectAsState(initial = null).value
    val pagerState =
        rememberPagerState(initialPage = index)

    //页面
    Box(modifier = Modifier.fillMaxSize()) {
        clanBattleInfo?.let { clanBattleList ->
            //该期团队战数据
            val clanBattleValue = clanBattleList[0]

            //五个Boss信息
            val bossDataList =
                clanViewModel.getAllBossAttr(clanBattleValue.enemyIdList).collectAsState(
                    initial = arrayListOf()
                ).value
            //所有部位信息
            val partEnemyMap = clanViewModel.getMultiEnemyAttr(clanBattleValue.targetCountData)
                .collectAsState(initial = hashMapOf()).value
            //图标列表
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //日期
                MainTitleText(
                    text = clanBattleValue.getDate(),
                    modifier = Modifier
                        .padding(
                            start = Dimen.largePadding,
                            top = Dimen.largePadding
                        )
                        .align(Alignment.Start)
                )
                //图标
                val urls = arrayListOf<String>()
                clanBattleValue.unitIdList.forEach {
                    urls.add(
                        ImageResourceHelper.getInstance()
                            .getUrl(ImageResourceHelper.ICON_UNIT, it)
                    )
                }
                IconHorizontalPagerIndicator(pagerState = pagerState, urls = urls)
                //BOSS信息
                HorizontalPager(
                    count = 5,
                    state = pagerState,
                ) { pagerIndex ->
                    if (bossDataList.isNotEmpty()) {
                        ClanBattleBossItem(
                            pagerIndex,
                            clanBattleValue.getMultiCount(pagerIndex) > 0,
                            bossDataList,
                            partEnemyMap,
                            toSummonDetail
                        )
                    }
                }
            }

            //阶段选择
            //阶段文本
            val tabs = arrayListOf<String>()
            for (i in 1..maxPhase) {
                tabs.add(stringResource(id = R.string.phase, getZhNumberText(i)))
            }
            val sectionColor = getSectionTextColor(section = phaseIndex.value + 1)
            SelectTypeCompose(
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


/**
 * Boss 信息详情
 */
@Composable
private fun ClanBattleBossItem(
    pagerIndex: Int,
    isMultiEnemy: Boolean,
    bossDataList: List<EnemyParameterPro>,
    partEnemyMap: HashMap<Int, List<EnemyParameterPro>>,
    toSummonDetail: ((Int, Int) -> Unit)? = null,
) {
    val bossDataValue = bossDataList[pagerIndex]
    val expanded = remember {
        mutableStateOf(false)
    }
    val attr = if (isMultiEnemy) {
        bossDataValue.attr.multiplePartEnemy()
    } else {
        bossDataValue.attr.enemy()
    }

    MainCard(
        shape = CardTopShape,
        modifier = Modifier
            .padding(top = Dimen.mediumPadding)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            //描述
            SelectionContainer {
                Text(
                    text = bossDataValue.getDesc(),
                    maxLines = if (expanded.value) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier
                        .animateContentSize()
                        .padding(Dimen.mediumPadding)
                        .clickable {
                            expanded.value = !expanded.value
                        }
                )
            }
            //名称
            MainText(
                text = bossDataValue.name,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = Dimen.mediumPadding),
                selectable = true
            )
            //等级
            CaptionText(
                text = bossDataValue.level.toString(),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            //属性
            AttrList(attrs = attr)
            //多目标部位属性
            partEnemyMap[bossDataValue.enemy_id]?.forEach {
                //名称
                MainText(
                    text = it.name,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = Dimen.largePadding),
                    selectable = true
                )
                //属性
                AttrList(attrs = it.attr.enemy())
            }
            DivCompose(Modifier.align(Alignment.CenterHorizontally))
            //技能
            BossSkillList(pagerIndex, bossDataList, UnitType.ENEMY, toSummonDetail)
            CommonSpacer()
        }
    }

}


/**
 * Boss 技能信息
 */
@Composable
fun BossSkillList(
    index: Int,
    bossList: List<EnemyParameterPro>,
    unitType: UnitType,
    toSummonDetail: ((Int, Int) -> Unit)? = null,
    skillViewModel: SkillViewModel = hiltViewModel()
) {
    skillViewModel.getAllEnemySkill(bossList)
    skillViewModel.getAllSkillLoops(bossList)
    val allSkillList = skillViewModel.allSkills.observeAsState()
    val allLoopData = skillViewModel.allAtkPattern.observeAsState()
    val allIcon = skillViewModel.allIconTypes.observeAsState()


    allSkillList.value?.let { list ->
        Column(
            modifier = Modifier
                .padding(Dimen.largePadding)
                .fillMaxSize()
        ) {
            if (allLoopData.value != null && allIcon.value != null) {
                SkillLoopList(
                    allLoopData.value!![index],
                    allIcon.value!![index],
                    unitType = unitType
                )
            }
            Spacer(modifier = Modifier.padding(top = Dimen.largePadding))
            list[index].forEachIndexed { index, skillDetail ->
                SkillItem(
                    skillIndex = index,
                    skillDetail = skillDetail,
                    unitType = unitType,
                    toSummonDetail = toSummonDetail
                )
            }
        }
    }
}