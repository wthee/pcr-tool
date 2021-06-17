package cn.wthee.pcrtool.ui.tool

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.EnemyParameter
import cn.wthee.pcrtool.data.db.view.ClanBattleInfo
import cn.wthee.pcrtool.data.db.view.enemy
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.skill.SkillItem
import cn.wthee.pcrtool.ui.skill.SkillLoopList
import cn.wthee.pcrtool.ui.theme.CardTopShape
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.getZhNumberText
import cn.wthee.pcrtool.viewmodel.ClanViewModel
import cn.wthee.pcrtool.viewmodel.SkillViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import kotlinx.coroutines.launch

/**
 * 每月 BOSS 信息列表
 */
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
fun ClanBattleList(
    scrollState: LazyListState,
    toClanBossInfo: (Int, Int) -> Unit,
    clanViewModel: ClanViewModel = hiltViewModel()
) {
    clanViewModel.getAllClanBattleData()
    val clanList = clanViewModel.clanInfoList.observeAsState()
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val visible = clanList.value != null && clanList.value!!.isNotEmpty()
        FadeAnimation(visible = visible) {
            LazyColumn(
                state = scrollState,
                contentPadding = PaddingValues(Dimen.largePadding)
            ) {
                clanList.value?.let { list ->
                    items(list) {
                        ClanBattleItem(it, toClanBossInfo)
                    }
                }
                item {
                    CommonSpacer()
                }
            }
        }
        FadeAnimation(visible = !visible) {
            LazyColumn(contentPadding = PaddingValues(Dimen.largePadding)) {
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
    val section = clanInfo.getAllBossInfo().size
    val list = clanInfo.getUnitIdList(0)

    //标题
    Row(
        modifier = Modifier.padding(bottom = Dimen.mediuPadding),
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
            .padding(bottom = Dimen.largePadding)
            .placeholder(
                visible = placeholder,
                highlight = PlaceholderHighlight.shimmer()
            )
    ) {
        //图标
        Row(
            modifier = Modifier.padding(Dimen.mediuPadding),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            list.forEachIndexed { index, it ->
                Box {
                    IconCompose(data = Constants.UNIT_ICON_URL + it.unitId + Constants.WEBP) {
                        if (!placeholder) {
                            toClanBossInfo(clanInfo.clan_battle_id, index)
                        }
                    }
                    //多目标提示
                    if (it.targetCount > 1) {
                        MainTitleText(
                            text = "${it.targetCount - 1}",
                            modifier = Modifier.align(Alignment.BottomEnd)
                        )
                    }
                }
            }
        }
    }
}


/**
 * 团队战 BOSS 详情
 */
@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
fun ClanBossInfoPager(
    clanId: Int,
    index: Int,
    clanViewModel: ClanViewModel = hiltViewModel()
) {
    clanViewModel.getClanInfo(clanId)
    val clanInfo = clanViewModel.clanInfo.observeAsState()
    val pagerState = rememberPagerState(pageCount = 5, initialPage = index)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    clanInfo.value?.let { clanValue ->
        //最大阶段数
        val maxSection = clanValue.getAllBossInfo().size
        //阶段选择状态
        val section = remember {
            mutableStateOf(maxSection - 1)
        }
        //阶段文本
        val tabs = arrayListOf<String>()
        for (i in 1..maxSection) {
            tabs.add(getZhNumberText(i))
        }
        //Boss 信息
        val bossInfoList = clanValue.getUnitIdList(section.value)
        val enemyIds = arrayListOf<Int>()
        bossInfoList.forEach {
            enemyIds.add(it.enemyId)
        }
        clanViewModel.getAllBossAttr(enemyIds)
        val bossDataList = clanViewModel.allClanBossAttr.observeAsState()

        //页面
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                //标题
                MainText(
                    text = clanValue.getDate(),
                    modifier = Modifier.padding(top = Dimen.mediuPadding)
                )
                //阶段选择
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = Dimen.largePadding, end = Dimen.largePadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MainText(text = stringResource(id = R.string.title_section))
                    TabRow(
                        selectedTabIndex = section.value,
                        backgroundColor = Color.Transparent,
                        contentColor = MaterialTheme.colors.primary,
                        indicator = {
                            TabRowDefaults.Indicator(color = Color.Transparent)
                        },
                        divider = {
                            TabRowDefaults.Divider(color = Color.Transparent)
                        },
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                text = {
                                    SelectText(
                                        selected = section.value == index,
                                        text = title,
                                        selectedColor = getSectionTextColor(section = index + 1)
                                    )
                                },
                                selected = section.value == index,
                                onClick = {
                                    section.value = index
                                    VibrateUtil(context).single()
                                },
                            )
                        }
                    }
                }
                //图标列表
                val list = clanValue.getUnitIdList(0)
                //图标
                TabRow(
                    modifier = Modifier.fillMaxWidth(0.95f),
                    selectedTabIndex = pagerState.currentPage,
                    backgroundColor = Color.Transparent,
                    contentColor = MaterialTheme.colors.primary,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                        )
                    },
                    divider = {
                        TabRowDefaults.Divider(color = Color.Transparent)
                    },
                ) {
                    for (tabIndex in 0 until 5) {
                        Tab(
                            modifier = Modifier.padding(bottom = Dimen.mediuPadding),
                            icon = {
                                val it = list[tabIndex]
                                Box {
                                    IconCompose(data = Constants.UNIT_ICON_URL + it.unitId + Constants.WEBP)
                                    //多目标提示
                                    if (it.targetCount > 1) {
                                        MainTitleText(
                                            text = "${it.targetCount - 1}",
                                            modifier = Modifier.align(Alignment.BottomEnd)
                                        )
                                    }
                                }
                            },
                            selected = pagerState.currentPage == tabIndex,
                            onClick = {
                                scope.launch {
                                    pagerState.scrollToPage(tabIndex)
                                }
                                VibrateUtil(context).single()
                            },
                        )
                    }
                }
                //BOSS信息
                val visible = bossDataList.value != null && bossDataList.value!!.isNotEmpty()
                SlideAnimation(visible = visible) {
                    HorizontalPager(state = pagerState) { pagerIndex ->
                        if (visible) {
                            val bossDataValue = bossDataList.value!![pagerIndex]
                            Card(
                                shape = CardTopShape,
                                elevation = Dimen.cardElevation,
                                modifier = Modifier
                                    .padding(top = Dimen.mediuPadding)
                                    .fillMaxSize()
                            ) {
                                Column(
                                    modifier = Modifier.verticalScroll(rememberScrollState())
                                ) {
                                    //名称
                                    MainText(
                                        text = bossDataValue.name,
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .padding(Dimen.mediuPadding),
                                        selectable = true
                                    )
                                    Subtitle2(
                                        text = "BOSS ${pagerIndex + 1}",
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    )
                                    //等级
                                    Subtitle2(
                                        text = stringResource(
                                            id = R.string.level,
                                            bossDataValue.level
                                        ),
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    )
                                    //属性
                                    AttrList(attrs = bossDataValue.attr.enemy())
                                    //技能
                                    BossSkillList(pagerIndex, bossDataList.value!!)
                                    CommonSpacer()
                                }
                            }
                        }

                    }
                }
            }
        }


    }
}

@Composable
private fun BossSkillList(
    index: Int,
    bossList: List<EnemyParameter>,
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
                    isClanBoss = true
                )
            }
            list[index].forEach {
                SkillItem(level = it.level, skillDetail = it, isClanBoss = true)
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

