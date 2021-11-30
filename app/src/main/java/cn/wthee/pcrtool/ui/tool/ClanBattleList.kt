package cn.wthee.pcrtool.ui.tool

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.ClanBattleInfo
import cn.wthee.pcrtool.data.db.view.ClanBossTargetInfo
import cn.wthee.pcrtool.data.db.view.EnemyParameterPro
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.PreviewBox
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.skill.SkillItem
import cn.wthee.pcrtool.ui.skill.SkillLoopList
import cn.wthee.pcrtool.ui.theme.CardTopShape
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.FadeAnimation
import cn.wthee.pcrtool.ui.theme.defaultSpring
import cn.wthee.pcrtool.utils.ImageResourceHelper
import cn.wthee.pcrtool.utils.ImageResourceHelper.Companion.ICON_UNIT
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.getZhNumberText
import cn.wthee.pcrtool.viewmodel.ClanViewModel
import cn.wthee.pcrtool.viewmodel.SkillViewModel
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 每月 BOSS 信息列表
 */
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalPagerApi
@Composable
fun ClanBattleList(
    scrollState: LazyListState,
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
                cells = GridCells.Adaptive(getItemWidth())
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
                cells = GridCells.Adaptive(getItemWidth())
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
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val openDialog = MainActivity.navViewModel.openChangeDataDialog.observeAsState().value ?: false
    val close = MainActivity.navViewModel.fabCloseClick.observeAsState().value ?: false
    val mainIcon = MainActivity.navViewModel.fabMainIcon.observeAsState().value ?: MainIconType.BACK
    //切换关闭监听
    if (close) {
        MainActivity.navViewModel.openChangeDataDialog.postValue(false)
        MainActivity.navViewModel.fabMainIcon.postValue(MainIconType.BACK)
        MainActivity.navViewModel.fabCloseClick.postValue(false)
    }
    if (mainIcon == MainIconType.BACK) {
        MainActivity.navViewModel.openChangeDataDialog.postValue(false)
    }
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
                TabRow(
                    modifier = Modifier
                        .padding(top = Dimen.largePadding)
                        .width(getItemWidth()),
                    selectedTabIndex = pagerState.currentPage,
                    backgroundColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
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
                            modifier = Modifier.padding(bottom = Dimen.mediumPadding),
                            icon = {
                                val it = list[tabIndex]
                                Box {
                                    IconCompose(
                                        data = ImageResourceHelper.getInstance()
                                            .getUrl(ICON_UNIT, it.unitId)
                                    )
                                    //多目标提示
                                    if (it.partEnemyIds.isNotEmpty()) {
                                        MainTitleText(
                                            text = it.partEnemyIds.size.toString(),
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
            SelectSectionCompose(
                section = section,
                openDialog = openDialog,
                coroutineScope = scope,
                maxSection = maxSection,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
            )
        }
    }
}


//阶段Boss选择弹窗
@Composable
private fun SelectSectionCompose(
    section: MutableState<Int>,
    openDialog: Boolean,
    coroutineScope: CoroutineScope,
    maxSection: Int,
    modifier: Modifier
) {
    val context = LocalContext.current
    //阶段文本
    val tabs = arrayListOf<String>()
    for (i in 1..maxSection) {
        tabs.add(stringResource(id = R.string.section, getZhNumberText(i)))
    }
    val sectionColor = getSectionTextColor(section = section.value + 1)

    //数据切换
    SmallFloatingActionButton(
        modifier = modifier
            .animateContentSize(defaultSpring())
            .padding(
                end = Dimen.fabMarginEnd,
                start = Dimen.fabMargin,
                top = Dimen.fabMargin,
                bottom = Dimen.fabMargin,
            ),
        containerColor = MaterialTheme.colorScheme.background,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = Dimen.fabElevation),
        shape = if (openDialog) androidx.compose.material.MaterialTheme.shapes.medium else CircleShape,
        onClick = {
            VibrateUtil(context).single()
            if (!openDialog) {
                MainActivity.navViewModel.fabMainIcon.postValue(MainIconType.CLOSE)
                MainActivity.navViewModel.openChangeDataDialog.postValue(true)
            } else {
                MainActivity.navViewModel.fabCloseClick.postValue(true)
            }
        },
    ) {
        if (openDialog) {
            Column(
                modifier = Modifier.width(Dimen.dataChangeWidth),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //阶段选择
                tabs.forEachIndexed { index, tab ->
                    val mModifier = if (section.value == index) {
                        Modifier.fillMaxWidth()
                    } else {
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                VibrateUtil(context).single()
                                MainActivity.navViewModel.openChangeDataDialog.postValue(false)
                                MainActivity.navViewModel.fabCloseClick.postValue(true)
                                if (section.value != index) {
                                    coroutineScope.launch {
                                        section.value = index
                                    }
                                }
                            }
                    }
                    SelectText(
                        selected = section.value == index,
                        text = tab,
                        textStyle = MaterialTheme.typography.titleLarge,
                        selectedColor = sectionColor,
                        modifier = mModifier.padding(Dimen.mediumPadding)
                    )
                }
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = Dimen.largePadding)
            ) {
                IconCompose(
                    data = MainIconType.CLAN_SECTION.icon,
                    tint = sectionColor,
                    size = Dimen.menuIconSize
                )
                Text(
                    text = tabs[section.value],
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center,
                    color = sectionColor,
                    modifier = Modifier.padding(
                        start = Dimen.mediumPadding,
                        end = Dimen.largePadding
                    )
                )
            }

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

    Card(
        shape = CardTopShape,
        elevation = Dimen.cardElevation,
        backgroundColor = MaterialTheme.colorScheme.surface,
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
            BossSkillList(pagerIndex, bossDataList, toSummonDetail)
            CommonSpacer()
        }
    }

}


@Composable
private fun BossSkillList(
    index: Int,
    bossList: List<EnemyParameterPro>,
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
                    isClanBoss = true
                )
            }
            list[index].forEach {
                SkillItem(
                    level = it.level,
                    skillDetail = it,
                    isClanBoss = true,
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
