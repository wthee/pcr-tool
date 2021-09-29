package cn.wthee.pcrtool.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.navArgument
import androidx.paging.ExperimentalPagingApi
import androidx.paging.compose.collectAsLazyPagingItems
import cn.wthee.pcrtool.data.db.view.PvpCharacterData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.FilterCharacter
import cn.wthee.pcrtool.data.model.FilterEquipment
import cn.wthee.pcrtool.database.getRegion
import cn.wthee.pcrtool.ui.character.*
import cn.wthee.pcrtool.ui.common.fadeIn
import cn.wthee.pcrtool.ui.common.fadeOut
import cn.wthee.pcrtool.ui.equip.EquipList
import cn.wthee.pcrtool.ui.equip.EquipMainInfo
import cn.wthee.pcrtool.ui.equip.EquipMaterialDeatil
import cn.wthee.pcrtool.ui.home.Overview
import cn.wthee.pcrtool.ui.tool.*
import cn.wthee.pcrtool.viewmodel.NewsViewModel
import cn.wthee.pcrtool.viewmodel.TweetViewModel
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

object Navigation {
    const val HOME = "home"
    const val CHARACTER_LIST = "characterList"
    const val CHARACTER_DETAIL = "characterDetail"
    const val CHARACTER_PICS = "characterPics"
    const val CHARACTER_BASIC_INFO = "characterBasicInfo"
    const val EQUIP_LIST = "equipList"
    const val EQUIP_DETAIL = "equipDetail"
    const val RANK_EQUIP = "rankEquip"
    const val RANK_COMPARE = "rankCompare"
    const val EQUIP_COUNT = "equipCount"
    const val EQUIP_MATERIAL = "equipMaterial"
    const val TOOL_LEADER = "toolLeader"
    const val TOOL_GACHA = "toolGacha"
    const val TOOL_EVENT = "toolEvent"
    const val TOOL_GUILD = "toolGuild"
    const val TOOL_CLAN = "toolClanBattle"
    const val TOOL_CLAN_BOSS_INFO = "toolClanBattleInfo"
    const val TOOL_PVP = "toolPvpSearch"
    const val TOOL_NEWS = "toolNews"
    const val TOOL_NEWS_DETAIL = "toolNewsDetail"
    const val MAIN_SETTINGS = "mainSettings"
    const val APP_NOTICE = "appNotice"
    const val TWEET = "tweet"
    const val COMIC = "comic"
    const val ALL_SKILL = "allSkill"
    const val ATTR_COE = "attrCoe"

    const val UNIT_ID = "unitId"
    const val EQUIP_ID = "equipId"
    const val MAX_RANK = "maxRank"
    const val LEVEL = "level"
    const val RARITY = "rarity"
    const val UNIQUE_EQUIP_LEVEL = "uniqueEquipLevel"
    const val COMIC_ID = "comicId"
    const val TOOL_CLAN_BOSS_ID = "toolClanBattleID"
    const val TOOL_CLAN_BOSS_INDEX = "toolClanBattleIndex"
    const val TOOL_NEWS_KEY = "toolNewsKey"

}


@ExperimentalCoilApi
@ExperimentalComposeUiApi
@ExperimentalPagingApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: NavViewModel,
    actions: NavActions,
    newsViewModel: NewsViewModel = hiltViewModel(),
    tweetViewModel: TweetViewModel = hiltViewModel(),
) {
    //fixme 公告页面、推文分页数据在此处加载，跳转后不会返回顶部
    val scrollState0 = rememberLazyListState()
    val scrollState1 = rememberLazyListState()
    val scrollState2 = rememberLazyListState()
    //公告默认显示页
    var initialPage = 0
    if (getRegion() - 2 == 0) {
        initialPage = getRegion() - 2
    }
    val pagerState =
        rememberPagerState(pageCount = 3, initialOffscreenLimit = 2, initialPage = initialPage)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    newsViewModel.getNews(2)
    newsViewModel.getNews(3)
    newsViewModel.getNews(4)
    val flow0 = newsViewModel.newsPageList0
    val flow1 = newsViewModel.newsPageList1
    val flow2 = newsViewModel.newsPageList2
    val news0 = remember(flow0, lifecycle) {
        flow0?.flowWithLifecycle(lifecycle = lifecycle)
    }?.collectAsLazyPagingItems()
    val news1 = remember(flow1, lifecycle) {
        flow1?.flowWithLifecycle(lifecycle = lifecycle)
    }?.collectAsLazyPagingItems()
    val news2 = remember(flow2, lifecycle) {
        flow2?.flowWithLifecycle(lifecycle = lifecycle)
    }?.collectAsLazyPagingItems()

    //推文
    val tweetScrollState = rememberLazyListState()
    tweetViewModel.getTweet()
    val flow = tweetViewModel.tweetPageList
    val tweet = remember(flow, lifecycle) {
        flow?.flowWithLifecycle(lifecycle = lifecycle)
    }?.collectAsLazyPagingItems()


    AnimatedNavHost(navController, startDestination = Navigation.HOME) {

        //首页
        composable(
            route = Navigation.HOME,
            enterTransition = { _, _ -> fadeIn },
            exitTransition = { _, _ -> fadeOut },
            popEnterTransition = { _, _ -> fadeIn }
        ) {
            viewModel.fabMainIcon.postValue(MainIconType.MAIN)
            Overview(actions = actions)
        }

        //角色列表
        composable(
            route = Navigation.CHARACTER_LIST,
            enterTransition = { _, _ -> fadeIn },
            exitTransition = { _, _ -> fadeOut },
            popEnterTransition = { _, _ -> fadeIn },
            popExitTransition = { _, _ -> fadeOut }
        ) {
            viewModel.fabMainIcon.postValue(MainIconType.BACK)
            val scrollState = rememberLazyListState()
            CharacterList(scrollState, actions.toCharacterDetail)
        }

        //角色属性详情
        composable(
            route = "${Navigation.CHARACTER_DETAIL}/{${Navigation.UNIT_ID}}",
            arguments = listOf(navArgument(Navigation.UNIT_ID) {
                type = NavType.IntType
            }),
            enterTransition = { _, _ -> fadeIn },
            exitTransition = { _, _ -> fadeOut },
            popEnterTransition = { _, _ -> fadeIn },
            popExitTransition = { _, _ -> fadeOut }
        ) {
            val arguments = requireNotNull(it.arguments)
            viewModel.fabMainIcon.postValue(MainIconType.BACK)
            val scrollState = rememberScrollState()
            CharacterDetail(
                scrollState,
                unitId = arguments.getInt(Navigation.UNIT_ID),
                actions,
                viewModel
            )
        }

        //角色图片详情
        composable(
            route = "${Navigation.CHARACTER_PICS}/{${Navigation.UNIT_ID}}",
            arguments = listOf(navArgument(Navigation.UNIT_ID) {
                type = NavType.IntType
            }),
            enterTransition = { _, _ -> fadeIn },
            exitTransition = { _, _ -> fadeOut },
            popEnterTransition = { _, _ -> fadeIn },
            popExitTransition = { _, _ -> fadeOut }
        ) {
            val arguments = requireNotNull(it.arguments)
            viewModel.fabMainIcon.postValue(MainIconType.BACK)
            CharacterAllPics(arguments.getInt(Navigation.UNIT_ID))
        }

        //角色资料
        composable(
            route = "${Navigation.CHARACTER_BASIC_INFO}/{${Navigation.UNIT_ID}}",
            arguments = listOf(navArgument(Navigation.UNIT_ID) {
                type = NavType.IntType
            }),
            enterTransition = { _, _ -> fadeIn },
            exitTransition = { _, _ -> fadeOut },
            popEnterTransition = { _, _ -> fadeIn },
            popExitTransition = { _, _ -> fadeOut }
        ) {
            val arguments = requireNotNull(it.arguments)
            val scrollState = rememberScrollState()
            CharacterBasicInfo(scrollState, unitId = arguments.getInt(Navigation.UNIT_ID))
        }

        //装备列表
        composable(
            route = Navigation.EQUIP_LIST,
            enterTransition = { _, _ -> fadeIn },
            exitTransition = { _, _ -> fadeOut },
            popEnterTransition = { _, _ -> fadeIn },
            popExitTransition = { _, _ -> fadeOut }
        ) {
            val scrollState = rememberLazyListState()
            viewModel.fabMainIcon.postValue(MainIconType.BACK)
            EquipList(
                scrollState,
                toEquipDetail = actions.toEquipDetail,
                toEquipMaterial = actions.toEquipMaterial
            )
        }

        //装备详情
        composable(
            route = "${Navigation.EQUIP_DETAIL}/{${Navigation.EQUIP_ID}}",
            arguments = listOf(navArgument(Navigation.EQUIP_ID) {
                type = NavType.IntType
            }),
            enterTransition = { _, _ -> fadeIn },
            exitTransition = { _, _ -> fadeOut },
            popEnterTransition = { _, _ -> fadeIn },
            popExitTransition = { _, _ -> fadeOut }
        ) {
            viewModel.fabMainIcon.postValue(MainIconType.BACK)
            val arguments = requireNotNull(it.arguments)
            EquipMainInfo(arguments.getInt(Navigation.EQUIP_ID), actions.toEquipMaterial)
        }

        //装备素材详情
        composable(
            route = "${Navigation.EQUIP_MATERIAL}/{${Navigation.EQUIP_ID}}",
            arguments = listOf(navArgument(Navigation.EQUIP_ID) {
                type = NavType.IntType
            }),
            enterTransition = { _, _ -> fadeIn },
            exitTransition = { _, _ -> fadeOut },
            popEnterTransition = { _, _ -> fadeIn },
            popExitTransition = { _, _ -> fadeOut }
        ) {
            val arguments = requireNotNull(it.arguments)
            EquipMaterialDeatil(arguments.getInt(Navigation.EQUIP_ID))
        }

        //角色 RANK 装备
        composable(
            route = "${Navigation.RANK_EQUIP}/{${Navigation.UNIT_ID}}",
            arguments = listOf(navArgument(Navigation.UNIT_ID) {
                type = NavType.IntType
            }),
            enterTransition = { _, _ -> fadeIn },
            exitTransition = { _, _ -> fadeOut },
            popEnterTransition = { _, _ -> fadeIn },
            popExitTransition = { _, _ -> fadeOut }
        ) {
            val arguments = requireNotNull(it.arguments)
            RankEquipList(
                unitId = arguments.getInt(Navigation.UNIT_ID),
                toEquipDetail = actions.toEquipDetail
            )
        }

        //角色 RANK 对比
        composable(
            route = "${Navigation.RANK_COMPARE}/{${Navigation.UNIT_ID}}/{${Navigation.MAX_RANK}}/{${Navigation.LEVEL}}/{${Navigation.RARITY}}/{${Navigation.UNIQUE_EQUIP_LEVEL}}",
            arguments = listOf(navArgument(Navigation.UNIT_ID) {
                type = NavType.IntType
            }, navArgument(Navigation.MAX_RANK) {
                type = NavType.IntType
            }, navArgument(Navigation.LEVEL) {
                type = NavType.IntType
            }, navArgument(Navigation.RARITY) {
                type = NavType.IntType
            }, navArgument(Navigation.UNIQUE_EQUIP_LEVEL) {
                type = NavType.IntType
            }),
            enterTransition = { _, _ -> fadeIn },
            exitTransition = { _, _ -> fadeOut },
            popEnterTransition = { _, _ -> fadeIn },
            popExitTransition = { _, _ -> fadeOut }
        ) {
            val arguments = requireNotNull(it.arguments)
            RankCompare(
                unitId = arguments.getInt(Navigation.UNIT_ID),
                maxRank = arguments.getInt(Navigation.MAX_RANK),
                level = arguments.getInt(Navigation.LEVEL),
                rarity = arguments.getInt(Navigation.RARITY),
                uniqueEquipLevel = arguments.getInt(Navigation.UNIQUE_EQUIP_LEVEL),
                navViewModel = viewModel
            )
        }

        //角色装备统计
        composable(
            route = "${Navigation.EQUIP_COUNT}/{${Navigation.UNIT_ID}}/{${Navigation.MAX_RANK}}",
            arguments = listOf(navArgument(Navigation.UNIT_ID) {
                type = NavType.IntType
            }, navArgument(Navigation.MAX_RANK) {
                type = NavType.IntType
            }),
            enterTransition = { _, _ -> fadeIn },
            exitTransition = { _, _ -> fadeOut },
            popEnterTransition = { _, _ -> fadeIn },
            popExitTransition = { _, _ -> fadeOut }
        ) {
            val arguments = requireNotNull(it.arguments)
            RankEquipCount(
                unitId = arguments.getInt(Navigation.UNIT_ID),
                maxRank = arguments.getInt(Navigation.MAX_RANK),
                actions.toEquipMaterial,
                navViewModel = viewModel
            )
        }

        //角色排行
        composable(
            route = Navigation.TOOL_LEADER,
            enterTransition = { _, _ -> fadeIn },
            exitTransition = { _, _ -> fadeOut },
            popEnterTransition = { _, _ -> fadeIn },
            popExitTransition = { _, _ -> fadeOut }
        ) {
            viewModel.fabMainIcon.postValue(MainIconType.BACK)
            val scrollState = rememberLazyListState()
            LeaderboardList(scrollState)
        }

        //角色卡池
        composable(
            route = Navigation.TOOL_GACHA,
            enterTransition = { _, _ -> fadeIn },
            exitTransition = { _, _ -> fadeOut },
            popEnterTransition = { _, _ -> fadeIn },
            popExitTransition = { _, _ -> fadeOut }
        ) {
            viewModel.fabMainIcon.postValue(MainIconType.BACK)
            val scrollState = rememberLazyListState()
            GachaList(scrollState, actions.toCharacterDetail)
        }

        //剧情活动
        composable(
            route = Navigation.TOOL_EVENT,
            enterTransition = { _, _ -> fadeIn },
            exitTransition = { _, _ -> fadeOut },
            popEnterTransition = { _, _ -> fadeIn },
            popExitTransition = { _, _ -> fadeOut }
        ) {
            viewModel.fabMainIcon.postValue(MainIconType.BACK)
            val scrollState = rememberLazyListState()
            EventList(scrollState, actions.toCharacterDetail)
        }

        //角色公会
        composable(
            route = Navigation.TOOL_GUILD,
            enterTransition = { _, _ -> fadeIn },
            exitTransition = { _, _ -> fadeOut },
            popEnterTransition = { _, _ -> fadeIn },
            popExitTransition = { _, _ -> fadeOut }
        ) {
            viewModel.fabMainIcon.postValue(MainIconType.BACK)
            val scrollState = rememberLazyListState()
            GuildList(scrollState, actions.toCharacterDetail)
        }

        //团队战
        composable(
            route = Navigation.TOOL_CLAN,
            enterTransition = { _, _ -> fadeIn },
            exitTransition = { _, _ -> fadeOut },
            popEnterTransition = { _, _ -> fadeIn },
            popExitTransition = { _, _ -> fadeOut }
        ) {
            val scrollState = rememberLazyListState()
            viewModel.fabMainIcon.postValue(MainIconType.BACK)
            ClanBattleList(scrollState, actions.toClanBossInfo)
        }

        //团队战详情
        composable(
            route = "${Navigation.TOOL_CLAN_BOSS_INFO}/{${Navigation.TOOL_CLAN_BOSS_ID}}/{${Navigation.TOOL_CLAN_BOSS_INDEX}}",
            arguments = listOf(navArgument(Navigation.TOOL_CLAN_BOSS_ID) {
                type = NavType.IntType
            }, navArgument(Navigation.TOOL_CLAN_BOSS_INDEX) {
                type = NavType.IntType
            }),
            enterTransition = { _, _ -> fadeIn },
            exitTransition = { _, _ -> fadeOut },
            popEnterTransition = { _, _ -> fadeIn },
            popExitTransition = { _, _ -> fadeOut }
        ) {
            val arguments = requireNotNull(it.arguments)
            ClanBossInfoPager(
                arguments.getInt(Navigation.TOOL_CLAN_BOSS_ID),
                arguments.getInt(Navigation.TOOL_CLAN_BOSS_INDEX)
            )
        }

        //竞技场查询
        composable(
            route = Navigation.TOOL_PVP,
            enterTransition = { _, _ -> fadeIn(1f) },
            exitTransition = { _, _ -> fadeOut },
            popEnterTransition = { _, _ -> fadeIn },
            popExitTransition = { _, _ -> fadeOut }
        ) {
            PvpSearchCompose(
                toCharacter = actions.toCharacterDetail
            )
        }

        //设置页面
        composable(
            route = Navigation.MAIN_SETTINGS,
            enterTransition = { _, _ -> fadeIn },
            exitTransition = { _, _ -> fadeOut },
            popEnterTransition = { _, _ -> fadeIn },
            popExitTransition = { _, _ -> fadeOut }
        ) {
            viewModel.fabMainIcon.postValue(MainIconType.BACK)
            MainSettings()
        }

        //更新通知
        composable(
            route = Navigation.APP_NOTICE,
            enterTransition = { _, _ -> fadeIn },
            exitTransition = { _, _ -> fadeOut },
            popEnterTransition = { _, _ -> fadeIn },
            popExitTransition = { _, _ -> fadeOut }
        ) {
            val scrollState = rememberLazyListState()
            viewModel.fabMainIcon.postValue(MainIconType.BACK)
            NoticeList(scrollState)
        }

        //公告
        composable(
            route = Navigation.TOOL_NEWS,
            enterTransition = { _, _ -> fadeIn },
            exitTransition = { _, _ -> fadeOut },
            popEnterTransition = { _, _ -> fadeIn },
            popExitTransition = { _, _ -> fadeOut }
        ) {
            viewModel.fabMainIcon.postValue(MainIconType.BACK)
            NewsList(
                pagerState,
                scrollState0,
                scrollState1,
                scrollState2,
                news0,
                news1,
                news2,
                actions.toNewsDetail
            )
        }

        //公告详情
        composable(
            route = "${Navigation.TOOL_NEWS_DETAIL}/{${Navigation.TOOL_NEWS_KEY}}",
            arguments = listOf(
                navArgument(Navigation.TOOL_NEWS_KEY) {
                    type = NavType.StringType
                },
            ),
            enterTransition = { _, _ -> fadeIn },
            exitTransition = { _, _ -> fadeOut },
            popEnterTransition = { _, _ -> fadeIn },
            popExitTransition = { _, _ -> fadeOut }
        ) {
            viewModel.fabMainIcon.postValue(MainIconType.BACK)
            val arguments = requireNotNull(it.arguments)
            NewsDetail(arguments.getString(Navigation.TOOL_NEWS_KEY) ?: "")
        }

        //推特信息
        composable(
            route = Navigation.TWEET,
            enterTransition = { _, _ -> fadeIn },
            exitTransition = { _, _ -> fadeOut },
            popEnterTransition = { _, _ -> fadeIn },
            popExitTransition = { _, _ -> fadeOut }
        ) {
            viewModel.fabMainIcon.postValue(MainIconType.BACK)
            TweetList(tweet, tweetScrollState, actions.toNewsDetail, actions.toComicListIndex)
        }

        //漫画信息
        composable(
            route = Navigation.COMIC,
            enterTransition = { _, _ -> fadeIn },
            exitTransition = { _, _ -> fadeOut },
            popEnterTransition = { _, _ -> fadeIn },
            popExitTransition = { _, _ -> fadeOut }
        ) {
            viewModel.fabMainIcon.postValue(MainIconType.BACK)
            ComicList()
        }

        //漫画跳转
        composable(
            route = "${Navigation.COMIC}/{${Navigation.COMIC_ID}}",
            arguments = listOf(navArgument(Navigation.COMIC_ID) {
                type = NavType.IntType
            }),
            enterTransition = { _, _ -> fadeIn },
            exitTransition = { _, _ -> fadeOut },
            popEnterTransition = { _, _ -> fadeIn },
            popExitTransition = { _, _ -> fadeOut }
        ) {
            val arguments = requireNotNull(it.arguments)
            ComicList(arguments.getInt(Navigation.COMIC_ID))
        }

        //技能列表
        composable(
            route = Navigation.ALL_SKILL,
            enterTransition = { _, _ -> fadeIn },
            exitTransition = { _, _ -> fadeOut },
            popEnterTransition = { _, _ -> fadeIn },
            popExitTransition = { _, _ -> fadeOut }
        ) {
            viewModel.fabMainIcon.postValue(MainIconType.BACK)
            AllSkillList()
        }

        //战力系数
        composable(
            route = Navigation.ATTR_COE,
            enterTransition = { _, _ -> fadeIn },
            exitTransition = { _, _ -> fadeOut },
            popEnterTransition = { _, _ -> fadeIn },
            popExitTransition = { _, _ -> fadeOut }
        ) {
            viewModel.fabMainIcon.postValue(MainIconType.BACK)
            CharacterStatusCoeCompose()
        }
    }
}

/**
 * 导航
 */
class NavActions(navController: NavHostController) {

    /**
     * 角色列表
     */
    val toCharacterList: () -> Unit = {
        navController.navigate(Navigation.CHARACTER_LIST)
    }

    /**
     * 角色详情
     */
    val toCharacterDetail: (Int) -> Unit = { unitId: Int ->
        navController.navigate("${Navigation.CHARACTER_DETAIL}/${unitId}")
    }

    /**
     * 角色图片详情
     */
    val toCharacterPics: (Int) -> Unit = { unitId: Int ->
        navController.navigate("${Navigation.CHARACTER_PICS}/${unitId}")
    }

    /**
     * 装备详情
     */
    val toEquipDetail: (Int) -> Unit = { equipId: Int ->
        navController.navigate("${Navigation.EQUIP_DETAIL}/${equipId}")
    }

    /**
     * 装备素材详情
     */
    val toEquipMaterial: (Int) -> Unit = { equipId: Int ->
        navController.navigate("${Navigation.EQUIP_MATERIAL}/${equipId}")
    }

    /**
     * 角色资料
     */
    val toCharacterBasicInfo: (Int) -> Unit = { unitId: Int ->
        navController.navigate("${Navigation.CHARACTER_BASIC_INFO}/${unitId}")
    }

    /**
     * 角色 RANK 装备
     */
    val toCharacteRankEquip: (Int) -> Unit = { unitId: Int ->
        navController.navigate("${Navigation.RANK_EQUIP}/${unitId}")
    }

    /**
     * 角色 RANK 对比
     */
    val toCharacteRankCompare: (Int, Int, Int, Int, Int) -> Unit =
        { unitId: Int, maxRank: Int, level: Int, rarity: Int, uniqueEquipLevel: Int ->
            navController.navigate("${Navigation.RANK_COMPARE}/${unitId}/${maxRank}/${level}/${rarity}/${uniqueEquipLevel}")
        }

    /**
     * 角装备统计
     */
    val toCharacteEquipCount: (Int, Int) -> Unit =
        { unitId: Int, maxRank: Int ->
            navController.navigate("${Navigation.EQUIP_COUNT}/${unitId}/${maxRank}")
        }


    /**
     * 团队战 BOSS
     */
    val toClanBossInfo: (Int, Int) -> Unit = { clanId: Int, index: Int ->
        navController.navigate("${Navigation.TOOL_CLAN_BOSS_INFO}/${clanId}/${index}")
    }

    /**
     * 官方公告详情
     */
    val toNewsDetail: (String) -> Unit = { key: String ->
        navController.navigate("${Navigation.TOOL_NEWS_DETAIL}/${key}")
    }

    /**
     * 卡池
     */
    val toGacha = {
        navController.navigate(Navigation.TOOL_GACHA)
    }

    /**
     * 团队战
     */
    val toClan = {
        navController.navigate(Navigation.TOOL_CLAN)
    }

    /**
     * 剧情活动
     */
    val toEvent = {
        navController.navigate(Navigation.TOOL_EVENT)
    }

    /**
     * 角色公会
     */
    val toGuild = {
        navController.navigate(Navigation.TOOL_GUILD)
    }

    /**
     * 公告
     */
    val toNews: () -> Unit = {
        navController.navigate(Navigation.TOOL_NEWS)
    }

    /**
     * 竞技场
     */
    val toPvp = {
        navController.navigate(Navigation.TOOL_PVP)
    }

    /**
     * 排行
     */
    val toLeader = {
        navController.navigate(Navigation.TOOL_LEADER)
    }

    /**
     * 装备列表
     */
    val toEquipList = {
        navController.navigate(Navigation.EQUIP_LIST)
    }

    /**
     * 通知
     */
    val toNotice = {
        navController.navigate(Navigation.APP_NOTICE)
    }

    /**
     * 设置
     */
    val toSetting = {
        navController.navigate(Navigation.MAIN_SETTINGS)
    }

    /**
     * 推特
     */
    val toTweetList = {
        navController.navigate(Navigation.TWEET)
    }

    /**
     * 漫画
     */
    val toComicList = {
        navController.navigate(Navigation.COMIC)
    }

    /**
     * 漫画
     */
    val toComicListIndex: (Int) -> Unit = { comicId ->
        navController.navigate("${Navigation.COMIC}/${comicId}")
    }

    /**
     * 技能列表
     */
    val toAllSkillList = {
        navController.navigate(Navigation.ALL_SKILL)
    }

    /**
     * 战力系数
     */
    val toCoe = {
        navController.navigate(Navigation.ATTR_COE)
    }
}

/**
 * 导航 ViewModel
 */
@HiltViewModel
class NavViewModel @Inject constructor() : ViewModel() {

    /**
     * fab 图标显示
     */
    val fabMainIcon = MutableLiveData(MainIconType.MAIN)

    /**
     * 确认
     */
    val fabOKCilck = MutableLiveData(false)

    /**
     * 关闭
     */
    val fabCloseClick = MutableLiveData(false)

    /**
     * 选择的 RANK
     */
    val selectRank = MutableLiveData(0)


    /**
     * 下载状态
     * -2: 隐藏
     * -1: 显示加载中
     * >0: 进度
     */
    val downloadProgress = MutableLiveData(-1)

    /**
     * 加载中
     */
    val loading = MutableLiveData(false)

    /**
     * 已六星的角色ID
     */
    val r6Ids = MutableLiveData(listOf<Int>())

    /**
     * 重置
     */
    val resetClick = MutableLiveData(false)

    /**
     * 角色筛选
     */
    var filterCharacter = MutableLiveData(FilterCharacter())

    /**
     * 装备筛选
     */
    var filterEquip = MutableLiveData(FilterEquipment())

    /**
     * 竞技场查询角色
     */
    val selectedPvpData = MutableLiveData(
        arrayListOf(
            PvpCharacterData(),
            PvpCharacterData(),
            PvpCharacterData(),
            PvpCharacterData(),
            PvpCharacterData()
        )
    )

    /**
     * rank 选择，当前
     */
    var curRank = MutableLiveData(0)

    /**
     * rank 选择，目标
     */
    var targetRank = MutableLiveData(0)

    /**
     * rank 选择，当前
     */
    var curRank1 = MutableLiveData(0)

    /**
     * rank 选择，目标
     */
    var targetRank1 = MutableLiveData(0)

    /**
     * 悬浮服务
     */
    val floatServiceRun = MutableLiveData(true)

    /**
     * 悬浮闯最小化
     */
    val floatSearchMin = MutableLiveData(false)

    /**
     * pvp 查询结果显示
     */
    val showResult = MutableLiveData(false)


    /**
     * 数据切换弹窗显示
     */
    val openChangeDataDialog = MutableLiveData(false)
}
