package cn.wthee.pcrtool.ui.tool

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.ClanBattleInfo
import cn.wthee.pcrtool.data.db.view.ClanBossTargetInfo
import cn.wthee.pcrtool.data.db.view.EnemyParameterPro
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.PreviewBox
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.skill.SkillItem
import cn.wthee.pcrtool.ui.skill.SkillLoopList
import cn.wthee.pcrtool.ui.theme.CardTopShape
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.FadeAnimation
import cn.wthee.pcrtool.utils.ImageResourceHelper
import cn.wthee.pcrtool.utils.ImageResourceHelper.Companion.ICON_UNIT
import cn.wthee.pcrtool.utils.getZhNumberText
import cn.wthee.pcrtool.viewmodel.ClanViewModel
import cn.wthee.pcrtool.viewmodel.SkillViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import kotlinx.coroutines.launch

/**
 * 每月 BOSS 信息列表
 */
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalPagerApi
@Composable
fun ClanBattleList(
    scrollState: LazyGridState,
    toClanBossInfo: (Int, Int) -> Unit,
    clanViewModel: ClanViewModel = hiltViewModel()
) {

    val clanList =
        clanViewModel.getAllClanBattleData().collectAsState(initial = arrayListOf()).value
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        val visible = clanList.isNotEmpty()
        FadeAnimation(visible = visible) {
            LazyVerticalGrid(
                state = scrollState,
                columns = GridCells.Adaptive(getItemWidth())
            ) {
                items(clanList) {
                    ClanBattleItem(it, toClanBossInfo)
                }
                item {
                    CommonSpacer()
                }
            }
        }
        FadeAnimation(visible = !visible) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(getItemWidth())
            ) {
                items(20) {
                    ClanBattleItem(ClanBattleInfo(), toClanBossInfo)
                }
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
                } catch (e: Exception) {
                }
            }
        }
    }

}

/**
 * 图标列表
 * type 0：点击查看详情， 1：点击切换 BOSS
 */
@ExperimentalMaterialApi
@ExperimentalPagerApi
@Composable
private fun ClanBattleItem(
    clanInfo: ClanBattleInfo,
    toClanBossInfo: (Int, Int) -> Unit,
) {
    val placeholder = clanInfo.clan_battle_id == -1
    val section = clanInfo.getAllBossIds().size
    val list = clanInfo.getUnitIdList(0)

    Column(
        modifier = Modifier.padding(
            horizontal = Dimen.largePadding,
            vertical = Dimen.mediumPadding
        )
    ) {
        //标题
        Row(
            modifier = Modifier.padding(bottom = Dimen.mediumPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MainTitleText(
                text = clanInfo.getDate(),
                modifier = Modifier.placeholder(
                    visible = placeholder,
                    highlight = PlaceholderHighlight.shimmer()
                )
            )
            MainTitleText(
                text = stringResource(
                    id = R.string.section,
                    getZhNumberText(section)
                ),
                backgroundColor = getSectionTextColor(section),
                modifier = Modifier
                    .padding(start = Dimen.smallPadding)
                    .placeholder(
                        visible = placeholder,
                        highlight = PlaceholderHighlight.shimmer()
                    ),
            )
        }

        MainCard(
            modifier = Modifier
                .placeholder(
                    visible = placeholder,
                    highlight = PlaceholderHighlight.shimmer()
                )
        ) {
            //图标
            Row {
                list.forEachIndexed { index, it ->
                    Box(
                        modifier = Modifier.padding(Dimen.mediumPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        IconCompose(
                            data = ImageResourceHelper.getInstance().getUrl(ICON_UNIT, it.unitId)
                        ) {
                            if (!placeholder) {
                                toClanBossInfo(clanInfo.clan_battle_id, index)
                            }
                        }
                        //多目标提示
                        if (it.partEnemyIds.isNotEmpty()) {
                            MainTitleText(
                                text = it.partEnemyIds.size.toString(),
                                modifier = Modifier.align(Alignment.BottomEnd)
                            )
                        }
                    }
                }
            }
        }
    }

}

/**
 * 团队战 BOSS 详情
 */
@ExperimentalMaterialApi
@ExperimentalPagerApi
@Composable
fun ClanBossInfoPager(
    clanId: Int,
    index: Int,
    toSummonDetail: ((Int, Int) -> Unit)? = null,
    clanViewModel: ClanViewModel = hiltViewModel()
) {
    val clanInfo =
        clanViewModel.getClanInfo(clanId).collectAsState(initial = null).value
    val pagerState =
        rememberPagerState(initialPage = index)

    //页面
    Box(modifier = Modifier.fillMaxSize()) {
        clanInfo?.let { clanValue ->
            //最大阶段数
            val maxSection = clanValue.getAllBossIds().size
            //阶段选择状态
            val section = remember {
                mutableStateOf(maxSection - 1)
            }
            //Boss 信息
            val bossInfoList = clanValue.getUnitIdList(section.value)
            val enemyIds = arrayListOf<Int>()
            bossInfoList.forEach {
                enemyIds.add(it.enemyId)
            }
            val bossDataList =
                clanViewModel.getAllBossAttr(enemyIds).collectAsState(initial = arrayListOf()).value
            //Boss 部位信息
            val partEnemyMap = clanViewModel.getPartEnemyAttr(bossInfoList)
                .collectAsState(initial = hashMapOf()).value
            //图标列表
            val list = clanValue.getUnitIdList(0)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //日期
                MainTitleText(
                    text = clanValue.getDate(),
                    modifier = Modifier
                        .padding(
                            start = Dimen.largePadding,
                            top = Dimen.largePadding
                        )
                        .align(Alignment.Start)
                )
                //图标
                val urls = arrayListOf<String>()
                list.forEach { clanBossTargetInfo ->
                    urls.add(
                        ImageResourceHelper.getInstance()
                            .getUrl(ICON_UNIT, clanBossTargetInfo.unitId)
                    )
                }
                IconHorizontalPagerIndicator(pagerState = pagerState, urls = urls)
                //BOSS信息
                val visible = bossDataList.isNotEmpty()
                HorizontalPager(
                    count = 5,
                    state = pagerState,
                ) { pagerIndex ->
                    if (visible) {
                        ClanBossInfoPagerItem(
                            bossDataList,
                            pagerIndex,
                            list,
                            partEnemyMap,
                            toSummonDetail
                        )
                    }
                }
            }

            //阶段选择
            //阶段文本
            val tabs = arrayListOf<String>()
            for (i in 1..maxSection) {
                tabs.add(stringResource(id = R.string.section, getZhNumberText(i)))
            }
            val sectionColor = getSectionTextColor(section = section.value + 1)
            SelectTypeCompose(
                icon = MainIconType.CLAN_SECTION,
                tabs = tabs,
                type = section,
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
@ExperimentalMaterialApi
@Composable
private fun ClanBossInfoPagerItem(
    bossDataList: List<EnemyParameterPro>,
    pagerIndex: Int,
    list: List<ClanBossTargetInfo>,
    partEnemyMap: HashMap<Int, List<EnemyParameterPro>>,
    toSummonDetail: ((Int, Int) -> Unit)? = null,
) {
    val bossDataValue = bossDataList[pagerIndex]
    val expanded = remember {
        mutableStateOf(false)
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
            val desc = bossDataValue.getDesc()
            SelectionContainer {
                Text(
                    text = desc,
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
            val attr = if (list[pagerIndex].partEnemyIds.isNotEmpty()) {
                bossDataValue.attr.multiplePartEnemy()
            } else {
                bossDataValue.attr.enemy()
            }
            AttrList(attrs = attr)
            //多目标部位属性
            partEnemyMap[bossDataValue.unit_id]?.forEach {
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
            BossSkillList(pagerIndex, bossDataList, 2, toSummonDetail)
            CommonSpacer()
        }
    }

}


@Composable
fun BossSkillList(
    index: Int,
    bossList: List<EnemyParameterPro>,
    unitType: Int,
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


/**
 * 获取团队战阶段字体颜色
 */
@Composable
fun getSectionTextColor(section: Int): Color {
    val color = when (section) {
        1 -> R.color.color_rank_2_3
        2 -> R.color.color_rank_4_6
        3 -> R.color.color_rank_7_10
        4 -> R.color.color_rank_11_17
        else -> R.color.color_rank_18_20
    }
    return colorResource(id = color)
}

@Preview
@ExperimentalMaterialApi
@ExperimentalPagerApi
@Composable
private fun ClanBattleItemPreview() {
    PreviewBox {
        Column {
            ClanBattleItem(
                clanInfo = ClanBattleInfo(clan_battle_id = 1001),
                toClanBossInfo = { _, _ -> })
        }
    }
}
