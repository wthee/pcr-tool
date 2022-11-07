package cn.wthee.pcrtool.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import cn.wthee.pcrtool.data.enums.AllPicsType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.UnitType
import cn.wthee.pcrtool.ui.character.*
import cn.wthee.pcrtool.ui.common.AllCardList
import cn.wthee.pcrtool.ui.common.NoDataText
import cn.wthee.pcrtool.ui.equip.EquipList
import cn.wthee.pcrtool.ui.equip.EquipMainInfo
import cn.wthee.pcrtool.ui.equip.EquipMaterialDetail
import cn.wthee.pcrtool.ui.home.Overview
import cn.wthee.pcrtool.ui.skill.SummonDetail
import cn.wthee.pcrtool.ui.theme.*
import cn.wthee.pcrtool.ui.tool.*
import cn.wthee.pcrtool.ui.tool.clan.ClanBattleDetail
import cn.wthee.pcrtool.ui.tool.clan.ClanBattleList
import cn.wthee.pcrtool.ui.tool.extraequip.ExtraEquipDetail
import cn.wthee.pcrtool.ui.tool.extraequip.ExtraEquipList
import cn.wthee.pcrtool.ui.tool.extraequip.ExtraEquipUnitList
import cn.wthee.pcrtool.ui.tool.mockgacha.MockGacha
import cn.wthee.pcrtool.ui.tool.pvp.PvpSearchCompose
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState

/**
 * 导航路径
 */
object Navigation {
    const val HOME = "home"
    const val CHARACTER_LIST = "characterList"
    const val CHARACTER_DETAIL = "characterDetail"
    const val ALL_PICS = "allPics"
    const val ALL_PICS_TYPE = "allPicsType"
    const val CHARACTER_BASIC_INFO = "characterBasicInfo"
    const val CHARACTER_STORY_DETAIL = "characterStoryDetail"
    const val EQUIP_LIST = "equipList"
    const val EQUIP_DETAIL = "equipDetail"
    const val EXTRA_EQUIP_DETAIL = "exEquipDetail"
    const val RANK_EQUIP = "rankEquip"
    const val RANK_COMPARE = "rankCompare"
    const val EQUIP_COUNT = "equipCount"
    const val EQUIP_MATERIAL = "equipMaterial"
    const val TOOL_LEADER = "toolLeader"
    const val TOOL_GACHA = "toolGacha"
    const val TOOL_FREE_GACHA = "toolFreeGacha"
    const val TOOL_STORY_EVENT = "toolStoryEvent"
    const val TOOL_GUILD = "toolGuild"
    const val TOOL_CLAN = "toolClanBattle"
    const val TOOL_CLAN_BOSS_INFO = "toolClanBattleInfo"
    const val TOOL_PVP = "toolPvpSearch"
    const val TOOL_NEWS = "toolNews"
    const val TOOL_NEWS_DETAIL = "toolNewsDetail"
    const val TOOL_MOCK_GACHA = "toolMockGacha"
    const val MAIN_SETTINGS = "mainSettings"
    const val TWEET = "tweet"
    const val COMIC = "comic"
    const val ALL_SKILL = "allSkill"
    const val ALL_EQUIP = "allEquip"
    const val ATTR_COE = "attrCoe"
    const val UNIT_ID = "unitId"
    const val EQUIP_ID = "equipId"
    const val MAX_RANK = "maxRank"
    const val LEVEL = "level"
    const val RARITY = "rarity"
    const val RANK = "rank"
    const val UNIQUE_EQUIP_LEVEL = "uniqueEquipLevel"
    const val COMIC_ID = "comicId"
    const val TOOL_CLAN_Battle_ID = "toolClanBattleID"
    const val TOOL_CLAN_BOSS_INDEX = "toolClanBattleIndex"
    const val TOOL_CLAN_BOSS_PHASE = "toolClanBattlePhase"
    const val TOOL_NEWS_ID = "toolNewsId"
    const val SUMMON_DETAIL = "summonDetail"
    const val UNIT_TYPE = "unitType"
    const val TOOL_EQUIP_AREA = "toolArea"
    const val TOOL_MORE = "toolMore"
    const val TOOL_MORE_EDIT_MODE = "toolMoreEditMode"
    const val TOOL_BIRTHDAY = "toolBirthday"
    const val TOOL_CALENDAR_EVENT = "toolCalendarEvent"
    const val CHARACTER_SKILL_LOOP = "characterSkillLoop"
    const val TOOL_EXTRA_EQUIP = "toolExtraEquip"
    const val TOOL_EXTRA_EQUIP_UNIT = "toolExtraEquipUnit"
    const val EXTRA_CATEGROY = "toolExtraEquipCategory"
    const val CHARACTER_EXTRA_EQUIP_SLOT = "characterExtraEquipSlot"
}

/**
 * 导航内容
 */
@OptIn(
    ExperimentalPagerApi::class,
    ExperimentalAnimationApi::class,
    ExperimentalMaterialNavigationApi::class
)
@Composable
fun NavGraph(
    bottomSheetNavigator: BottomSheetNavigator,
    navController: NavHostController,
    viewModel: NavViewModel,
    actions: NavActions,
) {
    val statusBarHeight = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()


    ModalBottomSheetLayout(
        modifier = Modifier.padding(top = statusBarHeight),
        sheetBackgroundColor = MaterialTheme.colorScheme.surface,
        scrimColor = if (isSystemInDarkTheme()) colorAlphaBlack else colorAlphaWhite,
        sheetShape = shapeTop(),
        bottomSheetNavigator = bottomSheetNavigator
    ) {
        AnimatedNavHost(
            navController = navController, startDestination = Navigation.HOME
        ) {

            //首页
            composable(route = Navigation.HOME,
                enterTransition = { myFadeIn },
                exitTransition = { myFadeOut },
                popEnterTransition = { myFadeIn }) {
                viewModel.fabMainIcon.postValue(MainIconType.MAIN)
                val scrollState = rememberLazyListState()
                Overview(actions = actions, scrollState)
            }

            //角色列表
            composable(route = Navigation.CHARACTER_LIST,
                enterTransition = { myFadeIn },
                exitTransition = { myFadeOut },
                popEnterTransition = { myFadeIn },
                popExitTransition = { myFadeOut }) {
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                val scrollState = rememberLazyGridState()
                CharacterList(scrollState, actions.toCharacterDetail)
            }

            //角色属性详情
            composable(route = "${Navigation.CHARACTER_DETAIL}/{${Navigation.UNIT_ID}}",
                arguments = listOf(navArgument(Navigation.UNIT_ID) {
                    type = NavType.IntType
                }),
                enterTransition = { myFadeIn },
                exitTransition = { myFadeOut },
                popEnterTransition = { myFadeIn },
                popExitTransition = { myFadeOut }) {
                val arguments = requireNotNull(it.arguments)
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                val scrollState = rememberScrollState()
                CharacterDetail(
                    scrollState, unitId = arguments.getInt(Navigation.UNIT_ID), actions
                )
            }

            //角色图片详情
            bottomSheet(
                route = "${Navigation.ALL_PICS}/{${Navigation.UNIT_ID}}/{${Navigation.ALL_PICS_TYPE}}",
                arguments = listOf(navArgument(Navigation.UNIT_ID) {
                    type = NavType.IntType
                }, navArgument(Navigation.ALL_PICS_TYPE) {
                    type = NavType.IntType
                })
            ) {
                val arguments = requireNotNull(it.arguments)
                viewModel.fabMainIcon.postValue(MainIconType.BACK)

                AllCardList(
                    arguments.getInt(Navigation.UNIT_ID),
                    AllPicsType.getByValue(arguments.getInt(Navigation.ALL_PICS_TYPE))
                )
            }

            //角色资料
            bottomSheet(
                route = "${Navigation.CHARACTER_BASIC_INFO}/{${Navigation.UNIT_ID}}",
                arguments = listOf(navArgument(Navigation.UNIT_ID) {
                    type = NavType.IntType
                })
            ) {
                val arguments = requireNotNull(it.arguments)
                CharacterBasicInfo(unitId = arguments.getInt(Navigation.UNIT_ID))
            }

            //角色剧情属性详情
            bottomSheet(
                route = "${Navigation.CHARACTER_STORY_DETAIL}/{${Navigation.UNIT_ID}}",
                arguments = listOf(navArgument(Navigation.UNIT_ID) {
                    type = NavType.IntType
                })
            ) {
                val arguments = requireNotNull(it.arguments)
                CharacterStoryDetail(unitId = arguments.getInt(Navigation.UNIT_ID))
            }

            //装备列表
            composable(route = Navigation.EQUIP_LIST,
                enterTransition = { myFadeIn },
                exitTransition = { myFadeOut },
                popEnterTransition = { myFadeIn },
                popExitTransition = { myFadeOut }) {
                val scrollState = rememberLazyListState()
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                EquipList(
                    scrollState = scrollState,
                    toEquipDetail = actions.toEquipDetail,
                    toEquipMaterial = actions.toEquipMaterial
                )
            }

            //ex装备列表
            composable(route = Navigation.TOOL_EXTRA_EQUIP,
                enterTransition = { myFadeIn },
                exitTransition = { myFadeOut },
                popEnterTransition = { myFadeIn },
                popExitTransition = { myFadeOut }) {
                val scrollState = rememberLazyListState()
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                //日服可用
                if (MainActivity.regionType == 4) {
                    ExtraEquipList(
                        scrollState = scrollState,
                        toEquipDetail = actions.toExtraEquipDetail
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        NoDataText()
                    }
                }

            }

            //装备详情
            composable(route = "${Navigation.EQUIP_DETAIL}/{${Navigation.EQUIP_ID}}",
                arguments = listOf(navArgument(Navigation.EQUIP_ID) {
                    type = NavType.IntType
                }),
                enterTransition = { myFadeIn },
                exitTransition = { myFadeOut },
                popEnterTransition = { myFadeIn },
                popExitTransition = { myFadeOut }) {
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                val arguments = requireNotNull(it.arguments)
                EquipMainInfo(arguments.getInt(Navigation.EQUIP_ID), actions.toEquipMaterial)
            }


            //ex装备详情
            composable(route = "${Navigation.EXTRA_EQUIP_DETAIL}/{${Navigation.EQUIP_ID}}",
                arguments = listOf(navArgument(Navigation.EQUIP_ID) {
                    type = NavType.IntType
                }),
                enterTransition = { myFadeIn },
                exitTransition = { myFadeOut },
                popEnterTransition = { myFadeIn },
                popExitTransition = { myFadeOut }) {
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                val arguments = requireNotNull(it.arguments)
                ExtraEquipDetail(
                    equipId = arguments.getInt(Navigation.EQUIP_ID),
                    toExtraEquipUnit = actions.toExtraEquipUnit
                )
            }

            //ex装备关联角色
            bottomSheet(
                route = "${Navigation.TOOL_EXTRA_EQUIP_UNIT}/{${Navigation.EXTRA_CATEGROY}}",
                arguments = listOf(navArgument(Navigation.EXTRA_CATEGROY) {
                    type = NavType.IntType
                })
            ) {
                val arguments = requireNotNull(it.arguments)
                ExtraEquipUnitList(category = arguments.getInt(Navigation.EXTRA_CATEGROY))
            }

            //装备素材详情
            bottomSheet(
                route = "${Navigation.EQUIP_MATERIAL}/{${Navigation.EQUIP_ID}}",
                arguments = listOf(navArgument(Navigation.EQUIP_ID) {
                    type = NavType.IntType
                })
            ) {
                val arguments = requireNotNull(it.arguments)
                EquipMaterialDetail(arguments.getInt(Navigation.EQUIP_ID))
            }

            //角色 RANK 装备
            bottomSheet(
                route = "${Navigation.RANK_EQUIP}/{${Navigation.UNIT_ID}}/{${Navigation.RANK}}",
                arguments = listOf(navArgument(Navigation.UNIT_ID) {
                    type = NavType.IntType
                }, navArgument(Navigation.RANK) {
                    type = NavType.IntType
                })
            ) {
                val arguments = requireNotNull(it.arguments)
                RankEquipList(
                    unitId = arguments.getInt(Navigation.UNIT_ID),
                    currentRank = arguments.getInt(Navigation.RANK)
                )
            }

            //角色 RANK 对比
            composable(route = "${Navigation.RANK_COMPARE}/{${Navigation.UNIT_ID}}/{${Navigation.MAX_RANK}}/{${Navigation.LEVEL}}/{${Navigation.RARITY}}/{${Navigation.UNIQUE_EQUIP_LEVEL}}",
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
                enterTransition = { myFadeIn },
                exitTransition = { myFadeOut },
                popEnterTransition = { myFadeIn },
                popExitTransition = { myFadeOut }) {
                val arguments = requireNotNull(it.arguments)
                RankCompare(
                    unitId = arguments.getInt(Navigation.UNIT_ID),
                    maxRank = arguments.getInt(Navigation.MAX_RANK),
                    level = arguments.getInt(Navigation.LEVEL),
                    rarity = arguments.getInt(Navigation.RARITY),
                    uniqueEquipLevel = arguments.getInt(Navigation.UNIQUE_EQUIP_LEVEL)
                )
            }

            //角色ex装备列表
            composable(
                route = "${Navigation.CHARACTER_EXTRA_EQUIP_SLOT}/{${Navigation.UNIT_ID}}",
                arguments = listOf(navArgument(Navigation.UNIT_ID) {
                    type = NavType.IntType
                })
            ) {
                val scrollState = rememberLazyListState()
                val arguments = requireNotNull(it.arguments)
                CharacterExtraEquip(
                    scrollState = scrollState,
                    unitId = arguments.getInt(Navigation.UNIT_ID),
                    toExtraEquipDetail = actions.toExtraEquipDetail
                )
            }

            //角色装备统计
            composable(route = "${Navigation.EQUIP_COUNT}/{${Navigation.UNIT_ID}}/{${Navigation.MAX_RANK}}",
                arguments = listOf(navArgument(Navigation.UNIT_ID) {
                    type = NavType.IntType
                }, navArgument(Navigation.MAX_RANK) {
                    type = NavType.IntType
                }),
                enterTransition = { myFadeIn },
                exitTransition = { myFadeOut },
                popEnterTransition = { myFadeIn },
                popExitTransition = { myFadeOut }) {
                val arguments = requireNotNull(it.arguments)
                RankEquipCount(
                    unitId = arguments.getInt(Navigation.UNIT_ID),
                    maxRank = arguments.getInt(Navigation.MAX_RANK),
                    actions.toEquipMaterial,
                    navViewModel = viewModel
                )
            }

            //角色排行
            composable(route = Navigation.TOOL_LEADER,
                enterTransition = { myFadeIn },
                exitTransition = { myFadeOut },
                popEnterTransition = { myFadeIn },
                popExitTransition = { myFadeOut }) {
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                val scrollState = rememberLazyListState()
                LeaderboardList(scrollState)
            }

            //角色卡池
            composable(route = Navigation.TOOL_GACHA,
                enterTransition = { myFadeIn },
                exitTransition = { myFadeOut },
                popEnterTransition = { myFadeIn },
                popExitTransition = { myFadeOut }) {
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                val scrollState = rememberLazyListState()
                GachaList(scrollState, actions.toCharacterDetail)
            }

            //免费十连
            composable(route = Navigation.TOOL_FREE_GACHA,
                enterTransition = { myFadeIn },
                exitTransition = { myFadeOut },
                popEnterTransition = { myFadeIn },
                popExitTransition = { myFadeOut }) {
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                val scrollState = rememberLazyListState()
                FreeGachaList(scrollState)
            }

            //剧情活动
            composable(route = Navigation.TOOL_STORY_EVENT,
                enterTransition = { myFadeIn },
                exitTransition = { myFadeOut },
                popEnterTransition = { myFadeIn },
                popExitTransition = { myFadeOut }) {
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                val scrollState = rememberLazyGridState()
                StoryEventList(scrollState, actions.toCharacterDetail, actions.toAllPics)
            }

            //角色公会
            composable(route = Navigation.TOOL_GUILD,
                enterTransition = { myFadeIn },
                exitTransition = { myFadeOut },
                popEnterTransition = { myFadeIn },
                popExitTransition = { myFadeOut }) {
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                val scrollState = rememberLazyListState()
                GuildList(scrollState, actions.toCharacterDetail)
            }

            //团队战
            composable(route = Navigation.TOOL_CLAN,
                enterTransition = { myFadeIn },
                exitTransition = { myFadeOut },
                popEnterTransition = { myFadeIn },
                popExitTransition = { myFadeOut }) {
                val scrollState = rememberLazyGridState()
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                ClanBattleList(scrollState, actions.toClanBossInfo)
            }

            //团队战详情
            composable(route = "${Navigation.TOOL_CLAN_BOSS_INFO}/{${Navigation.TOOL_CLAN_Battle_ID}}/{${Navigation.TOOL_CLAN_BOSS_INDEX}}/{${Navigation.TOOL_CLAN_BOSS_PHASE}}",
                arguments = listOf(navArgument(Navigation.TOOL_CLAN_Battle_ID) {
                    type = NavType.IntType
                }, navArgument(Navigation.TOOL_CLAN_BOSS_INDEX) {
                    type = NavType.IntType
                }, navArgument(Navigation.TOOL_CLAN_BOSS_PHASE) {
                    type = NavType.IntType
                }),
                enterTransition = { myFadeIn },
                exitTransition = { myFadeOut },
                popEnterTransition = { myFadeIn },
                popExitTransition = { myFadeOut }) {
                val arguments = requireNotNull(it.arguments)
                ClanBattleDetail(
                    arguments.getInt(Navigation.TOOL_CLAN_Battle_ID),
                    arguments.getInt(Navigation.TOOL_CLAN_BOSS_INDEX),
                    arguments.getInt(Navigation.TOOL_CLAN_BOSS_PHASE),
                    actions.toSummonDetail
                )
            }

            //竞技场查询
            composable(route = Navigation.TOOL_PVP,
                enterTransition = { myFadeIn },
                exitTransition = { myFadeOut },
                popEnterTransition = { myFadeIn },
                popExitTransition = { myFadeOut }) {
                val pagerState = rememberPagerState()
                val selectListState = rememberLazyGridState()
                val usedListState = rememberLazyGridState()
                val resultListState = rememberLazyGridState()
                val favoritesListState = rememberLazyGridState()
                val historyListState = rememberLazyGridState()

                PvpSearchCompose(
                    floatWindow = false,
                    pagerState = pagerState,
                    selectListState = selectListState,
                    usedListState = usedListState,
                    resultListState = resultListState,
                    favoritesListState = favoritesListState,
                    historyListState = historyListState,
                    toCharacter = actions.toCharacterDetail
                )
            }

            //设置页面
            bottomSheet(
                route = Navigation.MAIN_SETTINGS
            ) {
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                MainSettings()
            }

            //公告
            composable(route = Navigation.TOOL_NEWS,
                enterTransition = { myFadeIn },
                exitTransition = { myFadeOut },
                popEnterTransition = { myFadeIn },
                popExitTransition = { myFadeOut }) {
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                NewsList(actions.toNewsDetail)
            }

            //公告详情
            bottomSheet(
                route = "${Navigation.TOOL_NEWS_DETAIL}/{${Navigation.TOOL_NEWS_ID}}",
                arguments = listOf(
                    navArgument(Navigation.TOOL_NEWS_ID) {
                        type = NavType.StringType
                    },
                )
            ) {
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                val arguments = requireNotNull(it.arguments)
                NewsDetail(arguments.getString(Navigation.TOOL_NEWS_ID) ?: "")
            }

            //推特信息
            composable(route = Navigation.TWEET,
                enterTransition = { myFadeIn },
                exitTransition = { myFadeOut },
                popEnterTransition = { myFadeIn },
                popExitTransition = { myFadeOut }) {
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                TweetList(actions.toComicListIndex)
            }

            //漫画信息
            composable(route = Navigation.COMIC,
                enterTransition = { myFadeIn },
                exitTransition = { myFadeOut },
                popEnterTransition = { myFadeIn },
                popExitTransition = { myFadeOut }) {
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                ComicList()
            }

            //漫画跳转
            bottomSheet(
                route = "${Navigation.COMIC}/{${Navigation.COMIC_ID}}",
                arguments = listOf(navArgument(Navigation.COMIC_ID) {
                    type = NavType.IntType
                })
            ) {
                val arguments = requireNotNull(it.arguments)
                ComicList(arguments.getInt(Navigation.COMIC_ID))
            }

            //技能列表
            composable(route = Navigation.ALL_SKILL,
                enterTransition = { myFadeIn },
                exitTransition = { myFadeOut },
                popEnterTransition = { myFadeIn },
                popExitTransition = { myFadeOut }) {
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                AllSkillList(actions.toSummonDetail)
            }

            //战力系数
            bottomSheet(
                route = Navigation.ATTR_COE
            ) {
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                CharacterStatusCoeCompose()
            }

            //召唤物信息
            bottomSheet(
                route = "${Navigation.SUMMON_DETAIL}/{${Navigation.UNIT_ID}}/{${Navigation.UNIT_TYPE}}/{${Navigation.LEVEL}}/{${Navigation.RANK}}/{${Navigation.RARITY}}",
                arguments = listOf(navArgument(Navigation.UNIT_ID) {
                    type = NavType.IntType
                }, navArgument(Navigation.UNIT_TYPE) {
                    type = NavType.IntType
                }, navArgument(Navigation.LEVEL) {
                    type = NavType.IntType
                }, navArgument(Navigation.RANK) {
                    type = NavType.IntType
                }, navArgument(Navigation.RARITY) {
                    type = NavType.IntType
                })
            ) {
                val arguments = requireNotNull(it.arguments)
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                SummonDetail(
                    unitId = arguments.getInt(Navigation.UNIT_ID),
                    unitType = UnitType.getByValue(arguments.getInt(Navigation.UNIT_TYPE)),
                    level = arguments.getInt(Navigation.LEVEL),
                    rank = arguments.getInt(Navigation.RANK),
                    rarity = arguments.getInt(Navigation.RARITY)
                )
            }

            //技能循环信息
            bottomSheet(
                route = "${Navigation.CHARACTER_SKILL_LOOP}/{${Navigation.UNIT_ID}}",
                arguments = listOf(navArgument(Navigation.UNIT_ID) {
                    type = NavType.IntType
                })
            ) {
                val arguments = requireNotNull(it.arguments)
                CharacterSkillLoop(unitId = arguments.getInt(Navigation.UNIT_ID))
            }

            //所有角色所需装备统计
            composable(route = Navigation.ALL_EQUIP,
                enterTransition = { myFadeIn },
                exitTransition = { myFadeOut },
                popEnterTransition = { myFadeIn },
                popExitTransition = { myFadeOut }) {
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                AllCharacterRankEquipCount(actions.toEquipMaterial)
            }

            //额外随机装备掉落地区
            composable(route = "${Navigation.TOOL_EQUIP_AREA}/{${Navigation.EQUIP_ID}}",
                arguments = listOf(navArgument(Navigation.EQUIP_ID) {
                    type = NavType.IntType
                }),
                enterTransition = { myFadeIn },
                exitTransition = { myFadeOut },
                popEnterTransition = { myFadeIn },
                popExitTransition = { myFadeOut }) {
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                val scrollState = rememberLazyListState()
                val arguments = requireNotNull(it.arguments)
                RandomEquipArea(
                    arguments.getInt(Navigation.EQUIP_ID), scrollState
                )
            }

            //更多工具
            composable(route = "${Navigation.TOOL_MORE}/{${Navigation.TOOL_MORE_EDIT_MODE}}",
                arguments = listOf(navArgument(Navigation.TOOL_MORE_EDIT_MODE) {
                    type = NavType.BoolType
                }),
                enterTransition = { myFadeIn },
                exitTransition = { myFadeOut },
                popEnterTransition = { myFadeIn },
                popExitTransition = { myFadeOut }) {
                val arguments = requireNotNull(it.arguments)
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                val scrollState = rememberLazyListState()
                AllToolMenu(
                    arguments.getBoolean(Navigation.TOOL_MORE_EDIT_MODE), scrollState, actions
                )
            }

            //模拟抽卡
            composable(route = Navigation.TOOL_MOCK_GACHA,
                enterTransition = { myFadeIn },
                exitTransition = { myFadeOut },
                popEnterTransition = { myFadeIn },
                popExitTransition = { myFadeOut }) {
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                MockGacha()
            }

            //生日日程
            composable(route = Navigation.TOOL_BIRTHDAY,
                enterTransition = { myFadeIn },
                exitTransition = { myFadeOut },
                popEnterTransition = { myFadeIn },
                popExitTransition = { myFadeOut }) {
                val scrollState = rememberLazyGridState()
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                BirthdayList(scrollState, actions.toCharacterDetail)
            }

            //日程
            composable(route = Navigation.TOOL_CALENDAR_EVENT,
                enterTransition = { myFadeIn },
                exitTransition = { myFadeOut },
                popEnterTransition = { myFadeIn },
                popExitTransition = { myFadeOut }) {
                val scrollState = rememberLazyListState()
                viewModel.fabMainIcon.postValue(MainIconType.BACK)
                CalendarEventList(scrollState)
            }
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
    val toAllPics: (Int, Int) -> Unit = { unitId: Int, type: Int ->
        navController.navigate("${Navigation.ALL_PICS}/${unitId}/${type}")
    }

    /**
     * 装备详情
     */
    val toEquipDetail: (Int) -> Unit = { equipId: Int ->
        navController.navigate("${Navigation.EQUIP_DETAIL}/${equipId}")
    }

    /**
     * ex装备详情
     */
    val toExtraEquipDetail: (Int) -> Unit = { equipId: Int ->
        navController.navigate("${Navigation.EXTRA_EQUIP_DETAIL}/${equipId}")
    }

    /**
     * ex装备详情关联角色
     */
    val toExtraEquipUnit: (Int) -> Unit = { categroy: Int ->
        navController.navigate("${Navigation.TOOL_EXTRA_EQUIP_UNIT}/${categroy}")
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
     * 角色剧情属性详情
     */
    val toCharacterStoryDetail: (Int) -> Unit = { unitId: Int ->
        navController.navigate("${Navigation.CHARACTER_STORY_DETAIL}/${unitId}")
    }

    /**
     * 角色 RANK 装备
     */
    val toCharacterRankEquip: (Int, Int) -> Unit = { unitId: Int, currentRank: Int ->
        navController.navigate("${Navigation.RANK_EQUIP}/${unitId}/${currentRank}")
    }

    /**
     * 角色 RANK 对比
     */
    val toCharacterRankCompare: (Int, Int, Int, Int, Int) -> Unit =
        { unitId: Int, maxRank: Int, level: Int, rarity: Int, uniqueEquipLevel: Int ->
            navController.navigate("${Navigation.RANK_COMPARE}/${unitId}/${maxRank}/${level}/${rarity}/${uniqueEquipLevel}")
        }


    /**
     * 角色ex装备列表
     */
    val toCharacterExtraEquip: (Int) -> Unit = { unitId ->
        navController.navigate("${Navigation.CHARACTER_EXTRA_EQUIP_SLOT}/${unitId}")
    }


    /**
     * 角装备统计
     */
    val toCharacteEquipCount: (Int, Int) -> Unit = { unitId: Int, maxRank: Int ->
        navController.navigate("${Navigation.EQUIP_COUNT}/${unitId}/${maxRank}")
    }


    /**
     * 团队战 BOSS
     */
    val toClanBossInfo: (Int, Int, Int) -> Unit = { clanId: Int, index: Int, phase: Int ->
        navController.navigate("${Navigation.TOOL_CLAN_BOSS_INFO}/${clanId}/${index}/${phase}")
    }

    /**
     * 官方公告详情
     */
    val toNewsDetail: (Int) -> Unit = { id: Int ->
        navController.navigate("${Navigation.TOOL_NEWS_DETAIL}/${id}")
    }

    /**
     * 卡池
     */
    val toGacha = {
        navController.navigate(Navigation.TOOL_GACHA)
    }

    /**
     * 免费十连
     */
    val toFreeGacha = {
        navController.navigate(Navigation.TOOL_FREE_GACHA)
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
        navController.navigate(Navigation.TOOL_STORY_EVENT)
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
     * ex装备列表
     */
    val toExtraEquipList = {
        navController.navigate(Navigation.TOOL_EXTRA_EQUIP)
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

    /**
     * 召唤物信息
     */
    val toSummonDetail: (Int, Int, Int, Int, Int) -> Unit =
        { unitId, unitType, level, rank, rarity ->
            navController.navigate("${Navigation.SUMMON_DETAIL}/${unitId}/${unitType}/${level}/${rank}/${rarity}")
        }

    /**
     * 装备统计
     */
    val toAllEquipList = {
        navController.navigate(Navigation.ALL_EQUIP)
    }

    /**
     * 额外随机装备掉落地区
     */
    val toRandomEquipArea: (Int) -> Unit = { equipId ->
        navController.navigate("${Navigation.TOOL_EQUIP_AREA}/${equipId}")
    }

    /**
     * 更多工具
     */
    val toToolMore: (Boolean) -> Unit = { editMode ->
        navController.navigate("${Navigation.TOOL_MORE}/${editMode}")
    }

    /**
     * 模拟抽卡
     */
    val toMockGacha = {
        navController.navigate(Navigation.TOOL_MOCK_GACHA)
    }

    /**
     * 生日一览
     */
    val toBirthdayList = {
        navController.navigate(Navigation.TOOL_BIRTHDAY)
    }

    /**
     * 活动一览
     */
    val toCalendarEventList = {
        navController.navigate(Navigation.TOOL_CALENDAR_EVENT)
    }

    /**
     * 角色技能循环
     */
    val toCharacterSkillLoop: (Int) -> Unit = { unitId ->
        navController.navigate("${Navigation.CHARACTER_SKILL_LOOP}/${unitId}")
    }
}