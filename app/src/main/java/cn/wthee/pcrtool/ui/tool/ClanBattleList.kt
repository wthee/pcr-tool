package cn.wthee.pcrtool.ui.tool

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.EnemyParameter
import cn.wthee.pcrtool.data.db.view.ClanBattleInfo
import cn.wthee.pcrtool.data.db.view.enemy
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.skill.SkillItem
import cn.wthee.pcrtool.ui.skill.SkillLoopList
import cn.wthee.pcrtool.ui.theme.CardTopShape
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shapes
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.getZhNumberText
import cn.wthee.pcrtool.viewmodel.ClanViewModel
import cn.wthee.pcrtool.viewmodel.SkillViewModel
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

/**
 * 每月 BOSS 信息列表
 */
@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
fun ClanBattleList(
    toClanBossInfo: (Int, Int) -> Unit,
    clanViewModel: ClanViewModel = hiltViewModel()
) {
    clanViewModel.getAllClanBattleData()
    val clanList = clanViewModel.clanInfoList.observeAsState()
    val state = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    navViewModel.loading.postValue(true)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.bg_gray))
    ) {
        SlideAnimation(visible = clanList.value != null) {
            clanList.value?.let { data ->
                navViewModel.loading.postValue(false)
                LazyColumn(state = state, contentPadding = PaddingValues(Dimen.mediuPadding)) {
                    items(data) {
                        ClanBattleItem(it, toClanBossInfo)
                    }
                    item {
                        CommonSpacer()
                    }
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
                state.scrollToItem(0)
            }
        }
    }

}

/**
 * 图标列表
 * type 0：点击查看详情， 1：点击切换 BOSS
 */
@ExperimentalPagerApi
@Composable
private fun ClanBattleItem(
    clanInfo: ClanBattleInfo,
    toClanBossInfo: (Int, Int) -> Unit,
) {
    val section = clanInfo.getAllBossInfo().size
    val list = clanInfo.getUnitIdList(0)

    //标题
    Row(modifier = Modifier.padding(bottom = Dimen.mediuPadding)) {
        MainTitleText(text = clanInfo.getDate())
        MainTitleText(
            text = stringResource(
                id = R.string.section,
                getZhNumberText(section)
            ),
            backgroundColor = getSectionTextColor(section),
            modifier = Modifier.padding(start = Dimen.smallPadding),
        )
    }

    MainCard(modifier = Modifier.padding(bottom = Dimen.largePadding)) {
        //图标
        Row(
            modifier = Modifier.padding(Dimen.mediuPadding),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            list.forEachIndexed { index, it ->
                Box {
                    IconCompose(data = Constants.UNIT_ICON_URL + it.unitId + Constants.WEBP) {
                        toClanBossInfo(clanInfo.clan_battle_id, index)
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
        Box(modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.bg_gray))) {
            Column {
                //图标列表
                val list = clanValue.getUnitIdList(0)
                //标题
                Subtitle1(
                    text = "阶段${tabs[section.value]}",
                    color = getSectionTextColor(section = section.value + 1),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = Dimen.smallPadding)
                )
                Subtitle2(
                    text = clanValue.getDate(),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = Dimen.smallPadding)
                )
                //图标
                Row(
                    modifier = Modifier
                        .padding(Dimen.mediuPadding)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    list.forEachIndexed { index, it ->
                        Box {
                            IconCompose(data = Constants.UNIT_ICON_URL + it.unitId + Constants.WEBP) {
                                scope.launch {
                                    pagerState.scrollToPage(index)
                                }
                            }
                            //多目标提示
                            if (it.targetCount > 1) {
                                MainTitleText(
                                    text = "${it.targetCount - 1}",
                                    modifier = Modifier.align(Alignment.BottomEnd)
                                )
                            }
                            if (index == pagerState.currentPage) {
                                Spacer(
                                    modifier = Modifier
                                        .navigationBarsPadding()
                                        .size(Dimen.iconSize)
                                        .background(
                                            color = colorResource(id = R.color.alpha_primary),
                                            shape = Shapes.small
                                        )
                                )
                            }
                        }
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
                                            .padding(Dimen.mediuPadding)
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
            //阶段选择
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(
                        start = Dimen.fabMarginEnd,
                        end = Dimen.fabMarginEnd,
                        bottom = Dimen.fabMargin
                    )
                    .height(Dimen.fabSize)
                    .shadow(elevation = Dimen.fabElevation, shape = CircleShape, clip = true)
                    .background(color = MaterialTheme.colors.surface, shape = CircleShape)
            ) {
                TabRow(
                    selectedTabIndex = section.value,
                    backgroundColor = Color.Transparent,
                    contentColor = MaterialTheme.colors.primary,
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            text = {
                                Text(
                                    title,
                                    color = getSectionTextColor(section = index + 1)
                                )
                            },
                            selected = section.value == index,
                            onClick = {
                                section.value = index
                            },
                        )
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
                .padding(Dimen.mediuPadding)
                .fillMaxSize()
        ) {
            if (allLoopData.value != null && allIcon.value != null) {
                SkillLoopList(
                    allLoopData.value!![index],
                    allIcon.value!![index],
                    scrollable = false
                )
            }
            list[index].forEach {
                SkillItem(level = it.level, skillDetail = it)
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

